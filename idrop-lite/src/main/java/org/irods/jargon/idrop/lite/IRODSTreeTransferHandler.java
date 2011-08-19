/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.slf4j.LoggerFactory;

/**
 * Transfer handler to handle import/export from the IRODSTree that handles the Swing JTree depicting the iRODS file
 * system
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSTreeTransferHandler extends TransferHandler {

    @Override
    public void exportAsDrag(JComponent jc, InputEvent ie, int i) {
        log.info("export as drag");
        super.exportAsDrag(jc, ie, i);
    }

    @Override
    public void exportToClipboard(JComponent jc, Clipboard clpbrd, int i) throws IllegalStateException {
        super.exportToClipboard(jc, clpbrd, i);
    }

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTreeTransferHandler.class);

    public final iDropLiteApplet idropGui;

    public IRODSTreeTransferHandler(final iDropLiteApplet idropGui, final String string) {
        super(string);
        if (idropGui == null) {
            throw new IdropRuntimeException("null idrop gui");
        }
        this.idropGui = idropGui;
    }

    @Override
    public boolean importData(TransferSupport ts) {

        // FIXME: handle 'paste' drop
        log.info("importData in irods:{}", ts);
        // mac opt = 1 w/o = 2 (no plus icon for a 2 so it's a move) / for drag from local is 1 (copy)
        Point pt = ts.getDropLocation().getDropPoint();
        IRODSTree tree = (IRODSTree) ts.getComponent();
        TreePath targetPath = tree.getClosestPathForLocation(pt.x, pt.y);
        IRODSNode targetNode = (IRODSNode) targetPath.getLastPathComponent();
        log.info("drop node is: {}", targetNode);

        Transferable transferable = ts.getTransferable();

        DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();

        // see if this is a phymove gesture or an iRODS copy
        if (transferable.isDataFlavorSupported(IRODSTreeTransferable.irodsTreeDataFlavor)) {
            log.info("drop accepted, process as a move or copy, based on the action");

            if (ts.isDrop()) {
                if (ts.getUserDropAction() == 1) {
                    // copy
                    processCopyGesture(transferable, targetNode);
                    return true;
                } else {
                    processPhymoveGesture(transferable, targetNode);
                    return true;
                }
            }

        }

        // not a phymove

        boolean accepted = false;

        for (DataFlavor flavor : transferrableFlavors) {
            log.debug("flavor mime type:{}", flavor.getMimeType());
            log.debug("flavor human presentable name:{}", flavor.getHumanPresentableName());
            if (flavor.isFlavorJavaFileListType()) {
                log.info("drop accepted...process drop as file list from desktop");
                // dtde.acceptDrop(dtde.getDropAction());
                processDropOfFileList(transferable, targetNode);
                accepted = true;
                break;
            } else if (flavor.getMimeType().equals(
                    "application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.info("drop accepted: process drop as serialized object");
                // dtde.acceptDrop(dtde.getDropAction());
                processDropOfTreeSelectionModel(transferable, targetNode, flavor);
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

        // return super.importData(ts);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        Point location = support.getDropLocation().getDropPoint();
        IRODSTree tree = (IRODSTree) support.getComponent();

        log.warn("transferFlavors:{}", support.getDataFlavors());

        // if (support.getComponent() instanceof IRODSTree) {
        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                log.debug("found file list flavor, will import");
                return true;
            } else if (flavor.getMimeType().equals(
                    "application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.debug("found file list flavor, will import");
                return true;
            }
        }
        // }
        log.debug("cannot import");
        return false;

    }

    @Override
    public void exportDone(JComponent comp, Transferable trans, int action) {
        /* test code for drag to native, please leave in place MC
        DropTarget dt = comp.getDropTarget();
        log.debug("dt is:{}", dt);
        FlavorMap dtFlavorMap = dt.getFlavorMap();
        log.debug("flavormap:{}", dtFlavorMap);
        Component dropTargetComponent = dt.getComponent();
        log.debug("dt component:{}", dropTargetComponent);
        if (action != MOVE) {
         //   return;
        }
         */
    }

    /**
     * We support both copy and move actions.
     */
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        log.debug("creating a transferrable from the irods tree view");

        List<File> transferFiles = new ArrayList<File>();
        IRODSTree stagingViewTree = (IRODSTree) c;
        // get the selected node (one for now)
        int[] rows = idropGui.getIrodsTree().getSelectedRows();
        log.debug("selected rows for delete:{}", rows);

        List<IRODSNode> nodesToTransfer = new ArrayList<IRODSNode>();
        for (int row : rows) {
            nodesToTransfer.add((IRODSNode) idropGui.getIrodsTree().getValueAt(row, 0));
        }

        IRODSFileService irodsFileService;
        try {
            irodsFileService = new IRODSFileService(idropGui.getIrodsAccount(), idropGui.getiDropCore()
                    .getIrodsFileSystem());
        } catch (IdropException ex) {
            Logger.getLogger(IRODSTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        String objectPath;
        for (IRODSNode nodeToTransfer : nodesToTransfer) {
            CollectionAndDataObjectListingEntry listingEntry = (CollectionAndDataObjectListingEntry) nodeToTransfer
                    .getUserObject();
            if (listingEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
                objectPath = listingEntry.getPathOrName();
            } else {
                objectPath = listingEntry.getParentPath() + "/" + listingEntry.getPathOrName();
            }

            try {
                transferFiles.add((File) irodsFileService.getIRODSFileForPath(objectPath));
            } catch (IdropException ex) {
                Logger.getLogger(IRODSTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropRuntimeException(ex);
            }
        }

        return new IRODSTreeTransferable(transferFiles, stagingViewTree);

    }

    private void processDropOfTreeSelectionModel(final Transferable transferable, final IRODSNode parent,
            final DataFlavor dataFlavor) {
        final List<File> sourceFiles = new ArrayList<File>();
        CollectionAndDataObjectListingEntry putTarget = (CollectionAndDataObjectListingEntry) parent.getUserObject();
        final String targetIrodsFileAbsolutePath;

        if (putTarget.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
            targetIrodsFileAbsolutePath = putTarget.getPathOrName();
        } else {
            targetIrodsFileAbsolutePath = putTarget.getParentPath();
        }

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

        StringBuilder sb = new StringBuilder();

        if (sourceFiles.size() > 1) {
            sb.append("Would you like to put multiple files");
            sb.append(" to iRODS at ");
            sb.append(targetIrodsFileAbsolutePath);
        } else {
            sb.append("Would you like to put the file  ");
            sb.append(sourceFiles.get(0).getAbsolutePath());
            sb.append(" to iRODS at ");
            sb.append(targetIrodsFileAbsolutePath);
        }

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(idropGui, sb.toString(), "Confirm a Put to iRODS ",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {

            // process the drop as a put

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    for (File transferFile : sourceFiles) {
                        log.info("process a put from source: {}", transferFile.getAbsolutePath());

                        String localSourceAbsolutePath = transferFile.getAbsolutePath();
                        String sourceResource = idropGui.getIrodsAccount().getDefaultStorageResource();
                        log.info("initiating put transfer");
                        try {
                        	/*
                            idropGui.getiDropCore()
                                    .getTransferManager()
                                    .enqueueAPut(localSourceAbsolutePath, targetIrodsFileAbsolutePath, sourceResource,
                                            idropGui.getIrodsAccount());
                            */
                        	idropGui.getiDropCore().getTransferManager().putOperation(localSourceAbsolutePath,
                        			targetIrodsFileAbsolutePath, sourceResource, idropGui, null);
                        } catch (JargonException ex) {
                            java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                                    java.util.logging.Level.SEVERE, null, ex);
                            idropGui.showIdropException(ex);
                        }
                    }
                }
            });
        }

    }

    private void processPhymoveGesture(Transferable transferable, IRODSNode targetNode) {
        log.info("process as drop of file list");

        List<IRODSFile> sourceFiles;
        CollectionAndDataObjectListingEntry targetEntry = (CollectionAndDataObjectListingEntry) targetNode
                .getUserObject();
        if (targetEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
            log.warn("attempt to move a file to a data object, must be a collection");
            idropGui.showMessageFromOperation("unable to move file, the target of the move is not a collection");
            return;
        }

        try {
            // get the list of files
            sourceFiles = (List<IRODSFile>) transferable.getTransferData(IRODSTreeTransferable.irodsTreeDataFlavor);
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

        String targetFileAbsolutePath = targetEntry.getPathOrName();
        MoveOrCopyiRODSDialog moveIRODSFileOrDirectoryDialog;
        if (sourceFiles.size() == 1) {
            moveIRODSFileOrDirectoryDialog = new MoveOrCopyiRODSDialog(idropGui, true, targetNode,
                    idropGui.getIrodsTree(), sourceFiles.get(0), targetFileAbsolutePath, false);
        } else {
            moveIRODSFileOrDirectoryDialog = new MoveOrCopyiRODSDialog(idropGui, true, targetNode,
                    idropGui.getIrodsTree(), sourceFiles, targetFileAbsolutePath, false);
        }

        moveIRODSFileOrDirectoryDialog.setLocation((int) (idropGui.getLocation().getX() + idropGui.getWidth() / 2),
                (int) (idropGui.getLocation().getY() + idropGui.getHeight() / 2));
        moveIRODSFileOrDirectoryDialog.setVisible(true);

    }

    // handle a drop from the local file system
    private void processDropOfFileList(Transferable transferable, IRODSNode dropTargetNode) throws IdropRuntimeException {

        log.info("process as drop of file list");

        IRODSNode effectiveTargetNode;
        if (dropTargetNode.isLeaf()) {
            effectiveTargetNode = (IRODSNode) dropTargetNode.getParent();
        } else {
            effectiveTargetNode = dropTargetNode;
        }

        final String sourceResource = idropGui.getIrodsAccount().getDefaultStorageResource();
        final List<File> sourceFiles;
        CollectionAndDataObjectListingEntry putTarget = (CollectionAndDataObjectListingEntry) effectiveTargetNode.getUserObject();
        final String targetIrodsFileAbsolutePath = putTarget.getPathOrName();

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

        StringBuilder sb = new StringBuilder();

        if (sourceFiles.size() > 1) {
            sb.append("Would you like to put multiple files");
            sb.append(" to iRODS at ");
            sb.append(putTarget.getPathOrName());
        } else {
            sb.append("Would you like to put the file  ");
            sb.append(sourceFiles.get(0).getAbsolutePath());
            sb.append(" to iRODS at ");
            sb.append(putTarget.getPathOrName());
        }

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(idropGui, sb.toString(), "Confirm a Put to iRODS ",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {

            // process the drop as a put

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    for (File transferFile : sourceFiles) {
                        log.info("initiating put transfer for source file:{}", transferFile.getAbsolutePath());
                        try {
                        	idropGui.getiDropCore().getTransferManager().putOperation(transferFile.getAbsolutePath(),
                        			targetIrodsFileAbsolutePath, sourceResource, idropGui, new TreeTransferControlBlock());
                            /*
                            idropGui.getiDropCore()
                                    .getTransferManager()
                                    .enqueueAPut(transferFile.getAbsolutePath(), targetIrodsFileAbsolutePath,
                                            sourceResource, idropGui.getIrodsAccount());
                            */
                        } catch (JargonException ex) {
                            java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(
                                    java.util.logging.Level.SEVERE, null, ex);
                            idropGui.showIdropException(ex);
                        }
                    }
                }
            });

        }

    }

    private void processCopyGesture(Transferable transferable, IRODSNode targetNode) {
        log.info("process as drop of file list");

        List<IRODSFile> sourceFiles;
        CollectionAndDataObjectListingEntry targetEntry = (CollectionAndDataObjectListingEntry) targetNode
                .getUserObject();
        if (targetEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
            log.warn("attempt to copy a file to a data object, must be a collection");
            idropGui.showMessageFromOperation("unable to copy file, the target of the copy is not a collection");
            return;
        }

        try {
            // get the list of files
            sourceFiles = (List<IRODSFile>) transferable.getTransferData(IRODSTreeTransferable.irodsTreeDataFlavor);

            /*
             * for the source files, default to the resource that was specified at login,this might need to be
             * reconsidered but can cause a -321000 no resc error if no default set in irods.
             */

            for (IRODSFile sourceFile : sourceFiles) {
                sourceFile.setResource(idropGui.getiDropCore().getIrodsAccount().getDefaultStorageResource());
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

        String targetFileAbsolutePath = targetEntry.getPathOrName();
        MoveOrCopyiRODSDialog moveIRODSFileOrDirectoryDialog;
        if (sourceFiles.size() == 1) {
            moveIRODSFileOrDirectoryDialog = new MoveOrCopyiRODSDialog(idropGui, true, targetNode,
                    idropGui.getIrodsTree(), sourceFiles.get(0), targetFileAbsolutePath, true);
        } else {
            moveIRODSFileOrDirectoryDialog = new MoveOrCopyiRODSDialog(idropGui, true, targetNode,
                    idropGui.getIrodsTree(), sourceFiles, targetFileAbsolutePath, true);
        }

        moveIRODSFileOrDirectoryDialog.setLocation((int) (idropGui.getLocation().getX() + idropGui.getWidth() / 2),
                (int) (idropGui.getLocation().getY() + idropGui.getHeight() / 2));
        moveIRODSFileOrDirectoryDialog.setVisible(true);
    }

}
