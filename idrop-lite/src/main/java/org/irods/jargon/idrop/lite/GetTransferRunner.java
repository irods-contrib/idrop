package org.irods.jargon.idrop.lite;

import java.io.File;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.slf4j.LoggerFactory;

public class GetTransferRunner implements Runnable {
	
	public static org.slf4j.Logger log = LoggerFactory.getLogger(GetTransferRunner.class);
	private final List<File> sourceFiles;
	private final String targetIrodsFileAbsolutePath;
	private final iDropLiteApplet idropGui;
	//private final TransferControlBlock transferControlBlock;
	private TransferControlBlock transferControlBlock;
	
	public GetTransferRunner(final iDropLiteApplet gui,
			final String targetPath,
			final List<File> files)
			//final TransferControlBlock transferControlBlock)
			throws JargonException {

		if (files == null) {
			throw new JargonException("null file list");
		}
		
		if (targetPath == null) {
			throw new JargonException("null target path");
		}

		if (gui == null) {
			throw new JargonException("null idrop gui");
		}

//		if (transferControlBlock == null) {
//			throw new JargonException("null transferControlBlock");
//		}

		this.targetIrodsFileAbsolutePath = targetPath;
		this.sourceFiles = files;
		this.idropGui = gui;
		//this.transferControlBlock = transferControlBlock;

	}

	@Override
	public void run() {
           
		for (File transferFile : sourceFiles) {

			// need to create new Transfer Control Block for each transfer since it needs to be reset
            // on how many files there are to transfer and how many have been transferred so far
			TransferControlBlock tcb = null;
            try {
            	tcb = DefaultTransferControlBlock.instance();
                idropGui.getiDropCore().setTransferControlBlock(tcb);
                this.transferControlBlock = tcb;
            } catch (JargonException ex) {
            	java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                        java.util.logging.Level.SEVERE, null, ex);
                idropGui.showIdropException(ex);
            }
			if (transferFile instanceof IRODSFile) {
				log.info("initiating a transfer of iRODS file:{}", transferFile.getAbsolutePath());
                log.info("transfer to local file:{}", targetIrodsFileAbsolutePath);
                try {
                	DataTransferOperations dto = idropGui.getiDropCore().getIRODSAccessObjectFactory().getDataTransferOperations(
                			idropGui.getIrodsAccount());
                	dto.getOperation(transferFile.getAbsolutePath(), targetIrodsFileAbsolutePath, idropGui.getIrodsAccount().getDefaultStorageResource(),
                			idropGui, this.transferControlBlock);
                } catch (JargonException ex) {
                    java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                            java.util.logging.Level.SEVERE, null, ex);
                    idropGui.showIdropException(ex);
                } finally {
					idropGui.getiDropCore().getIrodsFileSystem().closeAndEatExceptions();
				}
			} else {
                    log.info("process a local to local move with source...not yet implemented : {}",
                            transferFile.getAbsolutePath());
            }
		}
		
	}

}
