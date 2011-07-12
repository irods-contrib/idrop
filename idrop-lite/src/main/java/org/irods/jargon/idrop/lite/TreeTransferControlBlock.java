package org.irods.jargon.idrop.lite;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class TreeTransferControlBlock implements TransferControlBlock {

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean filter(String absolutePath) throws JargonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaximumErrorsBeforeCanceling() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaximumErrorsBeforeCanceling(
			int maximumErrorsBeforeCancelling) throws JargonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getErrorCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean shouldTransferBeAbandonedDueToNumberOfErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reportErrorInTransfer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalFilesToTransfer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTotalFilesToTransfer(int totalFilesToTransfer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalFilesTransferredSoFar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int incrementFilesTransferredSoFar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalBytesTransferredSoFar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void incrementTotalBytesTransferredSoFar(
			long totalBytesTransferredSoFar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTotalBytesToTransfer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTotalBytesToTransfer(long totalBytesToTransfer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransferOptions(TransferOptions transferOptions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TransferOptions getTransferOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
