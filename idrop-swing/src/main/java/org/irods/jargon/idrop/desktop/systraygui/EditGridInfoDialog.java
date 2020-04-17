/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.GridAccountService;
import org.irods.jargon.core.connection.AuthScheme;
import org.irods.jargon.core.connection.ClientServerNegotiationPolicy;
import org.irods.jargon.core.connection.ClientServerNegotiationPolicy.SslNegotiationPolicy;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.transfer.dao.domain.GridAccount;
import org.irods.jargon.transfer.exception.PassPhraseInvalidException;
import org.openide.util.Exceptions;

/**
 *
 * @author lisa
 */
public class EditGridInfoDialog extends javax.swing.JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -2068814595906991664L;
    private GridAccount gridAccount = null;
    private final IDROPCore idropCore;

    private final iDrop idrop = null;
    /**
     * Creates new form EditGridInfoDialog
     */
    public EditGridInfoDialog(final java.awt.Frame parent, final boolean modal,
            final IDROPCore idropCore, final GridAccount gridAccount) {
        // final iDrop idrop) {

        super(parent, modal);
        initComponents();
        this.gridAccount = gridAccount;
        this.idropCore = idropCore;
        // this.idrop = idrop;
        populateGridAccountInfo();
        initAuthSchemesCombo();
        getRootPane().setDefaultButton(btnOK);
    }

    private void populateGridAccountInfo() {
        String host = gridAccount.getHost();
        if (host != null) {
            lblHost.setText(host);
        }

        int port = gridAccount.getPort();
        txtPort.setText(String.valueOf(port));

        String zone = gridAccount.getZone();
        if (zone != null) {
            lblZone.setText(zone);
        }

        String user = gridAccount.getUserName();
        if (user != null) {
            lblUser.setText(user);
        }

        // need to get plain test password
        GridAccountService gridAccountService = idropCore.getConveyorService()
                .getGridAccountService();
        IRODSAccount irodAccountForPswd = null;
        String pswd = null;
        try {
            irodAccountForPswd = gridAccountService
                    .irodsAccountForGridAccount(gridAccount);
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                    Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Could not retrieve stored password for this account.",
                    "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
        }
        if (irodAccountForPswd != null) {
            pswd = irodAccountForPswd.getPassword();
        }
        if (pswd != null) {
            txtPassword.setText(pswd);
            txtVerifyPassword.setText(pswd);
        }

        String defResc = gridAccount.getDefaultResource();
        if (defResc != null) {
            txtDefaultResource.setText(defResc);
        }

        String initPath = gridAccount.getDefaultPath();
        if (initPath != null) {
            txtInitialPath.setText(initPath);
        }

        String comment = gridAccount.getComment();
        if (comment != null) {
            textareaComment.setText(comment);
        }
        
        for (int i = 0; i < comboSslNegotiation.getItemCount(); i++) {
            if (comboSslNegotiation.getItemAt(i).equals(gridAccount.getSslNegotiationPolicy())) {
                comboSslNegotiation.setSelectedIndex(i);
                break;
            }
        }
  
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initAuthSchemesCombo() {
        cbAuthScheme.setModel(new DefaultComboBoxModel(AuthScheme.values()));
        // also select correct auth scheme for this grid
        cbAuthScheme.setSelectedItem(gridAccount.getAuthScheme());
    }

    private IRODSAccount createIRODSAccountFromForm() {
        IRODSAccount acct = null;

        String host = gridAccount.getHost();
        String strPort = txtPort.getText().trim();
        int port = 0;
        if ((strPort != null) && (!strPort.isEmpty())) {
            port = Integer.parseInt(strPort);
        }
        String zone = gridAccount.getZone();
        String user = gridAccount.getUserName();
        String password = new String(txtPassword.getPassword()).trim();
        String verifyPassword = new String(txtVerifyPassword.getPassword())
                .trim();
        String defaultResc = txtDefaultResource.getText().trim();
        String initialPath = txtInitialPath.getText().trim();
        if ((txtInitialPath.getText() == null)
                || (txtInitialPath.getText().isEmpty())) {
            StringBuilder homeBuilder = new StringBuilder();
            homeBuilder.append("/");
            homeBuilder.append(zone);
            homeBuilder.append("/home/");
            homeBuilder.append(user);
            initialPath = homeBuilder.toString();
        }

        // check to make sure passwords match
        if (!password.equals(verifyPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match. Please try again.",
                    "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            return acct;
        }

        try {
            acct = IRODSAccount.instance(host, port, user, password,
                    initialPath, zone, defaultResc);
        } catch (JargonException | IllegalArgumentException ex) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Please enter grid account information. Host, port, zone, and user name are required.",
                            "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            return acct;
        }

        // now add authorization scheme to gridaccount
        AuthScheme scheme = (AuthScheme) cbAuthScheme.getSelectedItem();
        if (scheme != null) {
            acct.setAuthenticationScheme(scheme);
        }
        
         ClientServerNegotiationPolicy negotiationPolicy = new ClientServerNegotiationPolicy();
        try {
            negotiationPolicy.setSslNegotiationPolicy(EditGridInfoDialog.translateSslNegotiationToEnum((String)this.comboSslNegotiation.getSelectedItem()));
        } catch (JargonException ex) {
             Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                JOptionPane
                        .showMessageDialog(
                                this,
                                "Update of grid account failed. Unrecognized SSL negotiation setting.",
                                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
        }
        
        acct.setClientServerNegotiationPolicy(negotiationPolicy);

        return acct;
    }

    // private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent
    // evt) {
    // IRODSAccount acct = createIRODSAccountFromForm();
    // if (acct != null) {
    // ChangePasswordDialog changePasswordDialog = new
    // ChangePasswordDialog(this, true, idrop, acct);
    // changePasswordDialog.setLocation(
    // (int)this.getLocation().getX(), (int)this.getLocation().getY());
    // changePasswordDialog.setVisible(true);
    //
    // // need to put new password in gridAccount in case OK button is now
    // pressed in this dialog
    // String newPasswd = changePasswordDialog.getNewPassword();
    // if (newPasswd != null) {
    // acct.setPassword(newPasswd);
    // try {
    // this.gridAccount =
    // idropCore.getConveyorService().getGridAccountService().findGridAccountByIRODSAccount(acct);
    // } catch (ConveyorExecutionException ex) {
    // Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
    // null, ex);
    // MessageManager.showError(this,
    // "Cannot retrieve grid account information", "Change Password");
    // }
    // }
    // }
    //
    // }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        pnlGridDetails = new javax.swing.JPanel();
        lblHostLabel = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        lblPortLabel = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        lblZoneLabel = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        lblUserLabel = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblDefaultResource = new javax.swing.JLabel();
        txtDefaultResource = new javax.swing.JTextField();
        lblStartingCollection = new javax.swing.JLabel();
        txtInitialPath = new javax.swing.JTextField();
        lblAuthScheme = new javax.swing.JLabel();
        cbAuthScheme = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaComment = new javax.swing.JTextArea();
        txtPassword = new javax.swing.JPasswordField();
        lblPassword = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        txtVerifyPassword = new javax.swing.JPasswordField();
        lblSslNegotiation = new javax.swing.JLabel();
        comboSslNegotiation = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.title")); // NOI18N
        setName("editGridInfo"); // NOI18N
        setPreferredSize(new java.awt.Dimension(430, 480));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setPreferredSize(new java.awt.Dimension(394, 406));
        jPanel2.setLayout(new java.awt.BorderLayout());

        pnlGridDetails.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 4, 10));
        pnlGridDetails.setName("host"); // NOI18N
        pnlGridDetails.setPreferredSize(new java.awt.Dimension(382, 370));
        pnlGridDetails.setLayout(new java.awt.GridBagLayout());

        lblHostLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHostLabel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblHost.text")); // NOI18N
        lblHostLabel.setName("lblHost"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblHostLabel, gridBagConstraints);

        lblHost.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.host.text")); // NOI18N
        lblHost.setName("host"); // NOI18N
        lblHost.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlGridDetails.add(lblHost, gridBagConstraints);

        lblPortLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPortLabel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblPort.text")); // NOI18N
        lblPortLabel.setName("lblPort"); // NOI18N
        lblPortLabel.setPreferredSize(new java.awt.Dimension(29, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblPortLabel, gridBagConstraints);

        txtPort.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.port.text")); // NOI18N
        txtPort.setName("port"); // NOI18N
        txtPort.setPreferredSize(new java.awt.Dimension(200, 28));
        txtPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGridDetails.add(txtPort, gridBagConstraints);

        lblZoneLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblZoneLabel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblZone.text")); // NOI18N
        lblZoneLabel.setName("lblZone"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblZoneLabel, gridBagConstraints);

        lblZone.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.zone.text")); // NOI18N
        lblZone.setName("zone"); // NOI18N
        lblZone.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlGridDetails.add(lblZone, gridBagConstraints);

        lblUserLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUserLabel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblUser.text")); // NOI18N
        lblUserLabel.setName("lblUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblUserLabel, gridBagConstraints);

        lblUser.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.user.text")); // NOI18N
        lblUser.setName("user"); // NOI18N
        lblUser.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlGridDetails.add(lblUser, gridBagConstraints);

        lblDefaultResource.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDefaultResource.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblDefaultResource.text")); // NOI18N
        lblDefaultResource.setName("lblDefaultResource"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblDefaultResource, gridBagConstraints);

        txtDefaultResource.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.defaultResource.text")); // NOI18N
        txtDefaultResource.setName("defaultResource"); // NOI18N
        txtDefaultResource.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGridDetails.add(txtDefaultResource, gridBagConstraints);

        lblStartingCollection.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStartingCollection.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblStartingCollection.text")); // NOI18N
        lblStartingCollection.setName("lblStartingCollection"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblStartingCollection, gridBagConstraints);

        txtInitialPath.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.startingCollection.text")); // NOI18N
        txtInitialPath.setName("startingCollection"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGridDetails.add(txtInitialPath, gridBagConstraints);

        lblAuthScheme.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAuthScheme.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblAuthScheme.text")); // NOI18N
        lblAuthScheme.setName("lblAuthScheme"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblAuthScheme, gridBagConstraints);

        cbAuthScheme.setName("authScheme"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlGridDetails.add(cbAuthScheme, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(224, 84));

        textareaComment.setColumns(20);
        textareaComment.setRows(5);
        textareaComment.setName("comment"); // NOI18N
        jScrollPane1.setViewportView(textareaComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlGridDetails.add(jScrollPane1, gridBagConstraints);

        txtPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.password.text")); // NOI18N
        txtPassword.setName("password"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlGridDetails.add(txtPassword, gridBagConstraints);

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblPassword.text")); // NOI18N
        lblPassword.setName("lblPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblPassword, gridBagConstraints);

        lblConfirmPassword.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblConfirmPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblConfirmPassword.text")); // NOI18N
        lblConfirmPassword.setName("lblConfirmPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlGridDetails.add(lblConfirmPassword, gridBagConstraints);

        txtVerifyPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.confirmPassword.text")); // NOI18N
        txtVerifyPassword.setName("confirmPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        pnlGridDetails.add(txtVerifyPassword, gridBagConstraints);

        lblSslNegotiation.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSslNegotiation.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblSslNegotiation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlGridDetails.add(lblSslNegotiation, gridBagConstraints);

        comboSslNegotiation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CS_NEG_DONT_CARE", "CS_NEG_REFUSE", "CS_NEG_REQUIRE", "NO_NEGOTIATION" }));
        comboSslNegotiation.setToolTipText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.comboSslNegotiation.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlGridDetails.add(comboSslNegotiation, gridBagConstraints);

        jPanel2.add(pnlGridDetails, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 1, 4, 1));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 60));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel5.setPreferredSize(new java.awt.Dimension(190, 50));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnCancel.setMnemonic('C');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.setMaximumSize(null);
        btnCancel.setMinimumSize(null);
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(110, 37));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel5.add(btnCancel);

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnOK.setMnemonic('S');
        btnOK.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnOk.text")); // NOI18N
        btnOK.setToolTipText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnOk.toolTipText")); // NOI18N
        btnOK.setLabel(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnOK.label")); // NOI18N
        btnOK.setName("btnOk"); // NOI18N
        btnOK.setPreferredSize(new java.awt.Dimension(90, 37));
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        jPanel5.add(btnOK);

        jPanel3.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jPanel2.add(jPanel3, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortActionPerformed

    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }// GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnOKActionPerformed

        IRODSAccount gridInfo = createIRODSAccountFromForm();

        if (gridInfo != null) {

            if (!validateGridAccount(gridInfo)) {
                MessageManager
                        .showError(this,
                                "Unable to process login, the server or account appears to be invalid");
                gridInfo = null;

                return;
            }

            GridAccountService gridAccountService = idropCore
                    .getConveyorService().getGridAccountService();
            
            

            // now add authorization scheme to gridaccount
            AuthScheme scheme = (AuthScheme) cbAuthScheme.getSelectedItem();
            if ((scheme != null) && (!(scheme.getTextValue().isEmpty()))) {
                gridInfo.setAuthenticationScheme(scheme);
            }
            
            ClientServerNegotiationPolicy negotiationPolicy = new ClientServerNegotiationPolicy();
            try {
                negotiationPolicy.setSslNegotiationPolicy(translateSslNegotiationToEnum((String) this.comboSslNegotiation.getSelectedItem()));
            } catch (JargonException ex) {
               Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                JOptionPane
                        .showMessageDialog(
                                this,
                                "Update of grid account failed. Unrecognized SSL negotiation setting.",
                                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            }
            
            gridInfo.setClientServerNegotiationPolicy(negotiationPolicy);

            try {
                gridAccountService
                        .addOrUpdateGridAccountBasedOnIRODSAccount(gridInfo);
                // use this when Mike adds comment to
                // addOrUpdateGridAccountBasedOnIRODSAccount()
                // gridAccountService.addOrUpdateGridAccountBasedOnIRODSAccount(gridInfo,
                // comment);
            } catch (PassPhraseInvalidException ex) {
                gridInfo = null;
                Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                JOptionPane
                        .showMessageDialog(
                                this,
                                "Update of grid account failed. Pass phrase is invalid.",
                                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                gridInfo = null;
                Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            dispose();
        }
    }// GEN-LAST:event_btnOKActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cbAuthScheme;
    private javax.swing.JComboBox<String> comboSslNegotiation;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAuthScheme;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblDefaultResource;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHostLabel;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPortLabel;
    private javax.swing.JLabel lblSslNegotiation;
    private javax.swing.JLabel lblStartingCollection;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUserLabel;
    private javax.swing.JLabel lblZone;
    private javax.swing.JLabel lblZoneLabel;
    private javax.swing.JPanel pnlGridDetails;
    private javax.swing.JTextArea textareaComment;
    private javax.swing.JTextField txtDefaultResource;
    private javax.swing.JTextField txtInitialPath;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPort;
    private javax.swing.JPasswordField txtVerifyPassword;
    // End of variables declaration//GEN-END:variables

    private boolean validateGridAccount(final IRODSAccount gridInfo) {
        try {
            idropCore.getIrodsFileSystem().getIRODSAccessObjectFactory()
                    .authenticateIRODSAccount(gridInfo);
            return true;
        } catch (AuthenticationException ex) {
            return false;
        } catch (JargonException je) {
            return false;
        }
    }
    
    /**
     * Ripe for refactoring once a proper enum is in place for ssl negotiation.
     * @param sslNegotiationPolicyString
     * @return {@link SslNegotiationPolicy} enum value
     * @throws JargonException 
     */
    public static SslNegotiationPolicy translateSslNegotiationToEnum(final String sslNegotiationPolicyString) throws JargonException {
        if (sslNegotiationPolicyString == null) {
            throw new IllegalArgumentException("null sslNegotiationPolicyString");
        }
        SslNegotiationPolicy returnPolicy = SslNegotiationPolicy.CS_NEG_DONT_CARE;
        String trimPolicy = sslNegotiationPolicyString.trim();
        if (sslNegotiationPolicyString.isEmpty()) {
            // just use default
        } else if (trimPolicy.equals(SslNegotiationPolicy.CS_NEG_DONT_CARE.toString())) {
            returnPolicy=SslNegotiationPolicy.CS_NEG_DONT_CARE;
        } else if (trimPolicy.equals(SslNegotiationPolicy.CS_NEG_REFUSE.toString())) {
            returnPolicy = SslNegotiationPolicy.CS_NEG_REFUSE;
        } else if (trimPolicy.equals(SslNegotiationPolicy.CS_NEG_REQUIRE.toString())) {
            returnPolicy = SslNegotiationPolicy.CS_NEG_REQUIRE;
        } else if (trimPolicy.equals(SslNegotiationPolicy.NO_NEGOTIATION.toString())) {
            returnPolicy = SslNegotiationPolicy.NO_NEGOTIATION;
        } else {
            throw new JargonException("unknown negotiation policy");
        }
        
        return returnPolicy;
        
    }
    
}
