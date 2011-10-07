package org.irods.jargon.idrop.commons;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;import org.irods.jargon.transfer.engine.TransferManager;
;
import org.slf4j.LoggerFactory;

/**
 * A transfer handler for the local file tree
 * @author Mike Conway - DICE (www.irods.org)
 */
public abstract class LocalTreeTransferHandler extends TransferHandler {
    
     public static org.slf4j.Logger log = LoggerFactory.getLogger(LocalTreeTransferHandler.class);
    public final IdropClient idropGui;

    @Override
    public boolean canImport(final TransferSupport support) {
        Point location = support.getDropLocation().getDropPoint();
        LocalFileTree tree = (LocalFileTree) support.getComponent();

        int closestRow = idropGui.getFileTree().getClosestRowForLocation((int) location.getX(), (int) location.getY());
        boolean highlighted = false;

        Graphics g = tree.getGraphics();

        // row changed

        if (tree.getHighlightedRow() != closestRow) {
            if (null != tree.getDirtyRegion()) {
                tree.paintImmediately(tree.getDirtyRegion());
            }

            for (int j = 0; j < tree.getRowCount(); j++) {
                if (closestRow == j) {

                    Rectangle firstRowRect = tree.getRowBounds(closestRow);
                    tree.setDirtyRegion(firstRowRect);
                    g.setColor(tree.getHighlightColor());

                    g.fillRect((int) tree.getDirtyRegion().getX(), (int) tree.getDirtyRegion().getY(), (int) tree.getDirtyRegion().getWidth(), (int) tree.getDirtyRegion().getHeight());
                    tree.setHighlightedRow(closestRow);
                }
            }

        }

        log.warn("transferFlavors:{}", support.getDataFlavors());

        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                log.debug("found file list flavor, will import");
                return true;
            } else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.debug("found file list flavor, will import");
                return true;
            }
        }

        log.debug("cannot import");
        return false;
    }

    @Override
    public void exportAsDrag(final JComponent jc, final InputEvent ie, final int i) {
        super.exportAsDrag(jc, ie, i);
    }

    @Override
    public void exportToClipboard(final JComponent jc, final Clipboard clpbrd, final int i) throws IllegalStateException {
        super.exportToClipboard(jc, clpbrd, i);
    }

    @Override
    public boolean importData(final TransferSupport ts) {
          log.info("importData event:{}", ts);
        Point pt = ts.getDropLocation().getDropPoint();
        JTree tree = (JTree) ts.getComponent();
        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
        LocalFileNode nodeThatWasDropTarget = (LocalFileNode) parentpath.getLastPathComponent();
        final File nodeThatWasDropTargetAsFile = (File) nodeThatWasDropTarget.getUserObject();
        log.info("local file node is: {}", nodeThatWasDropTargetAsFile);
        LocalFileSystemModel fileSystemModel = (LocalFileSystemModel) tree.getModel();

        Transferable transferable = ts.getTransferable();

        DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();
        
       boolean  imported = false;

        for (DataFlavor flavor : transferrableFlavors) {
            log.debug("flavor mime type:{}", flavor.getMimeType());
            if (flavor.isFlavorJavaFileListType()) {
                log.info("process drop as file list");
              
                processDropAfterAcceptingDataFlavor(transferable, nodeThatWasDropTargetAsFile);
                imported = true;
                break;
            } else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.info("process drop as serialized object");
                processDropFromSerializedObjectType(transferable, nodeThatWasDropTargetAsFile);
                imported = true;
                break;
            } else {
                log.debug("flavor not processed: {}", flavor);
            }
        }
        return imported;
    }
    
      private void processDropAfterAcceptingDataFlavor(final Transferable transferable, final File nodeThatWasDropTargetAsFile) throws IdropRuntimeException {

        final List<File> sourceFiles;

        try {
            // get the list of files
            sourceFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException ex) {
            throw new IdropRuntimeException("unsupported flavor getting data from transfer");
        } catch (IOException ex) {
            throw new IdropRuntimeException("io exception getting data from transfer");
        }

        if (sourceFiles.isEmpty()) {
            log.error("no source files in transfer");
            throw new IdropRuntimeException("no source files in transfer");
        }

        final String tempTargetLocalFileAbsolutePath;

        if (nodeThatWasDropTargetAsFile.isDirectory()) {
            tempTargetLocalFileAbsolutePath = nodeThatWasDropTargetAsFile.getAbsolutePath();
        } else {
            log.info("drop target was a file, use the parent collection name for the transfer");
            tempTargetLocalFileAbsolutePath = nodeThatWasDropTargetAsFile.getParent();
        }

        StringBuilder sb = new StringBuilder();

        if (sourceFiles.size() == 1) {
            sb.append("Would you like to copy the remote file ");
            sb.append(sourceFiles.get(0).getAbsolutePath());
            sb.append(" to ");
            sb.append(tempTargetLocalFileAbsolutePath);
        } else {
            sb.append("Would you like to copy multiple files to ");
            sb.append(tempTargetLocalFileAbsolutePath);

        }

        //default icon, custom title
        int n = idropGui.showConfirmDialog(sb.toString(), "Confirm a Get ",  JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {

            prosecuteTransfer(sourceFiles, idropGui.getiDropCore().getTransferManager(), idropGui.getiDropCore().getIrodsAccount());

            // process the drop as a get

        }
    }
      
       private void processDropFromSerializedObjectType(final Transferable transferable, final File parent) {
        log.debug("processing as a drop of a serialized object");
    }
       
    public LocalTreeTransferHandler(final IdropClient idropGui) {
        super("selectionModel");
        if (idropGui == null) {
            throw new IllegalArgumentException("null idropGui");
        }
        this.idropGui = idropGui;
    }

    /**
     * We support both copy and move actions.
     */
    @Override
    public int getSourceActions(final JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(final JComponent jc, final Transferable t, final int i) {
        super.exportDone(jc, t, i);
    }


    protected abstract void prosecuteTransfer(List<File> sourceFiles, TransferManager transferManager, IRODSAccount irodsAccount);


}
