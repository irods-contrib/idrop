/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.CollapsiblePane;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.HyperLinkButton;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class SettingsDialog extends javax.swing.JDialog {

    private final IDROPCore idropCore;
    private final iDrop idropGui;
    private final IRODSAccount irodsAcct;
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(SettingsDialog.class);

    /**
     * Creates new form SettingsDialog
     */
    public SettingsDialog(final iDrop parent,
            final boolean modal, final IDROPCore idropCore, IRODSAccount irodsAccount) {
        super(parent, modal);
        this.idropCore = idropCore;
        this.idropGui = parent;
        this.irodsAcct = irodsAccount;
        initComponents();

        setupUserSettingsPanel();
        CollapsiblePane cpUserSettings = new CollapsiblePane(pnlCollapsibles, "iDrop user settings", pnlUserSettings);
        cpUserSettings.setName("cpUserSettings");
        GridBagConstraints userSettingsConstraints = new GridBagConstraints();
        userSettingsConstraints.gridx = 0;
        userSettingsConstraints.gridy = 0;
        userSettingsConstraints.gridwidth = 1;
        userSettingsConstraints.fill = GridBagConstraints.BOTH;
        userSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
        userSettingsConstraints.weightx = 0.0;
        userSettingsConstraints.weighty = 0.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpUserSettings);
        pnlCollapsibles.add(sp, userSettingsConstraints);

        setupDataTransferSettingsPanel();
        GridBagConstraints dataTransferSettingsConstraints = new GridBagConstraints();
        dataTransferSettingsConstraints.gridx = 0;
        dataTransferSettingsConstraints.gridy = 1;
        dataTransferSettingsConstraints.gridwidth = 1;
        dataTransferSettingsConstraints.fill = GridBagConstraints.BOTH;
        dataTransferSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
        dataTransferSettingsConstraints.weightx = 0.0;
        dataTransferSettingsConstraints.weighty = 0.0;
        CollapsiblePane cpDataTransferSettings = new CollapsiblePane(pnlCollapsibles, "Data Transfer Settings", pnlDataTransferSettings);
        cpDataTransferSettings.setName("cpDataTransferSettings");
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpDataTransferSettings);
        pnlCollapsibles.add(sp, dataTransferSettingsConstraints);

        setupConnectionTestPanel();
        CollapsiblePane cpTestConnection = new CollapsiblePane(pnlCollapsibles, "Test Connection", pnlTestConnection);
        cpTestConnection.setName("cpTestConnection");
        GridBagConstraints testConnectionConstraints = new GridBagConstraints();
        testConnectionConstraints.gridx = 0;
        testConnectionConstraints.gridy = 2;
        testConnectionConstraints.gridwidth = 1;
        testConnectionConstraints.fill = GridBagConstraints.BOTH;
        testConnectionConstraints.anchor = GridBagConstraints.NORTHWEST;
        testConnectionConstraints.weightx = 0.0;
        testConnectionConstraints.weighty = 0.0;
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpTestConnection);
        pnlCollapsibles.add(sp, testConnectionConstraints);

        setupPipelineConfigPanel();
        GridBagConstraints pipelineConstraints = new GridBagConstraints();
        pipelineConstraints.gridx = 0;
        pipelineConstraints.gridy = 3;
        pipelineConstraints.gridwidth = 1;
        pipelineConstraints.fill = GridBagConstraints.BOTH;
        pipelineConstraints.anchor = GridBagConstraints.NORTHWEST;
        pipelineConstraints.weightx = 1.0;
        pipelineConstraints.weighty = 1.0;
        CollapsiblePane cpPipelineConfig = new CollapsiblePane(pnlCollapsibles, "Pipeline Configuration", pnlPipelineConfig);
        cpPipelineConfig.setName("cpPipelineConfig");
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpPipelineConfig);
        pnlCollapsibles.add(sp, pipelineConstraints);

        initWithConfigData();

    }

    private void setupUserSettingsPanel() {
        HyperLinkButton btnPerformDiff = new HyperLinkButton("Manage data resources");
        btnPerformDiff.setName("btnPerformDiff");
        btnPerformDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageDataResourcesActionPerformed(evt);
            }
        });
        pnlUserSettings.add(btnPerformDiff);
        //pnlUserSettings.add(filler1);
        chkShowGUI.setName("chkShowGUI");
        pnlUserSettings.add(chkShowGUI);
        pnlUserSettings.add(chkShowTransferProgress);
        chkShowTransferProgress.setName("chkShowTransferProgress");
    }

    private void setupConnectionTestPanel() {
        HyperLinkButton btnConnectionTest = new HyperLinkButton("Select link to test iDrop functionality and connection");
        btnConnectionTest.setName("btnConnectionTest");
        btnConnectionTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectionTestActionPerformed(evt);
            }
        });
        pnlTestConnection.add(btnConnectionTest);
    }

    private void setupDataTransferSettingsPanel() {
        spinMaxTransferAttempts.setValue(3);
        spinMaxTransferAttempts.setName("spinMaxTransferAttempts");
    }

    private void setupPipelineConfigPanel() {
//        setupParallelTransferPanel();
        CollapsiblePane cpParallelTransfer = new CollapsiblePane(pnlPipelineConfig, "Parallel Transfer Options", pnlParallelTransfer);
        GridBagConstraints parallelTransferConstraints = new GridBagConstraints();
        parallelTransferConstraints.gridx = 0;
        parallelTransferConstraints.gridy = 1;
        parallelTransferConstraints.gridwidth = 2;
        parallelTransferConstraints.fill = GridBagConstraints.BOTH;
        parallelTransferConstraints.anchor = GridBagConstraints.NORTHWEST;
        parallelTransferConstraints.weightx = 0.0;
        parallelTransferConstraints.weighty = 0.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpParallelTransfer);
        pnlPipelineConfig.add(sp, parallelTransferConstraints);

//        setupBufferOptionsPanel();
        CollapsiblePane cpBufferOptions = new CollapsiblePane(pnlPipelineConfig, "Buffer Options", pnlBufferOptions);
        GridBagConstraints bufferOptionsConstraints = new GridBagConstraints();
        bufferOptionsConstraints.gridx = 0;
        bufferOptionsConstraints.gridy = 2;
        bufferOptionsConstraints.gridwidth = 2;
        bufferOptionsConstraints.fill = GridBagConstraints.BOTH;
        bufferOptionsConstraints.anchor = GridBagConstraints.NORTHWEST;
        bufferOptionsConstraints.weightx = 1.0;
        bufferOptionsConstraints.weighty = 1.0;
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setViewportView(cpBufferOptions);
        pnlPipelineConfig.add(sp, bufferOptionsConstraints);

    }

    private void initWithConfigData() {

        IdropConfig idropConfig = idropCore.getIdropConfig();
        chkShowGUI.setSelected(idropConfig.isShowGuiAtStartup());
        chkLogTransfers.setSelected(idropConfig
                .isLogSuccessfulTransfers());
        chkLogTransfers.setName("chkLogTransfers");
        chkShowTransferProgress.setSelected(idropConfig
                .isIntraFileStatusCallbacks());
        chkShowTransferProgress.setName("chkShowTransferProgress");
        chkAllowRerouting.setSelected(idropConfig
                .isAllowConnectionRerouting());
        chkAllowRerouting.setName("chkAllowRerouting");
        chkRestartFailedConn.setSelected(idropConfig.isConnectionRestart());
        chkRestartFailedConn.setName("chkRestartFailedConn");
        spinMaxTransferAttempts.setValue(idropConfig.getMaxTransferErrors());

        chkExecutorPool.setSelected(idropConfig.isParallelUsePool());
        chkExecutorPool.setName("chkExecutorPool");
//        checkVerifyChecksumOnTransfer.setSelected(idropConfig
//                .isVerifyChecksum());

        spnTimeout.setValue(idropConfig
                .getIrodsConnectionTimeout());
        spnTimeout.setName("spinTimeout");
        spnParallelTimeout.setValue(idropConfig
                .getIrodsParallelConnectionTimeout());
        spnParallelTimeout.setName("spnParallelTimeout");
        spnMaxThreads.setValue(idropConfig
                .getIrodsParallelTransferMaxThreads());
        spnMaxThreads.setName("spnMaxThreads");
        chkAllowParallel.setSelected(idropConfig
                .isUseParallelTransfers());
        chkAllowParallel.setName("chkAllowParallel");
        chkNIO.setSelected(idropConfig
                .isUseNIOForParallelTransfers());
        chkNIO.setName("checkNIO");
        txtInternalInputBuffer.setText(String.valueOf(idropConfig
                .getInternalInputStreamBufferSize()));
        txtInternalInputBuffer.setName("txtInternalInputBuffer");
        txtInternalOutputBuffer.setText(String.valueOf(idropConfig
                .getInternalOutputStreamBufferSize()));
        txtInternalOutputBuffer.setName("txtInternalOutputBuffer");
        txtLocalFileInputBufferSize.setText(String.valueOf(idropConfig
                .getLocalFileInputStreamBufferSize()));
        txtLocalFileInputBufferSize.setName("txtLocalFileInputBufferSize");
        txtLocalFileOutputBufferSize.setText(String.valueOf(idropConfig
                .getLocalFileOutputStreamBufferSize()));
        txtLocalFileOutputBufferSize.setName("txtLocalFileOutputBufferSize");
        txtGetBufferSize
                .setText(String.valueOf(idropConfig.getGetBufferSize()));
        txtGetBufferSize.setName("txtGetBufferSize");
        txtPutBufferSize
                .setText(String.valueOf(idropConfig.getPutBufferSize()));
        txtPutBufferSize.setName("txtPutBufferSize");
        txtInputToOutputCopyBufferSize.setText(String.valueOf(idropConfig
                .getInputToOutputCopyBufferByteSize()));
        txtInputToOutputCopyBufferSize.setName("inputToOutputCopyBufferSize");
        txtInternalCacheBufferSize.setText(String.valueOf(idropConfig
                .getInternalCacheBufferSize()));
        txtInternalCacheBufferSize.setName("txtInternalCacheBufferSize");
    }

    private void updateConfigForGivenPropertyBasedOnCheckboxStateChange(
            final ItemEvent evt, final String propertyName)
            throws IdropRuntimeException {
        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating verify checksum to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(propertyName,
                    Boolean.toString(isSelected));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }

    private List<String> saveUserSettings() {
        List<String> msg = new ArrayList<String>();

        try {
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.SHOW_GUI,
                    Boolean.toString(chkShowGUI.isSelected()));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting show gui property";
            msg.add(error);
            log.error(error, ex);
        }

        try {
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.INTRA_FILE_STATUS_CALLBACKS,
                    Boolean.toString(this.chkShowTransferProgress.isSelected()));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting show data transfer property";
            msg.add(error);
            log.error(error, ex);
        }

        return msg;
    }

    private List<String> saveDataTransferSettings() {
        List<String> msg = new ArrayList<String>();

        try {
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES,
                    Boolean.toString(chkLogTransfers.isSelected()));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting log successful transfers property";
            msg.add(error);
            log.error(error, ex);
        }

        try {
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.ALLOW_CONNECTION_REROUTING,
                    Boolean.toString(chkAllowRerouting.isSelected()));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting allow connection re-routing property";
            msg.add(error);
            log.error(error, ex);
        }

        try {
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.IRODS_CONNECTION_RESTART,
                    Boolean.toString(this.chkRestartFailedConn.isSelected()));
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting restart failed connections property";
            msg.add(error);
            log.error(error, ex);
        }

        try {
            String val = (this.spinMaxTransferAttempts.getModel().getValue()
                    .toString());
            log.info("setting max transfer attempts to: {}", val);
            idropCore.getIdropConfigurationService().updateConfig(
                    IdropConfigurationService.MAX_TRANSFER_ERRORS, val);
            idropCore.getIdropConfigurationService()
                    .updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            String error = "Error setting maximum transfer attempts property";
            msg.add(error);
            log.error(error, ex);
        }

        return msg;
    }

    private List<String> savePipelineConfigSettings() {
        List<String> msg = new ArrayList<String>();

        try {
            // internal input buffer size
            String actual = txtInternalInputBuffer.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_INTERNAL_INPUT_STREAM_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtInternalInputBuffer.setBackground(Color.red);
                    //MessageManager.showWarning(this,
                    //"Invalid internal input buffer size",
                    //MessageManager.TITLE_MESSAGE);
                    String error = "Invalid internal input buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // internal output buffer size
            actual = txtInternalOutputBuffer.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_INTERNAL_OUTPUT_STREAM_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtInternalOutputBuffer.setBackground(Color.red);
                    String error = "Invalid internal output buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // local file input buffer size
            actual = txtLocalFileInputBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_LOCAL_INPUT_STREAM_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtLocalFileInputBufferSize.setBackground(Color.red);
                    String error = "Invalid local file input buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // local file output buffer size
            actual = txtLocalFileOutputBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_LOCAL_OUTPUT_STREAM_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtLocalFileOutputBufferSize.setBackground(Color.red);
                    String error = "Invalid local file output buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // get buffer size
            actual = txtGetBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(
                            IdropConfigurationService.IRODS_IO_GET_BUFFER_SIZE,
                            actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtGetBufferSize.setBackground(Color.red);
                    String error = "Invalid get buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // put buffer size
            actual = txtPutBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(
                            IdropConfigurationService.IRODS_IO_PUT_BUFFER_SIZE,
                            actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtPutBufferSize.setBackground(Color.red);
                    String error = "Invalid put buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // input to output copy buffer size
            actual = txtInputToOutputCopyBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_INPUT_TO_OUTPUT_COPY_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtInputToOutputCopyBufferSize.setBackground(Color.red);
                    String error = "Invalid input to output copy buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            // internal cache buffer size
            actual = txtInternalCacheBufferSize.getText();
            if (actual.isEmpty()) {
            } else {
                try {
                    Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService()
                            .updateConfig(
                                    IdropConfigurationService.IRODS_IO_INTERNAL_CACHE_BUFFER_SIZE,
                                    actual);
                    idropCore.getIdropConfigurationService()
                            .updateJargonPropertiesBasedOnIDROPConfig();
                } catch (NumberFormatException nfe) {
                    //txtInternalCacheBufferSize.setBackground(Color.red);
                    String error = "Invalid internal cache buffer size";
                    msg.add(error);
                    log.error(error, nfe);
                }
            }

            try {
                idropCore.getIdropConfigurationService().updateConfig(
                        IdropConfigurationService.IRODS_PARALLEL_USE_PARALLEL,
                        Boolean.toString(chkAllowParallel.isSelected()));
                idropCore.getIdropConfigurationService()
                        .updateJargonPropertiesBasedOnIDROPConfig();
            } catch (Exception ex) {
                String error = "Error setting allow parallel transfer property";
                msg.add(error);
                log.error(error, ex);
            }

            try {
                idropCore.getIdropConfigurationService().updateConfig(
                        IdropConfigurationService.IRODS_PARALLEL_USE_NIO,
                        Boolean.toString(chkNIO.isSelected()));
                idropCore.getIdropConfigurationService()
                        .updateJargonPropertiesBasedOnIDROPConfig();
            } catch (Exception ex) {
                String error = "Error setting use NIO for parallel transfer property";
                msg.add(error);
                log.error(error, ex);
            }

            try {
                idropCore.getIdropConfigurationService().updateConfig(
                        IdropConfigurationService.IRODS_PARALLEL_CONNECTION_MAX_THREADS,
                        spnMaxThreads.getValue().toString());
                idropCore.getIdropConfigurationService()
                        .updateJargonPropertiesBasedOnIDROPConfig();
            } catch (Exception ex) {
                String error = "Error setting max threads for parallel transfer property";
                msg.add(error);
                log.error(error, ex);
            }

            try {
                idropCore.getIdropConfigurationService().updateConfig(
                        IdropConfigurationService.IRODS_PARALLEL_CONNECTION_TIMEOUT,
                        spnParallelTimeout.getValue().toString());
                idropCore.getIdropConfigurationService()
                        .updateJargonPropertiesBasedOnIDROPConfig();
            } catch (Exception ex) {
                String error = "Error setting connection timeout for parallel transfer property";
                msg.add(error);
                log.error(error, ex);
            }

            try {
                idropCore.getIdropConfigurationService().updateConfig(
                        IdropConfigurationService.IRODS_CONNECTION_TIMEOUT,
                        spnTimeout.getValue().toString());
                idropCore.getIdropConfigurationService()
                        .updateJargonPropertiesBasedOnIDROPConfig();
            } catch (Exception ex) {
                String error = "Error setting irods connection timeout property";
                msg.add(error);
                log.error(error, ex);
            }

        } catch (Exception ex) {
            String error = "error setting pipeline configuration properties";
            msg.add(error);
            log.error(error, ex);
        }

        return msg;
    }

    private void btnManageDataResourcesActionPerformed(java.awt.event.ActionEvent evt) {
        // start up grid management dialog
        ManageDataResourcesDialog dlgManageDataResources = new ManageDataResourcesDialog(this, true, idropCore, irodsAcct);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (toolkit.getScreenSize().width - dlgManageDataResources
                .getWidth()) / 2;
        int y = (toolkit.getScreenSize().height - dlgManageDataResources
                .getHeight()) / 2;
        dlgManageDataResources.setLocation(x, y);
        dlgManageDataResources.setVisible(true);
    }

    private void btnConnectionTestActionPerformed(java.awt.event.ActionEvent evt) {
        // start up test connection dialog
        TestConnectionDialog dlgTestConnection = new TestConnectionDialog(this, true, idropCore);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (toolkit.getScreenSize().width - dlgTestConnection
                .getWidth()) / 2;
        int y = (toolkit.getScreenSize().height - dlgTestConnection
                .getHeight()) / 2;
        dlgTestConnection.setLocation(x, y);
        dlgTestConnection.setVisible(true);
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

        chkShowTransferProgress = new javax.swing.JCheckBox();
        chkShowGUI = new javax.swing.JCheckBox();
        pnlUserSettings = new javax.swing.JPanel();
        pnlDataTransferSettings = new javax.swing.JPanel();
        chkLogTransfers = new javax.swing.JCheckBox();
        chkAllowRerouting = new javax.swing.JCheckBox();
        chkRestartFailedConn = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        spinMaxTransferAttempts = new javax.swing.JSpinner();
        pnlTestConnection = new javax.swing.JPanel();
        pnlPipelineConfig = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spnTimeout = new javax.swing.JSpinner();
        pnlParallelTransfer = new javax.swing.JPanel();
        chkAllowParallel = new javax.swing.JCheckBox();
        chkNIO = new javax.swing.JCheckBox();
        chkExecutorPool = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        spnParallelTimeout = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        spnMaxThreads = new javax.swing.JSpinner();
        pnlBufferOptions = new javax.swing.JPanel();
        lblInternalInputBufferSize = new javax.swing.JLabel();
        txtInternalInputBuffer = new javax.swing.JTextField();
        lblInternalOutputBufferSize = new javax.swing.JLabel();
        txtInternalOutputBuffer = new javax.swing.JTextField();
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
        pnlMain = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        bntSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlCollapsibles = new javax.swing.JPanel();

        chkShowTransferProgress.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkShowTransferProgress.text")); // NOI18N

        chkShowGUI.setSelected(true);
        chkShowGUI.setLabel(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkShowGUI.label")); // NOI18N

        pnlUserSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlUserSettings.setLayout(new java.awt.GridLayout(3, 1));

        pnlDataTransferSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlDataTransferSettings.setPreferredSize(new java.awt.Dimension(463, 108));
        pnlDataTransferSettings.setLayout(new java.awt.GridBagLayout());

        chkLogTransfers.setSelected(true);
        chkLogTransfers.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkLogTransfers.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlDataTransferSettings.add(chkLogTransfers, gridBagConstraints);

        chkAllowRerouting.setSelected(true);
        chkAllowRerouting.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkAllowRerouting.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        pnlDataTransferSettings.add(chkAllowRerouting, gridBagConstraints);

        chkRestartFailedConn.setSelected(true);
        chkRestartFailedConn.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkRestartFailedConn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        pnlDataTransferSettings.add(chkRestartFailedConn, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        pnlDataTransferSettings.add(jLabel1, gridBagConstraints);

        spinMaxTransferAttempts.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        pnlDataTransferSettings.add(spinMaxTransferAttempts, gridBagConstraints);

        pnlTestConnection.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlTestConnection.setLayout(new java.awt.GridLayout(1, 0));

        pnlPipelineConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlPipelineConfig.setPreferredSize(new java.awt.Dimension(382, 600));
        pnlPipelineConfig.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlPipelineConfig.add(jLabel2, gridBagConstraints);

        spnTimeout.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        pnlPipelineConfig.add(spnTimeout, gridBagConstraints);

        pnlParallelTransfer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlParallelTransfer.setPreferredSize(new java.awt.Dimension(304, 150));
        pnlParallelTransfer.setLayout(new java.awt.GridBagLayout());

        chkAllowParallel.setSelected(true);
        chkAllowParallel.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkAllowParallel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlParallelTransfer.add(chkAllowParallel, gridBagConstraints);

        chkNIO.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkNIO.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlParallelTransfer.add(chkNIO, gridBagConstraints);

        chkExecutorPool.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkExecutorPool.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        pnlParallelTransfer.add(chkExecutorPool, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(jLabel3, gridBagConstraints);

        spnParallelTimeout.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(spnParallelTimeout, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(jLabel4, gridBagConstraints);

        spnMaxThreads.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        pnlParallelTransfer.add(spnMaxThreads, gridBagConstraints);

        pnlBufferOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlBufferOptions.setPreferredSize(new java.awt.Dimension(670, 280));
        pnlBufferOptions.setLayout(new java.awt.GridBagLayout());

        lblInternalInputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblInternalInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalInputBufferSize, gridBagConstraints);

        txtInternalInputBuffer.setColumns(20);
        txtInternalInputBuffer.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalInputBuffer.text")); // NOI18N
        txtInternalInputBuffer.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalInputBuffer.toolTipText")); // NOI18N
        txtInternalInputBuffer.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalInputBuffer, gridBagConstraints);

        lblInternalOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblInternalOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalOutputBufferSize, gridBagConstraints);

        txtInternalOutputBuffer.setColumns(20);
        txtInternalOutputBuffer.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalOutputBuffer.text")); // NOI18N
        txtInternalOutputBuffer.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalOutputBuffer.toolTipText")); // NOI18N
        txtInternalOutputBuffer.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalOutputBuffer, gridBagConstraints);

        lblLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblLocalFileInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblLocalFileInputBufferSize, gridBagConstraints);

        txtLocalFileInputBufferSize.setColumns(20);
        txtLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtLocalFileInputBufferSize.text")); // NOI18N
        txtLocalFileInputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtLocalFileInputBufferSize.toolTipText")); // NOI18N
        txtLocalFileInputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtLocalFileInputBufferSize, gridBagConstraints);

        lblLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblLocalFileOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblLocalFileOutputBufferSize, gridBagConstraints);

        txtLocalFileOutputBufferSize.setColumns(20);
        txtLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtLocalFileOutputBufferSize.text")); // NOI18N
        txtLocalFileOutputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtLocalFileOutputBufferSize.toolTipText")); // NOI18N
        txtLocalFileOutputBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtLocalFileOutputBufferSize, gridBagConstraints);

        lblPutBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblPutBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblPutBufferSize, gridBagConstraints);

        txtPutBufferSize.setColumns(20);
        txtPutBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtPutBufferSize.text")); // NOI18N
        txtPutBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtPutBufferSize.toolTipText")); // NOI18N
        txtPutBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtPutBufferSize, gridBagConstraints);

        lblGetBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblGetBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblGetBufferSize, gridBagConstraints);

        txtGetBufferSize.setColumns(20);
        txtGetBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtGetBufferSize.text")); // NOI18N
        txtGetBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtGetBufferSize.toolTipText")); // NOI18N
        txtGetBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtGetBufferSize, gridBagConstraints);

        lblIputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblIputToOutputCopyBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblIputToOutputCopyBufferSize, gridBagConstraints);

        txtInputToOutputCopyBufferSize.setColumns(20);
        txtInputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInputToOutputCopyBufferSize.text")); // NOI18N
        txtInputToOutputCopyBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInputToOutputCopyBufferSize.toolTipText")); // NOI18N
        txtInputToOutputCopyBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInputToOutputCopyBufferSize, gridBagConstraints);

        lblInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.lblInternalCacheBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(lblInternalCacheBufferSize, gridBagConstraints);

        txtInternalCacheBufferSize.setColumns(20);
        txtInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalCacheBufferSize.text")); // NOI18N
        txtInternalCacheBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.txtInternalCacheBufferSize.toolTipText")); // NOI18N
        txtInternalCacheBufferSize.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBufferOptions.add(txtInternalCacheBufferSize, gridBagConstraints);

        btnResetInputBuffer.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetInputBuffer.text")); // NOI18N
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

        btnResetOutputBuffer.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetOutputBuffer.text")); // NOI18N
        btnResetOutputBuffer.setToolTipText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetOutputBuffer.toolTipText")); // NOI18N
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

        btnResetInputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetInputBufferSize.text")); // NOI18N
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

        btnResetOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetOutputBufferSize.text")); // NOI18N
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

        btnResetGetBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetGetBufferSize.text")); // NOI18N
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

        btnResetPutBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetPutBufferSize.text")); // NOI18N
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

        btnResetCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetCopyBufferSize.text")); // NOI18N
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

        btnResetCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnResetCacheBufferSize.text")); // NOI18N
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.title")); // NOI18N
        setName("Settings"); // NOI18N
        setPreferredSize(new java.awt.Dimension(724, 530));

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlButtons.setLayout(new java.awt.GridBagLayout());

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnCancel.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.btnCancel.text")); // NOI18N
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
        bntSave.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.bntSave.text")); // NOI18N
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

        jScrollPane1.setViewportView(pnlCollapsibles);

        pnlCollapsibles.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlCollapsibles.setPreferredSize(new java.awt.Dimension(0, 900));
        pnlCollapsibles.setLayout(new java.awt.GridBagLayout());
        jScrollPane1.setViewportView(pnlCollapsibles);

        pnlMain.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void bntSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSaveActionPerformed
        List<String> msgs;

        msgs = saveUserSettings();
        if (msgs.size() > 0) {
            StringBuilder errorMsgs = new StringBuilder();
            for (String msg : msgs) {
                errorMsgs.append(msg);
                errorMsgs.append("\n");
            }
            JOptionPane.showMessageDialog(null, errorMsgs, "User Settings Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        msgs = saveDataTransferSettings();
        if (msgs.size() > 0) {
            StringBuilder errorMsgs = new StringBuilder();
            for (String msg : msgs) {
                errorMsgs.append(msg);
                errorMsgs.append("\n");
            }
            JOptionPane.showMessageDialog(null, errorMsgs, "Data Transfer Settings Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        msgs = savePipelineConfigSettings();
        if (msgs.size() > 0) {
            StringBuilder errorMsgs = new StringBuilder();
            for (String msg : msgs) {
                errorMsgs.append(msg);
                errorMsgs.append("\n");
            }
            JOptionPane.showMessageDialog(null, errorMsgs, "Pipeline Configuration Settings Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Successfully saved settings", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }//GEN-LAST:event_bntSaveActionPerformed

    private void btnResetInputBufferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetInputBufferActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtInternalInputBuffer
                    .setText(String.valueOf(properties.get("jargon.io.internal.input.stream.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore InternalInputStreamBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore InternalInputStreamBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetInputBufferActionPerformed

    private void btnResetOutputBufferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetOutputBufferActionPerformed
        try {
            //JargonProperties defaultJargonProperties = new DefaultPropertiesJargonConfig();
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtInternalOutputBuffer
                    .setText(String.valueOf(properties.get("jargon.io.internal.output.stream.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore InternalOutputStreamBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore InternalOutputStreamBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetOutputBufferActionPerformed

    private void btnResetInputBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetInputBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtLocalFileInputBufferSize
                    .setText(String.valueOf(properties.get("jargon.io.local.output.stream.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore LocalFileInputStreamBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore LocalFileInputStreamBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetInputBufferSizeActionPerformed

    private void btnResetOutputBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetOutputBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtLocalFileOutputBufferSize
                    .setText(String.valueOf(properties.get("jargon.io.local.output.stream.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore LocalFileOutputStreamBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore LocalFileOutputStreamBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetOutputBufferSizeActionPerformed

    private void btnResetGetBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetGetBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtGetBufferSize
                    .setText(String.valueOf(properties.get("jargon.get.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore GetBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore GetBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetGetBufferSizeActionPerformed

    private void btnResetPutBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPutBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtPutBufferSize
                    .setText(String.valueOf(properties.get("jargon.put.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore PutBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore PutBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetPutBufferSizeActionPerformed

    private void btnResetCopyBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetCopyBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtInputToOutputCopyBufferSize
                    .setText(String.valueOf(properties.get("jargon.io.input.to.output.copy.byte.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore InputToOutputCopyBufferByteSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore InputToOutputCopyBufferByteSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetCopyBufferSizeActionPerformed

    private void btnResetCacheBufferSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetCacheBufferSizeActionPerformed
        try {
            IdropPropertiesHelper propertiesHelper = new IdropPropertiesHelper();
            Properties properties = propertiesHelper.loadIdropProperties();
            this.txtInternalCacheBufferSize
                    .setText(String.valueOf(properties.get("jargon.io.internal.cache.buffer.size")));
        } catch (IdropException ex) {
            log.error("unable to restore InternalCacheBufferSize idrop property", ex);
            throw new IdropRuntimeException(
                    "unable to restore InternalCacheBufferSize idrop property", ex);
        }
    }//GEN-LAST:event_btnResetCacheBufferSizeActionPerformed

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
    private javax.swing.JCheckBox chkAllowRerouting;
    private javax.swing.JCheckBox chkExecutorPool;
    private javax.swing.JCheckBox chkLogTransfers;
    private javax.swing.JCheckBox chkNIO;
    private javax.swing.JCheckBox chkRestartFailedConn;
    private javax.swing.JCheckBox chkShowGUI;
    private javax.swing.JCheckBox chkShowTransferProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JPanel pnlDataTransferSettings;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlParallelTransfer;
    private javax.swing.JPanel pnlPipelineConfig;
    private javax.swing.JPanel pnlTestConnection;
    private javax.swing.JPanel pnlUserSettings;
    private javax.swing.JSpinner spinMaxTransferAttempts;
    private javax.swing.JSpinner spnMaxThreads;
    private javax.swing.JSpinner spnParallelTimeout;
    private javax.swing.JSpinner spnTimeout;
    private javax.swing.JTextField txtGetBufferSize;
    private javax.swing.JTextField txtInputToOutputCopyBufferSize;
    private javax.swing.JTextField txtInternalCacheBufferSize;
    private javax.swing.JTextField txtInternalInputBuffer;
    private javax.swing.JTextField txtInternalOutputBuffer;
    private javax.swing.JTextField txtLocalFileInputBufferSize;
    private javax.swing.JTextField txtLocalFileOutputBufferSize;
    private javax.swing.JTextField txtPutBufferSize;
    // End of variables declaration//GEN-END:variables
}
