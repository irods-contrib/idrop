/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatus.TransferState;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.RowModel;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSOutlineModel extends DefaultOutlineModel {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(IRODSOutlineModel.class);

    private IDROPDesktop idrop;

    public IRODSOutlineModel(IDROPDesktop idrop, TreeModel tm, TableModel tm1, boolean bln, String string) {
        super(tm, tm1, bln, string);
        this.idrop = idrop;
    }

    public IRODSOutlineModel(IDROPDesktop idrop, TreeModel tm, RowModel rm, boolean bln, String string) {
        super(tm, rm, bln, string);
        this.idrop = idrop;
    }

    public void notifyFileShouldBeRemoved(final IRODSTree irodsTree, final String nodeAbsolutePath)
            throws IdropException {
    }

    public void notifyFileShouldBeRemoved(final IRODSNode deletedNode) throws IdropException {
        log.info("deleting node from parent:{}", deletedNode);
        final IRODSNode parent = (IRODSNode) deletedNode.getParent();

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                parent.remove(deletedNode);
                try {
                    IRODSTree stagingViewTree = idrop.getIrodsTree();
                    CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) ((IRODSNode) parent)
                            .getUserObject();
                    TreePath path = TreeUtils.buildTreePathForIrodsAbsolutePath(stagingViewTree,
                            entry.getFormattedAbsolutePath());
                    stagingViewTree.collapsePath(path);
                    stagingViewTree.expandPath(path);
                } catch (IdropException ex) {
                    Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                    idrop.showIdropException(ex);
                }
            }
        });
    }

    public void notifyCompletionOfOperation(final IRODSTree irodsTree, final TransferStatus transferStatus)
            throws IdropException {
        log.info("tree model notified of status:{}", transferStatus);

        if (transferStatus.getTransferState() != TransferState.OVERALL_COMPLETION) {
            return;
        }

        // for put or copy operation, highlight the new node
        if (transferStatus.getTransferType() == TransferStatus.TransferType.PUT
                || transferStatus.getTransferType() == TransferStatus.TransferType.COPY) {
            log.info("successful put transfer, find the parent tree node, and clear the children");
            final IRODSOutlineModel thisOutlineModel = this;

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreePath containingNodePath;
                    try {
                        containingNodePath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree,
                                transferStatus.getTargetFileAbsolutePath());
                    } catch (IdropException ex) {
                        Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                        throw new IdropRuntimeException("error building tree path", ex);
                    }
                    log.debug("tree path for put: {}", containingNodePath);
                    IRODSNode targetNode = (IRODSNode) containingNodePath.getLastPathComponent();
                    CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) targetNode
                            .getUserObject();
                    if (entry.isDataObject()) {
                        log.info("substitute parent as target, as given node was a leaf");
                        targetNode = (IRODSNode) targetNode.getParent();
                    }

                    /*
                     * if the node was cached, children were loaded, so add a new one, otherwise, the expand path in
                     * irodsTree.highlightPath() will cause the loading of the children
                     */
                    if (targetNode.isCached()) {
                        try {
                            IRODSFile addedFile = idrop.getiDropCore().getIRODSFileFactoryForLoggedInAccount()
                                    .instanceIRODSFile(transferStatus.getTargetFileAbsolutePath());
                            CollectionAndDataObjectListingEntry newEntry = new CollectionAndDataObjectListingEntry();
                            newEntry.setCreatedAt(new Date(addedFile.lastModified()));
                            newEntry.setDataSize(addedFile.length());
                            newEntry.setModifiedAt(new Date(addedFile.lastModified()));

                            if (addedFile.isDirectory()) {
                                newEntry.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                                newEntry.setParentPath(addedFile.getParent());
                                newEntry.setPathOrName(addedFile.getAbsolutePath());
                            } else {
                                newEntry.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT);
                                newEntry.setParentPath(addedFile.getParent());
                                newEntry.setPathOrName(addedFile.getName());
                            }

                            IRODSNode newNode = new IRODSNode(newEntry, idrop.getiDropCore().getIrodsAccount(), idrop
                                    .getiDropCore().getIrodsFileSystem(), irodsTree);
                            targetNode.add(newNode);

                        } catch (JargonException ex) {
                            Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                            throw new IdropRuntimeException(ex);
                        }

                    }

                    if (entry.isDataObject()) {
                        try {
                            containingNodePath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree,
                                    entry.getParentPath());
                        } catch (IdropException ex) {
                            Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                            throw new IdropRuntimeException("error building tree path", ex);

                        }
                    }

                    irodsTree.highlightPath(containingNodePath);
                }
            });

        }
    }

    public void notifyFileShouldBeAdded(final IRODSTree irodsTree, final String irodsFileAbsolutePath) {
        log.info("notifyFileShouldBeAdded() for node:{}", irodsFileAbsolutePath);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                IRODSFileFactory irodsFileFactory = idrop.getiDropCore().getIRODSFileFactoryForLoggedInAccount();
                try {
                    IRODSFile addedFile = irodsFileFactory.instanceIRODSFile(irodsFileAbsolutePath);
                    if (!addedFile.exists()) {
                        log.info("looking for file that was added, I don't find it, so just move on: {}",
                                irodsFileAbsolutePath);
                        return;
                    }
                    TreePath parentPath;
                    try {
                        parentPath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, addedFile.getParent());
                    } catch (IdropException ex) {
                        Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                        throw new IdropRuntimeException(ex);
                    }

                    if (parentPath == null) {
                        log.info("null path for lookup, just move on");
                        return;
                    }
                    log.info("building a new node");
                    CollectionAndDataObjectListingEntry newEntry = new CollectionAndDataObjectListingEntry();
                    newEntry.setCreatedAt(new Date(addedFile.lastModified()));
                    newEntry.setDataSize(addedFile.length());
                    newEntry.setModifiedAt(new Date(addedFile.lastModified()));

                    if (addedFile.isDirectory()) {
                        newEntry.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                        newEntry.setParentPath(addedFile.getParent());
                        newEntry.setPathOrName(addedFile.getAbsolutePath());
                    } else {
                        newEntry.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT);
                        newEntry.setParentPath(addedFile.getParent());
                        newEntry.setPathOrName(addedFile.getName());
                    }

                    IRODSNode newNode = new IRODSNode(newEntry, idrop.getiDropCore().getIrodsAccount(), idrop
                            .getiDropCore().getIrodsFileSystem(), irodsTree);
                    ((IRODSNode) parentPath.getLastPathComponent()).add(newNode);
                    irodsTree.highlightPath(parentPath);
                } catch (JargonException ex) {
                    Logger.getLogger(IRODSOutlineModel.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    idrop.getiDropCore().closeIRODSConnectionForLoggedInAccount();
                }
            }
        });
    }
}
