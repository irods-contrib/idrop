package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.services.*;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.LookAndFeelManager;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.engine.TransferManagerImpl;
import org.irods.jargon.transfer.synch.SynchPeriodicScheduler;
import org.slf4j.LoggerFactory;

/**
 * Bootstrapping class for iDrop, load config, create necessary services, and start the appropriate
 * GUI components
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class StartupSequencer {

    /**
     * NOTE this class is in transition!!!!!!
     */
    private iDrop idrop;
    private IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(StartupSequencer.class);
    public static final int STARTUP_SEQUENCE_PAUSE_INTERVAL = 1000;

    public void doStartupSequence() {

        boolean guiShown = false;

        log.info("initiating startup sequence...");

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                "iDrop Client for iRODS");

        int count = 0;
        log.info("creating idropCore...");
        idropCore = new IDROPCore();

        try {
            idropCore.setIrodsFileSystem(IRODSFileSystem.instance());
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        log.info("creating idrop GUI app...");
        idrop = new iDrop(idropCore);

        IDROPSplashWindow idropSplashWindow = new IDROPSplashWindow(idrop);
        sleepABit();

        idropSplashWindow.setStatus("Initializing...", ++count);

        idropCore.setIconManager(new IconManager(idrop));
        sleepABit();

        String derivedConfigHomeDirectory = deriveConfigHomeDirectory();

        /*
         * Is this newer version that what the database and data represent? Look for a version file
         * and trigger any migration needed. This is the first step where the database has not been
         * started yet. There may be additinal steps added later to do migrations of data after the
         * database has started. This will happen later....
         */

        doAnyPreDatabaseLoadMigrationProcessing(derivedConfigHomeDirectory);

        /*
         * Try and load the database, look at database info and prefer over any configuration in the
         * deployed package. Merge the pre-configured data with any existing database info,
         * preferring what is in the database
         */

        idropSplashWindow.setStatus("Looking for configuration information...",
                ++count);

        Properties derivedProperties;
        IdropConfigurationService idropConfigurationService;

        try {

            /*
             * Here is where I first try and start the database to get the configuration. A database
             * error indicates that iDrop is already running
             */
            log.info("statup will now start up the existing database, a new one may be created....");
            idropConfigurationService = startUpTheDatabaseAndSetConfigurationServiceInIdropCore(derivedConfigHomeDirectory);

            log.info("checking for any necessary migrations, this may back up database data and return a value that indicates that iDrop will need to restart");

            /**
             * Code stub here. Think of using a backup dir/file to detect whether the pre migration
             * service needs to run. boolean restart =
             * idropDatabaseMigrationService.backupExistingDataForAnyMigration(previousVersion,
             * currentVersion, blah) if (restart) { display restart dialog exit } // now
             * idropDatabaseMigrationService.migrateBackedUpDataToNewDatabase();
             *
             */
            /*
             * Based on existing data in the database, and incoming data from the classpath
             * properties, come up with a merged set of properties
             */
            derivedProperties = idropConfigurationService.bootstrapConfigurationAndMergePropertiesFromLocalAndClasspath();
            sleepABit();

            idropSplashWindow.setStatus(
                    "Configuration information gathered, logging in...", ++count);

            log.info("config properties derived...");
            idropCore.setIdropConfig(new IdropConfig(derivedProperties));
            idropCore.getIdropConfig().setUpLogging();

        } catch (IdropAlreadyRunningException are) {
            log.error("idrop is already running, shutting down");
            JOptionPane.showMessageDialog((Component) null,
                    "iDrop is already running, cannot start", "iDrop Error",
                    JOptionPane.OK_OPTION);
            System.exit(1);
        } catch (IdropException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        log.info("setting jargon properties based on configurations in iDrop");
        try {
            idropCore.getIdropConfigurationService().pushIDROPConfigToJargonAndTransfer();
        } catch (Exception ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        log.info("setting initial look and feel");
        LookAndFeelManager laf = new LookAndFeelManager(idropCore);
        laf.setLookAndFeel(idropCore.getIdropConfig().getPropertyForKey(IdropConfigurationService.LOOK_AND_FEEL));

        log.info("logging in in splash background thread");
        idropSplashWindow.setStatus("Logging in...", ++count);

        final LoginDialog loginDialog = new LoginDialog(null, idropCore);
        Toolkit tk = idrop.getToolkit();
        int x = (tk.getScreenSize().width - loginDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - loginDialog.getHeight()) / 2;
        loginDialog.setLocation(x, y);
        idropSplashWindow.toBack();
        loginDialog.toFront();
        loginDialog.setVisible(true);

        if (idropCore.getIrodsAccount() == null) {
            log.warn("no login account, exiting");
            System.exit(0);
        }

        idropSplashWindow.toFront();
        sleepABit();

        idropSplashWindow.setStatus("Building transfer engine...", ++count);

        log.info("building transfer manager...");

        try {
            idropCore.setTransferManager(new TransferManagerImpl(idropCore.getIrodsFileSystem(), idrop));
            idropCore.getIdropConfigurationService().updateTransferOptions();
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error creating transferManager",
                    ex);
        }

        try {
            List<LocalIRODSTransfer> currentQueue = idropCore.getTransferManager().getCurrentQueue();

            if (!currentQueue.isEmpty()) {
                idropSplashWindow.toBack();
                int result = JOptionPane.showConfirmDialog((Component) null,
                        "Transfers are waiting to process, restart transfer?",
                        "iDrop Transfers in Progress",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION) {
                    idropCore.getTransferManager().pause();
                }
                idropSplashWindow.toFront();
            }
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error evaluating current queue",
                    ex);
        }
        sleepABit();

        log.info("logged in, now checking for first run...");
        sleepABit();

        idropSplashWindow.setStatus(
                "Checking if this is the first time run to set up synch...",
                ++count);

        String synchDeviceName = idropCore.getIdropConfig().getSynchDeviceName();
        idrop.signalIdropCoreReadyAndSplashComplete();

        // see if I show the gui at startup or show a message
        if (idropCore.getIdropConfig().isShowGuiAtStartup()) {
            idrop.showIdropGui();
        } else {
            Object[] options = {"Do not show GUI at startup",
                "Show GUI at startup"};

            int n = JOptionPane.showOptionDialog(idrop,
                    "iDrop has started.\nCheck your system tray to access the iDrop user interface. ",
                    "iDrop - Startup Complete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            log.info("response was:{}", n);
            if (n == 1) {
                log.info("switching to show GUI at startup");
                try {
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.SHOW_GUI, "true");
                    idrop.showIdropGui();
                } catch (IdropException ex) {
                    log.error("error setting show GUI at startup", ex);
                    throw new IdropRuntimeException(ex);
                }
            }
        }


        idropSplashWindow.setStatus("Starting work queue...", ++count);
        try {
            QueueSchedulerTimerTask queueSchedulerTimerTask = new QueueSchedulerTimerTask(
                    idropCore.getTransferManager(), idrop);
            SynchPeriodicScheduler synchPeriodicScheduler = new SynchPeriodicScheduler(idropCore.getTransferManager(), idropCore.getIRODSAccessObjectFactory());
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(queueSchedulerTimerTask, 10000, 120000);
            timer.scheduleAtFixedRate(synchPeriodicScheduler, 10000, 30000);

            idropCore.setQueueTimer(timer);

        } catch (IdropException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        log.info("signal that the startup sequence is complete");
        try {
            try {
                Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL * 2);
            } catch (InterruptedException e) {
                throw new IdropRuntimeException(e);
            }
            idropSplashWindow.setVisible(false);
            idropSplashWindow = null;
        } catch (Exception e) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, e);

            throw new IdropRuntimeException("error starting idrop gui", e);
        }

    }

    private IdropConfigurationService startUpTheDatabaseAndSetConfigurationServiceInIdropCore(String derivedConfigHomeDirectory) throws IdropException {
        IdropConfigurationService idropConfigurationService;
        idropConfigurationService = new IdropConfigurationServiceImpl(
                derivedConfigHomeDirectory, idropCore);
        idropCore.setIdropConfigurationService(idropConfigurationService);
        return idropConfigurationService;
    }

    private void sleepABit() throws IdropRuntimeException {
        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }
    }

    private void doAnyPreDatabaseLoadMigrationProcessing(String derivedConfigHomeDirectory) throws IdropRuntimeException {
        Properties propertiesLoadedFromIdropApplicationClasspath;
        try {

            IdropPropertiesHelper helper = new IdropPropertiesHelper();
            propertiesLoadedFromIdropApplicationClasspath = helper.loadIdropProperties();
            String currentVersion = propertiesLoadedFromIdropApplicationClasspath.getProperty(IdropConfigurationService.VERSION_NUMBER);

            if (currentVersion == null || currentVersion.isEmpty()) {
                throw new IdropRuntimeException("unknown version number, not present in idrop.config");
            }

            IdropPreDatabaseBootstrapperService idropPreBootstrapperService = new IdropPreDatabaseBootstrapperServiceImpl();
            String cachedVersion = idropPreBootstrapperService.detectPriorVersion(derivedConfigHomeDirectory);
            idropPreBootstrapperService.triggerMigrations(derivedConfigHomeDirectory, cachedVersion, currentVersion);
            idropPreBootstrapperService.storePriorVersion(derivedConfigHomeDirectory, currentVersion);

        } catch (IdropException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }
    }

    private String deriveConfigHomeDirectory() {
        log.info("determine config root directory");
        String userHomeDirectory = System.getProperty("user.home");
        StringBuilder sb = new StringBuilder();
        sb.append(userHomeDirectory);
        sb.append("/.idrop");
        String derivedConfigHomeDirectory = sb.toString();
        log.info("set config home directory as: {}", derivedConfigHomeDirectory);
        return derivedConfigHomeDirectory;
    }

    /**
     * Start up iDrop as a system tray application. This is the main entry point for iDrop
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) throws InterruptedException {
        StartupSequencer startupSequencer = new StartupSequencer();
        try {
            startupSequencer.doStartupSequence();
        } catch (Exception e) {
            log.error("unable to start application due to error", e);
            System.exit(1);
        }
    }
}
