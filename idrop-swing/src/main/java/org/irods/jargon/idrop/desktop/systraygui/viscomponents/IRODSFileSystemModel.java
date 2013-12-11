package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Model of an underlying file system for browsing in a tree view
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFileSystemModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4369718625614824989L;

	@Override
	public void removeNodeFromParent(final MutableTreeNode mtn) {
		super.removeNodeFromParent(mtn);
	}

	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(IRODSFileSystemModel.class);

	private static class TreeModelListener implements
			javax.swing.event.TreeModelListener {

		public TreeModelListener() {
		}

		@Override
		public void treeNodesChanged(final TreeModelEvent tme) {
		}

		@Override
		public void treeNodesInserted(final TreeModelEvent tme) {
		}

		@Override
		public void treeNodesRemoved(final TreeModelEvent tme) {
		}

		@Override
		public void treeStructureChanged(final TreeModelEvent tme) {
		}
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		triggerLazyLoading(parent);
		return super.getChild(parent, index);
	}

	@Override
	public int getChildCount(final Object parent) {
		triggerLazyLoading(parent);
		return super.getChildCount(parent);
	}

	private void triggerLazyLoading(final Object parent)
			throws IdropRuntimeException {
		// make sure children are loaded before counting
		IRODSNode parentAsNode = (IRODSNode) parent;
		try {
			parentAsNode.lazyLoadOfChildrenOfThisNode();
		} catch (IdropException ex) {
			Logger.getLogger(IRODSFileSystemModel.class.getName()).log(
					Level.SEVERE, null, ex);
			throw new IdropRuntimeException(ex);
		}
	}

	public IRODSFileSystemModel(final IRODSNode rootNode,
			final IRODSAccount irodsAccount) throws IdropException {
		super(rootNode);

		if (irodsAccount == null) {
			throw new IdropRuntimeException("null irodsAccount");
		}

		// pre-expand the child nodes of the root

		rootNode.lazyLoadOfChildrenOfThisNode();
		addTreeModelListener(new TreeModelListener() {
		});

	}

	public IRODSFileSystemModel(final IRODSAccount irodsAccount)
			throws IdropException {
		super(null);
		if (irodsAccount == null) {
			throw new IdropRuntimeException("null irodsAccount");
		}
		addTreeModelListener(new TreeModelListener() {
		});

	}
}
