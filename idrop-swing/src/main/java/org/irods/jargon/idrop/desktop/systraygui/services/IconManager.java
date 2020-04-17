package org.irods.jargon.idrop.desktop.systraygui.services;

import org.irods.jargon.conveyor.core.ConveyorExecutorService.ErrorStatus;
import org.irods.jargon.conveyor.core.ConveyorExecutorService.RunningStatus;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;

/**
 * Manager of icons for the system gui based on the status.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IconManager {

	private ErrorStatus errorStatus = null;
	private RunningStatus runningStatus = null;
	private final iDrop idropGui;

	public IconManager(final iDrop idropClient) {
		idropGui = idropClient;
	}

	public synchronized void setErrorStatus(final ErrorStatus errorStatus) {
		this.errorStatus = errorStatus;
		updateIcon();
	}

	public synchronized void setRunningStatus(final RunningStatus runningStatus) {
		this.runningStatus = runningStatus;
		updateIcon();
	}

	private void updateIcon() {
		String iconFile = "";
		if (runningStatus == RunningStatus.PAUSED) {
			iconFile = "images/media-playback-pause-3.png";
		} else if (errorStatus == ErrorStatus.ERROR) {
			iconFile = "images/dialog-error-3.png";
		} else if (errorStatus == ErrorStatus.WARNING) {
			iconFile = "images/dialog-warning.png";
		} else if (runningStatus == RunningStatus.IDLE) {
			iconFile = "images/dialog-ok-2.png";
		} else if (runningStatus == RunningStatus.BUSY
				|| runningStatus == RunningStatus.PAUSED_BUSY) {
			iconFile = "images/system-run-5.png";
		} else {
			iconFile = "images/dialog-ok-2.png";
		}
		idropGui.updateIcon(iconFile);
	}

}
