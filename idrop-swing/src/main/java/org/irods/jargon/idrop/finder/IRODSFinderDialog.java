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

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;
import org.irods.jargon.core.connection.IRODSAccount;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.utils.MiscIRODSUtils;
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

    /**
     *
     */
    private static final long serialVersionUID = -650660923688757395L;

    public static enum SelectionType {

        OBJS_ONLY_SELECTION_MODE, COLLS_ONLY_SELECTION_MODE, OBJS_AND_COLLS_SELECTION_MODE
    }

    private SelectionType selectionTypeSetting = SelectionType.COLLS_ONLY_SELECTION_MODE;

    private final IDROPCore idropCore;
    private String topOfTreeAbsolutePath = "/";

    private String selectedAbsolutePath = null;
    private List<String> selectedAbsolutePaths = null;
    private final IRODSAccount irodsAccount;

    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(IRODSFinderDialog.class);
    private IRODSFinderTree irodsTree = null;

    public String getSelectedAbsolutePath() {
        return selectedAbsolutePath;
    }

    public List<String> getSelectedAbsolutePaths() {
        return selectedAbsolutePaths;
    }

    public IDROPCore getIdropCore() {
        return idropCore;
    }

    public IRODSFinderTree getIrodsTree() {
        return irodsTree;
    }

    public void setIrodsTree(IRODSFinderTree irodsTree) {
        this.irodsTree = irodsTree;
    }

    /**
     *
     * @param parent
     * @param modal
     * @param idropCore
     * @param irodsAccount
     * @param topOfTreeAsolutePath
     */
    public IRODSFinderDialog(final java.awt.Frame parent, final boolean modal,
            final IDROPCore idropCore, final IRODSAccount irodsAccount, final String topOfTreeAsolutePath) {
        super(parent, modal);

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        if (irodsAccount == null) {
            throw new IllegalArgumentException("null irodsAccount");
        }

        if (topOfTreeAbsolutePath == null || topOfTreeAbsolutePath.isEmpty()) {
            throw new IllegalArgumentException("null or empty topOfTreeAbsolutePath");
        }

        this.topOfTreeAbsolutePath = topOfTreeAbsolutePath;
        this.idropCore = idropCore;
        initComponents();
        this.irodsAccount = irodsAccount;
        buildTargetTree();
    }

    /**
     * Creates new form IRODSFinderDialog
     */
    public IRODSFinderDialog(final java.awt.Frame parent, final boolean modal,
            final IDROPCore idropCore, final IRODSAccount irodsAccount) {
        super(parent, modal);

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        if (irodsAccount == null) {
            throw new IllegalArgumentException("null irodsAccount");
        }

        this.idropCore = idropCore;
        initComponents();
        this.irodsAccount = irodsAccount;
        String homeDir = MiscIRODSUtils.buildIRODSUserHomeForAccountUsingDefaultScheme(irodsAccount);
        setTopOfTreeAbsolutePath(homeDir);
        setIrodsTree(null);
        buildTargetTree();
    }

    public void enableButtonSelectFolder(final boolean state) {
        btnSelectFolder.setEnabled(state);
    }

    public void setSelectionType(final SelectionType selType) {
        selectionTypeSetting = selType;
    }

    /**
     * build the JTree that will depict the iRODS resource
     */
    private void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final IRODSFinderDialog gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();
                root.setPathOrName(gui.getTopOfTreeAbsolutePath());
                root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);

                log.info("building new iRODS tree");
                try {
                    if (irodsTree == null) {
                        irodsTree = new IRODSFinderTree(gui);
                        irodsTree.setName("irodsTree");
                        new IRODSNode(root, irodsAccount,
                                idropCore.getIrodsFileSystem(), irodsTree);
                        irodsTree.setRefreshingTree(true);
                    }
                    IRODSNode rootNode = new IRODSNode(root, idropCore
                            .irodsAccount(), idropCore.getIrodsFileSystem(),
                            irodsTree);

                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                            rootNode, irodsAccount);
                    IRODSFinderOutlineModel mdl = new IRODSFinderOutlineModel(
                            idropCore, irodsTree, irodsFileSystemModel,
                            new IRODSRowModel(), true, "File System");
                    irodsTree.setModel(mdl);

                } catch (Exception ex) {
                    log.error("exception building finder tree", ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    idropCore.getIrodsFileSystem().closeAndEatExceptions(
                            irodsAccount);
                }

                scrollIrodsTree.setViewportView(irodsTree);
                scrollIrodsTree.validate();
                gui.validate();

                irodsTree.setRefreshingTree(false);

            }
        });
    }

    private List<String> findSelectedPaths(
            final ListSelectionModel selectionModel) {
        List<String> paths = new ArrayList();

        for (int idx = selectionModel.getMinSelectionIndex(); idx <= selectionModel
                .getMaxSelectionIndex(); idx++) {

            if (selectionModel.isSelectedIndex(idx)) {
                IRODSFinderOutlineModel irodsFileSystemModel = (IRODSFinderOutlineModel) irodsTree
                        .getModel();
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                        .getValueAt(idx, 0);
                log.info("selected node:{}", selectedNode);
                CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) selectedNode
                        .getUserObject();
                paths.add(entry.getFormattedAbsolutePath());
            }
        }

        return paths;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        pnlIrodsTreeToolbar = new javax.swing.JPanel();
        btnRefreshTargetTree = new javax.swing.JButton();
        btnGoHomeTargetTree = new javax.swing.JButton();
        btnGoRootTargetTree = new javax.swing.JButton();
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

        pnlIrodsTreeToolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnRefreshTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_081_refresh.png"))); // NOI18N
        btnRefreshTargetTree.setMnemonic('r');
        btnRefreshTargetTree.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRefresh.text")); // NOI18N
        btnRefreshTargetTree.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRefresh.toolTipText")); // NOI18N
        btnRefreshTargetTree.setFocusable(false);
        btnRefreshTargetTree.setName("btnRefresh"); // NOI18N
        btnRefreshTargetTree.setPreferredSize(new java.awt.Dimension(100, 35));
        btnRefreshTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshTargetTreeActionPerformed(evt);
            }
        });
        pnlIrodsTreeToolbar.add(btnRefreshTargetTree);

        btnGoHomeTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_020_home.png"))); // NOI18N
        btnGoHomeTargetTree.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnHome.text")); // NOI18N
        btnGoHomeTargetTree.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnHome.toolTipText")); // NOI18N
        btnGoHomeTargetTree.setActionCommand(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnHome.actionCommand")); // NOI18N
        btnGoHomeTargetTree.setBorder(null);
        btnGoHomeTargetTree.setFocusable(false);
        btnGoHomeTargetTree.setName("btnHome"); // NOI18N
        btnGoHomeTargetTree.setPreferredSize(new java.awt.Dimension(100, 35));
        btnGoHomeTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoHomeTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoHomeTargetTreeActionPerformed(evt);
            }
        });
        pnlIrodsTreeToolbar.add(btnGoHomeTargetTree);

        btnGoRootTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_213_up_arrow.png"))); // NOI18N
        btnGoRootTargetTree.setMnemonic('r');
        btnGoRootTargetTree.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRoot.text")); // NOI18N
        btnGoRootTargetTree.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnRoot.toolTipText")); // NOI18N
        btnGoRootTargetTree.setBorder(null);
        btnGoRootTargetTree.setFocusable(false);
        btnGoRootTargetTree.setName("btnRoot"); // NOI18N
        btnGoRootTargetTree.setPreferredSize(new java.awt.Dimension(100, 35));
        btnGoRootTargetTree.setRolloverEnabled(false);
        btnGoRootTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoRootTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoRootTargetTreeActionPerformed(evt);
            }
        });
        pnlIrodsTreeToolbar.add(btnGoRootTargetTree);

        treePanel.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlIrodsTreeMaster.setLayout(new java.awt.BorderLayout());

        scrollIrodsTree.setMinimumSize(null);
        scrollIrodsTree.setPreferredSize(null);
        pnlIrodsTreeMaster.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        treePanel.add(pnlIrodsTreeMaster, java.awt.BorderLayout.CENTER);

        getContentPane().add(treePanel, java.awt.BorderLayout.PAGE_START);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.setMaximumSize(new java.awt.Dimension(77, 35));
        btnCancel.setMinimumSize(new java.awt.Dimension(77, 35));
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(100, 35));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        bottomPanel.add(btnCancel);

        btnSelectFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnSelectFolder.setMnemonic('s');
        btnSelectFolder.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnSelect.text")); // NOI18N
        btnSelectFolder.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnSelect.toolTipText")); // NOI18N
        btnSelectFolder.setName("btnSelect"); // NOI18N
        btnSelectFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFolderActionPerformed(evt);
            }
        });
        bottomPanel.add(btnSelectFolder);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoHomeTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoHomeTargetTreeActionPerformed
        log.info("btnGoHomeTargetTreeActionPerformed");
        final IRODSFinderDialog gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                String homeDir = MiscIRODSUtils.buildIRODSUserHomeForAccountUsingDefaultScheme(gui.irodsAccount);
                gui.setTopOfTreeAbsolutePath(homeDir);
                gui.setIrodsTree(null);
                gui.buildTargetTree();

            }
        });
    }//GEN-LAST:event_btnGoHomeTargetTreeActionPerformed

    private void btnGoRootTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoRootTargetTreeActionPerformed
        log.info("btnGoRootTargetTreeActionPerformed");
        final IRODSFinderDialog gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                gui.setTopOfTreeAbsolutePath("/");
                gui.setIrodsTree(null);
                gui.buildTargetTree();

            }
        });    }//GEN-LAST:event_btnGoRootTargetTreeActionPerformed

    private void btnRefreshTargetTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshTargetTreeActionPerformed
        buildTargetTree();
    }// GEN-LAST:event_btnRefreshTargetTreeActionPerformed

    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
        selectedAbsolutePath = null;
        setVisible(false);
    }// GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectFolderActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSelectFolderActionPerformed

        IRODSFinderOutlineModel irodsFileSystemModel = (IRODSFinderOutlineModel) irodsTree
                .getModel();

        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idx = selectionModel.getLeadSelectionIndex();

        if (idx == -1) {
            MessageManager.showWarning(this, "Please select a directory",
                    MessageManager.TITLE_MESSAGE);
            return;

        }

        // use first selection for info
        IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(
                idx, 0);
        log.info("selected node:{}", selectedNode);
        CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) selectedNode
                .getUserObject();
        if (selectionTypeSetting == SelectionType.COLLS_ONLY_SELECTION_MODE) {
            if (entry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT) {
                MessageManager.showWarning(this, "Please select a directory",
                        MessageManager.TITLE_MESSAGE);
                return;
            }
        }

        selectedAbsolutePath = entry.getFormattedAbsolutePath();

        selectedAbsolutePaths = findSelectedPaths(selectionModel);
        setVisible(false);

        enableButtonSelectFolder(true);

    }// GEN-LAST:event_btnSelectFolderActionPerformed

    public String getTopOfTreeAbsolutePath() {
        return topOfTreeAbsolutePath;
    }

    public void setTopOfTreeAbsolutePath(String topOfTreeAbsolutePath) {
        this.topOfTreeAbsolutePath = topOfTreeAbsolutePath;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnGoHomeTargetTree;
    private javax.swing.JButton btnGoRootTargetTree;
    private javax.swing.JButton btnRefreshTargetTree;
    private javax.swing.JButton btnSelectFolder;
    private javax.swing.JPanel pnlIrodsTreeMaster;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables
}
