/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.datautils.tree.DiffTreePostProcessor;
import org.irods.jargon.datautils.tree.FileTreeDiffUtility;
import org.irods.jargon.datautils.tree.FileTreeDiffUtilityImpl;
import org.irods.jargon.datautils.tree.FileTreeModel;
import static org.irods.jargon.idrop.desktop.systraygui.ToolsDialog.log;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.utils.MessageUtil;

import org.irods.jargon.idrop.desktop.systraygui.viscomponents.CollapsiblePane;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.DiffViewData;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.HyperLinkButton;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.exceptions.IdropException;

/**
 *
 * @author lisa
 */
public class Tools2Dialog extends javax.swing.JDialog {
    
    private final IDROPCore idropCore;
    private final iDrop idropGui;

    /**
     * Creates new form AdvancedOptionsDialog
     */
    public Tools2Dialog(final iDrop parent,
            final boolean modal, final IDROPCore idropCore) {
        super(parent, modal);
        this.idropCore = idropCore;
        this.idropGui = parent;
        initComponents();
       
        setupToolsPanel();
        CollapsiblePane cpDataTools = new CollapsiblePane(pnlCollapsibles, "Data Tools", pnlDataTools);
        GridBagConstraints dataToolsConstraints = new GridBagConstraints();
        dataToolsConstraints.gridx = 0;
        dataToolsConstraints.gridy = 0;
        dataToolsConstraints.gridwidth = 1;
        dataToolsConstraints.fill = GridBagConstraints.BOTH;
        dataToolsConstraints.anchor = GridBagConstraints.NORTHWEST;
        dataToolsConstraints.weightx = 1.0;
        dataToolsConstraints.weighty = 1.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpDataTools);
        pnlCollapsibles.add(sp, dataToolsConstraints);
        //pnlCollapsibles.add(new JScrollPane(cpDataTools), dataToolsConstraints); 
        
    }
    
    private void setupToolsPanel() {
        HyperLinkButton btnPerformDiff = new HyperLinkButton("Perform diff between data objects");
        btnPerformDiff.setName("btnPerformDiff");
        btnPerformDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerformDiffActionPerformed(evt);
            }
        });
        pnlDataTools.add(btnPerformDiff);
        pnlDataTools.add(jLabel4);
        pnlDataTools.add(filler1);
        HyperLinkButton btnSetupSync = new HyperLinkButton("Set up automatic synchronization between directories");
        btnSetupSync.setName("btnSetupSynch");
        btnSetupSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetupSyncActionPerformed(evt);
            }
        });
        pnlDataTools.add(btnSetupSync);
    }
    
    private void performDiff() {
        log.info("diff action performed");
        // look for selected local and iRODS files

        TreePath localPath = idropGui.getFileTree().getSelectionPath();
        if (localPath == null) {
                MessageUtil.showError(this,
                                "A local path needs to be selected to do a diff",
                                MessageUtil.ERROR_MESSAGE);
                return;
        }

        LocalFileNode selectedFileNode = (LocalFileNode) idropGui.getFileTree()
                        .getSelectionPath().getLastPathComponent();
        File targetPath = (File) selectedFileNode.getUserObject();
        final String localAbsPath = targetPath.getAbsolutePath();
        final File localFile = new File(localAbsPath);

        // look for iRODS absolute path for the right hand side of the diff

        IRODSFileService irodsFS;
        try {
                irodsFS = new IRODSFileService(idropGui.getiDropCore()
                                .irodsAccount(), idropGui.getiDropCore()
                                .getIrodsFileSystem());
        } catch (Exception ex) {

                log.error("cannot create irods file service", ex);
                MessageUtil.showError(this,
                                "Cannot create iRODS file Service, see exception log",
                                MessageUtil.ERROR_MESSAGE);
                return;
        }

        final String irodsAbsPath;
        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) idropGui
                        .getIrodsTree().getModel();
        ListSelectionModel selectionModel = idropGui.getIrodsTree()
                        .getSelectionModel();
        int idx = selectionModel.getLeadSelectionIndex();
        IRODSFile ifile;
        // make sure there is a selected node
        if (idx >= 0) {

                try {
                        IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                                        .getValueAt(idx, 0);
                        if (selectedNode == null) {
                                return;
                        }
                        ifile = irodsFS.getIRODSFileForPath(selectedNode.getFullPath());

                        // rule out "/" and choose parent if file is not a directory
                        String path = ifile.getAbsolutePath();
                        if (ifile.isFile()) {
                                path = ifile.getParent();
                        }
                        if ((path != null) && (!path.equals("/"))) {
                                irodsAbsPath = path;
                        } else {
                                irodsAbsPath = "/";
                        }
                        log.info("irods path for diff:{}", ifile.getAbsolutePath());
                } catch (IdropException ex) {
                        MessageUtil.showError(this, ex.getMessage(),
                                        MessageUtil.ERROR_MESSAGE);
                        return;
                }
        } else {
                MessageUtil.showError(this,
                                "An iRODS path needs to be selected to do a diff",
                                MessageUtil.ERROR_MESSAGE);
                return;
        }

        final Tools2Dialog thisDialog = this;

        log.info("local path for diff:{}", localAbsPath);

        java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {

                        idropGui.setCursor(Cursor
                                        .getPredefinedCursor(Cursor.WAIT_CURSOR));
                        FileTreeDiffUtility fileTreeDiffUtility = new FileTreeDiffUtilityImpl(
                                        idropGui.getiDropCore().irodsAccount(), idropGui
                                                        .getiDropCore().getIRODSAccessObjectFactory());
                        try {
                                FileTreeModel diffModel = fileTreeDiffUtility
                                                .generateDiffLocalToIRODS(localFile, irodsAbsPath,
                                                                0L, 0L);
                                DiffTreePostProcessor postProcessor = new DiffTreePostProcessor();
                                postProcessor.postProcessFileTreeModel(diffModel);

                                log.info("diffModel:{}", diffModel);
                                DiffViewData diffViewData = new DiffViewData();
                                diffViewData.setFileTreeModel(diffModel);
                                diffViewData.setIrodsAbsolutePath(irodsAbsPath);
                                diffViewData.setLocalAbsolutePath(localAbsPath);
                                DiffViewDialog diffViewDialog = new DiffViewDialog(
                                                thisDialog.idropGui, true, diffViewData);
                                diffViewDialog.setVisible(true);
                        } catch (JargonException ex) {
                                log.error("Error generating diff", ex);
                                MessageUtil.showError(
                                                thisDialog,
                                                "An error occurred generating the diff:\n"
                                                                + ex.getMessage(),
                                                MessageUtil.ERROR_MESSAGE);
                                return;
                        } finally {
                                idropGui.setCursor(Cursor
                                                .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        }
                }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlDataTools = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(32767, 5));
        pnlMain = new javax.swing.JPanel();
        pnlCollapsibles = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        bntSave = new javax.swing.JButton();

        pnlDataTools.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlDataTools.setLayout(new java.awt.GridLayout(4, 1));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(Tools2Dialog.class, "Tools2Dialog.jLabel4.text")); // NOI18N
        jLabel4.setAutoscrolls(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(Tools2Dialog.class, "Tools2Dialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(700, 200));

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlCollapsibles.setLayout(new java.awt.GridBagLayout());
        pnlMain.add(pnlCollapsibles, java.awt.BorderLayout.CENTER);

        pnlButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlButtons.setPreferredSize(new java.awt.Dimension(86, 50));
        pnlButtons.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        pnlButtons.add(jPanel1, gridBagConstraints);

        bntSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_388_exit.png"))); // NOI18N
        bntSave.setText(org.openide.util.NbBundle.getMessage(Tools2Dialog.class, "Tools2Dialog.bntSave.text")); // NOI18N
        bntSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bntSave.setPreferredSize(new java.awt.Dimension(66, 40));
        bntSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlButtons.add(bntSave, gridBagConstraints);

        pnlMain.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSaveActionPerformed
        dispose();
    }//GEN-LAST:event_bntSaveActionPerformed

    private void btnPerformDiffActionPerformed(java.awt.event.ActionEvent evt) {                                                
        DiffSelectDialog diffSelectDialog = new DiffSelectDialog(this, idropGui, true, this.idropCore);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (toolkit.getScreenSize().width - diffSelectDialog
                    .getWidth()) / 2;
        int y = (toolkit.getScreenSize().height - diffSelectDialog
                    .getHeight()) / 2;
        diffSelectDialog.setLocation(x, y);
        diffSelectDialog.setVisible(true);
    }                                               

    private void btnSetupSyncActionPerformed(java.awt.event.ActionEvent evt) {                                              
        SynchronizationDialogTwo synchDialog = new SynchronizationDialogTwo(this, true, this.idropCore);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (toolkit.getScreenSize().width - synchDialog
                    .getWidth()) / 2;
        int y = (toolkit.getScreenSize().height - synchDialog
                    .getHeight()) / 2;
        synchDialog.setLocation(x, y);
        synchDialog.setVisible(true);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntSave;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCollapsibles;
    private javax.swing.JPanel pnlDataTools;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}
