package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationServiceImpl;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropPreDatabaseBootstrapperService;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropPreDatabaseBootstrapperServiceImpl;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.LookAndFeelManager;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Bootstrapping class for iDrop, load config, create necessary services, and
 * start the appropriate GUI components
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IDROPDesktop {

    private iDrop idrop;
    private IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(IDROPDesktop.class);
    public static final int STARTUP_SEQUENCE_PAUSE_INTERVAL = 1000;

    public void doStartupSequence() {

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
            Logger.getLogger(IDROPDesktop.class.getName()).log(Level.SEVERE,
                    null, ex);
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
         * Is this newer version that what the database and data represent? Look
         * for a version file and trigger any migration needed. This is the
         * first step where the database has not been started yet. There may be
         * additinal steps added later to do migrations of data after the
         * database has started. This will happen later....
         */
        doAnyPreDatabaseLoadMigrationProcessing(derivedConfigHomeDirectory);

        /*
         * Try and load the database, look at database info and prefer over any
         * configuration in the deployed package. Merge the pre-configured data
         * with any existing database info, preferring what is in the database
         */
        idropSplashWindow.setStatus("Looking for configuration information...",
                ++count);

        Properties derivedProperties;
        IdropConfigurationService idropConfigurationService;

        try {

            /*
             * Here is where I first try and start the database to get the
             * configuration. A database error indicates that iDrop is already
             * running
             */
            log.info("statup will now start up the existing database, a new one may be created....");
            idropConfigurationService = startUpTheDatabaseAndSetConfigurationServiceInIdropCore(derivedConfigHomeDirectory);

            log.info("checking for any necessary migrations, this may back up database data and return a value that indicates that iDrop will need to restart");

            /**
             * Code stub here. Think of using a backup dir/file to detect
             * whether the pre migration service needs to run. boolean restart =
             * idropDatabaseMigrationService
             * .backupExistingDataForAnyMigration(previousVersion,
             * currentVersion, blah) if (restart) { display restart dialog exit
             * } // now
             * idropDatabaseMigrationService.migrateBackedUpDataToNewDatabase();
             *
             */
            /*
             * Based on existing data in the database, and incoming data from
             * the classpath properties, come up with a merged set of properties
             */
            derivedProperties = idropConfigurationService
                    .bootstrapConfigurationAndMergePropertiesFromLocalAndClasspath();
            sleepABit();

            idropSplashWindow.setStatus(
                    "Configuration information gathered, logging in...",
                    ++count);

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
            Logger.getLogger(IDROPDesktop.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new IdropRuntimeException(ex);
        }

        log.info("setting jargon properties based on configurations in iDrop");
        try {
            idropCore.getIdropConfigurationService()
                    .pushIDROPConfigToJargonAndTransfer();
        } catch (Exception ex) {
            Logger.getLogger(IDROPDesktop.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new IdropRuntimeException(ex);
        }

        log.info("setting initial look and feel");
        LookAndFeelManager laf = new LookAndFeelManager(idropCore);
        laf.setLookAndFeel(idropCore.getIdropConfig().getPropertyForKey(
                IdropConfigurationService.LOOK_AND_FEEL));

        log.info("logging in in splash background thread");
        idropSplashWindow.setStatus("Logging in...", ++count);
        boolean validated = false;

        try {
            if (idropCore.getConveyorService().getConfigurationService().isInTearOffMode()) {
                log.info("in tear off mode");
                validated = this.processTearOffMode();
            } else {
                log.info("processing normal pass phrase");
                validated = this.processNormalPassPhrase(idropSplashWindow);
            }
        } catch (IdropException ex) {
            Logger.getLogger(IDROPDesktop.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(IDROPDesktop.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        if (idropCore.irodsAccount() == null) {
            log.warn("no login account, exiting");
            System.exit(0);
        }

        log.info("validated, dequeue any pending and start timer task");
        try {
            idropCore.getConveyorService().init();
            idropCore.getConveyorService()
                    .beginFirstProcessAndRunPeriodicServiceInvocation();
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(IDROPDesktop.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        idropSplashWindow.toFront();
        sleepABit();

        log.info("logged in, now checking for first run...");
        sleepABit();
        /*

         idropSplashWindow.setStatus(
         "Checking if this is the first time run to set up synch...",
         ++count);

         idropCore.getIdropConfig().getSynchDeviceName();
         */

        idrop.signalIdropCoreReadyAndSplashComplete();

        idropSplashWindow.setStatus("Starting work queue...", ++count);

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
            Logger.getLogger(IDROPDesktop.class.getName()).log(Level.SEVERE,
                    null, e);

            throw new IdropRuntimeException("error starting idrop gui", e);
        }

    }

    private boolean processNormalPassPhrase(IDROPSplashWindow idropSplashWindow) throws IdropException, ConveyorExecutionException {
        log.info("process normal pass phrase");
        boolean validated = false;
        // check to see if need to set up initial pass phrase
        if (idropCore.getConveyorService().isPreviousPassPhraseStored()) {
            log.info("no previous pass phrase");
            // ask for pass phrase
            final PassPhraseDialog passPhraseDialog = new PassPhraseDialog(
                    null, true, idropCore);
            Toolkit tk = idrop.getToolkit();
            int x = (tk.getScreenSize().width - passPhraseDialog.getWidth()) / 2;
            int y = (tk.getScreenSize().height - passPhraseDialog
                    .getHeight()) / 2;
            passPhraseDialog.setLocation(x, y);
            idropSplashWindow.toBack();
            passPhraseDialog.toFront();
            passPhraseDialog.setVisible(true);
            validated = passPhraseDialog.isValidated();
            log.info("pass phrase dialog processed...validated? {}", validated);

        } else {
            // initialize pass phrase
            final InitialPassPhraseDialog initialPassPhraseDialog = new InitialPassPhraseDialog(
                    null, true, idropCore);
            Toolkit tk = idrop.getToolkit();
            int x = (tk.getScreenSize().width - initialPassPhraseDialog
                    .getWidth()) / 2;
            int y = (tk.getScreenSize().height - initialPassPhraseDialog
                    .getHeight()) / 2;
            initialPassPhraseDialog.setLocation(x, y);
            idropSplashWindow.toBack();
            initialPassPhraseDialog.toFront();
            initialPassPhraseDialog.setVisible(true);
            validated = initialPassPhraseDialog.isValidated();
            log.info("pass phrase dialog processed...validated? {}", validated);
        }

        if (!validated) {
            log.info("not validated...exit");
            System.exit(0);
        }
        final GridMemoryDialog gridMemoryDialog = new GridMemoryDialog(
                null, true, idropCore, null);
        Toolkit tk = idrop.getToolkit();
        int x = (tk.getScreenSize().width - gridMemoryDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - gridMemoryDialog.getHeight()) / 2;
        gridMemoryDialog.setLocation(x, y);
        gridMemoryDialog.toFront();
        gridMemoryDialog.setVisible(true);
        return validated;

    }

    private boolean processTearOffMode() throws IdropException {
        LoginDialog loginDialog = new LoginDialog(null, idropCore);
        loginDialog.setVisible(true);
        return loginDialog.isValidated();
    }

    private IdropConfigurationService startUpTheDatabaseAndSetConfigurationServiceInIdropCore(
            final String derivedConfigHomeDirectory) throws IdropException {
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

    private void doAnyPreDatabaseLoadMigrationProcessing(
            final String derivedConfigHomeDirectory)
            throws IdropRuntimeException {
        Properties propertiesLoadedFromIdropApplicationClasspath;
        try {

            IdropPropertiesHelper helper = new IdropPropertiesHelper();
            propertiesLoadedFromIdropApplicationClasspath = helper
                    .loadIdropProperties();
            String currentVersion = propertiesLoadedFromIdropApplicationClasspath
                    .getProperty(IdropConfigurationService.VERSION_NUMBER);

            if (currentVersion == null || currentVersion.isEmpty()) {
                throw new IdropRuntimeException(
                        "unknown version number, not present in idrop.config");
            }

            IdropPreDatabaseBootstrapperService idropPreBootstrapperService = new IdropPreDatabaseBootstrapperServiceImpl();
            String cachedVersion = idropPreBootstrapperService
                    .detectPriorVersion(derivedConfigHomeDirectory);
            idropPreBootstrapperService.triggerMigrations(
                    derivedConfigHomeDirectory, cachedVersion, currentVersion);
            idropPreBootstrapperService.storePriorVersion(
                    derivedConfigHomeDirectory, currentVersion);

        } catch (IdropException ex) {
            Logger.getLogger(IDROPDesktop.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new IdropRuntimeException(ex);
        }
    }

    private String deriveConfigHomeDirectory() {
        log.info("determine config root directory");
        String userHomeDirectory = System.getProperty("user.home");
        userHomeDirectory = userHomeDirectory.replaceAll("\\\\", "/");
        StringBuilder sb = new StringBuilder();
        sb.append(userHomeDirectory);
        sb.append("/.idrop");
        String derivedConfigHomeDirectory = sb.toString();
        log.info("set config home directory as: {}", derivedConfigHomeDirectory);
        return derivedConfigHomeDirectory;
    }

    /**
     * Start up iDrop as a system tray application. This is the main entry point
     * for iDrop
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) throws InterruptedException {
        IDROPDesktop startupSequencer = new IDROPDesktop();
        try {
            startupSequencer.doStartupSequence();
        } catch (Exception e) {
            log.error("unable to start application due to error", e);
            System.exit(1);
        } finally {
            startupSequencer.idropCore.closeAllIRODSConnections();
        }
    }
}
