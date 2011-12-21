package org.irods.jargon.idrop.lite;

public class TransferProgressInfo {
	Boolean isIntraFile = false;
	int percentDone = 0;
	int totalFilesToTransfer = 0;
	int totalFilesTransferredSoFar = 0;
	int displayMode = -1;
	
	public TransferProgressInfo() {
	}
	
	public TransferProgressInfo(int mode) {
		this.displayMode = mode;
	}
	
	public TransferProgressInfo(long fileSize, long fileSoFar, int toTransfer, int soFar) {
		this.percentDone = this.calcPercentDone(fileSoFar, fileSize);
		this.totalFilesToTransfer = toTransfer;
		this.totalFilesTransferredSoFar = soFar;
	}
	
	public TransferProgressInfo(long fileSize, long fileSoFar, int toTransfer, int soFar, Boolean isIntra) {
		this.percentDone = this.calcPercentDone(fileSoFar, fileSize);
		this.totalFilesToTransfer = toTransfer;
		this.totalFilesTransferredSoFar = soFar;
		this.isIntraFile = isIntra;
	}
	
	public int calcPercentDone(long bytesTransfered, long totalSize) {
		
		float percent = 0;
	
		float bt = bytesTransfered * 100;
		float tot = totalSize;
		if(tot != 0) {
			percent = bt / tot;
		}
		
		return (int)percent;
	}
	
	public int getPercentDone() {
		return this.percentDone;
	}
	
	public void setPercentDone(int percent) {
		this.percentDone = percent;
	}
	
	public int getTotalFilesToTransfer() {
		return this.totalFilesToTransfer;
	}
	
	public void setTotalFilesToTransfer(int toTransfer) {
		this.totalFilesToTransfer = toTransfer;
	}
	
	public int getTotalFilesTransferredSoFar() {
		return this.totalFilesTransferredSoFar;
	}
	
	public void setTotalFilesTransferredSoFar(int soFar) {
		this.totalFilesTransferredSoFar = soFar;
	}
}
