package org.irods.jargon.idrop.desktop.systraygui.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileSystemModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.TreePathSupport;
import org.slf4j.LoggerFactory;

/**
 * This is a set of utilities for manipulating a swing Jtree
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class TreeUtils {

    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(TreeUtils.class);

    /**
     * Given a <code>TreeNode</code> get the corresponding <code>TreePath</code>
     *
     * @param treeNode
     * @return
     */
    public static TreePath getPath(TreeNode treeNode) {
        List<Object> nodes = new ArrayList<Object>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    public static IRODSNode findChild(final IRODSNode parent,
            final String userObject) throws IdropException {
        log.debug("finding child of parent:{}", parent);
        log.debug("user object:{}", userObject);
        parent.getUserObject();
        CollectionAndDataObjectListingEntry childEntry = null;

        IRODSNode foundNode = null;
        try {
            parent.lazyLoadOfChildrenOfThisNode();
        } catch (IdropException ex) {
            Logger.getLogger(TreeUtils.class.getName()).log(Level.SEVERE, null,
                    ex);
            throw new IdropException("unable to load children of node");
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            childEntry = (CollectionAndDataObjectListingEntry) ((IRODSNode) parent
                    .getChildAt(i)).getUserObject();

            if (childEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
                log.debug("child entry is a collection");
                if (userObject.equals(childEntry.getPathOrName())) {
                    foundNode = (IRODSNode) parent.getChildAt(i);
                    break;
                }
            } else {
                log.debug("child entry is a data object");
                StringBuilder sb = new StringBuilder();
                sb.append(childEntry.getParentPath());
                sb.append('/');
                sb.append(childEntry.getPathOrName());
                log.debug(
                        "looking for match when child entry is a file with abs path:{}",
                        sb.toString());
                if (userObject.equals(sb.toString())) {
                    foundNode = (IRODSNode) parent.getChildAt(i);
                    break;
                }
            }
        }
        return foundNode;
    }

    public static LocalFileNode findChild(final LocalFileNode parent,
            final String userObject) throws IdropException {
        log.debug("finding child of parent:{}", parent);
        log.debug("user object:{}", userObject);
        parent.getUserObject();
        File childEntry = null;

        LocalFileNode foundNode = null;

        parent.lazyLoadOfChildrenOfThisNode();

        String normalizedPath = null;

        for (int i = 0; i < parent.getChildCount(); i++) {
            childEntry = (File) ((LocalFileNode) parent.getChildAt(i))
                    .getUserObject();

            normalizedPath = childEntry.getAbsolutePath().replace('\\', '/');

            if (normalizedPath.length() >= 2) {
                if (normalizedPath.charAt(1) == ':') {
                    normalizedPath = normalizedPath.substring(2);
                }
            }

            if (childEntry.isDirectory()) {
                log.debug("child entry is a collection");
                if (userObject.equals(normalizedPath)) {
                    foundNode = (LocalFileNode) parent.getChildAt(i);
                    break;
                }
            } else {
                log.debug("child entry is a data object");

                log.debug(
                        "looking for match when child entry is a file with abs path:{}",
                        childEntry.getAbsolutePath());
                if (userObject.equals(normalizedPath)) {
                    foundNode = (LocalFileNode) parent.getChildAt(i);
                    break;
                }
            }
        }
        return foundNode;
    }

    public static TreePath buildTreePathForLocalAbsolutePath(final JTree tree,
            final String absolutePath) throws IdropException {
        LocalFileSystemModel fileSystemModel = (LocalFileSystemModel) tree
                .getModel();
        LocalFileNode localNode = (LocalFileNode) fileSystemModel.getRoot();
        TreePath calculatedTreePath = new TreePath(localNode);
        localNode.getUserObject();

        String normalizedPath = absolutePath.replace('\\', '/');

        String[] pathComponents = normalizedPath.split("/");

        StringBuilder searchRoot = new StringBuilder();
        LocalFileNode currentNode = (LocalFileNode) fileSystemModel.getRoot();
        File entry = (File) currentNode.getUserObject();
        searchRoot.append(entry.getName());
        if (searchRoot.length() == 0) {
            searchRoot.append("/");
        }

        String nextPathComponent;

        for (int i = 0; i < pathComponents.length; i++) {

            nextPathComponent = pathComponents[i];

            /*
             * In windows, the drive letter is the first part of the path, so if
             * the first path component is length 2 and the second char is ':',
             * then it will be ignored
             */
            if (i == 0 && nextPathComponent.length() == 2
                    && nextPathComponent.charAt(1) == ':') {
                log.debug("skipping drive in path");
                continue;
            }

			// next element from userObjects is the child of the current node,
            // note that for the first node (typically
            // '/') a delimiting slash is not needed
            if (searchRoot.length() > 1) {
                searchRoot.append('/');
            }

            searchRoot.append(nextPathComponent);
            if (i > 0) {
                currentNode = findChild(currentNode, searchRoot.toString());
            }

            if (currentNode == null) {
                log.warn(
                        "cannot find node for path, will attempt to return parent {}:",
                        searchRoot.toString());
                break;
            } else {
                // root node is already part of the calculcated tree path
                if (currentNode.getUserObject().toString().equals("/")) {
                    // ignore this node
                } else {
                    calculatedTreePath = calculatedTreePath
                            .pathByAddingChild(currentNode);
                }
            }
        }
        if (calculatedTreePath == null) {
            throw new IdropException("cannot find path to node:"
                    + normalizedPath);
        }
        return calculatedTreePath;

    }

    /**
     * Given an absolute path to a file from the iRODS view, build the
     * corresponding <code>TreePath</code> that points to the position in the
     * tree model.
     *
     * @param tree <code>JTree</code> that depicts the iRODS file hierarchy.
     * @param irodsAbsolutePath <code>String</code> that gives the absolute path
     * to the iRODS file.
     * @return <code>TreePath</code> to the given node at the given absolute
     * path in iRODS.
     * @throws IdropException
     */
    public static TreePath buildTreePathForIrodsAbsolutePath(final JTree tree,
            final String irodsAbsolutePath) throws IdropException {

        IRODSFileSystemModel irodsFileSystemModel = (IRODSFileSystemModel) tree
                .getModel();
		// the root of the model, which may not be a path underneath the root of
        // the irods resource
        IRODSNode rootNode = (IRODSNode) irodsFileSystemModel.getRoot();
        TreePath calculatedTreePath = new TreePath(rootNode);
        CollectionAndDataObjectListingEntry rootEntry = (CollectionAndDataObjectListingEntry) rootNode
                .getUserObject();
        String[] irodsPathComponents = irodsAbsolutePath.split("/");

        /*
         * get an array that has the path components that descend from the root
         * of the iRODS file system to the subdirectory which the tree model
         * considers the root of the tree
         */
        String[] irodsRootNodePathComponents = rootEntry.getPathOrName().split(
                "/");

        /*
         * determine the relative calculatedTreePath of the given iRODS file
         * underneath the root. There are cases where the root is not '/'.
         */
        StringBuilder searchRoot = new StringBuilder();
        IRODSNode currentNode = (IRODSNode) irodsFileSystemModel.getRoot();
        CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) currentNode
                .getUserObject();
        searchRoot.append(entry.getPathOrName());

        /*
         * calculatedTreePath now holds the path from the root of iRODS to the
         * root of the tree, now accumulate any TreePath entries that represent
         * the path below the root of the tree contained in the absolute path.
         * The relative path starts at the path component in the position after
         * the length of the root path.
         */
        int relativePathStartsAfter = irodsRootNodePathComponents.length - 1;
        String nextPathComponent;

        for (int i = (relativePathStartsAfter + 1); i < irodsPathComponents.length; i++) {
			// next element from userObjects is the child of the current node,
            // note that for the first node (typically
            // '/') a delimiting slash is not needed
            if (searchRoot.length() > 1) {
                searchRoot.append('/');
            }

            nextPathComponent = irodsPathComponents[i];
            searchRoot.append(nextPathComponent);
            if (i > 0) {
                currentNode = findChild(currentNode, searchRoot.toString());
            }

            if (currentNode == null) {
                log.warn(
                        "cannot find node for path, will attempt to return parent {}:",
                        searchRoot.toString());
                break;
            } else {
                // root node is already part of the calculcated tree path
                if (currentNode.getUserObject().toString().equals("/")) {
                    // ignore this node
                } else {
                    calculatedTreePath = calculatedTreePath
                            .pathByAddingChild(currentNode);
                }
            }
        }
        if (calculatedTreePath == null) {
            throw new IdropException("cannot find path to node:"
                    + irodsAbsolutePath);
        }
        return calculatedTreePath;
    }

    public static TreePath buildTreePathForIrodsAbsolutePath(
            final Outline tree, final String irodsAbsolutePath)
            throws IdropException {

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) tree
                .getModel();
		// the root of the model, which may not be a path underneath the root of
        // the irods resource
        IRODSNode rootNode = (IRODSNode) irodsFileSystemModel.getRoot();
        TreePath calculatedTreePath = new TreePath(rootNode);
        CollectionAndDataObjectListingEntry rootEntry = (CollectionAndDataObjectListingEntry) rootNode
                .getUserObject();
        String[] irodsPathComponents = irodsAbsolutePath.split("/");

        /*
         * get an array that has the path components that descend from the root
         * of the iRODS file system to the subdirectory which the tree model
         * considers the root of the tree
         */
        String[] irodsRootNodePathComponents = rootEntry.getPathOrName().split(
                "/");

        /*
         * determine the relative calculatedTreePath of the given iRODS file
         * underneath the root. There are cases where the root is not '/'.
         */
        StringBuilder searchRoot = new StringBuilder();
        IRODSNode currentNode = (IRODSNode) irodsFileSystemModel.getRoot();
        CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) currentNode
                .getUserObject();
        searchRoot.append(entry.getPathOrName());

        /*
         * calculatedTreePath now holds the path from the root of iRODS to the
         * root of the tree, now accumulate any TreePath entries that represent
         * the path below the root of the tree contained in the absolute path.
         * The relative path starts at the path component in the position after
         * the length of the root path.
         */
        int relativePathStartsAfter = irodsRootNodePathComponents.length - 1;
        String nextPathComponent;

        for (int i = (relativePathStartsAfter + 1); i < irodsPathComponents.length; i++) {
			// next element from userObjects is the child of the current node,
            // note that for the first node (typically
            // '/') a delimiting slash is not needed
            if (searchRoot.length() > 1) {
                searchRoot.append('/');
            }

            nextPathComponent = irodsPathComponents[i];
            searchRoot.append(nextPathComponent);
            if (i > 0) {
                currentNode = findChild(currentNode, searchRoot.toString());
            }

            if (currentNode == null) {
                log.warn(
                        "cannot find node for path, will attempt to return parent {}:",
                        searchRoot.toString());
                break;
            } else {
                // root node is already part of the calculcated tree path
                if (currentNode.getUserObject().toString().equals("/")) {
                    // ignore this node
                } else {
                    calculatedTreePath = calculatedTreePath
                            .pathByAddingChild(currentNode);
                }
            }
        }
        if (calculatedTreePath == null) {
            throw new IdropException("cannot find path to node:"
                    + irodsAbsolutePath);
        }
        return calculatedTreePath;
    }

    public static void expandAll(final JTree tree, final TreePath parent,
            final boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public static void expandAll(final Outline tree, final TreePath parent,
            final boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    // FIXME: consider getting rid of defunct code below...
    /**
     * Given a tree node, get the nodes that are in the given expansion state as
     * a list of TreePath
     *
     * @param tree <code>JTree</code> that will be inspected
     * @param expanded <code>boolean</code> that indicates the desired state
     * that will be preserved in the tree paths
     * @return <code>TreePath[]</code> with the list of paths in the given state
     */
    public static TreePath[] getPaths(final JTree tree, final boolean expanded) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Create array to hold the treepaths
        List<TreePath> list = new ArrayList<TreePath>();

        // Traverse tree from root adding treepaths for all nodes to list
        getPaths(tree, new TreePath(root), expanded, list);

        // Convert list to array
        return list.toArray(new TreePath[list.size()]);
    }

    public static TreePath[] getPaths(final Outline tree, final boolean expanded) {

        TreeNode root = (TreeNode) tree.getOutlineModel().getRoot();
        log.debug("tree root:{}", root);
        TreePath rootPath = getPath(root);
        log.debug("root path:{}", rootPath);
        TreePathSupport treePathSupport = tree.getOutlineModel()
                .getTreePathSupport();

        // Create array to hold the treepaths
        List<TreePath> list = new ArrayList<TreePath>();

        // Traverse tree from root adding treepaths for all nodes to list
        getPaths(tree, new TreePath(root), expanded, list, treePathSupport);

        // Convert list to array
        return list.toArray(new TreePath[list.size()]);
    }

    private static void getPaths(final JTree tree, final TreePath parent,
            final boolean expanded, final List<TreePath> list) {
        // Return if node is not expanded
        if (expanded && !tree.isVisible(parent)) {
            return;
        }

        // Add node to list
        list.add(parent);

        // Create paths for all children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                getPaths(tree, path, expanded, list);
            }
        }
    }

    private static void getPaths(final Outline tree, final TreePath parent,
            final boolean expanded, final List<TreePath> list,
            final TreePathSupport treePathSupport) {
		// Return if node is not expanded
		/*
         * if (expanded && !tree.isVisible(parent)) { return; }
         */

        log.debug("getPaths for parent:{}", parent);

        // Create paths for all children
        IRODSNode node = (IRODSNode) parent.getLastPathComponent();
        if (treePathSupport.hasBeenExpanded(parent)) {
            // Add node to list
            log.info("path is expanded, adding to list and checking children");
            list.add(parent);

            if (!node.isCached()) {
                log.debug("node not cached, not expanded");
                return;
            }

            log.debug("iterating cached children of this node....");
            if (node.getChildCount() >= 0) {
                for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                    TreeNode n = (TreeNode) e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    getPaths(tree, path, expanded, list, treePathSupport);
                }
            }
        }
    }
}
