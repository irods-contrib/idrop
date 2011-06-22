package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.DeleteIRODSDialog;
import org.irods.jargon.idrop.desktop.systraygui.NewIRODSDirectoryDialog;
import org.irods.jargon.idrop.desktop.systraygui.RenameIRODSDirectoryDialog;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.TreePathSupport;
import org.slf4j.LoggerFactory;

/**
 * Swing JTree component for viewing iRODS server file system
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSTree extends Outline implements TreeWillExpandListener, TreeExpansionListener {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);
    protected iDrop idropParentGui = null;
    protected JPopupMenu m_popup = null;
    protected Action m_action;
    protected TreePath m_clickedPath;
    protected IRODSTree thisTree;
    private boolean refreshingTree = false;
    TreePathSupport tps;

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
        super();

        OutlineModel mdl = DefaultOutlineModel.createOutlineModel(
                newModel, new IRODSRowModel(), true, "File System");
        this.idropParentGui = idropParentGui;
        tps = new TreePathSupport(mdl, this.getLayoutCache());

        tps.addTreeExpansionListener(this);
        tps.addTreeWillExpandListener(this);
        initializeMenusAndListeners();
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
        IrodsSelectionListenerForBuildingInfoPanel treeListener;
        try {
            treeListener = new IrodsSelectionListenerForBuildingInfoPanel(idropParentGui);
        } catch (IdropException ex) {
            Logger.getLogger(IRODSTree.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error initializing selection listener", ex);
        }
        this.getSelectionModel().addListSelectionListener(treeListener);

    }

    /**
     * Set up context sensitive tree menu
     */
    private void setUpTreeMenu() {
        thisTree = this;
        m_popup = new JPopupMenu();
        m_action = new AbstractAction() {

            @Override
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

            @Override
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
                int[] rows = thisTree.getSelectedRows();
                log.debug("selected rows for delete:{}", rows);

                DeleteIRODSDialog deleteDialog;

                if (rows.length == 1) {

                    IRODSNode toDelete = (IRODSNode) thisTree.getValueAt(rows[0], 0);
                    log.info("deleting a single node: {}", toDelete);
                    deleteDialog = new DeleteIRODSDialog(idropParentGui, true, thisTree, toDelete);
                } else {
                    List<IRODSNode> nodesToDelete = new ArrayList<IRODSNode>();
                    for (int row : rows) {
                        nodesToDelete.add((IRODSNode) thisTree.getValueAt(row, 0));

                    }

                    deleteDialog = new DeleteIRODSDialog(idropParentGui, true, thisTree, nodesToDelete);
                }

                deleteDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                deleteDialog.setVisible(true);
            }
        };

        m_popup.add(a1);
        Action a2 = new AbstractAction("Rename") {

            @Override
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

                TreePath path = thisTree.getClosestPathForLocation(x, y);
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
                TreePath path = thisTree.getClosestPathForLocation(x, y);
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

    public void highlightPath(final TreePath pathToHighlight) {
        final IRODSTree highlightTree = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                highlightTree.collapsePath(pathToHighlight);
                highlightTree.expandPath(pathToHighlight);
                // highlightTree.sc
                // highlightTree.scrollPathToVisible(pathToHighlight);
            }
        });
    }
}
