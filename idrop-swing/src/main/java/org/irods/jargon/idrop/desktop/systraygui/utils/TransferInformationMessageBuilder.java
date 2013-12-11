/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.utils;

import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.irods.jargon.transfer.dao.domain.TransferStateEnum;
import org.irods.jargon.transfer.dao.domain.TransferStatusEnum;
import org.irods.jargon.transfer.dao.domain.TransferType;

/**
 * Build textual descriptions of a transfer
 * 
 * @author Mike Conway
 */
public class TransferInformationMessageBuilder {

	public enum AttemptType {

		SKIPPED, TRANSFERRED, ERROR
	}

	public static String buildTransferAttemptSummary(
			final TransferAttempt transferAttempt, final AttemptType attemptType) {
		if (transferAttempt == null) {
			throw new IllegalArgumentException("null transferAttempt");
		}

		if (attemptType == null) {
			throw new IllegalArgumentException("null attamptType");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<html><p>This transfer attempt started ");
		sb.append(transferAttempt.getAttemptStart());

		if (transferAttempt.getAttemptEnd() != null) {
			sb.append(" and ended ");
			sb.append(transferAttempt.getAttemptEnd());
		}

		sb.append("</p><p> This attempt involved ");
		sb.append(transferAttempt.getTotalFilesCount());
		sb.append(" files. ");

		if (attemptType == AttemptType.ERROR) {

			if (transferAttempt.getErrorMessage() == null
					|| transferAttempt.getErrorMessage().isEmpty()) {

				sb.append(" and in this attempt there were ");
				sb.append(transferAttempt.getTotalFilesErrorSoFar());
				sb.append(" errors");
			} else {
				sb.append(" in this transfer, an error occurred:");
				sb.append(transferAttempt.getErrorMessage());
			}
		} else if (attemptType == AttemptType.SKIPPED) {
			sb.append(" and in this attempt there was a restart that skipped ");
			sb.append(transferAttempt.getTotalFilesSkippedSoFar());
		} else {
			sb.append(" and in this attempt there were ");
			sb.append(transferAttempt.getTotalFilesTransferredSoFar()
					- transferAttempt.getTotalFilesSkippedSoFar());
			sb.append(" files actually transferred");
		}

		sb.append(".</p></html>");

		return sb.toString();
	}

	public static String buildTransferSummary(final Transfer transfer) {

		if (transfer == null) {
			throw new IllegalArgumentException("null transfer");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<html><p>This is a <b>");
		sb.append(transfer.getTransferType());
		sb.append("</b> This transfer ");

		if (transfer.getTransferState() == TransferStateEnum.CANCELLED) {
			sb.append("was cancelled by the user.");
		} else if (transfer.getTransferState() == TransferStateEnum.COMPLETE) {

			if (transfer.getLastTransferStatus() == TransferStatusEnum.ERROR) {
				sb.append("completed with errors.");
			} else if (transfer.getLastTransferStatus() == TransferStatusEnum.WARNING) {
				sb.append("completed successfully with a few warnings.  Check the file details <br/> for individual files that may have had an error.");
			} else {
				sb.append("successfully completed.");
			}
		} else if (transfer.getTransferState() == TransferStateEnum.ENQUEUED) {
			sb.append("is enqueued and waiting to process.");
		} else if (transfer.getTransferState() == TransferStateEnum.PROCESSING) {
			sb.append("is currently being processed.");
		}

		sb.append("</p>");

		if (transfer.getTransferType() == TransferType.PUT) {
			sb.append("<p>The source of the transfer is:");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getLocalAbsolutePath()));
			sb.append("<br/>The target is:");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getIrodsAbsolutePath()));
		} else if (transfer.getTransferType() == TransferType.GET) {
			sb.append("<p>The source of the transfer is:");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getIrodsAbsolutePath()));
			sb.append("<br/>The target is:");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getLocalAbsolutePath()));
		} else if (transfer.getTransferType() == TransferType.COPY) {
			sb.append("<p>The iRODS file or collection::");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getLocalAbsolutePath()));
			sb.append("<br/>Is being copied to:");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getIrodsAbsolutePath()));
		} else if (transfer.getTransferType() == TransferType.REPLICATE) {
			sb.append("<p>The iRODS file or collection::");
			sb.append(MiscIRODSUtils.abbreviateFileName(transfer
					.getIrodsAbsolutePath()));
			sb.append("<br/>Is being replicated to:");
			sb.append(transfer.getResourceName());
		}

		sb.append("</p></html>");
		return sb.toString();
	}
}
