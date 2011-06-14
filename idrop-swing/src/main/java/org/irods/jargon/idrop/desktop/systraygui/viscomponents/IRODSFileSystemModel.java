package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatus.TransferState;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Model of an underlying file system for browsing in a tree view
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFileSystemModel extends DefaultTreeModel {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSFileSystemModel.class);

    private static class TreeModelListener implements javax.swing.event.TreeModelListener {

        public TreeModelListener() {
        }

        @Override
        public void treeNodesChanged(TreeModelEvent tme) {
        }

        @Override
        public void treeNodesInserted(TreeModelEvent tme) {
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent tme) {
        }

        @Override
        public void treeStructureChanged(TreeModelEvent tme) {
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        triggerLazyLoading(parent);
        return super.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        triggerLazyLoading(parent);
        return super.getChildCount(parent);
    }

    private void triggerLazyLoading(Object parent) throws IdropRuntimeException {
        // make sure children are loaded before counting
        IRODSNode parentAsNode = (IRODSNode) parent;
        try {
            parentAsNode.lazyLoadOfChildrenOfThisNode();
        } catch (IdropException ex) {
            Logger.getLogger(IRODSFileSystemModel.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException(ex);
        }
    }
    private final IRODSAccount irodsAccount;

    public IRODSFileSystemModel(final IRODSNode rootNode, final IRODSAccount irodsAccount) throws IdropException {
        super(rootNode);

        if (irodsAccount == null) {
            throw new IdropRuntimeException("null irodsAccount");
        }
        this.irodsAccount = irodsAccount;

        // pre-expand the child nodes of the root

        rootNode.lazyLoadOfChildrenOfThisNode();
        this.addTreeModelListener(new TreeModelListener() {
        });

    }

    public void notifyCompletionOfOperation(final IRODSTree irodsTree, final TransferStatus transferStatus) throws IdropException {
        log.info("tree model notified of status:{}", transferStatus);

        if (transferStatus.getTransferState() != TransferState.OVERALL_COMPLETION) {
            return;
        }
        
        // for put or copy operation, highlight the new node
        if (transferStatus.getTransferType() == TransferStatus.TransferType.PUT
                || transferStatus.getTransferType() == TransferStatus.TransferType.COPY) {
            log.info("successful put transfer, find the parent tree node, and clear the children");
             
            TreePath parentNodePath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, transferStatus.getTargetFileAbsolutePath());
            log.debug("tree path for put: {}", parentNodePath);
            IRODSNode targetNode = (IRODSNode) parentNodePath.getLastPathComponent();
            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) targetNode.getUserObject();
            if (entry.isDataObject()) {
                log.info("substitute parent as target, as given node was a leaf");
                targetNode = (IRODSNode) targetNode.getParent();
            }
            targetNode.forceReloadOfChildrenOfThisNode();
            targetNode.lazyLoadOfChildrenOfThisNode();
            this.reload(targetNode);
            if (entry.isDataObject()) {
                parentNodePath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, entry.getParentPath());
                irodsTree.highlightPath(parentNodePath);
            } else {
                irodsTree.highlightPath(parentNodePath);

            }

        }
    }
}
