package org.irods.jargon.idrop.desktop.systraygui.services;

import org.irods.jargon.conveyor.core.QueueStatus;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.transfer.engine.TransferManager;

/**
 * Manager of icons for the system gui based on the status.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IconManager {

    private QueueStatus.ErrorStatus errorStatus = null;
    private QueueStatus.RunningStatus runningStatus = null;
    private final iDrop idropGui;

    public IconManager(final iDrop idropClient) {
        this.idropGui = idropClient;
    }

    public synchronized void setErrorStatus(
            final QueueStatus.ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
        updateIcon();
    }

    public synchronized void setRunningStatus(
            final QueueStatus.RunningStatus runningStatus) {
        this.runningStatus = runningStatus;
        updateIcon();
    }

    private void updateIcon() {
        String iconFile = "";
        if (runningStatus == QueueStatus.RunningStatus.PAUSED) {
            iconFile = "images/media-playback-pause-3.png";
        } else if (errorStatus == QueueStatus.ErrorStatus.ERROR) {
            iconFile = "images/dialog-error-3.png";
        } else if (errorStatus == QueueStatus.ErrorStatus.WARNING) {
            iconFile = "images/dialog-warning.png";
        } else if (runningStatus == QueueStatus.RunningStatus.IDLE) {
            iconFile = "images/dialog-ok-2.png";
        } else if (runningStatus == QueueStatus.RunningStatus.RUNNING) {
            iconFile = "images/system-run-5.png";
        } else {
            iconFile = "images/dialog-ok-2.png";
        }
        idropGui.updateIcon(iconFile);
    }
}
