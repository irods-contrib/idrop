/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
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
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.DeleteLocalFileDialog;
import org.irods.jargon.idrop.desktop.systraygui.NewLocalDirectoryDialog;
import org.irods.jargon.idrop.desktop.systraygui.RenameLocalDirectoryDialog;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.exceptions.IdropException;

/**
 * JTree for viewing local file system, includes DnD support from StagingViewTree.
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LocalFileTree extends JTree implements TreeWillExpandListener {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(LocalFileTree.class);
    private iDrop idropParentGui = null;
    protected JPopupMenu m_popup = null;
    protected Action m_action;
    protected TreePath m_clickedPath;
    protected LocalFileTree thisTree;
    private int highlightedRow = -1;
     private Rectangle dirtyRegion = null;
    private Color highlightColor = new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 100);

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
   

    public LocalFileTree(TreeModel newModel, iDrop idropParentGui) {
        super(newModel);
        this.idropParentGui = idropParentGui;
        setDragEnabled(true);
        this.setTransferHandler(new LocalTreeTransferHandler(idropParentGui));
        this.setCellRenderer(new DefaultTreeCellRenderer());
        setUpTreeMenu();
        setDropMode(javax.swing.DropMode.ON);

    }

    /**
     * Utility method takes an <code>Enumeration</code> of tree paths, such as would be returned
     * by calling <code>getExpandedDescendants()</code> on the local file tree.  This method will
     * go through the tree paths and expand the nodes.  Note that the nodes are lazily computed, so
     * this method triggers that lazy access.
     * @param currentPaths <code>Enumeration<TreePath></code> with the previously expanded nodes
     * @throws IdropException
     */
    public void expandTreeNodesBasedOnListOfPreviouslyExpandedNodes(final Enumeration<TreePath> currentPaths) throws IdropException {

        log.info("expandTreeNodes()");

        if (currentPaths == null) {
            throw new IdropException("null currentPaths");
        }

        TreePath treePath = null;
        while (currentPaths.hasMoreElements()) {
            treePath = currentPaths.nextElement();
            log.debug("expanding treePath: {}", treePath);
            this.findNodeInTreeGivenATreePathAndExpand(treePath);
        }

    }

    /**
     * Given a treePath, find that path in the tree model.  In searching, the lazy loading
     * behavior of the child nodes is triggered and the tree is expanded to the node.
     * @param treePath <code>TreePath</code> that should be looked up in the tree.
     * @return {@link LocalFileNode} that is the treeNode at the given path.
     * @throws IdropException
     */
    private LocalFileNode findNodeInTreeGivenATreePathAndExpand(final TreePath treePath) throws IdropException {

        if (treePath == null) {
            throw new IdropException("treePath is null");
        }

        log.debug("findNodeInTreeGivenATreePath:{}", treePath);
        LocalFileNode currentTreeNode = (LocalFileNode) this.getModel().getRoot();

        TreePath intermediateTreePath = new TreePath(currentTreeNode);
        boolean rootNodeSkippedInPathElement = false;

        // walk down the treeModel (which had been refreshed), and load and expand each path
        for (Object pathElement : treePath.getPath()) {
            if (!rootNodeSkippedInPathElement) {
                rootNodeSkippedInPathElement = true;
                continue;
            }

            currentTreeNode = matchTreePathToANodeAndExpandLazyChildren(currentTreeNode, pathElement);

            // if null is returned, this means I did not find a matching node, this is ignored
            if (currentTreeNode == null) {
                log.info("no matching node found for {}, stopping search for this tree path", pathElement);
                return null;
            } else {

                // found a node, expand the tree down to this node
                intermediateTreePath = intermediateTreePath.pathByAddingChild(currentTreeNode);
                log.debug("found a node, expanding down to:{}", intermediateTreePath);
                this.expandPath(intermediateTreePath);
            }
        }

        return currentTreeNode;

    }

    /**
     * Given a nodeThatWasDropTargetAsFile node in the tree, search the children for the given path
     * @param localFileNode {@link LocalFileNode} that is the nodeThatWasDropTargetAsFile node that should contain a child node
     * with the given path
     * @param pathElementIAmSearchingFor <code>Object</code> that is the <code>TreePath</code> of the child I am
     * searching for within the given nodeThatWasDropTargetAsFile.
     * @return {@link LocalFileNode} that is the matching child node, or null if no matching child node was discovered.
     * @throws IdropException
     */
    private LocalFileNode matchTreePathToANodeAndExpandLazyChildren(final LocalFileNode localFileNode, final Object pathElementIAmSearchingFor) throws IdropException {

        if (localFileNode == null) {
            throw new IdropException("localFileNode is null");
        }

        LocalFileNode matchedChildNode = null;

        // trigger loading of children so I can search
        localFileNode.lazyLoadOfChildrenOfThisNode();

        LocalFileNode childNode = null;
        Enumeration<LocalFileNode> childNodeEnumeration = localFileNode.children();

        while (childNodeEnumeration.hasMoreElements()) {
            childNode = childNodeEnumeration.nextElement();
            if (childNode.equals(pathElementIAmSearchingFor)) {
                log.debug("found a matching node:{}", childNode);
                matchedChildNode = childNode;
                break;
            }
        }

        // either I'm matched, or I didn't find the child (in which case null is returned).
        return matchedChildNode;

    }

    private void setUpTreeMenu() {
        this.thisTree = this;
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

            @Override
            public void actionPerformed(ActionEvent e) {

                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        log.info("adding new node");
                        LocalFileNode parentNode = (LocalFileNode) m_clickedPath.getLastPathComponent();
                        File parentFile = (File) parentNode.getUserObject();

                        NewLocalDirectoryDialog newLocalDirectoryDialog = new NewLocalDirectoryDialog(idropParentGui, true, parentFile.getAbsolutePath(), thisTree, parentNode);
                        newLocalDirectoryDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                        newLocalDirectoryDialog.setVisible(true);

                    }
                });
                //  thisTree.repaint();
            }
        };

        m_popup.add(newAction);

        m_popup.addSeparator();

        Action a1 = new AbstractAction("Delete") {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("deleting local node node");
                LocalFileNode parentNode = (LocalFileNode) m_clickedPath.getLastPathComponent();
                File parentFile = (File) parentNode.getUserObject();

                DeleteLocalFileDialog deleteLocalFileDialog = new DeleteLocalFileDialog(idropParentGui, true, parentFile.getAbsolutePath(), thisTree, parentNode);
                deleteLocalFileDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                deleteLocalFileDialog.setVisible(true);

            }
        };
        m_popup.add(a1);


        Action a2 = new AbstractAction("Rename") {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("renaming node");

                LocalFileNode parentNode = (LocalFileNode) m_clickedPath.getLastPathComponent();
                File parentFile = (File) parentNode.getUserObject();

                RenameLocalDirectoryDialog renameLocalDirectoryDialog = new RenameLocalDirectoryDialog(idropParentGui, true, parentFile.getAbsolutePath(), thisTree, parentNode);
                renameLocalDirectoryDialog.setLocation((int) (idropParentGui.getLocation().getX() + idropParentGui.getWidth() / 2), (int) (idropParentGui.getLocation().getY() + idropParentGui.getHeight() / 2));
                renameLocalDirectoryDialog.setVisible(true);

            }
        };
        m_popup.add(a2);
        thisTree.add(m_popup);
        thisTree.addMouseListener(new PopupTrigger());
        thisTree.addTreeWillExpandListener(thisTree);

    }

    /**
     * Tree expansion is used to lazily load children of the selected nodeThatWasDropTargetAsFile
     * @param event
     * @throws ExpandVetoException
     */
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        log.debug("tree expansion event:{}", event);
        LocalFileNode expandingNode = (LocalFileNode) event.getPath().getLastPathComponent();
        expandingNode.lazyLoadOfChildrenOfThisNode();
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
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
}
