package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatus.TransferState;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.slf4j.LoggerFactory;

/**
 * (NEW) Implementation of the DefaultTreeModel for the local file system.
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LocalFileSystemModel extends DefaultTreeModel {

    /**
     *
     */
    private static final long serialVersionUID = 8353168431307770952L;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(LocalFileSystemModel.class);

    public LocalFileSystemModel(final DefaultMutableTreeNode node) {
        super(node);
        // pre-expand the child nodes of the root
        LocalFileNode localFileNode = (LocalFileNode) node;
        localFileNode.lazyLoadOfChildrenOfThisNode();
    }

    public void notifyFileShouldBeAdded(final LocalFileTree fileTree,
            final String newFileAbsolutePath) throws IdropException {
        TreePath parentNodePath = TreeUtils.buildTreePathForLocalAbsolutePath(
                fileTree, newFileAbsolutePath);
        log.debug("tree path for put: {}", parentNodePath);
        LocalFileNode targetNode = (LocalFileNode) parentNodePath
                .getLastPathComponent();
        File entry = (File) targetNode.getUserObject();
        if (entry.isFile()) {
            log.info("substitute parent as target, as given node was a leaf");
            targetNode = (LocalFileNode) targetNode.getParent();
        }
        targetNode.forceReloadOfChildrenOfThisNode();
        targetNode.lazyLoadOfChildrenOfThisNode();
        this.reload(targetNode);
        if (entry.isFile()) {
            parentNodePath = TreeUtils.buildTreePathForLocalAbsolutePath(
                    fileTree, entry.getParent());
            fileTree.highlightPath(parentNodePath);
        } else {
            fileTree.highlightPath(parentNodePath);

        }

    }

    public void notifyCompletionOfOperation(final LocalFileTree fileTree,
            final TransferStatus transferStatus) throws IdropException {
        log.info("tree model notified of status:{}", transferStatus);

        if (transferStatus.getTransferState() != TransferState.OVERALL_COMPLETION) {
            return;
        }

        // for put or copy operation, highlight the new node
        if (transferStatus.getTransferType() == TransferStatus.TransferType.GET) {
            log.info("successful get transfer, find the parent tree node, and clear the children");

            TreePath parentNodePath = TreeUtils
                    .buildTreePathForLocalAbsolutePath(fileTree,
                            transferStatus.getTargetFileAbsolutePath());
            log.debug("tree path for put: {}", parentNodePath);
            LocalFileNode targetNode = (LocalFileNode) parentNodePath
                    .getLastPathComponent();
            File entry = (File) targetNode.getUserObject();
            if (entry.isFile()) {
                log.info("substitute parent as target, as given node was a leaf");
                targetNode = (LocalFileNode) targetNode.getParent();
            }
            targetNode.forceReloadOfChildrenOfThisNode();
            targetNode.lazyLoadOfChildrenOfThisNode();
            this.reload(targetNode);
            if (entry.isFile()) {
                parentNodePath = TreeUtils.buildTreePathForLocalAbsolutePath(
                        fileTree, entry.getParent());
                fileTree.highlightPath(parentNodePath);
            } else {
                fileTree.highlightPath(parentNodePath);

            }

        }
    }
}
