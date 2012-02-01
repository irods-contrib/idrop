package org.irods.jargon.idrop.lite;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.slf4j.LoggerFactory;

public class PutTransferRunner implements Runnable {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTreeTransferHandler.class);
//    private final List<File> sourceFiles = null;
    private final List<UploadDataObj> sourceFiles;
    private final String targetIrodsFileAbsolutePath;
    private final iDropLiteApplet idropGui;
    private TransferControlBlock transferControlBlock;

    public PutTransferRunner(final iDropLiteApplet gui,
            final String targetPath,
            final List<UploadDataObj> files)
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
        //for (File transferFile : sourceFiles) {
        for (UploadDataObj uploadData : sourceFiles) {
            // handle overall cancel if requested by client
            if (idropGui.isTransferCancelled()) {
                log.info("put transfer cancelled by client");
                idropGui.setTransferCancelled(false);
                break;
            }
            if (uploadData.isURL) {
                processPutURL(uploadData);
            } else {
                processPutFile(uploadData);
            }
        }
    }

    private void processPutFile(UploadDataObj uploadData) {
        // this is just a regular local file or folder

        log.info("process a put from source: {}", uploadData.getFile().getAbsolutePath());
        //String localSourceAbsolutePath = transferFile.getAbsolutePath();
        String localSourceAbsolutePath = uploadData.getFile().getAbsolutePath();
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

    private void processPutURL(UploadDataObj uploadData) {
        // this is an import from URL

        log.info("process a put from an url: {}", uploadData.getFileName());
        String localSourceAbsolutePath = uploadData.getFileName();
        String sourceResource = idropGui.getIrodsAccount().getDefaultStorageResource();

// need to create new Transfer Control Block for each transfer since it needs to be reset
// on how many files there are to transfer and how many have been transferred so far
       
        log.info("initiating put transfer");
        try {
           this.transferControlBlock =  idropGui.getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().buildDefaultTransferControlBlockBasedOnJargonProperties();
           transferControlBlock.getTransferOptions().setIntraFileStatusCallbacks(true); 
           idropGui.getiDropCore().setTransferControlBlock(transferControlBlock);
            idropGui.getiDropCore().getTransferManager().putOperationURL(localSourceAbsolutePath,
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
