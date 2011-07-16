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

import java.util.logging.Level;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.IRODSTreeContainingComponent;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSRowModel;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mikeconway
 */
public class IRODSFinderDialog extends javax.swing.JDialog {
    
    private final IDROPCore idropCore;
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

                log.info("building new iRODS tree");
                try {
                    if (irodsTree == null) {
                        irodsTree = new IRODSFinderTree(gui);
                        IRODSNode rootNode = new IRODSNode(root,
                                idropCore.getIrodsAccount(), idropCore.getIrodsFileSystem(), irodsTree);
                        irodsTree.setRefreshingTree(true);
                        // irodsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    }
                    IRODSNode rootNode = new IRODSNode(root, idropCore.getIrodsAccount(),
                            idropCore.getIrodsFileSystem(), irodsTree);

                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                            rootNode, idropCore.getIrodsAccount());
                    IRODSFinderOutlineModel mdl = new IRODSFinderOutlineModel(
                            irodsFileSystemModel, new IRODSRowModel(), true,
                            "File System");
                    irodsTree.setModel(mdl);

                    /*
                     * IrodsTreeListenerForBuildingInfoPanel treeListener = new
                     * IrodsTreeListenerForBuildingInfoPanel(gui);
                     * irodsTree.addTreeExpansionListener(treeListener);
                     * irodsTree.addTreeSelectionListener(treeListener); //
                     * preset to display root tree node
                     * irodsTree.setSelectionRow(0);
                     */
                } catch (Exception ex) {
                   log.error("exception building finder tree", ex);
                    throw new IdropRuntimeException(ex);
                }

                scrollIrodsTree.setViewportView(irodsTree);
                /*
                 * TreePath currentPath;
                 * 
                 * if (currentPaths != null) { while
                 * (currentPaths.hasMoreElements()) { currentPath = (TreePath)
                 * currentPaths.nextElement();
                 * log.debug("expanding tree path:{}", currentPath);
                 * irodsTree.expandPath(currentPath); } }
                 */
                irodsTree.setRefreshingTree(false);

                idropCore.getIrodsFileSystem().closeAndEatExceptions(
                        idropCore.getIrodsAccount());
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
        btnNewFolder = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSelectFolder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.layout.GroupLayout topPanelLayout = new org.jdesktop.layout.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 497, Short.MAX_VALUE)
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.CENTER);

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

        pnlIrodsTreeMaster.setLayout(new java.awt.BorderLayout());
        pnlIrodsTreeMaster.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        org.jdesktop.layout.GroupLayout treePanelLayout = new org.jdesktop.layout.GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);
        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 497, Short.MAX_VALUE)
            .add(treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(treePanelLayout.createSequentialGroup()
                    .add(0, 60, Short.MAX_VALUE)
                    .add(treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlIrodsTreeToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 377, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(pnlIrodsTreeMaster, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 377, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(0, 60, Short.MAX_VALUE)))
        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 527, Short.MAX_VALUE)
            .add(treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(treePanelLayout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(pnlIrodsTreeToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnlIrodsTreeMaster, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 488, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(treePanel, java.awt.BorderLayout.PAGE_START);

        btnNewFolder.setMnemonic('n');
        btnNewFolder.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnNewFolder.text")); // NOI18N
        bottomPanel.add(btnNewFolder);

        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnCancel.text")); // NOI18N
        bottomPanel.add(btnCancel);

        btnSelectFolder.setMnemonic('s');
        btnSelectFolder.setText(org.openide.util.NbBundle.getMessage(IRODSFinderDialog.class, "IRODSFinderDialog.btnSelectFolder.text")); // NOI18N
        bottomPanel.add(btnSelectFolder);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshTargetTreeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshTargetTreeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNewFolder;
    private javax.swing.JButton btnRefreshTargetTree;
    private javax.swing.JButton btnSelectFolder;
    private javax.swing.JPanel pnlIrodsTreeMaster;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables

}
