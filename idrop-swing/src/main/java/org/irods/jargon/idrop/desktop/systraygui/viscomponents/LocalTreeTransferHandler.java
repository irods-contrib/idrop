package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

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
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * A transfer handler for the local file tree
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LocalTreeTransferHandler extends TransferHandler {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(LocalTreeTransferHandler.class);
    public final iDrop idropGui;

    @Override
    public boolean canImport(final TransferSupport support) {
        Point location = support.getDropLocation().getDropPoint();
        LocalFileTree tree = (LocalFileTree) support.getComponent();

        int closestRow = idropGui.getFileTree().getClosestRowForLocation(
                (int) location.getX(), (int) location.getY());
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
    public void exportAsDrag(final JComponent jc, final InputEvent ie,
            final int i) {
        super.exportAsDrag(jc, ie, i);
    }

    @Override
    public void exportToClipboard(final JComponent jc, final Clipboard clpbrd,
            final int i) throws IllegalStateException {
        super.exportToClipboard(jc, clpbrd, i);
    }

    @Override
    public boolean importData(final TransferSupport ts) {
        log.info("importData event:{}", ts);

        LocalFileNode nodeThatWasDropTarget = null;
        File nodeThatWasDropTargetAsFile = null;

        JTree tree = (JTree) ts.getComponent();

        if (ts.isDrop()) {
            Point pt = ts.getDropLocation().getDropPoint();
            TreePath targetPath = tree.getClosestPathForLocation(pt.x, pt.y);
            nodeThatWasDropTarget = (LocalFileNode) targetPath.getLastPathComponent();
        } else {
            nodeThatWasDropTarget = (LocalFileNode) tree.getSelectionPath().getLastPathComponent();
        }

        log.info("drop node is: {}", nodeThatWasDropTarget);
        nodeThatWasDropTargetAsFile = (File) nodeThatWasDropTarget.getUserObject();
        log.info("local file node is: {}", nodeThatWasDropTargetAsFile);
        tree.getModel();

        Transferable transferable = ts.getTransferable();

        DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();

        boolean imported = false;

        for (DataFlavor flavor : transferrableFlavors) {
            log.debug("flavor mime type:{}", flavor.getMimeType());
            if (flavor.isFlavorJavaFileListType()) {
                log.info("process drop as file list");

                processDropAfterAcceptingDataFlavor(transferable,
                        nodeThatWasDropTargetAsFile);
                imported = true;
                break;
            } else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.info("process drop as serialized object");
                processDropFromSerializedObjectType(transferable,
                        nodeThatWasDropTargetAsFile, flavor,
                        ts.getUserDropAction());
                imported = true;
                break;
            } else {
                log.debug("flavor not processed: {}", flavor);
            }
        }
        return imported;
    }

    private void processDropAfterAcceptingDataFlavor(
            final Transferable transferable,
            final File nodeThatWasDropTargetAsFile)
            throws IdropRuntimeException {

        final List<File> sourceFiles;

        try {
            // get the list of files
            sourceFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException ex) {
            throw new IdropRuntimeException(
                    "unsupported flavor getting data from transfer");
        } catch (IOException ex) {
            throw new IdropRuntimeException(
                    "io exception getting data from transfer");
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

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(idropGui, sb.toString(),
                "Confirm a Get ", JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {

            // process the drop as a get

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        for (File transferFile : sourceFiles) {

                            if (transferFile instanceof IRODSFile) {
                                log.info(
                                        "initiating a transfer of iRODS file:{}",
                                        transferFile.getAbsolutePath());
                                log.info("transfer to local file:{}",
                                        tempTargetLocalFileAbsolutePath);
                                idropGui.getiDropCore().getTransferManager().enqueueAGet(
                                        transferFile.getAbsolutePath(),
                                        tempTargetLocalFileAbsolutePath,
                                        "", idropGui.getIrodsAccount());
                            } else {
                                log.info(
                                        "process a local to local move with source...not yet implemented : {}",
                                        transferFile.getAbsolutePath());
                            }
                        }
                    } catch (JargonException ex) {
                        java.util.logging.Logger.getLogger(
                                LocalFileTree.class.getName()).log(
                                java.util.logging.Level.SEVERE, null, ex);
                        idropGui.showIdropException(ex);
                        throw new IdropRuntimeException(ex);
                    }
                }
            });

        }
    }

    /**
     * Drop from local file tree onto local file tree for copy/move operation
     * 
     * @param transferable
     * @param parent
     */
    private void processDropFromSerializedObjectType(
            final Transferable transferable, final File parent,
            final DataFlavor flavor, final int userDropAction) {

        log.info("process as drop of file list to target:{}",
                parent.getAbsolutePath());

        File effectiveTarget;
        if (parent.isDirectory()) {
            effectiveTarget = parent;
        } else {
            effectiveTarget = parent.getParentFile();
        }
        try {
            Object transferObj = transferable.getTransferData(flavor);
            log.debug("transfer object:{}", transferObj);

            boolean isTreeModel = transferObj instanceof DefaultTreeSelectionModel;
            if (!isTreeModel) {
                log.warn("unknown object type in transferable for local file tree");
                return;
            }

            DefaultTreeSelectionModel transferableAsTreeModel = (DefaultTreeSelectionModel) transferObj;

            LocalFileSystemModel fileSystemModel = (LocalFileSystemModel) idropGui.getFileTree().getModel();
            log.debug("tree model in transferable:{}", transferableAsTreeModel);

            TreePath[] selectionPaths = transferableAsTreeModel.getSelectionPaths();

            File sourceFile;
            LocalFileNode sourceNode;
            for (TreePath selectionPath : selectionPaths) {
                sourceNode = (LocalFileNode) selectionPath.getLastPathComponent();
                sourceFile = (File) sourceNode.getUserObject();
                log.info("sourceFile:{}", sourceFile.getAbsolutePath());

                // target normalized to a directory
                if (sourceFile.isDirectory()) {
                    if (userDropAction == 1) {
                        FileUtils.copyDirectoryToDirectory(sourceFile,
                                effectiveTarget);

                    } else {
                        FileUtils.moveDirectory(sourceFile, effectiveTarget);
                        LocalFileNode parentNode = (LocalFileNode) sourceNode.getParent();
                        parentNode.remove(sourceNode);
                    }
                    fileSystemModel.notifyFileShouldBeAdded(
                            idropGui.getFileTree(),
                            effectiveTarget.getAbsolutePath());
                } else {
                    if (userDropAction == 1) {
                        FileUtils.copyFileToDirectory(sourceFile,
                                effectiveTarget);

                    } else {
                        FileUtils.moveToDirectory(sourceFile, effectiveTarget,
                                false);
                        LocalFileNode parentNode = (LocalFileNode) sourceNode.getParent();
                        parentNode.remove(sourceNode);
                    }
                    fileSystemModel.notifyFileShouldBeAdded(
                            idropGui.getFileTree(),
                            effectiveTarget.getAbsolutePath() + "/"
                            + sourceFile.getName());

                }

            }

        } catch (IdropException ex) {
            log.error("error updating local file tree after add", ex);
            throw new IdropRuntimeException(ex);

        } catch (UnsupportedFlavorException ex) {
            log.error("error updating local file tree after add", ex);
            throw new IdropRuntimeException(ex);
        } catch (IOException ex) {
            log.error("error updating local file tree after add", ex);
            throw new IdropRuntimeException(ex);
        }

    }

    public LocalTreeTransferHandler(final iDrop idropGui) {
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
    protected void exportDone(final JComponent jc, final Transferable t,
            final int i) {
        super.exportDone(jc, t, i);
    }
}
