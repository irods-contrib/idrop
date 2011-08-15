package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationServiceImpl;
import org.irods.jargon.idrop.desktop.systraygui.services.QueueSchedulerTimerTask;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.utils.LookAndFeelManager;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.SetupWizard;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.engine.TransferEngineConfigurationProperties;
import org.irods.jargon.transfer.engine.TransferManagerImpl;
import org.irods.jargon.transfer.synch.SynchPeriodicScheduler;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 * Bootstrapping class for iDrop, load config, create necessary services, and
 * start the appropriate GUI components
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class StartupSequencer {

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

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus("Initializing...", ++count);

        idropCore.setIconManager(new IconManager(idrop));

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        log.info("determine config root directory");
        String userHomeDirectory = System.getProperty("user.home");
        StringBuilder sb = new StringBuilder();
        sb.append(userHomeDirectory);
        sb.append("/.idrop");
        String derivedConfigHomeDirectory = sb.toString();
        log.info("set config home directory as: {}", derivedConfigHomeDirectory);

        /*
         * Here is where I first try and start the database to get the
         * configuration. A database error indicates that iDrop is already
         * running
         */

        idropSplashWindow.setStatus("Looking for configuration information...",
                ++count);

        Properties derivedProperties = null;
        try {
            IdropConfigurationService idropConfigurationService = new IdropConfigurationServiceImpl(
                    derivedConfigHomeDirectory, idropCore);
            idropCore.setIdropConfigurationService(idropConfigurationService);
            derivedProperties = idropConfigurationService.bootstrapConfiguration();

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

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus(
                "Configuration information gathered, logging in...", ++count);

        log.info("config properties derived...");
        idropCore.setIdropConfig(new IdropConfig(derivedProperties));
        idropCore.getIdropConfig().setUpLogging();

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

        //loginDialog.setAlwaysOnTop(true);


        idropSplashWindow.toBack();
        loginDialog.toFront();
        loginDialog.setVisible(true);

        if (idropCore.getIrodsAccount() == null) {
            log.warn("no login account, exiting");
            System.exit(0);
        }

        idropSplashWindow.toFront();

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

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

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
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

        log.info("logged in, now checking for first run...");

        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus(
                "Checking if this is the first time run to set up synch...",
                ++count);

        String synchDeviceName = idropCore.getIdropConfig().getSynchDeviceName();
        idrop.signalIdropCoreReadyAndSplashComplete();
        if (synchDeviceName == null && idropCore.getIdropConfig().isShowStartupWizard()) {
            log.info("first time running idrop, starting configuration wizard");
            log.info("showing gui first time run");
            doFirstTimeConfigurationWizard();
            idrop.showIdropGui();
        } else {
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

    /**
     * Start up iDrop as a system tray application. This is the main entry point
     * for iDrop
     * 
     * @param args
     *            the command line arguments
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

    private void doFirstTimeConfigurationWizard() {
        log.info("doFirstTimeConfigurationWizard()..do I show");
        // there is a force.no.synch property in idrop.properties that prevents
        // synch from coming up if in the build that way

        if (idropCore.getIdropConfig().isShowStartupWizard()) {
            log.info("doing setup wizard");
            // idrop will be visible, don't z fight and declutter for wizard...s
            idrop.setVisible(false);
            SetupWizard setupWizard = new SetupWizard(idrop, true);
            setupWizard.toFront();
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = (screenSize.width - setupWizard.getWidth()) / 2;
            final int y = (screenSize.height - setupWizard.getHeight()) / 2;
            setupWizard.setLocation(x, y);

            setupWizard.setVisible(true);
            idrop.setVisible(true);
        }
    }
}
