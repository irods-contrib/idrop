/*
 * SetupWizard.java
 *
 * Created on Jul 12, 2011, 5:54:17 PM
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.MessageManager;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.FrequencyType;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.dao.domain.SynchronizationType;
import org.irods.jargon.transfer.engine.synch.ConflictingSynchException;
import org.irods.jargon.transfer.engine.synch.SynchException;
import org.irods.jargon.transfer.util.HibernateUtil;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 * Initial setup of iDrop and synchronization as part of first load
 * @author mikeconway
 * d.setLocationRelativeTo(null);

 */
public class SetupWizard extends javax.swing.JDialog {

    private final IDROPCore idropCore;
    private final IdropConfigurationService idropConfigurationService;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SetupWizard.class);
    public final String SETUP_ERROR_TITLE = "iDrop - Setup";
    private int tabStep = 0;
    private boolean tabAdvancing = false;

    /** Creates new form SetupWizard */
    public SetupWizard(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        super.setLocationRelativeTo(parent);
        initComponents();
        iDrop idrop = (iDrop) parent;
        idropCore = idrop.getiDropCore();
        idropConfigurationService = idropCore.getIdropConfigurationService();
        tabWizardTabs.addChangeListener(new ChangeListener() {

            /*
             * Quash a manual tab move, can only be done via the 'wizard' 
             */
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!tabAdvancing) {
                    tabWizardTabs.setSelectedIndex(tabStep);
                }
                tabAdvancing = false;
            }
        });
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
        java.awt.GridBagConstraints gridBagConstraints;

        bnGroupSynchType = new javax.swing.ButtonGroup();
        panelTop = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        tabWizardTabs = new javax.swing.JTabbedPane();
        panelTabSeeSysTray = new javax.swing.JPanel();
        panelTabSeeSysTrayQuestion = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        panelTabSeeSysTrayAnswer = new javax.swing.JPanel();
        btnSeeSystemTrayYes = new javax.swing.JButton();
        btnSeeSystemTrayNo = new javax.swing.JButton();
        panelTabNameDevice = new javax.swing.JPanel();
        panelTabNameDeviceQuestion = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        panelTabNameDeviceAnswer = new javax.swing.JPanel();
        lblDeviceName = new javax.swing.JLabel();
        txtDeviceName = new javax.swing.JTextField();
        pnlInitialSynchSetup = new javax.swing.JPanel();
        pnlInitialSynchSetupQuestion = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnlInitialSynchSetupAnswer = new javax.swing.JPanel();
        pnlSynchData = new javax.swing.JPanel();
        pnlLocalSynch = new javax.swing.JPanel();
        txtLocalPath = new javax.swing.JTextField();
        btnChooseLocalSynch = new javax.swing.JButton();
        pnlIrodsSynch = new javax.swing.JPanel();
        txtIrodsPath = new javax.swing.JTextField();
        btnChooseIrodsSynch = new javax.swing.JButton();
        pnlSynchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        radioBackup = new javax.swing.JRadioButton();
        radioFeed = new javax.swing.JRadioButton();
        radioSynch = new javax.swing.JRadioButton();
        pnlSynchFrequency = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcomboSynchFrequency = new javax.swing.JComboBox();
        pnlWizardToolbar = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnLater = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.title")); // NOI18N

        panelTop.setBackground(new java.awt.Color(102, 102, 102));
        panelTop.setFont(new java.awt.Font("Lucida Grande", 0, 12));

        lblWelcome.setFont(new java.awt.Font("Lucida Grande", 0, 18));
        lblWelcome.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.lblWelcome.text")); // NOI18N
        lblWelcome.setMaximumSize(null);
        lblWelcome.setMinimumSize(null);
        lblWelcome.setPreferredSize(null);
        panelTop.add(lblWelcome);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        tabWizardTabs.setBackground(new java.awt.Color(102, 102, 102));
        tabWizardTabs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabWizardTabsKeyPressed(evt);
            }
        });

        panelTabSeeSysTray.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jLabel2.text")); // NOI18N
        panelTabSeeSysTrayQuestion.add(jLabel2);

        panelTabSeeSysTray.add(panelTabSeeSysTrayQuestion, java.awt.BorderLayout.NORTH);

        btnSeeSystemTrayYes.setMnemonic('y');
        btnSeeSystemTrayYes.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnSeeSystemTrayYes.toolTipText")); // NOI18N
        btnSeeSystemTrayYes.setLabel(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnSeeSystemTrayYes.label")); // NOI18N
        btnSeeSystemTrayYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeeSystemTrayYesActionPerformed(evt);
            }
        });
        panelTabSeeSysTrayAnswer.add(btnSeeSystemTrayYes);

        btnSeeSystemTrayNo.setMnemonic('n');
        btnSeeSystemTrayNo.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnSeeSystemTrayNo.text")); // NOI18N
        btnSeeSystemTrayNo.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnSeeSystemTrayNo.toolTipText")); // NOI18N
        btnSeeSystemTrayNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeeSystemTrayNoActionPerformed(evt);
            }
        });
        panelTabSeeSysTrayAnswer.add(btnSeeSystemTrayNo);

        panelTabSeeSysTray.add(panelTabSeeSysTrayAnswer, java.awt.BorderLayout.SOUTH);

        tabWizardTabs.addTab(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.panelTabSeeSysTray.TabConstraints.tabTitle"), panelTabSeeSysTray); // NOI18N

        panelTabNameDevice.setLayout(new java.awt.BorderLayout());

        jLabel3.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jLabel3.text")); // NOI18N
        panelTabNameDeviceQuestion.add(jLabel3);

        panelTabNameDevice.add(panelTabNameDeviceQuestion, java.awt.BorderLayout.CENTER);

        lblDeviceName.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.lblDeviceName.text")); // NOI18N
        panelTabNameDeviceAnswer.add(lblDeviceName);

        txtDeviceName.setColumns(20);
        txtDeviceName.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.txtDeviceName.text")); // NOI18N
        panelTabNameDeviceAnswer.add(txtDeviceName);

        panelTabNameDevice.add(panelTabNameDeviceAnswer, java.awt.BorderLayout.SOUTH);

        tabWizardTabs.addTab(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.panelTabNameDevice.TabConstraints.tabTitle"), panelTabNameDevice); // NOI18N

        pnlInitialSynchSetup.setLayout(new java.awt.BorderLayout());

        jLabel4.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jLabel4.text")); // NOI18N
        pnlInitialSynchSetupQuestion.add(jLabel4);

        pnlInitialSynchSetup.add(pnlInitialSynchSetupQuestion, java.awt.BorderLayout.CENTER);

        pnlInitialSynchSetupAnswer.setLayout(new java.awt.GridBagLayout());

        pnlSynchData.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSynchData.setLayout(new java.awt.GridBagLayout());

        pnlLocalSynch.setLayout(new java.awt.GridBagLayout());

        txtLocalPath.setColumns(60);
        txtLocalPath.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.txtLocalPath.text")); // NOI18N
        txtLocalPath.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.txtLocalPath.toolTipText")); // NOI18N
        txtLocalPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocalPathActionPerformed(evt);
            }
        });
        pnlLocalSynch.add(txtLocalPath, new java.awt.GridBagConstraints());

        btnChooseLocalSynch.setMnemonic('c');
        btnChooseLocalSynch.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnChooseLocalSynch.text")); // NOI18N
        btnChooseLocalSynch.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnChooseLocalSynch.toolTipText")); // NOI18N
        btnChooseLocalSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseLocalSynchActionPerformed(evt);
            }
        });
        pnlLocalSynch.add(btnChooseLocalSynch, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlSynchData.add(pnlLocalSynch, gridBagConstraints);

        pnlIrodsSynch.setLayout(new java.awt.GridBagLayout());

        txtIrodsPath.setColumns(60);
        txtIrodsPath.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.txtIrodsPath.text")); // NOI18N
        txtIrodsPath.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.txtIrodsPath.toolTipText")); // NOI18N
        txtIrodsPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIrodsPathActionPerformed(evt);
            }
        });
        pnlIrodsSynch.add(txtIrodsPath, new java.awt.GridBagConstraints());

        btnChooseIrodsSynch.setMnemonic('i');
        btnChooseIrodsSynch.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnChooseIrodsSynch.text")); // NOI18N
        btnChooseIrodsSynch.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnChooseIrodsSynch.toolTipText")); // NOI18N
        btnChooseIrodsSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseIrodsSynchActionPerformed(evt);
            }
        });
        pnlIrodsSynch.add(btnChooseIrodsSynch, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlSynchData.add(pnlIrodsSynch, gridBagConstraints);

        pnlSynchMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSynchMode.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jLabel1.text")); // NOI18N
        pnlSynchMode.add(jLabel1);

        bnGroupSynchType.add(radioBackup);
        radioBackup.setSelected(true);
        radioBackup.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.radioBackup.text")); // NOI18N
        pnlSynchMode.add(radioBackup);

        bnGroupSynchType.add(radioFeed);
        radioFeed.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.radioFeed.text")); // NOI18N
        radioFeed.setEnabled(false);
        pnlSynchMode.add(radioFeed);

        bnGroupSynchType.add(radioSynch);
        radioSynch.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.radioSynch.text")); // NOI18N
        radioSynch.setEnabled(false);
        pnlSynchMode.add(radioSynch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        pnlSynchData.add(pnlSynchMode, gridBagConstraints);

        pnlSynchFrequency.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSynchFrequency.setLayout(new java.awt.GridLayout(0, 1));

        jLabel5.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jLabel5.text")); // NOI18N
        pnlSynchFrequency.add(jLabel5);

        jcomboSynchFrequency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hourly", "Weekly", "Daily", "Every 15 Minutes", " " }));
        jcomboSynchFrequency.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.jcomboSynchFrequency.toolTipText")); // NOI18N
        pnlSynchFrequency.add(jcomboSynchFrequency);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        pnlSynchData.add(pnlSynchFrequency, gridBagConstraints);

        pnlInitialSynchSetupAnswer.add(pnlSynchData, new java.awt.GridBagConstraints());

        pnlInitialSynchSetup.add(pnlInitialSynchSetupAnswer, java.awt.BorderLayout.SOUTH);

        tabWizardTabs.addTab(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.pnlInitialSynchSetup.TabConstraints.tabTitle"), pnlInitialSynchSetup); // NOI18N

        getContentPane().add(tabWizardTabs, java.awt.BorderLayout.CENTER);

        pnlWizardToolbar.setBackground(new java.awt.Color(102, 102, 102));
        pnlWizardToolbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnBack.setMnemonic('b');
        btnBack.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnBack.text")); // NOI18N
        btnBack.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnBack.toolTipText")); // NOI18N
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        pnlWizardToolbar.add(btnBack);

        btnForward.setMnemonic('b');
        btnForward.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnForward.text")); // NOI18N
        btnForward.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnForward.toolTipText")); // NOI18N
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });
        pnlWizardToolbar.add(btnForward);

        btnLater.setMnemonic('l');
        btnLater.setText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnLater.text")); // NOI18N
        btnLater.setToolTipText(org.openide.util.NbBundle.getMessage(SetupWizard.class, "SetupWizard.btnLater.toolTipText")); // NOI18N
        btnLater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaterActionPerformed(evt);
            }
        });
        pnlWizardToolbar.add(btnLater);

        getContentPane().add(pnlWizardToolbar, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** 
     * User indicates that he can see the system tray icon, right now should not show GUI at startup
     * @param evt 
     */
    private void btnSeeSystemTrayYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeeSystemTrayYesActionPerformed
        saveSeeSystemTrayYes();
    }

    private void saveSeeSystemTrayYes() throws IdropRuntimeException {
        log.info("indicates system try shown, set to not load gui");
        try {
            idropConfigurationService.updateConfig(IdropConfigurationService.SHOW_GUI, "false");
            log.info("config is updated");
        } catch (IdropException ex) {
            log.error("error updating configuration", ex);
            throw new IdropRuntimeException("error updating configuration", ex);
        }
        advanceTab();
    }//GEN-LAST:event_btnSeeSystemTrayYesActionPerformed

    private void saveDeviceName() {
        if (txtDeviceName.getText().length() == 0) {
            txtDeviceName.setBackground(Color.red);
            MessageManager.showError(this, "Device name is not entered", SETUP_ERROR_TITLE);
            return;
        }
        try {
            idropConfigurationService.updateConfig(IdropConfigurationService.DEVICE_NAME, txtDeviceName.getText());
            log.info("device name is set to:{}", txtDeviceName.getText());
            // FIXME: check name in iRODS
        } catch (IdropException ex) {
            throw new IdropRuntimeException("error setting device name", ex);
        }
        advanceTab();

    }

    private void tabWizardTabsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabWizardTabsKeyPressed
    }//GEN-LAST:event_tabWizardTabsKeyPressed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        reverseTab();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnLaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaterActionPerformed
        log.info("indicates system try not shown, set to always load gui");
        try {
            idropConfigurationService.updateConfig(IdropConfigurationService.SHOW_GUI, "true");
            log.info("clearing device name to force wizard next time");
            idropConfigurationService.removeConfigProperty(IdropConfigurationService.DEVICE_NAME);
            log.info("config is updated");
        } catch (IdropException ex) {
            log.error("error updating configuration", ex);
            throw new IdropRuntimeException("error updating configuration", ex);
        }
        this.dispose();
    }//GEN-LAST:event_btnLaterActionPerformed

    private void btnChooseLocalSynchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseLocalSynchActionPerformed
        // TODO add your handling code here:
        JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(false);
        localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // FIXME: look at NPE here when cancel
        int returnVal = localFileChooser.showOpenDialog(this);
        txtLocalPath.setText(localFileChooser.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_btnChooseLocalSynchActionPerformed

    private void btnChooseIrodsSynchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseIrodsSynchActionPerformed
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
    }//GEN-LAST:event_btnChooseIrodsSynchActionPerformed

    private void txtIrodsPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIrodsPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIrodsPathActionPerformed

    private void txtLocalPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLocalPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocalPathActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        // forward acts differently according to the current tab
        if (tabWizardTabs.getSelectedIndex() == 0) {
            // advance from 'can you see icon'
            saveSeeSystemTrayYes();
        } else if (tabWizardTabs.getSelectedIndex() == 1) {
            // advance tabs from 'name device;
            saveDeviceName();
        } else if (tabWizardTabs.getSelectedIndex() == 2) {
            // advance from set up synch
            saveSynch();
        }
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnSeeSystemTrayNoActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSeeSystemTrayNoActionPerformed
        log.info("indicates system try not shown, set to always load gui");
        try {
            idropConfigurationService.updateConfig(IdropConfigurationService.SHOW_GUI, "true");
            log.info("config is updated");
        } catch (IdropException ex) {
            log.error("error updating configuration", ex);
            throw new IdropRuntimeException("error updating configuration", ex);
        }
        advanceTab();
    }// GEN-LAST:event_btnSeeSystemTrayNoActionPerformed

    private void reverseTab() {
        int tabLength = tabWizardTabs.getTabCount();
        int currentTab = tabWizardTabs.getSelectedIndex();

        if (--currentTab < 0) {
            tabStep = 0;
        } else {
            tabAdvancing = true;
            tabWizardTabs.setSelectedIndex(currentTab);
            tabStep = currentTab;
        }
    }

    private void advanceTab() {
        int tabLength = tabWizardTabs.getTabCount();
        int currentTab = tabWizardTabs.getSelectedIndex();

        if (++currentTab >= tabLength) {
            log.info("done with tabs");
            finishWizard();
            return;
        }

        tabAdvancing = true;
        tabWizardTabs.setSelectedIndex(currentTab);

        // for synch setup, if a synch exists, do not allow setup
        if (currentTab == 2) {
            try {
                if (idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService().listAllSynchronizations().size() > 0) {
                    log.info("synch already present, skip");
                    finishWizard();
                    return;
                } else {
                    log.info("will proceed to synch setup wizard");
                }
            } catch (SynchException ex) {
                log.error("error looking for existing synchs", ex);
                throw new IdropRuntimeException(ex);
            }
        }

        tabStep = currentTab;

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bnGroupSynchType;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnChooseIrodsSynch;
    private javax.swing.JButton btnChooseLocalSynch;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnLater;
    private javax.swing.JButton btnSeeSystemTrayNo;
    private javax.swing.JButton btnSeeSystemTrayYes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox jcomboSynchFrequency;
    private javax.swing.JLabel lblDeviceName;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel panelTabNameDevice;
    private javax.swing.JPanel panelTabNameDeviceAnswer;
    private javax.swing.JPanel panelTabNameDeviceQuestion;
    private javax.swing.JPanel panelTabSeeSysTray;
    private javax.swing.JPanel panelTabSeeSysTrayAnswer;
    private javax.swing.JPanel panelTabSeeSysTrayQuestion;
    private javax.swing.JPanel panelTop;
    private javax.swing.JPanel pnlInitialSynchSetup;
    private javax.swing.JPanel pnlInitialSynchSetupAnswer;
    private javax.swing.JPanel pnlInitialSynchSetupQuestion;
    private javax.swing.JPanel pnlIrodsSynch;
    private javax.swing.JPanel pnlLocalSynch;
    private javax.swing.JPanel pnlSynchData;
    private javax.swing.JPanel pnlSynchFrequency;
    private javax.swing.JPanel pnlSynchMode;
    private javax.swing.JPanel pnlWizardToolbar;
    private javax.swing.JRadioButton radioBackup;
    private javax.swing.JRadioButton radioFeed;
    private javax.swing.JRadioButton radioSynch;
    private javax.swing.JTabbedPane tabWizardTabs;
    private javax.swing.JTextField txtDeviceName;
    private javax.swing.JTextField txtIrodsPath;
    private javax.swing.JTextField txtLocalPath;
    // End of variables declaration//GEN-END:variables

    private void finishWizard() {
        log.info("finishing wizard");
        this.dispose();
    }

    private void saveSynch() {
        if (txtLocalPath.getText().trim().length() == 0 && txtIrodsPath.getText().trim().length() == 0) {
            log.info("ignoring synch for now");
        } else if (txtLocalPath.getText().trim().length() == 0 || txtIrodsPath.getText().trim().length() == 0) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Skip sych?",
                    "Not all synch data entered, do you wish to skip? (This may be configured later)",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                advanceTab();
            } else {
                log.info("retry setup of synch");
            }
        } else {
            try {
                log.info("saving synch data");
                Synchronization synchronization = new Synchronization();
                synchronization.setCreatedAt(new Date());
                synchronization.setDefaultResourceName(idropCore.getIrodsAccount().getDefaultStorageResource());
                synchronization.setFrequencyType(FrequencyType.EVERY_HOUR); // FIXME: create code to set this viz the combo
                synchronization.setIrodsHostName(idropCore.getIrodsAccount().getHost());

                synchronization.setIrodsPassword(HibernateUtil.obfuscate(idropCore.getIrodsAccount().getPassword()));

                synchronization.setIrodsPort(idropCore.getIrodsAccount().getPort());
                synchronization.setIrodsSynchDirectory(txtIrodsPath.getText());
                synchronization.setLocalSynchDirectory(txtLocalPath.getText());
                synchronization.setIrodsUserName(idropCore.getIrodsAccount().getUserName());
                synchronization.setIrodsZone(idropCore.getIrodsAccount().getZone());
                synchronization.setName("Default");
                synchronization.setSynchronizationMode(SynchronizationType.ONE_WAY_LOCAL_TO_IRODS); // FIXME: set properly from radio

                this.idropConfigurationService.createNewSynchronization(synchronization);
                advanceTab();
            } catch (JargonException ex) {
                MessageManager.showError(this, ex.getMessage(), SETUP_ERROR_TITLE);
                throw new IdropRuntimeException(ex);
            } catch (IdropException ex) {
                MessageManager.showError(this, ex.getMessage(), SETUP_ERROR_TITLE);
                throw new IdropRuntimeException(ex);
            } catch (ConflictingSynchException ex) {
                MessageManager.showError(this, ex.getMessage(), SETUP_ERROR_TITLE);
                throw new IdropRuntimeException(ex);

            }
        }
    }
}
