/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.services.QueueSchedulerTimerTask;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.engine.TransferManager;
import org.irods.jargon.transfer.engine.TransferManagerImpl;
import org.slf4j.LoggerFactory;

/**
 * Bootstrapping class for iDrop, load config, create necessary services, and start the appropriate GUI components
 * @author Mike Conway - DICE (www.irods.org)
 */
public class StartupSequencer {

    private iDrop idrop;
    private IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(StartupSequencer.class);

    public void doStartupSequence() {

        log.info("initiating startup sequence...");
        int count = 0;
        log.info("creating idropCore...");
        idropCore = new IDROPCore();

        try {
            idropCore.setIrodsFileSystem(IRODSFileSystem.instance());
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, ex);
        }

        log.info("creating idrop GUI app...");
        idrop = new iDrop(idropCore);

        IDropSplashWindow idropSplashWindow = new IDropSplashWindow(idrop);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus("Initializing...", ++count);


        idropCore.setIconManager(new IconManager(idrop));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus("Checking configuration...", ++count);

        // needs redo, config created via database
        try {
            IdropConfig config = new IdropConfig();
            config.setUpLogging();
            idropCore.setIdropConfig(config);
        } catch (IdropException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error creating idropConfig", ex);
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus("Building transfer engine...", ++count);

        log.info("building transfer manager...");
       
        try {
            idropCore.setTransferManager(new TransferManagerImpl(idropCore.getIrodsFileSystem(), idrop, idropCore.getIdropConfig().isLogSuccessfulTransfers()));
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error creating transferManager", ex);
        }

        log.info("logging in in splash background thread");
        idropSplashWindow.setStatus("Logging in...", ++count);

        final LoginDialog loginDialog = new LoginDialog(idrop);
        Toolkit tk = idrop.getToolkit();
        int x = (tk.getScreenSize().width - loginDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - loginDialog.getHeight()) / 2;
        loginDialog.setLocation(x, y);
        idropSplashWindow.toBack();
        loginDialog.setAlwaysOnTop(true);
        loginDialog.toFront();
        loginDialog.setVisible(true);

        try {
            List<LocalIRODSTransfer> currentQueue = idropCore.getTransferManager().getCurrentQueue();

            if (!currentQueue.isEmpty()) {
                int result = JOptionPane.showConfirmDialog((Component) null, "Transfers are waiting to process, restart transfer?",
                        "iDrop Transfers in Progress", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION) {
                    idropCore.getTransferManager().pause();
                } 
            }
        } catch (JargonException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error evaluating current queue", ex);
        }
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }

        idropSplashWindow.setStatus("Starting work queue...", ++count);
        try {
            QueueSchedulerTimerTask queueSchedulerTimerTask = new QueueSchedulerTimerTask(idropCore.getTransferManager(), idrop);
             Timer timer = new Timer();
             timer.scheduleAtFixedRate(queueSchedulerTimerTask, 10000, 120000);
                     idropCore.setQueueTimer(timer);
        } catch (IdropException ex) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        log.info("signal that the startup sequence is complete");
        try {
            idrop.signalIdropCoreReadyAndSplashComplete();
            idropSplashWindow.setVisible(false);
            idropSplashWindow = null;
        } catch (Exception e) {
            Logger.getLogger(StartupSequencer.class.getName()).log(Level.SEVERE, null, e);

            throw new IdropRuntimeException("error starting idrop gui", e);
        }

    }

    /**
     * Start up iDrop as a system tray application. This is the main entry point for iDrop
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) throws InterruptedException {
        StartupSequencer startupSequencer = new StartupSequencer();
        try {
        startupSequencer.doStartupSequence();
        } catch (Exception e) {
            log.error("unable to start application due to error", e);
            System.exit(1);
        }
    }
}
