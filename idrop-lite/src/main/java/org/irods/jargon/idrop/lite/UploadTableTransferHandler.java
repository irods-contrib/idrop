package org.irods.jargon.idrop.lite;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

public class UploadTableTransferHandler extends TransferHandler {
	
	iDropLiteApplet idropApplet;
	
	public static org.slf4j.Logger log = LoggerFactory.getLogger(LocalTreeTransferHandler.class);

     @Override
     public boolean canImport(TransferHandler.TransferSupport support) {
        
        JTable target = (JTable) support.getComponent();
        
        if(support.isDrop()) {
        	log.warn("transferFlavors:{}", support.getDataFlavors());

        	for (DataFlavor flavor : support.getDataFlavors()) {
        		if (flavor.equals(DataFlavor.javaFileListFlavor)) {
        			log.debug("found file list flavor, will import");
        			target.setCursor(DragSource.DefaultMoveDrop);
                    return true;
                } else if (flavor.getMimeType().equals(
                		"application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                	log.debug("found file list flavor, will import");
                	target.setCursor(DragSource.DefaultMoveDrop);
                	return true;
                 }
             }
        }
        
        log.debug("cannot import");
        target.setCursor(DragSource.DefaultMoveNoDrop);
        return false;
     }

     @Override
     public boolean importData(TransferHandler.TransferSupport support) {
    	 
    	 // if we can't handle the import, say so
    	 if (!canImport(support)) {
    		 return false;
    	 }

    	 // fetch the drop location
    	 JTable target = (JTable) support.getComponent();
    	 JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();

    	 Transferable transferable = support.getTransferable();

    	 DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();

    	 boolean accepted = false;

    	 for (DataFlavor flavor : transferrableFlavors) {
    		 log.debug("flavor mime type:{}", flavor.getMimeType());
    		 log.debug("flavor human presentable name:{}", flavor.getHumanPresentableName());
    		 if (flavor.isFlavorJavaFileListType()) {
    			 log.info("drop accepted...process drop as file list from desktop");
    			 // dtde.acceptDrop(dtde.getDropAction());
    			 processDropOfFileList(transferable, target);
    			 accepted = true;
    			 break;
    		 } else if (flavor.getMimeType().equals(
                    "application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
    			 log.info("drop accepted: process drop as serialized object");
    			 // dtde.acceptDrop(dtde.getDropAction());
    			 processDropOfTreeSelectionModel(transferable, target, flavor);
    			 accepted = true;
    			 break;
    		 } else {
    			 log.debug("flavor not processed: {}", flavor);
    		 }
    	 }
    	 
    	 if (!accepted) {
    		 log.info("drop rejected");
    		 accepted = false;
    	 }

    	 return accepted;
     }
     
     private void processDropOfTreeSelectionModel(final Transferable transferable, final JTable table,
             final DataFlavor dataFlavor) {
         final List<File> sourceFiles = new ArrayList<File>();

         if(idropApplet.isTransferInProgress()) {
        	 // do nothing??
        	 // JOptionPane.showMessageDialog(idropApplet, "Cannot Copy Files for Upload - Transfer Currently in Progress", "Transfer In Progress", JOptionPane.OK_OPTION);
         }
         else {

         try {
             // get the list of files
             TreeSelectionModel transferableSelectionModel = (TreeSelectionModel) transferable
                     .getTransferData(dataFlavor);
             TreePath[] treePaths = transferableSelectionModel.getSelectionPaths();

             for (TreePath treePath : treePaths) {
                 LocalFileNode lastPathComponent = (LocalFileNode) treePath.getLastPathComponent();
                 sourceFiles.add((File) lastPathComponent.getUserObject());
             }

         } catch (UnsupportedFlavorException ex) {
             Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
             throw new IdropRuntimeException("unsupported flavor getting data from transfer");
         } catch (IOException ex) {
             Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
             throw new IdropRuntimeException("io exception getting data from transfer");
         }

         if (sourceFiles.isEmpty()) {
             log.error("no source files in transfer");
             throw new IdropRuntimeException("no source files in transfer");
         }
         
         DefaultTableModel tm = (DefaultTableModel)table.getModel();
         
         for (File transferFile : sourceFiles) {
             log.info("put file in upload table: {}", transferFile.getAbsolutePath());

             String localSourceAbsolutePath = transferFile.getAbsolutePath();
             Object [] rowData = new Object[2];
             rowData[0] = localSourceAbsolutePath;
             rowData[1] = Boolean.TRUE;
             tm.addRow(rowData);
             if(idropApplet != null) {
            	 idropApplet.updateFileStats(tm);
             }
          }
         }
     }
     
     private void processDropOfFileList(Transferable transferable, JTable table) throws IdropRuntimeException {

         log.info("process as drop of file list");

         final List<File> sourceFiles;

         try {
             // get the list of files
             sourceFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
         } catch (UnsupportedFlavorException ex) {
             Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
             throw new IdropRuntimeException("unsupported flavor getting data from transfer");
         } catch (IOException ex) {
             Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
             throw new IdropRuntimeException("io exception getting data from transfer");
         }

         if (sourceFiles.isEmpty()) {
             log.error("no source files in transfer");
             throw new IdropRuntimeException("no source files in transfer");
         }

         DefaultTableModel tm = (DefaultTableModel)table.getModel();
         
         for (File transferFile : sourceFiles) {
        	 String localSourceAbsolutePath = transferFile.getAbsolutePath();
        	 log.info("put file in upload table: {}", localSourceAbsolutePath);

             Object [] rowData = new Object[2];
             rowData[0] = localSourceAbsolutePath;
             rowData[1] = Boolean.TRUE;
             tm.addRow(rowData);
             if(idropApplet != null) {
            	 idropApplet.updateFileStats(tm);
             }
         }

     } 
     
     public void setGUI(iDropLiteApplet gui) {
    	 idropApplet = gui;
     }

}
