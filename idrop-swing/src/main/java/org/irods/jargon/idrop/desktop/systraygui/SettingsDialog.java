/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.CollapsiblePane;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.HyperLinkButton;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
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
        GridBagConstraints userSettingsConstraints = new GridBagConstraints();
        userSettingsConstraints.gridx = 0;
        userSettingsConstraints.gridy = 0;
        userSettingsConstraints.gridwidth = 1;
        userSettingsConstraints.fill = GridBagConstraints.BOTH;
        userSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
        userSettingsConstraints.weightx = 0.1;
        userSettingsConstraints.weighty = 0.0;
        JScrollPane sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpUserSettings);
        pnlCollapsibles.add(sp, userSettingsConstraints); 
        
        setupDataTransferSettingsPanel();
        GridBagConstraints dataTransferSettingsConstraints = new GridBagConstraints();
        dataTransferSettingsConstraints.gridx = 0;
        dataTransferSettingsConstraints.gridy = 1;
        dataTransferSettingsConstraints.gridwidth = 1;
        dataTransferSettingsConstraints.fill = GridBagConstraints.BOTH;
        dataTransferSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
        dataTransferSettingsConstraints.weightx = 0.1;
        dataTransferSettingsConstraints.weighty = 0.1;
        CollapsiblePane cpDataTransferSettings = new CollapsiblePane(pnlCollapsibles, "Data Transfer Settings", pnlDataTransferSettings);
        sp = new JScrollPane();
        sp.setBorder(new EmptyBorder(0,0,0,0));
        sp.setViewportView(cpDataTransferSettings);
        pnlCollapsibles.add(sp, dataTransferSettingsConstraints);
        
        initWithConfigData();
        
    }
    
    private void setupUserSettingsPanel() {
        HyperLinkButton btnPerformDiff = new HyperLinkButton("Manage data resources");
        btnPerformDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageDataResourcesActionPerformed(evt);
            }
        });
        pnlUserSettings.add(btnPerformDiff);
        //pnlUserSettings.add(filler1);
        pnlUserSettings.add(chkShowGUI);
        pnlUserSettings.add(chkShowTransferProgress);
    }
    
    private void setupDataTransferSettingsPanel() {
        spinMaxTransferAttempts.setValue(3);
    }
    
    private void initWithConfigData() {

        IdropConfig idropConfig = idropCore.getIdropConfig();
        chkShowGUI.setSelected(idropConfig.isShowGuiAtStartup());
        chkLogTransfers.setSelected(idropConfig
                .isLogSuccessfulTransfers());
        chkShowTransferProgress.setSelected(idropConfig
                .isIntraFileStatusCallbacks());
        chkAllowRerouting.setSelected(idropConfig
                .isAllowConnectionRerouting());
        chkRestartFailedConn.setSelected(idropConfig.isConnectionRestart());
        spinMaxTransferAttempts.setValue(idropConfig.getMaxTransferErrors());
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
        pnlMain = new javax.swing.JPanel();
        pnlCollapsibles = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        bntSave = new javax.swing.JButton();

        chkShowTransferProgress.setText(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkShowTransferProgress.text")); // NOI18N

        chkShowGUI.setSelected(true);
        chkShowGUI.setLabel(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.chkShowGUI.label")); // NOI18N

        pnlUserSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pnlUserSettings.setLayout(new java.awt.GridLayout(3, 1));

        pnlDataTransferSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SettingsDialog.class, "SettingsDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(475, 330));

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
        
        JOptionPane.showMessageDialog(null, "Successfully saved settings", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }//GEN-LAST:event_bntSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntSave;
    private javax.swing.JButton btnCancel;
    private javax.swing.JCheckBox chkAllowRerouting;
    private javax.swing.JCheckBox chkLogTransfers;
    private javax.swing.JCheckBox chkRestartFailedConn;
    private javax.swing.JCheckBox chkShowGUI;
    private javax.swing.JCheckBox chkShowTransferProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCollapsibles;
    private javax.swing.JPanel pnlDataTransferSettings;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlUserSettings;
    private javax.swing.JSpinner spinMaxTransferAttempts;
    // End of variables declaration//GEN-END:variables
}
