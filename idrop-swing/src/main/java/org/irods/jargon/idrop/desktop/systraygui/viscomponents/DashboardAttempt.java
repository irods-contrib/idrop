/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import org.irods.jargon.transfer.dao.domain.TransferAttempt;

/**
 * 
 * @author Mike
 */
public class DashboardAttempt {

	private TransferAttempt transferAttempt;

	private float percentWidth = 0;
	private float percentTotalHeight = 0;
	private float percentHeightSkipped = 0;
	private float percentHeightTransferred = 0;
	private float percentHeightError = 0;

	DashboardAttempt() {
	}

	public float getPercentWidth() {
		return percentWidth;
	}

	public void setPercentWidth(final float percentWidth) {
		this.percentWidth = percentWidth;
	}

	public float getPercentTotalHeight() {
		return percentTotalHeight;
	}

	public void setPercentTotalHeight(final float percentTotalHeight) {
		this.percentTotalHeight = percentTotalHeight;
	}

	public float getPercentHeightSkipped() {
		return percentHeightSkipped;
	}

	public void setPercentHeightSkipped(final float percentHeightSkipped) {
		this.percentHeightSkipped = percentHeightSkipped;
	}

	public float getPercentHeightTransferred() {
		return percentHeightTransferred;
	}

	public void setPercentHeightTransferred(final float percentHeightTransferred) {
		this.percentHeightTransferred = percentHeightTransferred;
	}

	public float getPercentHeightError() {
		return percentHeightError;
	}

	public void setPercentHeightError(final float percentHeightError) {
		this.percentHeightError = percentHeightError;
	}

	public DashboardAttempt(final TransferAttempt transferAttempt) {
		this.transferAttempt = transferAttempt;
	}

	public TransferAttempt getTransferAttempt() {
		return transferAttempt;
	}

	public void setTransferAttempt(final TransferAttempt transferAttempt) {
		this.transferAttempt = transferAttempt;
	}

}
