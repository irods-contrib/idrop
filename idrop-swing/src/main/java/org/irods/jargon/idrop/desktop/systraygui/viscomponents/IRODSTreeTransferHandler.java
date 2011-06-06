package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.MoveIRODSFileToNewIRODSLocationDialog;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Transfer handler to handle import/export from the IRODSTree that handles the Swing JTree depicting
 * the iRODS file system
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSTreeTransferHandler extends TransferHandler {
    
      public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTreeTransferHandler.class);
    public final iDrop idropGui;

    public IRODSTreeTransferHandler(final iDrop idropGui, final String string) {
        super(string);
        if (idropGui == null) {
            throw new IdropRuntimeException("null idrop gui");
        }
        this.idropGui = idropGui;
    }


    @Override
    public boolean importData(TransferSupport ts) {
        
        log.info("importData in irods:{}", ts);
        
        Point pt = ts.getDropLocation().getDropPoint();
        JTree tree = (JTree) ts.getComponent();
        TreePath targetPath = tree.getClosestPathForLocation(pt.x, pt.y);
        IRODSNode targetNode = (IRODSNode) targetPath.getLastPathComponent();
        log.info("drop node is: {}", targetNode);
        
        Transferable transferable = ts.getTransferable();

        DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();

        // see if this is a phymove gesture
        if (transferable.isDataFlavorSupported(IRODSTreeTransferable.localPhymoveFlavor)) {
            log.info("drop accepted, process as a move");
            processPhymoveGesture(transferable, targetNode);
            return true;
        }

        // not a phymove

        boolean accepted = false;

        for (DataFlavor flavor : transferrableFlavors) {
            log.debug("flavor mime type:{}", flavor.getMimeType());
            log.debug("flavor human presentable name:{}", flavor.getHumanPresentableName());
            if (flavor.isFlavorJavaFileListType()) {
                log.info("drop accepted...process drop as file list from desktop");
                //dtde.acceptDrop(dtde.getDropAction());
                processDropOfFileList(transferable, targetNode);
                accepted = true;
                break;
            } else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
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
            accepted=false;
        }
        
        return accepted;
        
       // return super.importData(ts);
    }

  
    @Override
    public boolean canImport(TransferSupport support) {
       // return super.canImport(support);
        Point location = support.getDropLocation().getDropPoint();
        IRODSTree tree = (IRODSTree) support.getComponent();
       
        int closestRow = idropGui.getIrodsTree().getClosestRowForLocation((int) location.getX(), (int) location.getY());
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
                    
       // if (support.getComponent() instanceof IRODSTree) {
            for (DataFlavor flavor :  support.getDataFlavors()) {
                if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    log.debug("found file list flavor, will import");
                    return true;
                }  else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                     log.debug("found file list flavor, will import");
                    return true;
                }
            }
        //}
        log.debug("cannot import");
        return false;

    }

    @Override
     public void exportDone(JComponent comp, Transferable trans, int action) {
        if (action != MOVE) {
            return;
        }
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

        TreePath[] selectionPaths = stagingViewTree.getSelectionModel().getSelectionPaths();
        log.info("transferrable path:{}", selectionPaths);

        IRODSFileService irodsFileService;
        try {
            irodsFileService = new IRODSFileService(idropGui.getIrodsAccount(), idropGui.getiDropCore().getIrodsFileSystem());
        } catch (IdropException ex) {
            Logger.getLogger(IRODSTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }

        IRODSNode listingEntryNode;
        String objectPath;
        for (TreePath selectionPath : selectionPaths) {
            listingEntryNode = (IRODSNode) selectionPath.getLastPathComponent();
            CollectionAndDataObjectListingEntry listingEntry = (CollectionAndDataObjectListingEntry) listingEntryNode.getUserObject();
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
    
     private void processDropOfTreeSelectionModel(final Transferable transferable, final IRODSNode parent, final DataFlavor dataFlavor) {
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
            TreeSelectionModel transferableSelectionModel = (TreeSelectionModel) transferable.getTransferData(dataFlavor);
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

        //default icon, custom title
        int n = JOptionPane.showConfirmDialog(
                idropGui,
                sb.toString(),
                "Confirm a Put to iRODS ",
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
                            idropGui.getiDropCore().getTransferManager().enqueueAPut(localSourceAbsolutePath, targetIrodsFileAbsolutePath, sourceResource, idropGui.getIrodsAccount());
                        } catch (JargonException ex) {
                            java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        CollectionAndDataObjectListingEntry targetEntry = (CollectionAndDataObjectListingEntry) targetNode.getUserObject();
        if (targetEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
            log.warn("attempt to move a file to a data object, must be a collection");
            idropGui.showMessageFromOperation("unable to move file, the target of the move is not a collection");
            return;
        }

        try {
            // get the list of files
            sourceFiles = (List<IRODSFile>) transferable.getTransferData(IRODSTreeTransferable.localPhymoveFlavor);
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
        MoveIRODSFileToNewIRODSLocationDialog moveIRODSFileOrDirectoryDialog;
        if (sourceFiles.size() == 1) {
            moveIRODSFileOrDirectoryDialog = new MoveIRODSFileToNewIRODSLocationDialog(idropGui, true, targetNode, idropGui.getIrodsTree(), sourceFiles.get(0), targetFileAbsolutePath);
        } else {
            moveIRODSFileOrDirectoryDialog = new MoveIRODSFileToNewIRODSLocationDialog(idropGui, true, targetNode, idropGui.getIrodsTree(), sourceFiles, targetFileAbsolutePath);
        }

        moveIRODSFileOrDirectoryDialog.setLocation((int) (idropGui.getLocation().getX() + idropGui.getWidth() / 2), (int) (idropGui.getLocation().getY() + idropGui.getHeight() / 2));
        moveIRODSFileOrDirectoryDialog.setVisible(true);

    }
      
       // handle a drop from the local file system
    private void processDropOfFileList(Transferable transferable, IRODSNode parent) throws IdropRuntimeException {

        log.info("process as drop of file list");

        final String sourceResource = idropGui.getIrodsAccount().getDefaultStorageResource();
        final List<File> sourceFiles;
        CollectionAndDataObjectListingEntry putTarget = (CollectionAndDataObjectListingEntry) parent.getUserObject();
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

        //default icon, custom title
        int n = JOptionPane.showConfirmDialog(
                idropGui,
                sb.toString(),
                "Confirm a Put to iRODS ",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {

            // process the drop as a put

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    for (File transferFile : sourceFiles) {
                        log.info("initiating put transfer for source file:{}", transferFile.getAbsolutePath());
                        try {
                            idropGui.getiDropCore().getTransferManager().enqueueAPut(transferFile.getAbsolutePath(), targetIrodsFileAbsolutePath, sourceResource, idropGui.getIrodsAccount());
                        } catch (JargonException ex) {
                            java.util.logging.Logger.getLogger(LocalFileTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            idropGui.showIdropException(ex);
                        }
                    }
                }
            });

        }

    }

}
