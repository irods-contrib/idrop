/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.lite;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.irods.jargon.core.connection.IRODSAccount;
import org.slf4j.LoggerFactory;

/**
 * Model of an underlying file system for browsing in a tree view
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFileSystemModel extends DefaultTreeModel {

    @Override
    public void removeNodeFromParent(MutableTreeNode mtn) {
        super.removeNodeFromParent(mtn);
    }

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

    public IRODSFileSystemModel(final IRODSAccount irodsAccount) throws IdropException {
        super(null);
        if (irodsAccount == null) {
            throw new IdropRuntimeException("null irodsAccount");
        }
        this.irodsAccount = irodsAccount;

        this.addTreeModelListener(new TreeModelListener() {
        });

    }

}
