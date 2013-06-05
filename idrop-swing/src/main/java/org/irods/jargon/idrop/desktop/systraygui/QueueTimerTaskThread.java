package org.irods.jargon.idrop.desktop.systraygui;

import org.irods.jargon.idrop.desktop.systraygui.services.QueueSchedulerTimerTask;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.slf4j.LoggerFactory;

public class QueueTimerTaskThread extends Thread {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(iDrop.class);
    private iDrop iDrop;

    
    public QueueTimerTaskThread(final iDrop iDrop) {
        super();
        this.iDrop = iDrop;
    }

    @Override
    public void run() {

        /*
         * A timer task monitors the queue, and can be extended to process
         * things like retrys and file synchronization
         */
        logger.info("creating timer for queue manager");

        // FIXME: conveyor
        /*
        try {
            QueueSchedulerTimerTask queueTimerTask = new QueueSchedulerTimerTask(
                    iDrop.getiDropCore().getTransferManager(), iDrop);
            iDrop.getiDropCore().getQueueTimer().scheduleAtFixedRate(queueTimerTask, 1000,
                    QueueSchedulerTimerTask.EVERY_10_MINUTES);
        } catch (IdropException ex) {
            logger.error(ex.getMessage());
            System.exit(1);
        }
        * */

    }

}
