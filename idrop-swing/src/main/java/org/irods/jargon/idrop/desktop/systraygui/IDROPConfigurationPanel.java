/*
 * IDROPConfigurationPanel.java
 *
 * Created on Jul 18, 2011, 9:17:35 AM
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.irods.jargon.core.connection.DefaultPropertiesJargonConfig;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.JargonProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.SynchConfigTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.FrequencyType;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.dao.domain.SynchronizationType;

import org.slf4j.LoggerFactory;

/**
 * Tools/Preferences dialog to set preferences
 *
 * @author mikeconway
 */
public class IDROPConfigurationPanel extends javax.swing.JDialog {

    private final IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IDROPConfigurationPanel.class);
    private JTable jTableSynch = null;
    private Synchronization selectedSynchronization = null;
    private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
    private final iDrop idropGui;
    private boolean initializing = true;

    /**
     * Creates new form IDROPConfigurationPanel
     */
    public IDROPConfigurationPanel(java.awt.Frame parent, boolean modal, IDROPCore idropCore) {
        super(parent, modal);
        this.idropCore = idropCore;
        this.idropGui = (iDrop) parent;
        initComponents();
        initWithConfigData();
    }

    private boolean checkIfSynchChanged() throws IdropRuntimeException {
        // compare data and update synch first if necessary

        boolean areEqual = true;

        if (!selectedSynchronization.getLocalSynchDirectory().equals(txtLocalPath.getText())) {
            areEqual = false;
        }

        if (!selectedSynchronization.getName().equals(txtSynchName.getText())) {
            areEqual = false;
        }

        if (!selectedSynchronization.getIrodsSynchDirectory().equals(txtIrodsPath.getText())) {
            areEqual = false;
        }

        SynchronizationType currentSynchronizationType = getSynchTypeFromGUI();

        if (currentSynchronizationType != selectedSynchronization.getSynchronizationMode()) {
            areEqual = false;
        }

        FrequencyType currentFrequencyType = null;
        currentFrequencyType = getSynchFrequencyFromGUI();

        if (selectedSynchronization.getFrequencyType() != currentFrequencyType) {
            areEqual = false;
        }

        return areEqual;
    }

    private FrequencyType getSynchFrequencyFromGUI() {
        FrequencyType currentFrequencyType = null;
        if (jcomboSynchFrequency.getSelectedIndex() == 0) {
            currentFrequencyType = FrequencyType.EVERY_HOUR;
        } else if (jcomboSynchFrequency.getSelectedIndex() == 1) {
            currentFrequencyType = FrequencyType.EVERY_WEEK;
        } else if (jcomboSynchFrequency.getSelectedIndex() == 2) {
            currentFrequencyType = FrequencyType.EVERY_DAY;
        } else if (jcomboSynchFrequency.getSelectedIndex() == 3) {
            currentFrequencyType = FrequencyType.EVERY_TWO_MINUTES;
        }
        return currentFrequencyType;
    }

    private SynchronizationType getSynchTypeFromGUI() throws IdropRuntimeException {
        SynchronizationType currentSynchronizationType;
        if (radioBackup.isSelected()) {
            currentSynchronizationType = SynchronizationType.ONE_WAY_LOCAL_TO_IRODS;
        } else if (radioFeed.isSelected()) {
            currentSynchronizationType = SynchronizationType.ONE_WAY_IRODS_TO_LOCAL;
        } else if (radioSynch.isSelected()) {
            currentSynchronizationType = SynchronizationType.BI_DIRECTIONAL;
        } else {
            log.error("unknown synchronization type in GUI");
            throw new IdropRuntimeException("unknown synchroization type in GUI");
        }
        return currentSynchronizationType;
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

        buttonGroupSynchMode = new javax.swing.ButtonGroup();
        pnlConfigSynch = new javax.swing.JPanel();
        pnlConfigSynchListing = new javax.swing.JPanel();
        pnlSynchRefresh = new javax.swing.JPanel();
        btnRefreshSynch = new javax.swing.JButton();
        scrollSynchTable = new javax.swing.JScrollPane();
        pnlConfigSynchDetails = new javax.swing.JPanel();
        pnlSynchData = new javax.swing.JPanel();
        pnlSynchName = new javax.swing.JPanel();
        lblSynchName = new javax.swing.JLabel();
        txtSynchName = new javax.swing.JTextField();
        lblSynchDateLabel = new javax.swing.JLabel();
        lblSynchDate = new javax.swing.JLabel();
        pnlSynchIcon = new javax.swing.JPanel();
        lblSynchStatus = new javax.swing.JLabel();
        pnlLocalSynch = new javax.swing.JPanel();
        txtLocalPath = new javax.swing.JTextField();
        btnChooseLocalSynch = new javax.swing.JButton();
        pnlSynchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        radioBackup = new javax.swing.JRadioButton();
        radioFeed = new javax.swing.JRadioButton();
        radioSynch = new javax.swing.JRadioButton();
        pnlSynchFrequency = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcomboSynchFrequency = new javax.swing.JComboBox();
        pnlIrodsSynch = new javax.swing.JPanel();
        txtIrodsPath = new javax.swing.JTextField();
        btnChooseIrodsSynch = new javax.swing.JButton();
        panelSynchToolbar = new javax.swing.JPanel();
        btnDeleteSynch = new javax.swing.JButton();
        btnNewSynch = new javax.swing.JButton();
        btnUpdateSynch = new javax.swing.JButton();
        btnSynchNow = new javax.swing.JButton();
        pnlTop = new javax.swing.JPanel();
        pnlCenter = new javax.swing.JPanel();
        tabConfig = new javax.swing.JTabbedPane();
        pnlConfigIdrop = new javax.swing.JPanel();
        checkShowGUI = new javax.swing.JCheckBox();
        checkShowFileProgress = new javax.swing.JCheckBox();
        pnlConfigTransfers = new javax.swing.JPanel();
        pnlTransferManagement = new javax.swing.JPanel();
        checkLogSuccessfulTransfer = new javax.swing.JCheckBox();
        checkVerifyChecksumOnTransfer = new javax.swing.JCheckBox();
        checkAllowRerouting = new javax.swing.JCheckBox();
        checkConnectionRestart = new javax.swing.JCheckBox();
        lblMaxTransferErrors = new javax.swing.JLabel();
        spinnerMaxTransferErrors = new javax.swing.JSpinner();
        pnlPipelineConfiguration = new javax.swing.JPanel();
        plnPipelineConfigurationDetails = new javax.swing.JPanel();
        lblIrodsSocketTimeout = new javax.swing.JLabel();
        spinnerIrodsSocketTimeout = new javax.swing.JSpinner();
        pnlParallelTransferOptions = new javax.swing.JPanel();
        checkAllowParallelTransfers = new javax.swing.JCheckBox();
        checkUseNIOForParallelTransfers = new javax.swing.JCheckBox();
        checkUseExecutorPool = new javax.swing.JCheckBox();
        lblIrodsParallelSocketTimeout = new javax.swing.JLabel();
        spinnerIrodsParallelSocketTimeout = new javax.swing.JSpinner();
        lblMaximumParallelTransferThreads = new javax.swing.JLabel();
        spinnerIrodsMaxParallelThreads = new javax.swing.JSpinner();
        pnlBuffers = new javax.swing.JPanel();
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
        btnRestoreDefaults = new javax.swing.JButton();
        btnApplyPipelineConfig = new javax.swing.JButton();
        pnlBottom = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();

        pnlConfigSynch.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                pnlConfigSynchComponentShown(evt);
            }
        });
        pnlConfigSynch.setLayout(new java.awt.BorderLayout());

        pnlConfigSynchListing.setMinimumSize(new java.awt.Dimension(23, 100));
        pnlConfigSynchListing.setLayout(new java.awt.BorderLayout());

        btnRefreshSynch.setMnemonic('r');
        btnRefreshSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnRefreshSynch.text")); // NOI18N
        btnRefreshSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnRefreshSynch.toolTipText")); // NOI18N
        btnRefreshSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshSynchActionPerformed(evt);
            }
        });
        pnlSynchRefresh.add(btnRefreshSynch);

        pnlConfigSynchListing.add(pnlSynchRefresh, java.awt.BorderLayout.NORTH);

        scrollSynchTable.setMinimumSize(new java.awt.Dimension(23, 100));
        scrollSynchTable.setPreferredSize(new java.awt.Dimension(100, 100));
        pnlConfigSynchListing.add(scrollSynchTable, java.awt.BorderLayout.CENTER);

        pnlConfigSynch.add(pnlConfigSynchListing, java.awt.BorderLayout.CENTER);

        pnlConfigSynchDetails.setLayout(new java.awt.BorderLayout());

        pnlSynchData.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlSynchData.setLayout(new java.awt.GridBagLayout());

        pnlSynchName.setLayout(new java.awt.GridBagLayout());

        lblSynchName.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblSynchName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlSynchName.add(lblSynchName, gridBagConstraints);

        txtSynchName.setColumns(40);
        txtSynchName.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtSynchName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlSynchName.add(txtSynchName, gridBagConstraints);

        lblSynchDateLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblSynchDateLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlSynchName.add(lblSynchDateLabel, gridBagConstraints);

        lblSynchDate.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblSynchDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlSynchName.add(lblSynchDate, gridBagConstraints);

        lblSynchStatus.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblSynchStatus.text")); // NOI18N
        lblSynchStatus.setMaximumSize(null);
        lblSynchStatus.setMinimumSize(new java.awt.Dimension(10, 10));
        lblSynchStatus.setPreferredSize(new java.awt.Dimension(10, 10));
        pnlSynchIcon.add(lblSynchStatus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlSynchName.add(pnlSynchIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 30, 20);
        pnlSynchData.add(pnlSynchName, gridBagConstraints);

        txtLocalPath.setColumns(80);
        txtLocalPath.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalPath.text")); // NOI18N
        txtLocalPath.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalPath.toolTipText")); // NOI18N
        txtLocalPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocalPathActionPerformed(evt);
            }
        });
        pnlLocalSynch.add(txtLocalPath);

        btnChooseLocalSynch.setMnemonic('c');
        btnChooseLocalSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChooseLocalSynch.text")); // NOI18N
        btnChooseLocalSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChooseLocalSynch.toolTipText")); // NOI18N
        btnChooseLocalSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseLocalSynchActionPerformed(evt);
            }
        });
        pnlLocalSynch.add(btnChooseLocalSynch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlSynchData.add(pnlLocalSynch, gridBagConstraints);

        pnlSynchMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSynchMode.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.jLabel1.text")); // NOI18N
        pnlSynchMode.add(jLabel1);

        buttonGroupSynchMode.add(radioBackup);
        radioBackup.setSelected(true);
        radioBackup.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.radioBackup.text")); // NOI18N
        pnlSynchMode.add(radioBackup);

        buttonGroupSynchMode.add(radioFeed);
        radioFeed.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.radioFeed.text")); // NOI18N
        radioFeed.setEnabled(false);
        pnlSynchMode.add(radioFeed);

        buttonGroupSynchMode.add(radioSynch);
        radioSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.radioSynch.text")); // NOI18N
        radioSynch.setEnabled(false);
        pnlSynchMode.add(radioSynch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        pnlSynchData.add(pnlSynchMode, gridBagConstraints);

        pnlSynchFrequency.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSynchFrequency.setLayout(new java.awt.GridLayout(0, 1));

        jLabel5.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.jLabel5.text")); // NOI18N
        pnlSynchFrequency.add(jLabel5);

        jcomboSynchFrequency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hourly", "Weekly", "Daily", "Every 2 Minutes (testing)", "", "" }));
        jcomboSynchFrequency.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.jcomboSynchFrequency.toolTipText")); // NOI18N
        pnlSynchFrequency.add(jcomboSynchFrequency);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        pnlSynchData.add(pnlSynchFrequency, gridBagConstraints);

        txtIrodsPath.setColumns(80);
        txtIrodsPath.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtIrodsPath.text")); // NOI18N
        txtIrodsPath.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtIrodsPath.toolTipText")); // NOI18N
        txtIrodsPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIrodsPathActionPerformed(evt);
            }
        });
        pnlIrodsSynch.add(txtIrodsPath);

        btnChooseIrodsSynch.setMnemonic('i');
        btnChooseIrodsSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChooseIrodsSynch.text")); // NOI18N
        btnChooseIrodsSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChooseIrodsSynch.toolTipText")); // NOI18N
        btnChooseIrodsSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseIrodsSynchActionPerformed(evt);
            }
        });
        pnlIrodsSynch.add(btnChooseIrodsSynch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlSynchData.add(pnlIrodsSynch, gridBagConstraints);

        pnlConfigSynchDetails.add(pnlSynchData, java.awt.BorderLayout.CENTER);

        btnDeleteSynch.setMnemonic('d');
        btnDeleteSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnDeleteSynch.text")); // NOI18N
        btnDeleteSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnDeleteSynch.toolTipText")); // NOI18N
        btnDeleteSynch.setEnabled(false);
        btnDeleteSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSynchActionPerformed(evt);
            }
        });
        panelSynchToolbar.add(btnDeleteSynch);

        btnNewSynch.setMnemonic('n');
        btnNewSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnNewSynch.text")); // NOI18N
        btnNewSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnNewSynch.toolTipText")); // NOI18N
        btnNewSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewSynchActionPerformed(evt);
            }
        });
        panelSynchToolbar.add(btnNewSynch);

        btnUpdateSynch.setMnemonic('u');
        btnUpdateSynch.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnUpdateSynch.text")); // NOI18N
        btnUpdateSynch.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnUpdateSynch.toolTipText")); // NOI18N
        btnUpdateSynch.setEnabled(false);
        btnUpdateSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSynchActionPerformed(evt);
            }
        });
        panelSynchToolbar.add(btnUpdateSynch);

        btnSynchNow.setMnemonic('s');
        btnSynchNow.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnSynchNow.text")); // NOI18N
        btnSynchNow.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnSynchNow.toolTipText")); // NOI18N
        btnSynchNow.setEnabled(false);
        btnSynchNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSynchNowActionPerformed(evt);
            }
        });
        panelSynchToolbar.add(btnSynchNow);

        pnlConfigSynchDetails.add(panelSynchToolbar, java.awt.BorderLayout.SOUTH);

        pnlConfigSynch.add(pnlConfigSynchDetails, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.title")); // NOI18N
        setMinimumSize(null);
        setPreferredSize(new java.awt.Dimension(1000, 600));

        pnlTop.setMinimumSize(null);

        org.jdesktop.layout.GroupLayout pnlTopLayout = new org.jdesktop.layout.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1067, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlCenter.setMinimumSize(null);
        pnlCenter.setPreferredSize(null);

        tabConfig.setMinimumSize(null);
        tabConfig.setPreferredSize(null);

        pnlConfigIdrop.setMinimumSize(null);
        pnlConfigIdrop.setPreferredSize(null);
        pnlConfigIdrop.setLayout(new java.awt.GridBagLayout());

        checkShowGUI.setMnemonic('s');
        checkShowGUI.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowGUI.text")); // NOI18N
        checkShowGUI.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowGUI.toolTipText")); // NOI18N
        checkShowGUI.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkShowGUIItemStateChanged(evt);
            }
        });
        checkShowGUI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkShowGUIActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlConfigIdrop.add(checkShowGUI, gridBagConstraints);

        checkShowFileProgress.setMnemonic('w');
        checkShowFileProgress.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowFileProgress.text")); // NOI18N
        checkShowFileProgress.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowFileProgress.toolTipText")); // NOI18N
        checkShowFileProgress.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkShowFileProgressItemStateChanged(evt);
            }
        });
        checkShowFileProgress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkShowFileProgressActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlConfigIdrop.add(checkShowFileProgress, gridBagConstraints);

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlConfigIdrop.TabConstraints.tabTitle"), pnlConfigIdrop); // NOI18N

        pnlConfigTransfers.setMinimumSize(null);
        pnlConfigTransfers.setPreferredSize(null);

        pnlTransferManagement.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlTransferManagement.border.title"))); // NOI18N
        pnlTransferManagement.setMinimumSize(null);
        pnlTransferManagement.setPreferredSize(null);
        pnlTransferManagement.setLayout(new java.awt.GridBagLayout());

        checkLogSuccessfulTransfer.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkLogSuccessfulTransfer.text")); // NOI18N
        checkLogSuccessfulTransfer.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkLogSuccessfulTransfer.toolTipText")); // NOI18N
        checkLogSuccessfulTransfer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkLogSuccessfulTransferItemStateChanged(evt);
            }
        });
        checkLogSuccessfulTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkLogSuccessfulTransferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferManagement.add(checkLogSuccessfulTransfer, gridBagConstraints);

        checkVerifyChecksumOnTransfer.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkVerifyChecksumOnTransfer.text")); // NOI18N
        checkVerifyChecksumOnTransfer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkVerifyChecksumOnTransferItemStateChanged(evt);
            }
        });
        checkVerifyChecksumOnTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVerifyChecksumOnTransferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferManagement.add(checkVerifyChecksumOnTransfer, gridBagConstraints);

        checkAllowRerouting.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkAllowRerouting.text")); // NOI18N
        checkAllowRerouting.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkAllowRerouting.toolTipText")); // NOI18N
        checkAllowRerouting.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkAllowReroutingItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferManagement.add(checkAllowRerouting, gridBagConstraints);

        checkConnectionRestart.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkConnectionRestart.text")); // NOI18N
        checkConnectionRestart.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkConnectionRestart.toolTipText")); // NOI18N
        checkConnectionRestart.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkConnectionRestartItemStateChanged(evt);
            }
        });
        checkConnectionRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkConnectionRestartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferManagement.add(checkConnectionRestart, gridBagConstraints);

        lblMaxTransferErrors.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblMaxTransferErrors.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferManagement.add(lblMaxTransferErrors, gridBagConstraints);

        spinnerMaxTransferErrors.setModel(new javax.swing.SpinnerNumberModel(0, 0, 600, 10));
        spinnerMaxTransferErrors.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.spinnerMaxTransferErrors.toolTipText")); // NOI18N
        spinnerMaxTransferErrors.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaxTransferErrorsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferManagement.add(spinnerMaxTransferErrors, gridBagConstraints);

        pnlConfigTransfers.add(pnlTransferManagement);

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlConfigTransfers.TabConstraints.tabTitle"), pnlConfigTransfers); // NOI18N

        pnlPipelineConfiguration.setMinimumSize(null);
        pnlPipelineConfiguration.setPreferredSize(null);

        plnPipelineConfigurationDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.plnPipelineConfigurationDetails.border.title"))); // NOI18N
        plnPipelineConfigurationDetails.setMinimumSize(null);
        plnPipelineConfigurationDetails.setPreferredSize(null);
        plnPipelineConfigurationDetails.setLayout(new java.awt.GridBagLayout());

        lblIrodsSocketTimeout.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblIrodsSocketTimeout.text")); // NOI18N
        lblIrodsSocketTimeout.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        plnPipelineConfigurationDetails.add(lblIrodsSocketTimeout, gridBagConstraints);

        spinnerIrodsSocketTimeout.setModel(new javax.swing.SpinnerNumberModel(0, 0, 600, 10));
        spinnerIrodsSocketTimeout.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.spinnerIrodsSocketTimeout.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        plnPipelineConfigurationDetails.add(spinnerIrodsSocketTimeout, gridBagConstraints);

        pnlParallelTransferOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlParallelTransferOptions.border.title"))); // NOI18N
        pnlParallelTransferOptions.setLayout(new java.awt.GridBagLayout());

        checkAllowParallelTransfers.setMnemonic('p');
        checkAllowParallelTransfers.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkAllowParallelTransfers.text")); // NOI18N
        checkAllowParallelTransfers.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkAllowParallelTransfers.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlParallelTransferOptions.add(checkAllowParallelTransfers, gridBagConstraints);

        checkUseNIOForParallelTransfers.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkUseNIOForParallelTransfers.text")); // NOI18N
        checkUseNIOForParallelTransfers.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkUseNIOForParallelTransfers.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlParallelTransferOptions.add(checkUseNIOForParallelTransfers, gridBagConstraints);

        checkUseExecutorPool.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkUseExecutorPool.text")); // NOI18N
        checkUseExecutorPool.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkUseExecutorPoolItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlParallelTransferOptions.add(checkUseExecutorPool, gridBagConstraints);

        lblIrodsParallelSocketTimeout.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblIrodsParallelSocketTimeout.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(28, 0, 0, 0);
        pnlParallelTransferOptions.add(lblIrodsParallelSocketTimeout, gridBagConstraints);

        spinnerIrodsParallelSocketTimeout.setModel(new javax.swing.SpinnerNumberModel(0, 0, 600, 10));
        spinnerIrodsParallelSocketTimeout.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.spinnerIrodsParallelSocketTimeout.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 159;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(22, 0, 0, 0);
        pnlParallelTransferOptions.add(spinnerIrodsParallelSocketTimeout, gridBagConstraints);

        lblMaximumParallelTransferThreads.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblMaximumParallelTransferThreads.text")); // NOI18N
        lblMaximumParallelTransferThreads.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblMaximumParallelTransferThreads.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 180, 0, 0);
        pnlParallelTransferOptions.add(lblMaximumParallelTransferThreads, gridBagConstraints);

        spinnerIrodsMaxParallelThreads.setModel(new javax.swing.SpinnerNumberModel(4, 0, 16, 1));
        spinnerIrodsMaxParallelThreads.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.spinnerIrodsMaxParallelThreads.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 171;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 22, 0);
        pnlParallelTransferOptions.add(spinnerIrodsMaxParallelThreads, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        plnPipelineConfigurationDetails.add(pnlParallelTransferOptions, gridBagConstraints);

        pnlBuffers.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlBuffers.border.title"))); // NOI18N
        pnlBuffers.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlBuffers.toolTipText")); // NOI18N
        pnlBuffers.setLayout(new java.awt.GridBagLayout());

        lblInternalInputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblInternalInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlBuffers.add(lblInternalInputBufferSize, gridBagConstraints);

        txtInternalInputBufferSize.setColumns(20);
        txtInternalInputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalInputBufferSize.text")); // NOI18N
        txtInternalInputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalInputBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBuffers.add(txtInternalInputBufferSize, gridBagConstraints);

        lblInternalOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblInternalOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlBuffers.add(lblInternalOutputBufferSize, gridBagConstraints);

        txtInternalOutputBufferSize.setColumns(20);
        txtInternalOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalOutputBufferSize.text")); // NOI18N
        txtInternalOutputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalOutputBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBuffers.add(txtInternalOutputBufferSize, gridBagConstraints);

        lblLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblLocalFileInputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblLocalFileInputBufferSize, gridBagConstraints);

        txtLocalFileInputBufferSize.setColumns(20);
        txtLocalFileInputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalFileInputBufferSize.text")); // NOI18N
        txtLocalFileInputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalFileInputBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtLocalFileInputBufferSize, gridBagConstraints);

        lblLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblLocalFileOutputBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblLocalFileOutputBufferSize, gridBagConstraints);

        txtLocalFileOutputBufferSize.setColumns(20);
        txtLocalFileOutputBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalFileOutputBufferSize.text")); // NOI18N
        txtLocalFileOutputBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtLocalFileOutputBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtLocalFileOutputBufferSize, gridBagConstraints);

        lblPutBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblPutBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblPutBufferSize, gridBagConstraints);

        txtPutBufferSize.setColumns(20);
        txtPutBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtPutBufferSize.text")); // NOI18N
        txtPutBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtPutBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtPutBufferSize, gridBagConstraints);

        lblGetBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblGetBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblGetBufferSize, gridBagConstraints);

        txtGetBufferSize.setColumns(20);
        txtGetBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtGetBufferSize.text")); // NOI18N
        txtGetBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtGetBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtGetBufferSize, gridBagConstraints);

        lblIputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblIputToOutputCopyBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblIputToOutputCopyBufferSize, gridBagConstraints);

        txtInputToOutputCopyBufferSize.setColumns(20);
        txtInputToOutputCopyBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInputToOutputCopyBufferSize.text")); // NOI18N
        txtInputToOutputCopyBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInputToOutputCopyBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtInputToOutputCopyBufferSize, gridBagConstraints);

        lblInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblInternalCacheBufferSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlBuffers.add(lblInternalCacheBufferSize, gridBagConstraints);

        txtInternalCacheBufferSize.setColumns(20);
        txtInternalCacheBufferSize.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalCacheBufferSize.text")); // NOI18N
        txtInternalCacheBufferSize.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.txtInternalCacheBufferSize.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlBuffers.add(txtInternalCacheBufferSize, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        plnPipelineConfigurationDetails.add(pnlBuffers, gridBagConstraints);

        btnRestoreDefaults.setMnemonic('a');
        btnRestoreDefaults.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnRestoreDefaults.text")); // NOI18N
        btnRestoreDefaults.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnRestoreDefaults.toolTipText")); // NOI18N
        btnRestoreDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestoreDefaultsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        plnPipelineConfigurationDetails.add(btnRestoreDefaults, gridBagConstraints);

        btnApplyPipelineConfig.setMnemonic('a');
        btnApplyPipelineConfig.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnApplyPipelineConfig.text")); // NOI18N
        btnApplyPipelineConfig.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnApplyPipelineConfig.toolTipText")); // NOI18N
        btnApplyPipelineConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyPipelineConfigActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        plnPipelineConfigurationDetails.add(btnApplyPipelineConfig, gridBagConstraints);

        pnlPipelineConfiguration.add(plnPipelineConfigurationDetails);

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlPipelineConfiguration.TabConstraints.tabTitle"), pnlPipelineConfiguration); // NOI18N

        pnlCenter.add(tabConfig);

        getContentPane().add(pnlCenter, java.awt.BorderLayout.CENTER);

        pnlBottom.setMinimumSize(null);
        pnlBottom.setPreferredSize(null);
        pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnOK.setMnemonic('O');
        btnOK.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnOK.text")); // NOI18N
        btnOK.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnOK.toolTipText")); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        pnlBottom.add(btnOK);

        getContentPane().add(pnlBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Refresh the displayed synch
     *
     * @param evt
     */
    private void btnRefreshSynchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshSynchActionPerformed
        refreshSynchConfigPanel();
    }//GEN-LAST:event_btnRefreshSynchActionPerformed

    private void checkShowFileProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkShowFileProgressActionPerformed
        //
    }//GEN-LAST:event_checkShowFileProgressActionPerformed

    private void checkShowGUIItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkShowGUIItemStateChanged

        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating show gui at startup to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.SHOW_GUI, Boolean.toString(isSelected));
        } catch (IdropException ex) {
            log.error("error setting show gui property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkShowGUIItemStateChanged

    private void checkShowFileProgressItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkShowFileProgressItemStateChanged
        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating show intra-file progress to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.INTRA_FILE_STATUS_CALLBACKS, Boolean.toString(isSelected));
            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkShowFileProgressItemStateChanged

    private void checkVerifyChecksumOnTransferItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkVerifyChecksumOnTransferItemStateChanged
        updateConfigForGivenPropertyBasedOnCheckboxStateChange(evt, IdropConfigurationService.VERIFY_CHECKSUM_ON_TRANSFER);
    }

    private void updateConfigForGivenPropertyBasedOnCheckboxStateChange(ItemEvent evt, String propertyName) throws IdropRuntimeException {
        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating verify checksum to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(propertyName, Boolean.toString(isSelected));
            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkVerifyChecksumOnTransferItemStateChanged

    private void checkLogSuccessfulTransferItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkLogSuccessfulTransferItemStateChanged

        updateConfigForGivenPropertyBasedOnCheckboxStateChange(evt, IdropConfigurationService.TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES);
    }//GEN-LAST:event_checkLogSuccessfulTransferItemStateChanged

    private void checkUseExecutorPoolItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkUseExecutorPoolItemStateChanged
        updateConfigForGivenPropertyBasedOnCheckboxStateChange(evt, IdropConfigurationService.IRODS_PARALLEL_USE_POOL);
    }//GEN-LAST:event_checkUseExecutorPoolItemStateChanged

    private void checkAllowReroutingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkAllowReroutingItemStateChanged
        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating allow rerouting to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.ALLOW_CONNECTION_REROUTING, Boolean.toString(isSelected));
            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkAllowReroutingItemStateChanged

    private void resetTransferPipelineEditColors() {
        txtInternalInputBufferSize.setBackground(Color.white);
        txtInternalOutputBufferSize.setBackground(Color.white);
        txtLocalFileInputBufferSize.setBackground(Color.white);
        txtLocalFileOutputBufferSize.setBackground(Color.white);
        txtGetBufferSize.setBackground(Color.white);
        txtPutBufferSize.setBackground(Color.white);
        txtInputToOutputCopyBufferSize.setBackground(Color.white);
        txtInternalCacheBufferSize.setBackground(Color.white);
    }

    /**
     * Update the pipeline configuration information properties based on the
     * screen data
     *
     * @param evt
     */
    private void btnApplyPipelineConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyPipelineConfigActionPerformed
        try {

            resetTransferPipelineEditColors();
            // edit and set

            // internal input buffer size
            String actual = txtInternalInputBufferSize.getText();
            int actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_INTERNAL_INPUT_STREAM_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtInternalInputBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid internal input buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // internal output buffer size
            actual = txtInternalOutputBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_INTERNAL_OUTPUT_STREAM_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtInternalOutputBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid internal output buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // local file  input buffer size
            actual = txtLocalFileInputBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_LOCAL_INPUT_STREAM_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtLocalFileInputBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid local file input buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // local file  output buffer size
            actual = txtLocalFileOutputBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_LOCAL_OUTPUT_STREAM_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtLocalFileOutputBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid local file output buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // get buffer size
            actual = txtGetBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_GET_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtGetBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid get buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // put buffer size
            actual = txtPutBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_PUT_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtPutBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid put buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // input to output copy buffer size
            actual = txtInputToOutputCopyBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_INPUT_TO_OUTPUT_COPY_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtInputToOutputCopyBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid input to output copy buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            // internal cache buffer size
            actual = txtInternalCacheBufferSize.getText();
            actualAsInt = 0;
            if (actual.isEmpty()) {
                actualAsInt = 0;
            } else {
                try {
                    actualAsInt = Integer.parseInt(actual);
                    idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_IO_INTERNAL_CACHE_BUFFER_SIZE, actual);
                } catch (NumberFormatException nfe) {
                    txtInternalCacheBufferSize.setBackground(Color.red);
                    MessageManager.showWarning(this, "Invalid internal cache buffer size", MessageManager.TITLE_MESSAGE);
                    return;
                }
            }

            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_PARALLEL_USE_PARALLEL, Boolean.toString(checkAllowParallelTransfers.isSelected()));
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_PARALLEL_USE_NIO, Boolean.toString(checkUseNIOForParallelTransfers.isSelected()));
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_PARALLEL_CONNECTION_MAX_THREADS, spinnerIrodsMaxParallelThreads.getValue().toString());
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_PARALLEL_CONNECTION_TIMEOUT, spinnerIrodsParallelSocketTimeout.getValue().toString());
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_CONNECTION_TIMEOUT, spinnerIrodsSocketTimeout.getValue().toString());

            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_btnApplyPipelineConfigActionPerformed

    private void btnRestoreDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreDefaultsActionPerformed
        log.info("restoring pipeline config to jargon properties settings");
        try {
            JargonProperties defaultJargonProperties = new DefaultPropertiesJargonConfig();
            IdropConfigurationService configService = idropCore.getIdropConfigurationService();
            configService.restoreIDROPConfigFromJargonProperties(defaultJargonProperties);
            initWithConfigData();
            MessageManager.showMessage(this, "Values restored to defaults, hit apply to update", MessageManager.TITLE_MESSAGE);
        } catch (JargonException ex) {
            log.error("unable to restore jargon properties", ex);
            throw new IdropRuntimeException("unable to restore jargon properties", ex);
        }

    }//GEN-LAST:event_btnRestoreDefaultsActionPerformed

    private void comboPrefsDefaultResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPrefsDefaultResourceActionPerformed
        /* String newResource = (String)comboPrefsDefaultResource.getSelectedItem();
         idropCore.getIrodsAccount().setDefaultStorageResource(newResource);
         if (! initializing) {
         idropGui.reinitializeForChangedIRODSAccount();
         }
         else { 
         initializing = false;         
         }*/
    }//GEN-LAST:event_comboPrefsDefaultResourceActionPerformed

    private void checkConnectionRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkConnectionRestartActionPerformed
    }//GEN-LAST:event_checkConnectionRestartActionPerformed

    private void checkConnectionRestartItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkConnectionRestartItemStateChanged
        boolean isSelected = false;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            isSelected = true;
        }
        log.info("updating connection restart to:{}", isSelected);
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.IRODS_CONNECTION_RESTART, Boolean.toString(isSelected));
            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkConnectionRestartItemStateChanged

    private void spinnerMaxTransferErrorsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaxTransferErrorsStateChanged
        log.info("spinnerMaxTransferErrorsStateChanged:{}", evt);

        try {
            String val = (spinnerMaxTransferErrors.getModel().getValue().toString());
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.MAX_TRANSFER_ERRORS, val);
            idropCore.getIdropConfigurationService().updateJargonPropertiesBasedOnIDROPConfig();
        } catch (Exception ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }    }//GEN-LAST:event_spinnerMaxTransferErrorsStateChanged

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void checkShowGUIActionPerformed(java.awt.event.ActionEvent evt) {
        //
    }

    private void checkLogSuccessfulTransferActionPerformed(java.awt.event.ActionEvent evt) {
        //
    }

    private void pnlConfigSynchComponentShown(java.awt.event.ComponentEvent evt) {

        refreshSynchConfigPanel();

    }

    private void refreshSynchConfigPanel() {
        log.info("lazily loading synch data");

        final IDROPConfigurationPanel thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {


                //FIXME:conveyor                  SynchManagerService synchConfigurationService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();

                try {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    /*
                     List<Synchronization> synchronizations = synchConfigurationService.listAllSynchronizations();
                     SynchConfigTableModel synchConfigTableModel = null;
                     if (jTableSynch == null) {
                     synchConfigTableModel = new SynchConfigTableModel(idropCore, synchronizations);
                     jTableSynch = new JTable(synchConfigTableModel);
                     jTableSynch.getSelectionModel().addListSelectionListener(new SynchListSelectionHandler(thisPanel));
                     scrollSynchTable.setViewportView(jTableSynch);
                     scrollSynchTable.validate();
                     pnlConfigSynchListing.validate();
                     } else {
                     synchConfigTableModel = (SynchConfigTableModel) jTableSynch.getModel();
                     synchConfigTableModel.setSynchronizations(synchronizations);
                     synchConfigTableModel.fireTableDataChanged();
                     }

                     if (synchConfigTableModel.getRowCount() > 0) {
                     jTableSynch.setRowSelectionInterval(0, 0);
                     } else {
                     lockSynchPanelForNewOnly();
                     }
                     } catch (SynchException ex) {
                     log.error("error setting up synchs table", ex);
                     throw new IdropRuntimeException(ex);*/
                } finally {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            private void lockSynchPanelForNewOnly() {
                clearAndResetSynchPanel();
                setLockStatusSynchPanel(false);
            }
        });
    }

    private void txtLocalPathActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnChooseLocalSynchActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(false);
        localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = localFileChooser.showOpenDialog(this);
        txtLocalPath.setText(localFileChooser.getSelectedFile().getAbsolutePath());
    }

    private void txtIrodsPathActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnChooseIrodsSynchActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            IRODSFinderDialog irodsFileSystemChooserView = new IRODSFinderDialog(null, true, idropCore);
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = (screenSize.width - irodsFileSystemChooserView.getWidth()) / 2;
            final int y = (screenSize.height - irodsFileSystemChooserView.getHeight()) / 2;
            irodsFileSystemChooserView.setLocation(x, y);
            irodsFileSystemChooserView.setVisible(true);
            String absPath = irodsFileSystemChooserView.getSelectedAbsolutePath();
            irodsFileSystemChooserView.dispose();
            if (absPath != null) {
                txtIrodsPath.setText(irodsFileSystemChooserView.getSelectedAbsolutePath());
            }

            // int returnVal = irodsFileChooser.showSaveDialog(this);
        } catch (Exception e) {
            log.error("exception choosings iRODS file");
            throw new IdropRuntimeException("exception choosing irods fie", e);
        } finally {
            idropCore.getIrodsFileSystem().closeAndEatExceptions();
        }
    }

    /**
     * Delete the selected synchronization
     *
     * @param evt
     */
    private void btnDeleteSynchActionPerformed(java.awt.event.ActionEvent evt) {

        final IDROPConfigurationPanel thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (selectedSynchronization == null) {
                    MessageManager.showError(thisPanel, "Please select a synchronization from the table", MessageManager.TITLE_MESSAGE);
                    return;
                }
                Synchronization synchronization = selectedSynchronization;

                int result = JOptionPane.showConfirmDialog(thisPanel,
                        "Do you wish to delete this synchronization?",
                        MessageManager.TITLE_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                try {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // FIXME: conveyor
                    /*
                     SynchManagerService synchConfigurationService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();
                     log.info("deleting synchronization:{}", synchronization);

                     if (synchConfigurationService.isSynchRunning(synchronization)) {
                     MessageManager.showMessage(thisPanel, "Cannot delete the synchronization, a synch is currently running", MessageManager.TITLE_MESSAGE);
                     return;
                     }

                     ListSelectionModel lsm = (ListSelectionModel) thisPanel.getSynchTable().getSelectionModel();
                     SynchConfigTableModel model = (SynchConfigTableModel) thisPanel.getSynchTable().getModel();

                     synchConfigurationService.deleteSynchronization(synchronization);
                     log.info("synch deleted, refreshing model");
                     List<Synchronization> synchronizations = synchConfigurationService.listAllSynchronizations();

                     model.setSynchronizations(synchronizations);
                     model.fireTableDataChanged();

                     MessageManager.showMessage(thisPanel, "Configuration deleted", MessageManager.TITLE_MESSAGE);
                     btnDeleteSynch.setEnabled(false);
                     btnUpdateSynch.setEnabled(false);
                     btnSynchNow.setEnabled(false);
                     refreshSynchConfigPanel();
                     } catch (Exception ex) {
                     MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                     * */
                } finally {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    idropCore.closeIRODSConnection(
                            idropCore.getIrodsAccount());
                }
            }
        });
    }

    /**
     * Called to clear and prepare for adding a new synchronization
     *
     * @param evt
     */
    private void btnNewSynchActionPerformed(java.awt.event.ActionEvent evt) {
        clearAndResetSynchPanel();
        if (jTableSynch.getModel().getRowCount() > 0) {
            jTableSynch.getSelectionModel().removeIndexInterval(0, jTableSynch.getModel().getRowCount() - 1);
        }
        selectedSynchronization = new Synchronization();
        btnDeleteSynch.setEnabled(false);
        btnUpdateSynch.setEnabled(true);
        btnSynchNow.setEnabled(false);
        MessageManager.showMessage(this, "Enter the data for the new Synchronization and press Update to save", MessageManager.TITLE_MESSAGE);
        setLockStatusSynchPanel(true);
        btnSynchNow.setEnabled(false);
        btnDeleteSynch.setEnabled(false);
    }

    /**
     * User signals that the displayed synchronization should be updated
     *
     * @param evt
     */
    private void btnUpdateSynchActionPerformed(java.awt.event.ActionEvent evt) {
        updateSynch();
    }

    private void updateSynch() {
        final IDROPConfigurationPanel thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                resetSynchPanel();
                if (selectedSynchronization == null) {
                    MessageManager.showError(thisPanel, "Please select a synchronization from the table", MessageManager.TITLE_MESSAGE);
                    return;
                }

                if (txtLocalPath.getText().trim().isEmpty()) {
                    txtLocalPath.setBackground(Color.RED);
                    MessageManager.showError(thisPanel, "Please select a local path", MessageManager.TITLE_MESSAGE);
                    return;
                }

                if (txtIrodsPath.getText().trim().isEmpty()) {
                    txtIrodsPath.setBackground(Color.RED);
                    MessageManager.showError(thisPanel, "Please select an iRODS path", MessageManager.TITLE_MESSAGE);
                    return;
                }

                if (txtSynchName.getText().trim().isEmpty()) {
                    txtSynchName.setBackground(Color.RED);
                    MessageManager.showError(thisPanel, "Please enter a unique name for this synchronization", MessageManager.TITLE_MESSAGE);
                    return;
                }

                boolean isNew = (selectedSynchronization.getId() == null);
                if (isNew) {
                    log.info("adding new synch");
                }

                // FIXME: conveyor
                /*
                 SynchManagerService synchConfigurationService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();

                 // edits pass, do update
                 log.info("saving synch data");
                 Synchronization synchronization = selectedSynchronization;
                 synchronization.setUpdatedAt(new Date());
                 synchronization.setFrequencyType(getSynchFrequencyFromGUI());

                 synchronization.setName(txtSynchName.getText().trim());
                 synchronization.setSynchronizationMode(getSynchTypeFromGUI());
                 synchronization.setLocalSynchDirectory(txtLocalPath.getText().trim());
                 synchronization.setIrodsSynchDirectory(txtIrodsPath.getText().trim());
                 IRODSAccount irodsAccount = idropCore.getIrodsAccount();
                 synchronization.setIrodsHostName(irodsAccount.getHost());

                 try {
                 synchronization.setIrodsPassword(HibernateUtil.obfuscate(irodsAccount.getPassword()));
                 } catch (JargonException ex) {
                 log.error("exception obfuscating password", ex);
                 MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                 throw new IdropRuntimeException(ex);
                 }

                 synchronization.setIrodsPort(irodsAccount.getPort());
                 synchronization.setIrodsUserName(irodsAccount.getUserName());
                 synchronization.setIrodsZone(irodsAccount.getZone());
                 synchronization.setDefaultResourceName(irodsAccount.getDefaultStorageResource());
                 synchronization.setCreatedAt(new Date());
                 selectedSynchronization = synchronization;
                 * */

                try {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    // FIXME: conveyor

                    /*
                     if (synchConfigurationService.isSynchRunning(selectedSynchronization)) {
                     MessageManager.showMessage(thisPanel, "Cannot update the synchronization, a synch is currently running", MessageManager.TITLE_MESSAGE);
                     return;
                     }

                     idropCore.getIdropConfigurationService().updateSynchronization(synchronization);
                     */
                    MessageManager.showMessage(thisPanel, "Configuration updated", MessageManager.TITLE_MESSAGE);
                    ListSelectionModel lsm = (ListSelectionModel) thisPanel.getSynchTable().getSelectionModel();
                    SynchConfigTableModel model = (SynchConfigTableModel) thisPanel.getSynchTable().getModel();

                    if (isNew) {
                        // FIXME: conveyor
                        /*
                         List<Synchronization> synchronizations = synchConfigurationService.listAllSynchronizations();

                         model.setSynchronizations(synchronizations);
                         * */
                        model.fireTableDataChanged();
                    } else {
                        if (lsm.isSelectionEmpty()) {
                            return;
                        } else {
                            // Find out which indexes are selected.
                            int minIndex = lsm.getMinSelectionIndex();
                            int maxIndex = lsm.getMaxSelectionIndex();
                            for (int i = minIndex; i <= maxIndex; i++) {
                                if (lsm.isSelectedIndex(i)) {
                                    int modelIdx = thisPanel.getSynchTable().convertRowIndexToModel(i);

                                    // FIXME: conveyor model.getSynchronizations().set(modelIdx, synchronization);
                                    model.fireTableDataChanged();
                                    break;
                                }
                            }
                        }
                    }

                    btnDeleteSynch.setEnabled(true);
                    btnUpdateSynch.setEnabled(true);
                    btnSynchNow.setEnabled(true);
                    /*
                     } catch (IdropException ex) {
                     MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                     } catch (SynchException ex) {
                     MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                     * */
                } finally {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    idropCore.closeIRODSConnection(
                            idropCore.getIrodsAccount());
                }
            }
        });
    }

    /**
     * Force a synchronization process on the selected synchronization
     *
     * @param evt
     */
    private void btnSynchNowActionPerformed(java.awt.event.ActionEvent evt) {
        log.info("synch now button pressed");
        if (selectedSynchronization == null) {
            MessageManager.showWarning(this, "Please select a synhronization", MessageManager.TITLE_MESSAGE);
            return;
        }

        log.info("selected synchronization is:{}", selectedSynchronization);
        boolean synchIsUnchanged = checkIfSynchChanged();

        if (!synchIsUnchanged) {
            log.info("synch had been changed, update first");
            updateSynch();
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Synchronize?",
                "Do you want to synchronize now?",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // FIXME: conveyor
            /*
             try {
             SynchManagerService synchConfigurationService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();

             if (synchConfigurationService.isSynchRunning(selectedSynchronization)) {
             MessageManager.showMessage(this, "Cannot schedule the synchronization, a synch is currently running", MessageManager.TITLE_MESSAGE);
             return;
             }
             idropCore.getTransferManager().enqueueASynch(selectedSynchronization, selectedSynchronization.buildIRODSAccountFromSynchronizationData());
             } catch (Exception ex) {
             log.error("error starting synch", ex);
             MessageManager.showError(this, ex.getMessage(), MessageManager.TITLE_MESSAGE);
             throw new IdropRuntimeException(ex);
             }*/
        }
    }

    private void checkVerifyChecksumOnTransferActionPerformed(java.awt.event.ActionEvent evt) {
        //
    }

    protected JTable getSynchTable() {
        return jTableSynch;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyPipelineConfig;
    private javax.swing.JButton btnChooseIrodsSynch;
    private javax.swing.JButton btnChooseLocalSynch;
    private javax.swing.JButton btnDeleteSynch;
    private javax.swing.JButton btnNewSynch;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnRefreshSynch;
    private javax.swing.JButton btnRestoreDefaults;
    private javax.swing.JButton btnSynchNow;
    private javax.swing.JButton btnUpdateSynch;
    private javax.swing.ButtonGroup buttonGroupSynchMode;
    private javax.swing.JCheckBox checkAllowParallelTransfers;
    private javax.swing.JCheckBox checkAllowRerouting;
    private javax.swing.JCheckBox checkConnectionRestart;
    private javax.swing.JCheckBox checkLogSuccessfulTransfer;
    private javax.swing.JCheckBox checkShowFileProgress;
    private javax.swing.JCheckBox checkShowGUI;
    private javax.swing.JCheckBox checkUseExecutorPool;
    private javax.swing.JCheckBox checkUseNIOForParallelTransfers;
    private javax.swing.JCheckBox checkVerifyChecksumOnTransfer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox jcomboSynchFrequency;
    private javax.swing.JLabel lblGetBufferSize;
    private javax.swing.JLabel lblInternalCacheBufferSize;
    private javax.swing.JLabel lblInternalInputBufferSize;
    private javax.swing.JLabel lblInternalOutputBufferSize;
    private javax.swing.JLabel lblIputToOutputCopyBufferSize;
    private javax.swing.JLabel lblIrodsParallelSocketTimeout;
    private javax.swing.JLabel lblIrodsSocketTimeout;
    private javax.swing.JLabel lblLocalFileInputBufferSize;
    private javax.swing.JLabel lblLocalFileOutputBufferSize;
    private javax.swing.JLabel lblMaxTransferErrors;
    private javax.swing.JLabel lblMaximumParallelTransferThreads;
    private javax.swing.JLabel lblPutBufferSize;
    private javax.swing.JLabel lblSynchDate;
    private javax.swing.JLabel lblSynchDateLabel;
    private javax.swing.JLabel lblSynchName;
    private javax.swing.JLabel lblSynchStatus;
    private javax.swing.JPanel panelSynchToolbar;
    private javax.swing.JPanel plnPipelineConfigurationDetails;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlBuffers;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlConfigIdrop;
    private javax.swing.JPanel pnlConfigSynch;
    private javax.swing.JPanel pnlConfigSynchDetails;
    private javax.swing.JPanel pnlConfigSynchListing;
    private javax.swing.JPanel pnlConfigTransfers;
    private javax.swing.JPanel pnlIrodsSynch;
    private javax.swing.JPanel pnlLocalSynch;
    private javax.swing.JPanel pnlParallelTransferOptions;
    private javax.swing.JPanel pnlPipelineConfiguration;
    private javax.swing.JPanel pnlSynchData;
    private javax.swing.JPanel pnlSynchFrequency;
    private javax.swing.JPanel pnlSynchIcon;
    private javax.swing.JPanel pnlSynchMode;
    private javax.swing.JPanel pnlSynchName;
    private javax.swing.JPanel pnlSynchRefresh;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTransferManagement;
    private javax.swing.JRadioButton radioBackup;
    private javax.swing.JRadioButton radioFeed;
    private javax.swing.JRadioButton radioSynch;
    private javax.swing.JScrollPane scrollSynchTable;
    private javax.swing.JSpinner spinnerIrodsMaxParallelThreads;
    private javax.swing.JSpinner spinnerIrodsParallelSocketTimeout;
    private javax.swing.JSpinner spinnerIrodsSocketTimeout;
    private javax.swing.JSpinner spinnerMaxTransferErrors;
    private javax.swing.JTabbedPane tabConfig;
    private javax.swing.JTextField txtGetBufferSize;
    private javax.swing.JTextField txtInputToOutputCopyBufferSize;
    private javax.swing.JTextField txtInternalCacheBufferSize;
    private javax.swing.JTextField txtInternalInputBufferSize;
    private javax.swing.JTextField txtInternalOutputBufferSize;
    private javax.swing.JTextField txtIrodsPath;
    private javax.swing.JTextField txtLocalFileInputBufferSize;
    private javax.swing.JTextField txtLocalFileOutputBufferSize;
    private javax.swing.JTextField txtLocalPath;
    private javax.swing.JTextField txtPutBufferSize;
    private javax.swing.JTextField txtSynchName;
    // End of variables declaration//GEN-END:variables

    private void initWithConfigData() {
        resetTransferPipelineEditColors();
        IdropConfig idropConfig = idropCore.getIdropConfig();
        checkShowGUI.setSelected(idropConfig.isShowGuiAtStartup());
        checkLogSuccessfulTransfer.setSelected(idropConfig.isLogSuccessfulTransfers());
        checkUseExecutorPool.setSelected(idropConfig.isParallelUsePool());
        checkVerifyChecksumOnTransfer.setSelected(idropConfig.isVerifyChecksum());
        checkShowFileProgress.setSelected(idropConfig.isIntraFileStatusCallbacks());
        checkAllowRerouting.setSelected(idropConfig.isAllowConnectionRerouting());
        checkUseExecutorPool.setSelected(idropConfig.isParallelUsePool());
        spinnerIrodsSocketTimeout.setValue(idropConfig.getIrodsConnectionTimeout());
        spinnerIrodsParallelSocketTimeout.setValue(idropConfig.getIrodsParallelConnectionTimeout());
        spinnerIrodsMaxParallelThreads.setValue(idropConfig.getIrodsParallelTransferMaxThreads());
        checkAllowParallelTransfers.setSelected(idropConfig.isUseParallelTransfers());
        checkConnectionRestart.setSelected(idropConfig.isConnectionRestart());
        checkUseNIOForParallelTransfers.setSelected(idropConfig.isUseNIOForParallelTransfers());
        txtInternalInputBufferSize.setText(String.valueOf(idropConfig.getInternalInputStreamBufferSize()));
        txtInternalOutputBufferSize.setText(String.valueOf(idropConfig.getInternalOutputStreamBufferSize()));
        txtLocalFileInputBufferSize.setText(String.valueOf(idropConfig.getLocalFileInputStreamBufferSize()));
        txtLocalFileOutputBufferSize.setText(String.valueOf(idropConfig.getLocalFileOutputStreamBufferSize()));
        txtGetBufferSize.setText(String.valueOf(idropConfig.getGetBufferSize()));
        txtPutBufferSize.setText(String.valueOf(idropConfig.getPutBufferSize()));
        txtInputToOutputCopyBufferSize.setText(String.valueOf(idropConfig.getInputToOutputCopyBufferByteSize()));
        txtInternalCacheBufferSize.setText(String.valueOf(idropConfig.getInternalCacheBufferSize()));
        spinnerMaxTransferErrors.setValue(idropConfig.getMaxTransferErrors());
//        refreshAccountData();
    }

    /**
     * Reset colors in synch panel when re-validating
     */
    private void resetSynchPanel() {
        txtLocalPath.setBackground(Color.WHITE);
        txtIrodsPath.setBackground(Color.WHITE);
        txtSynchName.setBackground(Color.WHITE);
    }

    /**
     * Clear synch panel values and colors
     */
    private void clearAndResetSynchPanel() {
        resetSynchPanel();
        txtLocalPath.setText("");
        txtIrodsPath.setText("");
        txtSynchName.setText("");
        radioBackup.setSelected(true);
        lblSynchDate.setText("");
        pnlSynchIcon.removeAll();
        pnlSynchIcon.validate();
        jcomboSynchFrequency.setSelectedIndex(0);
    }

    protected void updateDetailsForSelectedSynch(int i) {
        // make sure the most up-to-date information is displayed
        int modelIdx = getSynchTable().convertRowIndexToModel(i);
        SynchConfigTableModel model = (SynchConfigTableModel) getSynchTable().getModel();

        selectedSynchronization = model.getSynchronizationAt(modelIdx);

        if (selectedSynchronization == null) {
            model.removeRow(modelIdx);
            return;
        }

        // initialize data
        txtLocalPath.setText(selectedSynchronization.getLocalSynchDirectory());
        txtIrodsPath.setText(selectedSynchronization.getIrodsSynchDirectory());
        txtSynchName.setText(selectedSynchronization.getName());

        if (selectedSynchronization.getFrequencyType() == FrequencyType.EVERY_HOUR) {
            jcomboSynchFrequency.setSelectedIndex(0);
        } else if (selectedSynchronization.getFrequencyType() == FrequencyType.EVERY_WEEK) {
            jcomboSynchFrequency.setSelectedIndex(1);
        } else if (selectedSynchronization.getFrequencyType() == FrequencyType.EVERY_DAY) {
            jcomboSynchFrequency.setSelectedIndex(2);
        } else if (selectedSynchronization.getFrequencyType() == FrequencyType.EVERY_TWO_MINUTES) {
            jcomboSynchFrequency.setSelectedIndex(3);
        } else {
            // default to hourly to avoid errors
            log.error("unknown frequency type for synch:{}", selectedSynchronization.getFrequencyType());
            jcomboSynchFrequency.setSelectedIndex(0);
        }

        if (selectedSynchronization.getSynchronizationMode() == SynchronizationType.BI_DIRECTIONAL) {
            radioSynch.setSelected(true);
        } else if (selectedSynchronization.getSynchronizationMode() == SynchronizationType.ONE_WAY_IRODS_TO_LOCAL) {
            radioFeed.setSelected(true);
        } else if (selectedSynchronization.getSynchronizationMode() == SynchronizationType.ONE_WAY_LOCAL_TO_IRODS) {
            radioBackup.setSelected(true);
        } else {
            log.error("unknown synchronization mode for synch:{}", selectedSynchronization.getSynchronizationMode());
            throw new IdropRuntimeException("unknown synchronization mode");
        }

        btnDeleteSynch.setEnabled(true);
        btnUpdateSynch.setEnabled(true);
        btnSynchNow.setEnabled(true);

        setSynchIcon(selectedSynchronization);
        if (selectedSynchronization.getLastSynchronized() == null) {
            lblSynchDate.setText("None");
        } else {
            lblSynchDate.setText(dateFormat.format(selectedSynchronization.getLastSynchronized()));
        }

    }

    protected void setLockStatusSynchPanel(boolean lockStatus) {
        txtSynchName.setEnabled(lockStatus);
        txtLocalPath.setEnabled(lockStatus);
        btnChooseLocalSynch.setEnabled(lockStatus);
        radioBackup.setEnabled(lockStatus);
        //radioFeed.setEnabled(lockStatus);
        //radioSynch.setEnabled(lockStatus);
        jcomboSynchFrequency.setEnabled(lockStatus);
        txtIrodsPath.setEnabled(lockStatus);
        btnChooseIrodsSynch.setEnabled(lockStatus);
        btnDeleteSynch.setEnabled(lockStatus);
        btnUpdateSynch.setEnabled(lockStatus);
        btnSynchNow.setEnabled(lockStatus);
        //btnSynchDetails.setEnabled(lockStatus);
    }

    private void setSynchIcon(Synchronization synchronization) {

        JLabel labelToUse = null;

        // FIXME: conveyor
        /*
         SynchManagerService synchManagerService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();
         try {
         boolean isRunning = synchManagerService.isSynchRunning(synchronization);
         if (isRunning) {
         labelToUse = IconHelper.getRunningIcon();
         } else if (synchronization.getLastSynchronizationStatus() == null) {
         labelToUse = IconHelper.getOkIcon();
         } else if (synchronization.getLastSynchronizationStatus() == TransferStatus.ERROR) {
         labelToUse = IconHelper.getErrorIcon();
         } else {
         labelToUse = IconHelper.getOkIcon();
         }
         } catch (SynchException ex) {
         log.error("error checking if synch is already running:{}", synchronization, ex);
         throw new IdropRuntimeException("exception checking if synch is already running", ex);
         }
         * */

        pnlSynchIcon.removeAll();
        lblSynchStatus = labelToUse;
        pnlSynchIcon.add(lblSynchStatus);
        pnlSynchIcon.validate();

    }
}

class SynchListSelectionHandler implements ListSelectionListener {

    private final IDROPConfigurationPanel idropConfigurationPanel;

    SynchListSelectionHandler(final IDROPConfigurationPanel configurationPanel) {
        idropConfigurationPanel = configurationPanel;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {

        if (e.getValueIsAdjusting() == true) {
            return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        if (lsm.isSelectionEmpty()) {
            return;
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    idropConfigurationPanel.updateDetailsForSelectedSynch(i);
                }
            }
        }

    }
}