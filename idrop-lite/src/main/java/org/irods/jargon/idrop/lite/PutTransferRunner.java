package org.irods.jargon.idrop.lite;

import java.io.File;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.slf4j.LoggerFactory;

public class PutTransferRunner implements Runnable {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTreeTransferHandler.class);
    private final List<File> sourceFiles;
    private final String targetIrodsFileAbsolutePath;
    private final iDropLiteApplet idropGui;
    private TransferControlBlock transferControlBlock;

    public PutTransferRunner(final iDropLiteApplet gui,
            final String targetPath,
            final List<File> files)
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

        this.targetIrodsFileAbsolutePath = targetPath;
        this.sourceFiles = files;
        this.idropGui = gui;

    }

    @Override
    public void run() {
        for (File transferFile : sourceFiles) {
            log.info("process a put from source: {}", transferFile.getAbsolutePath());
            
            // handle overall cancel if requested by client
            if(idropGui.isTransferCancelled()) {
            	log.info("put transfer cancelled by client");
            	idropGui.setTransferCancelled(false);
            	break;
            }
            
            String localSourceAbsolutePath = transferFile.getAbsolutePath();
            String sourceResource = idropGui.getIrodsAccount().getDefaultStorageResource();
            
            // need to create new Transfer Control Block for each transfer since it needs to be reset
            // on how many files there are to transfer and how many have been transferred so far
            TransferControlBlock tcb = null;;
            try {
            	tcb = DefaultTransferControlBlock.instance();
                idropGui.getiDropCore().setTransferControlBlock(tcb);
                this.transferControlBlock = tcb;
            } catch (JargonException ex) {
            	java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                        java.util.logging.Level.SEVERE, null, ex);
                idropGui.showIdropException(ex);
            }
            log.info("initiating put transfer");
            try {
                idropGui.getiDropCore().getTransferManager().putOperation(localSourceAbsolutePath,
                        targetIrodsFileAbsolutePath, sourceResource, idropGui, transferControlBlock);
            } catch (JargonException ex) {
                java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                        java.util.logging.Level.SEVERE, null, ex);
                idropGui.showIdropException(ex);
            } finally {
                idropGui.getiDropCore().getIrodsFileSystem().closeAndEatExceptions();
            }
        }
    }
}
