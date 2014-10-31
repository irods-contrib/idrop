/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
public class AdvancedOptionsDialog extends javax.swing.JDialog {
    
    private final IDROPCore idropCore;
    private final iDrop idropGui;

    /**
     * Creates new form AdvancedOptionsDialog
     */
    public AdvancedOptionsDialog(final iDrop parent,
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
        dataToolsConstraints.weightx = 0.1;
        dataToolsConstraints.weighty = 0.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpDataTools);
        pnlCollapsibles.add(sp, dataToolsConstraints);
        //pnlCollapsibles.add(new JScrollPane(cpDataTools), dataToolsConstraints); 
        
        setupPipelineConfigPanel();
        GridBagConstraints pipelineConstraints = new GridBagConstraints();
        pipelineConstraints.gridx = 0;
        pipelineConstraints.gridy = 1;
        pipelineConstraints.gridwidth = 1;
        pipelineConstraints.fill = GridBagConstraints.BOTH;
        pipelineConstraints.anchor = GridBagConstraints.NORTHWEST;
        pipelineConstraints.weightx = 0.1;
        pipelineConstraints.weighty = 0.1;
        CollapsiblePane cpPipelineConfig = new CollapsiblePane(pnlCollapsibles, "Pipeline Configuration", pnlPipelineConfig);
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpPipelineConfig);
        pnlCollapsibles.add(sp, pipelineConstraints);
        //pnlCollapsibles.add(new JScrollPane(cpPipelineConfig), pipelineConstraints);
        
    }
    
    private void setupToolsPanel() {
        HyperLinkButton btnPerformDiff = new HyperLinkButton("Perform diff between data objects");
        btnPerformDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerformDiffActionPerformed(evt);
            }
        });
        pnlDataTools.add(btnPerformDiff);
        pnlDataTools.add(jLabel4);
        pnlDataTools.add(filler1);
        HyperLinkButton btnSetupSync = new HyperLinkButton("Set up automatic synchronization between directories");
        btnSetupSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetupSyncActionPerformed(evt);
            }
        });
        pnlDataTools.add(btnSetupSync);
    }
    
    private void setupPipelineConfigPanel() {
        setupParallelTransferPanel();
        CollapsiblePane cpParallelTransfer = new CollapsiblePane(pnlPipelineConfig, "Parallel Transfer Options", pnlParallelTransfer);
        GridBagConstraints parallelTransferConstraints = new GridBagConstraints();
        parallelTransferConstraints.gridx = 0;
        parallelTransferConstraints.gridy = 1;
        parallelTransferConstraints.gridwidth = 2;
        parallelTransferConstraints.fill = GridBagConstraints.BOTH;
        parallelTransferConstraints.anchor = GridBagConstraints.NORTHWEST;
        parallelTransferConstraints.weightx = 0.1;
        parallelTransferConstraints.weighty = 0.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpParallelTransfer);
        pnlPipelineConfig.add(sp, parallelTransferConstraints);
        
        setupBufferOptionsPanel();
        CollapsiblePane cpBufferOptions = new CollapsiblePane(pnlPipelineConfig, "Buffer Options", pnlBufferOptions);
        GridBagConstraints bufferOptionsConstraints = new GridBagConstraints();
        bufferOptionsConstraints.gridx = 0;
        bufferOptionsConstraints.gridy = 2;
        bufferOptionsConstraints.gridwidth = 2;
        bufferOptionsConstraints.fill = GridBagConstraints.BOTH;
        bufferOptionsConstraints.anchor = GridBagConstraints.NORTHWEST;
        bufferOptionsConstraints.weightx = 0.1;
        bufferOptionsConstraints.weighty = 0.1;
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpBufferOptions);
        pnlPipelineConfig.add(sp, bufferOptionsConstraints);
        
    }
    
    private void setupParallelTransferPanel() {
        
    }
    
    private void setupBufferOptionsPanel() {
        
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
//                dispose();
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
//                        dispose();
                        return;
                }
        } else {
                MessageUtil.showError(this,
                                "An iRODS path needs to be selected to do a diff",
                                MessageUtil.ERROR_MESSAGE);
//                dispose();
                return;
        }

        final AdvancedOptionsDialog thisDialog = this;

        log.info("local path for diff:{}", localAbsPath);

        java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {

//                        thisDialog.dispose();
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
//                                thisDialog.dispose();
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
        pnlPipelineConfig = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        spnTimeout = new javax.swing.JSpinner();
        pnlParallelTransfer = new javax.swing.JPanel();
        chkAllowParallel = new javax.swing.JCheckBox();
        chkNIO = new javax.swing.JCheckBox();
        chkExecutorPool = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        spnParallelTimeout = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spnMaxThreads = new javax.swing.JSpinner();
        pnlBufferOptions = new javax.swing.JPanel();
        lblInternalInputBufferSize = new javax.swing.JLabel();
        txtInternalInputBufferSize = new javax.swing.JTextField();
        lblInternalOutputBufferSize = new javax.swing.JLabel();
        txtInternalOutputBufferSize = new javax.swing.JTextField();
        lblLocalFileInputBufferSize = new javax.swing.JLabel();
        txtLocalFileInputBufferSize = new javax.swing.JTextField();
        lblLocalFileOutputBufferSize = new javax.swing.JLabel();
        txtLocalFileOutputBufferSize = new javax.swing.JTextField();
        lblPutBufferSize = new javax.swing.JLabel();
        txtPutBufferSize = new javax.swing.JTextField();
        lblGetBufferSize = new javax.swing.JLabel();
        txtGetBufferSize = new javax.swing.JTextField();
        lblIputToOutputCopyBufferSize = new javax.swing.JLabel();
        txtInputToOutputCopyBufferSize = new javax.swing.JTextField();
        lblInternalCacheBufferSize = new javax.swing.JLabel();
        txtInternalCacheBufferSize = new javax.swing.JTextField();
        btnResetInputBuffer = new javax.swing.JButton();
        btnResetOutputBuffer = new javax.swing.JButton();
        btnResetInputBufferSize = new javax.swing.JButton();
        btnResetOutputBufferSize = new javax.swing.JButton();
        btnResetGetBufferSize = new javax.swing.JButton();
        btnResetPutBufferSize = new javax.swing.JButton();
        btnResetCopyBufferSize = new javax.swing.JButton();
        btnResetCacheBufferSize = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(32767, 5));
        pnlMain = new javax.swing.JPanel();
        pnlCollapsibles = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        bntSave = new javax.swing.JButton();

        pnlDataTools.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlDataTools.setLayout(new java.awt.GridLayout(4, 1));

        pnlPipelineConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlPipelineConfig.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlPipelineConfig.add(jLabel1, gridBagConstraints);

        spnTimeout.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.1;
        pnlPipelineConfig.add(spnTimeout, gridBagConstraints);

        pnlParallelTransfer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlParallelTransfer.setLayout(new java.awt.GridBagLayout());

        chkAllowParallel.setSelected(true);
        chkAllowParallel.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.chkAllowParallel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlParallelTransfer.add(chkAllowParallel, gridBagConstraints);

        chkNIO.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.chkNIO.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlParallelTransfer.add(chkNIO, gridBagConstraints);

        chkExecutorPool.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.chkExecutorPool.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        pnlParallelTransfer.add(chkExecutorPool, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(jLabel2, gridBagConstraints);

        spnParallelTimeout.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(spnParallelTimeout, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(jLabel3, gridBagConstraints);

        spnMaxThreads.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(spnMaxThreads, gridBagConstraints);

        pnlBufferOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlBufferOptions.setLayout(new java.awt.GridBagLayout());

        lblInternalInputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblInternalInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalInputBufferSize, gridBagConstraints);

        txtInternalInputBufferSize.setColumns(20);
        txtInternalInputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalInputBufferSize.text")); // NOI18N
        txtInternalInputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalInputBufferSize.toolTipText")); // NOI18N
        txtInternalInputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalInputBufferSize, gridBagConstraints);

        lblInternalOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblInternalOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalOutputBufferSize, gridBagConstraints);

        txtInternalOutputBufferSize.setColumns(20);
        txtInternalOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalOutputBufferSize.text")); // NOI18N
        txtInternalOutputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalOutputBufferSize.toolTipText")); // NOI18N
        txtInternalOutputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalOutputBufferSize, gridBagConstraints);

        lblLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblLocalFileInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblLocalFileInputBufferSize, gridBagConstraints);

        txtLocalFileInputBufferSize.setColumns(20);
        txtLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtLocalFileInputBufferSize.text")); // NOI18N
        txtLocalFileInputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtLocalFileInputBufferSize.toolTipText")); // NOI18N
        txtLocalFileInputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtLocalFileInputBufferSize, gridBagConstraints);

        lblLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblLocalFileOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblLocalFileOutputBufferSize, gridBagConstraints);

        txtLocalFileOutputBufferSize.setColumns(20);
        txtLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtLocalFileOutputBufferSize.text")); // NOI18N
        txtLocalFileOutputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtLocalFileOutputBufferSize.toolTipText")); // NOI18N
        txtLocalFileOutputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtLocalFileOutputBufferSize, gridBagConstraints);

        lblPutBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblPutBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblPutBufferSize, gridBagConstraints);

        txtPutBufferSize.setColumns(20);
        txtPutBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtPutBufferSize.text")); // NOI18N
        txtPutBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtPutBufferSize.toolTipText")); // NOI18N
        txtPutBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtPutBufferSize, gridBagConstraints);

        lblGetBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblGetBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblGetBufferSize, gridBagConstraints);

        txtGetBufferSize.setColumns(20);
        txtGetBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtGetBufferSize.text")); // NOI18N
        txtGetBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtGetBufferSize.toolTipText")); // NOI18N
        txtGetBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtGetBufferSize, gridBagConstraints);

        lblIputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblIputToOutputCopyBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblIputToOutputCopyBufferSize, gridBagConstraints);

        txtInputToOutputCopyBufferSize.setColumns(20);
        txtInputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInputToOutputCopyBufferSize.text")); // NOI18N
        txtInputToOutputCopyBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInputToOutputCopyBufferSize.toolTipText")); // NOI18N
        txtInputToOutputCopyBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInputToOutputCopyBufferSize, gridBagConstraints);

        lblInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.lblInternalCacheBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalCacheBufferSize, gridBagConstraints);

        txtInternalCacheBufferSize.setColumns(20);
        txtInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalCacheBufferSize.text")); // NOI18N
        txtInternalCacheBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.txtInternalCacheBufferSize.toolTipText")); // NOI18N
        txtInternalCacheBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalCacheBufferSize, gridBagConstraints);

        btnResetInputBuffer.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetInputBuffer.text")); // NOI18N
        btnResetInputBuffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetInputBufferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetInputBuffer, gridBagConstraints);

        btnResetOutputBuffer.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetOutputBuffer.text")); // NOI18N
        btnResetOutputBuffer.setToolTipText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetOutputBuffer.toolTipText")); // NOI18N
        btnResetOutputBuffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetOutputBufferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetOutputBuffer, gridBagConstraints);

        btnResetInputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetInputBufferSize.text")); // NOI18N
        btnResetInputBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetInputBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetInputBufferSize, gridBagConstraints);

        btnResetOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetOutputBufferSize.text")); // NOI18N
        btnResetOutputBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetOutputBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetOutputBufferSize, gridBagConstraints);

        btnResetGetBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetGetBufferSize.text")); // NOI18N
        btnResetGetBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetGetBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetGetBufferSize, gridBagConstraints);

        btnResetPutBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetPutBufferSize.text")); // NOI18N
        btnResetPutBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPutBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetPutBufferSize, gridBagConstraints);

        btnResetCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetCopyBufferSize.text")); // NOI18N
        btnResetCopyBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetCopyBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetCopyBufferSize, gridBagConstraints);

        btnResetCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnResetCacheBufferSize.text")); // NOI18N
        btnResetCacheBufferSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetCacheBufferSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pnlBufferOptions.add(btnResetCacheBufferSize, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.jLabel4.text")); // NOI18N
        jLabel4.setAutoscrolls(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(740, 400));

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlCollapsibles.setLayout(new java.awt.GridBagLayout());
        pnlMain.add(pnlCollapsibles, java.awt.BorderLayout.CENTER);

        pnlButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlButtons.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        pnlButtons.add(jPanel1, gridBagConstraints);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnCancel.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlButtons.add(btnCancel, gridBagConstraints);

        bntSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        bntSave.setText(org.openide.util.NbBundle.getMessage(AdvancedOptionsDialog.class, "AdvancedOptionsDialog.bntSave.text")); // NOI18N
        bntSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void bntSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bntSaveActionPerformed

    private void btnResetOutputBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetOutputBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetOutputBufferSizeActionPerformed

    private void btnResetInputBufferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetInputBufferActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetInputBufferActionPerformed

    private void btnResetOutputBufferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetOutputBufferActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetOutputBufferActionPerformed

    private void btnResetInputBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetInputBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetInputBufferSizeActionPerformed

    private void btnResetGetBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetGetBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetGetBufferSizeActionPerformed

    private void btnResetPutBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPutBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetPutBufferSizeActionPerformed

    private void btnResetCopyBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetCopyBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetCopyBufferSizeActionPerformed

    private void btnResetCacheBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetCacheBufferSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetCacheBufferSizeActionPerformed

    private void btnPerformDiffActionPerformed(java.awt.event.ActionEvent evt) {                                                
        performDiff();
    }                                               

    private void btnSetupSyncActionPerformed(java.awt.event.ActionEvent evt) {                                              
        SynchronizationDialog synchDialog = new SynchronizationDialog(this, true, this.idropCore);
        synchDialog.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(AdvancedOptionsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(AdvancedOptionsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(AdvancedOptionsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(AdvancedOptionsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                AdvancedOptionsDialog dialog = new AdvancedOptionsDialog(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntSave;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnResetCacheBufferSize;
    private javax.swing.JButton btnResetCopyBufferSize;
    private javax.swing.JButton btnResetGetBufferSize;
    private javax.swing.JButton btnResetInputBuffer;
    private javax.swing.JButton btnResetInputBufferSize;
    private javax.swing.JButton btnResetOutputBuffer;
    private javax.swing.JButton btnResetOutputBufferSize;
    private javax.swing.JButton btnResetPutBufferSize;
    private javax.swing.JCheckBox chkAllowParallel;
    private javax.swing.JCheckBox chkExecutorPool;
    private javax.swing.JCheckBox chkNIO;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblGetBufferSize;
    private javax.swing.JLabel lblInternalCacheBufferSize;
    private javax.swing.JLabel lblInternalInputBufferSize;
    private javax.swing.JLabel lblInternalOutputBufferSize;
    private javax.swing.JLabel lblIputToOutputCopyBufferSize;
    private javax.swing.JLabel lblLocalFileInputBufferSize;
    private javax.swing.JLabel lblLocalFileOutputBufferSize;
    private javax.swing.JLabel lblPutBufferSize;
    private javax.swing.JPanel pnlBufferOptions;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCollapsibles;
    private javax.swing.JPanel pnlDataTools;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlParallelTransfer;
    private javax.swing.JPanel pnlPipelineConfig;
    private javax.swing.JSpinner spnMaxThreads;
    private javax.swing.JSpinner spnParallelTimeout;
    private javax.swing.JSpinner spnTimeout;
    private javax.swing.JTextField txtGetBufferSize;
    private javax.swing.JTextField txtInputToOutputCopyBufferSize;
    private javax.swing.JTextField txtInternalCacheBufferSize;
    private javax.swing.JTextField txtInternalInputBufferSize;
    private javax.swing.JTextField txtInternalOutputBufferSize;
    private javax.swing.JTextField txtLocalFileInputBufferSize;
    private javax.swing.JTextField txtLocalFileOutputBufferSize;
    private javax.swing.JTextField txtPutBufferSize;
    // End of variables declaration//GEN-END:variables
}
