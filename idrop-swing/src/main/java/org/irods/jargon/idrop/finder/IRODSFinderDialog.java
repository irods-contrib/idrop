/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IRODSFinderDialog.java
 *
 * Created on Jul 16, 2011, 1:58:04 PM
 */
package org.irods.jargon.idrop.finder;

import javax.swing.ListSelectionModel;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.MessageManager;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSRowModel;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mikeconway
 */
public class IRODSFinderDialog extends javax.swing.JDialog {
    
    public static enum SelectionType {
        OBJS_ONLY_SELECTION_MODE,
        COLLS_ONLY_SELECTION_MODE,
        OBJS_AND_COLLS_SELECTION_MODE
    }
    
    private SelectionType selectionTypeSetting = SelectionType.COLLS_ONLY_SELECTION_MODE;

    private final IDROPCore idropCore;
    private String selectedAbsolutePath = null;

    public String getSelectedAbsolutePath() {
        return selectedAbsolutePath;
    }

    public IDROPCore getIdropCore() {
        return idropCore;
    }

    public IRODSFinderTree getIrodsTree() {
        return irodsTree;
    }
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IRODSFinderDialog.class);
    private IRODSFinderTree irodsTree = null;

    /** Creates new form IRODSFinderDialog */
    public IRODSFinderDialog(java.awt.Frame parent, boolean modal, IDROPCore idropCore) {
        super(parent, modal);

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        this.idropCore = idropCore;
        initComponents();
        buildTargetTree();
    }
    
    public void enableButtonSelectFolder(boolean state) {
        this.btnSelectFolder.setEnabled(state);
    }
      
    public void setSelectionType(SelectionType selType) {
        this.selectionTypeSetting = selType;
    }

    /**
     * build the JTree that will depict the iRODS resource
     */
    public void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final IRODSFinderDialog gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {


                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();

                if (idropCore.getIdropConfig().isLoginPreset()) {
                    log.info("using policy preset home directory");
                    StringBuilder sb = new StringBuilder();
                    sb.append("/");
                    sb.append(idropCore.getIrodsAccount().getZone());
                    sb.append("/");
                    sb.append("home");
                    root.setParentPath(sb.toString());
                    root.setPathOrName(idropCore.getIrodsAccount().getHomeDirectory());
                } else {
                    log.info("using root path, no login preset");
                    root.setPathOrName("/");
                }

                root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);

                log.info("building new iRODS tree");
                try {
                    if (irodsTree == null) {
                        irodsTree = new IRODSFinderTree(gui);
                        IRODSNode rootNode = new IRODSNode(root,
                                idropCore.getIrodsAccount(), idropCore.getIrodsFileSystem(), irodsTree);
                        irodsTree.setRefreshingTree(true);
                    }
                    IRODSNode rootNode = new IRODSNode(root, idropCore.getIrodsAccount(),
                            idropCore.getIrodsFileSystem(), irodsTree);

                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                            rootNode, idropCore.getIrodsAccount());
                    IRODSFinderOutlineModel mdl = new IRODSFinderOutlineModel(idropCore, irodsTree,
                            irodsFileSystemModel, new IRODSRowModel(), true,
                            "File System");
                    irodsTree.setModel(mdl);


                } catch (Exception ex) {
                    log.error("exception building finder tree", ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    idropCore.getIrodsFileSystem().closeAndEatExceptions(
                            idropCore.getIrodsAccount());
                }

                scrollIrodsTree.setViewportView(irodsTree);
                scrollIrodsTree.validate();
                gui.validate();

                irodsTree.setRefreshingTree(false);

            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        pnlIrodsTreeToolbar = new javax.swing.JPanel();
        btnRefreshTargetTree = new javax.swing.JButton();
        pnlIrodsTreeMaster = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();
        bottomPanel = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnSelectFolder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        org.jdesktop.layout.GroupLayout topPanelLayout = new org.jdesktop.layout.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 593, Short.MAX_VALUE)
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 484, Short.MAX_VALUE)
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.CENTER);

        treePanel.setLayout(new java.awt.BorderLayout());

        btnRefreshTargetTree.setMnemonic('r');
        btnRefreshTargetTree.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRefreshTargetTree.text")); // NOI18N
        btnRefreshTargetTree.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRefreshTargetTree.toolTipText")); // NOI18N
        btnRefreshTargetTree.setFocusable(false);
        btnRefreshTargetTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshTargetTreeActionPerformed(evt);
            }
        });
        pnlIrodsTreeToolbar.add(btnRefreshTargetTree);

        treePanel.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlIrodsTreeMaster.setLayout(new java.awt.BorderLayout());

        scrollIrodsTree.setMinimumSize(null);
        scrollIrodsTree.setPreferredSize(null);
        pnlIrodsTreeMaster.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        treePanel.add(pnlIrodsTreeMaster, java.awt.BorderLayout.CENTER);

        getContentPane().add(treePanel, java.awt.BorderLayout.PAGE_START);

        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        bottomPanel.add(btnCancel);

        btnSelectFolder.setMnemonic('s');
        btnSelectFolder.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnSelectFolder.text")); // NOI18N
        btnSelectFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFolderActionPerformed(evt);
            }
        });
        bottomPanel.add(btnSelectFolder);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshTargetTreeActionPerformed
        buildTargetTree();
    }//GEN-LAST:event_btnRefreshTargetTreeActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.selectedAbsolutePath = null;
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectFolderActionPerformed

        IRODSFinderOutlineModel irodsFileSystemModel = (IRODSFinderOutlineModel) irodsTree.getModel();


        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idx = selectionModel.getLeadSelectionIndex();

        if (idx == -1) {
            MessageManager.showWarning(this, "Please select a directory", MessageManager.TITLE_MESSAGE);
            return;

        }

        // use first selection for info
        IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(
                idx, 0);
        log.info("selected node:{}", selectedNode);
        CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) selectedNode.getUserObject();
        if ( this.selectionTypeSetting == SelectionType.COLLS_ONLY_SELECTION_MODE) {
        if (entry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
            MessageManager.showWarning(this, "Please select a directory", MessageManager.TITLE_MESSAGE);
            return;
        }
        }

        this.selectedAbsolutePath = entry.getFormattedAbsolutePath();
        this.setVisible(false);
        
        enableButtonSelectFolder(true);

    }//GEN-LAST:event_btnSelectFolderActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRefreshTargetTree;
    private javax.swing.JButton btnSelectFolder;
    private javax.swing.JPanel pnlIrodsTreeMaster;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables
}
