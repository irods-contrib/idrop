package org.irods.jargon.idrop.desktop.systraygui.services;

import java.awt.Component;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.util.MessageUtil;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.engine.TransferManager;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class QueueSchedulerTimerTask extends TimerTask {

    private final TransferManager transferManager;

    public final static long EVERY_10_MINUTES = 1000 * 60 * 10;

    public final static long EVERY_30_SECONDS = 1000 * 30;

    private final org.slf4j.Logger log = LoggerFactory.getLogger(QueueSchedulerTimerTask.class);
    
    private Component parent;

    public QueueSchedulerTimerTask(TransferManager transferManager, Component parent) throws IdropException {
        super();

        if (transferManager == null) {
            throw new IdropException("null transfer manager");
        }

        this.transferManager = transferManager;
        this.parent = parent;
    }

    @Override
    public void run() {
        log.info("timer task running");

        if (transferManager.isPaused()) {
            log.info("timer is paused");
            return;
        }

        try {
            log.info("***** timer queue asking transfer manager to process next");
            transferManager.processNextInQueueIfIdle();
        } catch (JargonException ex) {
            Logger.getLogger(QueueSchedulerTimerTask.class.getName()).log(Level.SEVERE, null, ex);
            MessageUtil.showError(parent, ex.getMessage(), "Error starting queue");
            return;
        }

    }
}
