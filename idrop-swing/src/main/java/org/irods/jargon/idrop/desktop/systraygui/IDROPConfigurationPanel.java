
/*
 * IDROPConfigurationPanel.java
 *
 * Created on Jul 18, 2011, 9:17:35 AM
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.IconHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.SynchConfigTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.FrequencyType;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.dao.domain.SynchronizationType;
import org.irods.jargon.transfer.dao.domain.TransferStatus;
import org.irods.jargon.transfer.engine.synch.ConflictingSynchException;
import org.irods.jargon.transfer.engine.synch.SynchException;
import org.irods.jargon.transfer.engine.synch.SynchManagerService;
import org.irods.jargon.transfer.util.HibernateUtil;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 * Tools/Preferences dialog to set preferences
 * @author mikeconway
 */
public class IDROPConfigurationPanel extends javax.swing.JDialog {

    private final IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IDROPConfigurationPanel.class);
    private JTable jTableSynch = null;
    private Synchronization selectedSynchronization = null;
    private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    /** Creates new form IDROPConfigurationPanel */
    public IDROPConfigurationPanel(java.awt.Frame parent, boolean modal, IDROPCore idropCore) {
        super(parent, modal);
        this.idropCore = idropCore;
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupSynchMode = new javax.swing.ButtonGroup();
        pnlTop = new javax.swing.JPanel();
        pnlCenter = new javax.swing.JPanel();
        tabConfig = new javax.swing.JTabbedPane();
        pnlConfigIdrop = new javax.swing.JPanel();
        checkShowGUI = new javax.swing.JCheckBox();
        checkShowFileProgress = new javax.swing.JCheckBox();
        pnlConfigGrids = new javax.swing.JPanel();
        pnlCurrentGrid = new javax.swing.JPanel();
        lblHostLabel = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        lblPortLabel = new javax.swing.JLabel();
        lblPort = new javax.swing.JLabel();
        lblZoneLabel = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        lblResourceLabel = new javax.swing.JLabel();
        lblResource = new javax.swing.JLabel();
        lblUserNameLabel = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        pnlCurrentGridToolbar = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        btnChangePassword = new javax.swing.JButton();
        pnlConfigTransfers = new javax.swing.JPanel();
        checkLogSuccessfulTransfer = new javax.swing.JCheckBox();
        checkVerifyChecksumOnTransfer = new javax.swing.JCheckBox();
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
        btnSynchDetails = new javax.swing.JButton();
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
        pnlBottom = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.title")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlTopLayout = new org.jdesktop.layout.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1020, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlCenter.setLayout(new java.awt.GridLayout(1, 0));

        pnlConfigIdrop.setLayout(new java.awt.GridBagLayout());

        checkShowGUI.setMnemonic('s');
        checkShowGUI.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowGUI.text")); // NOI18N
        checkShowGUI.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkShowGUI.toolTipText")); // NOI18N
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

        pnlConfigGrids.setLayout(new java.awt.BorderLayout());

        pnlCurrentGrid.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlCurrentGrid.border.title"))); // NOI18N
        pnlCurrentGrid.setLayout(new java.awt.GridBagLayout());

        lblHostLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblHostLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentGrid.add(lblHostLabel, gridBagConstraints);

        lblHost.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblHost.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentGrid.add(lblHost, gridBagConstraints);

        lblPortLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblPortLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentGrid.add(lblPortLabel, gridBagConstraints);

        lblPort.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblPort.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentGrid.add(lblPort, gridBagConstraints);

        lblZoneLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblZoneLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentGrid.add(lblZoneLabel, gridBagConstraints);

        lblZone.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblZone.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentGrid.add(lblZone, gridBagConstraints);

        lblResourceLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblResourceLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentGrid.add(lblResourceLabel, gridBagConstraints);

        lblResource.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblResource.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentGrid.add(lblResource, gridBagConstraints);

        lblUserNameLabel.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblUserNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentGrid.add(lblUserNameLabel, gridBagConstraints);

        lblUserName.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.lblUserName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentGrid.add(lblUserName, gridBagConstraints);

        btnLogout.setMnemonic('l');
        btnLogout.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnLogout.text")); // NOI18N
        btnLogout.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnLogout.toolTipText")); // NOI18N
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        pnlCurrentGridToolbar.add(btnLogout);

        btnChangePassword.setMnemonic('c');
        btnChangePassword.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChangePassword.text")); // NOI18N
        btnChangePassword.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnChangePassword.toolTipText")); // NOI18N
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
            }
        });
        pnlCurrentGridToolbar.add(btnChangePassword);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlCurrentGrid.add(pnlCurrentGridToolbar, gridBagConstraints);

        pnlConfigGrids.add(pnlCurrentGrid, java.awt.BorderLayout.CENTER);

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlConfigGrids.TabConstraints.tabTitle"), pnlConfigGrids); // NOI18N

        pnlConfigTransfers.setLayout(new java.awt.GridBagLayout());

        checkLogSuccessfulTransfer.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkLogSuccessfulTransfer.text")); // NOI18N
        checkLogSuccessfulTransfer.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkLogSuccessfulTransfer.toolTipText")); // NOI18N
        checkLogSuccessfulTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkLogSuccessfulTransferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlConfigTransfers.add(checkLogSuccessfulTransfer, gridBagConstraints);

        checkVerifyChecksumOnTransfer.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.checkVerifyChecksumOnTransfer.text")); // NOI18N
        checkVerifyChecksumOnTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVerifyChecksumOnTransferActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlConfigTransfers.add(checkVerifyChecksumOnTransfer, gridBagConstraints);

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlConfigTransfers.TabConstraints.tabTitle"), pnlConfigTransfers); // NOI18N

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

        btnSynchDetails.setMnemonic('H');
        btnSynchDetails.setText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnSynchDetails.text")); // NOI18N
        btnSynchDetails.setToolTipText(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.btnSynchDetails.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlSynchName.add(btnSynchDetails, gridBagConstraints);

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

        tabConfig.addTab(org.openide.util.NbBundle.getMessage(IDROPConfigurationPanel.class, "IDROPConfigurationPanel.pnlConfigSynch.TabConstraints.tabTitle"), pnlConfigSynch); // NOI18N

        pnlCenter.add(tabConfig);

        getContentPane().add(pnlCenter, java.awt.BorderLayout.CENTER);

        pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

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
     * @param evt 
     */
    private void btnRefreshSynchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshSynchActionPerformed
        refreshSynchConfigPanel();
    }//GEN-LAST:event_btnRefreshSynchActionPerformed

    private void checkShowFileProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkShowFileProgressActionPerformed
         log.info("updating show intra-file progress to:{}", checkShowFileProgress.isSelected());
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.INTRA_FILE_STATUS_CALLBACKS, Boolean.toString(checkShowGUI.isSelected()));
        } catch (IdropException ex) {
            log.error("error setting  property", ex);
            throw new IdropRuntimeException(ex);
        }
    }//GEN-LAST:event_checkShowFileProgressActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void checkShowGUIActionPerformed(java.awt.event.ActionEvent evt) {
        log.info("updating show gui at startup to:{}", checkShowGUI.isSelected());
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.SHOW_GUI, Boolean.toString(checkShowGUI.isSelected()));
        } catch (IdropException ex) {
            log.error("error setting show gui property", ex);
            throw new IdropRuntimeException(ex);
        }
    }

    private void checkLogSuccessfulTransferActionPerformed(java.awt.event.ActionEvent evt) {
        log.info("updating log successful transfers to:{}", checkLogSuccessfulTransfer.isSelected());
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES, Boolean.toString(checkShowGUI.isSelected()));
        } catch (IdropException ex) {
            log.error("error setting log successful property", ex);
            throw new IdropRuntimeException(ex);
        }

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


                SynchManagerService synchConfigurationService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();

                try {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
                    throw new IdropRuntimeException(ex);
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

                try {
                    thisPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (synchConfigurationService.isSynchRunning(selectedSynchronization)) {
                            MessageManager.showMessage(thisPanel, "Cannot update the synchronization, a synch is currently running", MessageManager.TITLE_MESSAGE);
                            return;
                        }

                    idropCore.getIdropConfigurationService().updateSynchronization(synchronization);
                    MessageManager.showMessage(thisPanel, "Configuration updated", MessageManager.TITLE_MESSAGE);
                    ListSelectionModel lsm = (ListSelectionModel) thisPanel.getSynchTable().getSelectionModel();
                    SynchConfigTableModel model = (SynchConfigTableModel) thisPanel.getSynchTable().getModel();

                    if (isNew) {
                    
                        List<Synchronization> synchronizations = synchConfigurationService.listAllSynchronizations();

                        model.setSynchronizations(synchronizations);
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

                                    model.getSynchronizations().set(modelIdx, synchronization);
                                    model.fireTableDataChanged();
                                    break;
                                }
                            }
                        }
                    }

                    btnDeleteSynch.setEnabled(true);
                    btnUpdateSynch.setEnabled(true);
                    btnSynchNow.setEnabled(true);

                } catch (IdropException ex) {
                    MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                } catch (SynchException ex) {
                    MessageManager.showError(thisPanel, ex.getMessage(), MessageManager.TITLE_MESSAGE);
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
            }
        }
    }

    private void checkVerifyChecksumOnTransferActionPerformed(java.awt.event.ActionEvent evt) {
        log.info("updating verify checksom to:{}", checkVerifyChecksumOnTransfer.isSelected());
        try {
            idropCore.getIdropConfigurationService().updateConfig(IdropConfigurationService.VERIFY_CHECKSUM_ON_TRANSFER, Boolean.toString(checkVerifyChecksumOnTransfer.isSelected()));
        } catch (Exception ex) {
            log.error("error setting show gui property", ex);
            throw new IdropRuntimeException(ex);
        }
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        log.info("logging out to log in to a new grid");

        final IDROPConfigurationPanel thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                IRODSAccount savedAccount = idropCore.getIrodsAccount();
                idropCore.setIrodsAccount(null);
                iDrop idrop = (iDrop) thisPanel.getParent();
                LoginDialog loginDialog = new LoginDialog(thisPanel, idropCore);
                loginDialog.setVisible(true);

                if (idropCore.getIrodsAccount() == null) {
                    log.warn("no account, reverting");
                    idropCore.setIrodsAccount(savedAccount);
                } else {
                    idrop.reinitializeForChangedIRODSAccount();
                }
                refreshAccountData();
            }
        });

    }

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog((iDrop) this.getParent(), true);
        changePasswordDialog.setLocationRelativeTo(this);
        changePasswordDialog.setVisible(true);
    }

    protected JTable getSynchTable() {
        return jTableSynch;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnChooseIrodsSynch;
    private javax.swing.JButton btnChooseLocalSynch;
    private javax.swing.JButton btnDeleteSynch;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNewSynch;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnRefreshSynch;
    private javax.swing.JButton btnSynchDetails;
    private javax.swing.JButton btnSynchNow;
    private javax.swing.JButton btnUpdateSynch;
    private javax.swing.ButtonGroup buttonGroupSynchMode;
    private javax.swing.JCheckBox checkLogSuccessfulTransfer;
    private javax.swing.JCheckBox checkShowFileProgress;
    private javax.swing.JCheckBox checkShowGUI;
    private javax.swing.JCheckBox checkVerifyChecksumOnTransfer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox jcomboSynchFrequency;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHostLabel;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblPortLabel;
    private javax.swing.JLabel lblResource;
    private javax.swing.JLabel lblResourceLabel;
    private javax.swing.JLabel lblSynchDate;
    private javax.swing.JLabel lblSynchDateLabel;
    private javax.swing.JLabel lblSynchName;
    private javax.swing.JLabel lblSynchStatus;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblUserNameLabel;
    private javax.swing.JLabel lblZone;
    private javax.swing.JLabel lblZoneLabel;
    private javax.swing.JPanel panelSynchToolbar;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlConfigGrids;
    private javax.swing.JPanel pnlConfigIdrop;
    private javax.swing.JPanel pnlConfigSynch;
    private javax.swing.JPanel pnlConfigSynchDetails;
    private javax.swing.JPanel pnlConfigSynchListing;
    private javax.swing.JPanel pnlConfigTransfers;
    private javax.swing.JPanel pnlCurrentGrid;
    private javax.swing.JPanel pnlCurrentGridToolbar;
    private javax.swing.JPanel pnlIrodsSynch;
    private javax.swing.JPanel pnlLocalSynch;
    private javax.swing.JPanel pnlSynchData;
    private javax.swing.JPanel pnlSynchFrequency;
    private javax.swing.JPanel pnlSynchIcon;
    private javax.swing.JPanel pnlSynchMode;
    private javax.swing.JPanel pnlSynchName;
    private javax.swing.JPanel pnlSynchRefresh;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JRadioButton radioBackup;
    private javax.swing.JRadioButton radioFeed;
    private javax.swing.JRadioButton radioSynch;
    private javax.swing.JScrollPane scrollSynchTable;
    private javax.swing.JTabbedPane tabConfig;
    private javax.swing.JTextField txtIrodsPath;
    private javax.swing.JTextField txtLocalPath;
    private javax.swing.JTextField txtSynchName;
    // End of variables declaration//GEN-END:variables

    private void initWithConfigData() {
        IdropConfig idropConfig = idropCore.getIdropConfig();
        checkShowGUI.setSelected(idropConfig.isShowGuiAtStartup());
        checkLogSuccessfulTransfer.setSelected(idropConfig.isLogSuccessfulTransfers());
        checkVerifyChecksumOnTransfer.setSelected(idropConfig.isVerifyChecksum());
        checkShowFileProgress.setSelected(idropConfig.isIntraFileStatusCallbacks());
        refreshAccountData();
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

    private void refreshAccountData() {

        if (idropCore.getIrodsAccount() == null) {
            lblHost.setText("");
            lblPort.setText("");
            lblZone.setText("");
            lblResource.setText("");
            lblUserName.setText("");
        } else {
            lblHost.setText(idropCore.getIrodsAccount().getHost());
            lblPort.setText(String.valueOf(idropCore.getIrodsAccount().getPort()));
            lblZone.setText(idropCore.getIrodsAccount().getZone());
            lblResource.setText(idropCore.getIrodsAccount().getDefaultStorageResource());
            lblUserName.setText(idropCore.getIrodsAccount().getUserName());
        }
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
            log.error("unknown frequency type for synch:{}", selectedSynchronization.getFrequencyType());
            throw new IdropRuntimeException("unknown frequency type for synch");
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
        btnSynchDetails.setEnabled(lockStatus);
    }

    private void setSynchIcon(Synchronization synchronization) {

        JLabel labelToUse = null;

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

        pnlSynchIcon.removeAll();
        lblSynchStatus = labelToUse;
        pnlSynchIcon.add(lblSynchStatus);
        pnlSynchIcon.validate();

    }
}

class SynchListSelectionHandler implements ListSelectionListener {

    private final IDROPConfigurationPanel idropConfigurationPanel;

    SynchListSelectionHandler(IDROPConfigurationPanel configurationPanel) {
        this.idropConfigurationPanel = configurationPanel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

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