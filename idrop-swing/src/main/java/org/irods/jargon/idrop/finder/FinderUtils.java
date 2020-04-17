package org.irods.jargon.idrop.finder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.netbeans.swing.outline.Outline;
import org.slf4j.LoggerFactory;

/**
 * This is a set of utilities for manipulating a swing Jtree
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class FinderUtils {

    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(FinderUtils.class);

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
            Logger.getLogger(FinderUtils.class.getName()).log(Level.SEVERE,
                    null, ex);
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

    public static TreePath buildTreePathForIrodsAbsolutePath(
            final Outline tree, final String irodsAbsolutePath)
            throws IdropException {

        IRODSFinderOutlineModel irodsFileSystemModel = (IRODSFinderOutlineModel) tree
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

    public static TreePath[] getPaths(final Outline tree, final boolean expanded) {
        TreeNode root = (TreeNode) tree.getOutlineModel().getRoot();

        // Create array to hold the treepaths
        List list = new ArrayList();

        // Traverse tree from root adding treepaths for all nodes to list
        getPaths(tree, new TreePath(root), expanded, list);

        // Convert list to array
        return (TreePath[]) list.toArray(new TreePath[list.size()]);
    }

    private static void getPaths(final Outline tree, final TreePath parent,
            final boolean expanded, final List list) {
        // Return if node is not expanded
        if (expanded && !tree.isVisible(parent)) {
            return;
        }

        // Add node to list
        list.add(parent);

        // Create paths for all children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                getPaths(tree, path, expanded, list);
            }
        }
    }
}
