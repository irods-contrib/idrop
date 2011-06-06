package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.DeleteIRODSDialog;
import org.irods.jargon.idrop.desktop.systraygui.MoveIRODSFileToNewIRODSLocationDialog;
import org.irods.jargon.idrop.desktop.systraygui.NewIRODSDirectoryDialog;
import org.irods.jargon.idrop.desktop.systraygui.RenameIRODSDirectoryDialog;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Swing JTree component for viewing iRODS server file system
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSTree extends JTree implements DropTargetListener, TreeWillExpandListener, TreeExpansionListener {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);
    protected iDrop idropParentGui = null;
    protected JPopupMenu m_popup = null;
    protected Action m_action;
    protected TreePath m_clickedPath;
    protected IRODSTree thisTree;
    private int highlightedRow = -1;

    public Rectangle getDirtyRegion() {
        return dirtyRegion;
    }

    public void setDirtyRegion(Rectangle dirtyRegion) {
        this.dirtyRegion = dirtyRegion;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public int getHighlightedRow() {
        return highlightedRow;
    }

    public void setHighlightedRow(int highlightedRow) {
        this.highlightedRow = highlightedRow;
    }
    private Rectangle dirtyRegion = null;
    private Color highlightColor = new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 100);
    private boolean refreshingTree = false;

    public boolean isRefreshingTree() {
        synchronized (this) {
            return refreshingTree;
        }
    }

    public void setRefreshingTree(boolean refreshingTree) {
        synchronized (this) {
            this.refreshingTree = refreshingTree;
        }
    }

    public IRODSTree(TreeModel newModel, iDrop idropParentGui) {
        super(newModel);
        this.idropParentGui = idropParentGui;
        initializeMenusAndListeners();

        //this.setEditable(true);
    }

    public IRODSTree() {
        super();
    }

    public IRODSTree(iDrop idropParentGui) {
        super();
        this.idropParentGui = idropParentGui;
        initializeMenusAndListeners();
    }

    private void initializeMenusAndListeners() {
        setDragEnabled(true);
        setDropMode(javax.swing.DropMode.ON);
        setTransferHandler(new IRODSTreeTransferHandler(idropParentGui, "selectionModel"));
        setUpTreeMenu();
       // setUpDropListener();
        addTreeExpansionListener(this);
        addTreeWillExpandListener(this);
    }

    /**
     * Set up context sensitive tree menu
     */
    private void setUpTreeMenu() {
        thisTree = this;
        m_popup = new JPopupMenu();
        m_action = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (m_clickedPath == null) {
                    return;
                }

                if (thisTree.isExpanded(m_clickedPath)) {
                    thisTree.collapsePath(m_clickedPath);
                } else {
                    thisTree.expandPath(m_clickedPath);
                }
            }
        };

        m_popup.add(m_action);

        Action newAction = new AbstractAction("New Folder") {

            public void actionPerformed(ActionEvent e) {

                log.info("adding new node");

                IRODSNode parent = (IRODSNode) m_clickedPath.getLastPathComponent();
                log.info("parent of new node is: {}", parent);
                CollectionAndDataObjectListingEntry dataEntry = (CollectionAndDataObjectListingEntry) parent.getUserObject();
                if (dataEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
                    JOptionPane.showMessageDialog(thisTree,
                            "The selected item is not a folder, cannot create a new directory",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    log.info("new folder not created, the selected parent is not a collection");
                    return;
                }
                // show a dialog asking for the new directory name...
                NewIRODSDirectoryDialog newDirectoryDialog = new NewIRODSDirectoryDialog(idropParentGui, true, dataEntry.getPathOrName(), thisTree, parent);
                newDirectoryDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                newDirectoryDialog.setVisible(true);
            }
        };
        m_popup.add(newAction);

        m_popup.addSeparator();

        Action a1 = new AbstractAction("Delete") {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("deleting a node");

                TreePath[] selects = thisTree.getSelectionPaths();
                DeleteIRODSDialog deleteDialog;

                if (selects.length == 1) {
                    IRODSNode toDelete = (IRODSNode) m_clickedPath.getLastPathComponent();
                    log.info("deleting a single node: {}", toDelete);
                    deleteDialog = new DeleteIRODSDialog(idropParentGui, true, thisTree, toDelete);
                } else {
                    List<IRODSNode> nodesToDelete = new ArrayList<IRODSNode>();
                    for (TreePath treePath : selects) {
                        nodesToDelete.add((IRODSNode) treePath.getLastPathComponent());
                    }
                    deleteDialog = new DeleteIRODSDialog(idropParentGui, true, thisTree, nodesToDelete);
                }

                deleteDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                deleteDialog.setVisible(true);
            }
        };

        m_popup.add(a1);
        Action a2 = new AbstractAction("Rename") {

            public void actionPerformed(ActionEvent e) {
                log.info("renaming node");

                IRODSNode toRename = (IRODSNode) m_clickedPath.getLastPathComponent();
                log.info("node to rename  is: {}", toRename);
                CollectionAndDataObjectListingEntry dataEntry = (CollectionAndDataObjectListingEntry) toRename.getUserObject();

                //dialog uses absolute path, so munge it for files
                StringBuilder sb = new StringBuilder();
                if (dataEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
                    sb.append(dataEntry.getPathOrName());
                } else {
                    sb.append(dataEntry.getParentPath());
                    sb.append('/');
                    sb.append(dataEntry.getPathOrName());
                }

                // show a dialog asking for the new directory name...
                RenameIRODSDirectoryDialog renameDialog = new RenameIRODSDirectoryDialog(idropParentGui, true, sb.toString(), thisTree, toRename);
                renameDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                renameDialog.setVisible(true);
            }
        };
        m_popup.add(a2);
        thisTree.add(m_popup);
        thisTree.addMouseListener(new PopupTrigger());

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

        // drop from explorer - human presentable name = application/x-java-url; class=java.net.URL
        // process a drop onto this tree

        /*
         * User drop action depends on the drop actions supported by the drag source and the drop action selected by the user. 
         * The user can select a drop action by pressing modifier keys during the drag operation:
        Ctrl + Shift -> ACTION_LINK
        Ctrl         -> ACTION_COPY (1)
        Shift        -> ACTION_MOVE (2)
         * 
         * default = 2
         */

/*
        log.info("drop event:{}", dtde);
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath targetPath = tree.getClosestPathForLocation(pt.x, pt.y);
        IRODSNode targetNode = (IRODSNode) targetPath.getLastPathComponent();
        log.info("drop node is: {}", targetNode);

        Transferable transferable = dtde.getTransferable();

        DataFlavor[] transferrableFlavors = transferable.getTransferDataFlavors();

        // see if this is a phymove gesture
        if (transferable.isDataFlavorSupported(IRODSTreeTransferable.localPhymoveFlavor)) {
            log.info("drop accepted, process as a move");
            dtde.acceptDrop(dtde.getDropAction());
            processPhymoveGesture(transferable, targetNode);
            return;
        }

        // not a phymove

        boolean accepted = false;

        for (DataFlavor flavor : transferrableFlavors) {
            log.debug("flavor mime type:{}", flavor.getMimeType());
            log.debug("flavor human presentable name:{}", flavor.getHumanPresentableName());
            if (flavor.isFlavorJavaFileListType()) {
                log.info("drop accepted...process drop as file list from desktop");
                dtde.acceptDrop(dtde.getDropAction());
                processDropOfFileList(transferable, targetNode);
                accepted = true;
                break;
            } else if (flavor.getMimeType().equals("application/x-java-jvm-local-objectref; class=javax.swing.tree.TreeSelectionModel")) {
                log.info("drop accepted: process drop as serialized object");
                dtde.acceptDrop(dtde.getDropAction());
                processDropOfTreeSelectionModel(transferable, targetNode, flavor);
                accepted = true;
                break;
            } else {
                log.debug("flavor not processed: {}", flavor);
            }
        }

        if (!accepted) {
            log.info("drop rejected");
            dtde.rejectDrop();
        }
 * 
 */

    }

   
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

        Point location = dtde.getLocation();
        int closestRow = this.getClosestRowForLocation((int) location.getX(), (int) location.getY());
        boolean highlighted = false;

        Graphics g = getGraphics();

        // row changed

        if (highlightedRow != closestRow) {
            if (null != dirtyRegion) {
                paintImmediately(dirtyRegion);
            }

            for (int j = 0; j < getRowCount(); j++) {
                if (closestRow == j) {

                    Rectangle firstRowRect = getRowBounds(closestRow);
                    this.dirtyRegion = firstRowRect;
                    g.setColor(highlightColor);

                    g.fillRect((int) dirtyRegion.getX(), (int) dirtyRegion.getY(), (int) dirtyRegion.getWidth(), (int) dirtyRegion.getHeight());
                    highlightedRow = closestRow;
                }
            }

        }

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        if (null != dirtyRegion) {
            paintImmediately(dirtyRegion);
        }
    }
    // processes drop from local file tree

   

    private void setUpDropListener() throws IdropRuntimeException {
        try {
            DropTarget dt = new DropTarget(this, this);
        } catch (Exception ex) {
            Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("too many event listeners");
        }
    }

   

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
    }

    class PopupTrigger extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                TreePath path = thisTree.getPathForLocation(x, y);
                if (path != null) {
                    if (thisTree.isExpanded(path)) {
                        m_action.putValue(Action.NAME, "Collapse");
                    } else {
                        m_action.putValue(Action.NAME, "Expand");
                    }
                    m_popup.show(thisTree, x, y);
                    m_clickedPath = path;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                TreePath path = thisTree.getPathForLocation(x, y);
                if (path != null) {
                    if (thisTree.isExpanded(path)) {
                        m_action.putValue(Action.NAME, "Collapse");
                    } else {
                        m_action.putValue(Action.NAME, "Expand");
                    }
                    m_popup.show(thisTree, x, y);
                    m_clickedPath = path;
                }
            }
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        log.debug("tree expansion event:{}", event);
        IRODSNode expandingNode = (IRODSNode) event.getPath().getLastPathComponent();
        // If I am refreshing the tree, then do not close the connection after each load.  It will be closed in the thing doing the refreshing
        try {
            expandingNode.lazyLoadOfChildrenOfThisNode(!isRefreshingTree());
        } catch (IdropException ex) {
            Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
            idropParentGui.showIdropException(ex);
            throw new IdropRuntimeException("error expanding irodsNode");
        } finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
