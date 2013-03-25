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
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.transfer.dao.domain.GridAccount;
import org.irods.jargon.transfer.exception.PassPhraseInvalidException;
import org.openide.util.Exceptions;

/**
 *
 * @author lisa
 */
public class EditGridInfoDialog extends javax.swing.JDialog {
    
    private GridAccount gridAccount = null;
    private final IDROPCore idropCore;
//    private final iDrop idrop;

    /**
     * Creates new form EditGridInfoDialog
     */
    public EditGridInfoDialog(java.awt.Frame parent,
            boolean modal,
            final IDROPCore idropCore,
            final GridAccount gridAccount) {
//            final iDrop idrop) {
        
        super(parent, modal);
        initComponents();
        this.gridAccount = gridAccount;
        this.idropCore = idropCore;
//        this.idrop = idrop;
        populateGridAccountInfo();
        initAuthSchemesCombo();
        this.getRootPane().setDefaultButton(btnOK);
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
        GridAccountService gridAccountService = idropCore.getConveyorService().getGridAccountService();
        IRODSAccount irodAccountForPswd = null;
        String pswd = null;
        try {
            irodAccountForPswd = gridAccountService.irodsAccountForGridAccount(gridAccount);
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                    Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                this,
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
    }
    
    private void initAuthSchemesCombo() {
        cbAuthScheme.setModel(new DefaultComboBoxModel(AuthScheme.values()));
        // also select correct auth scheme for this grid
        cbAuthScheme.setSelectedItem(gridAccount.getAuthScheme());
    }
    
    private IRODSAccount createIRODSAccountFromForm() {
        IRODSAccount acct = null;
        
        String host = this.gridAccount.getHost();
        String strPort = txtPort.getText().trim();
        int port=0;
        if ((strPort != null) && (!strPort.isEmpty())) {
            port = Integer.valueOf(strPort).intValue();
        }
        String zone = this.gridAccount.getZone();
        String user = this.gridAccount.getUserName();
        String password = txtPassword.getText().trim();
        String verifyPassword = txtVerifyPassword.getText().trim();
        String defaultResc = txtDefaultResource.getText().trim();
        String initialPath = txtInitialPath.getText().trim();
        if ((txtInitialPath.getText() == null) || (txtInitialPath.getText().isEmpty())) {
            StringBuilder homeBuilder = new StringBuilder();
            homeBuilder.append("/");
            homeBuilder.append(zone);
            homeBuilder.append("/home/");
            homeBuilder.append(user);
            initialPath = homeBuilder.toString();
        }
        
        // check to make sure passwords match
        if (! password.equals(verifyPassword)) {
            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match. Please try again.",
                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            return acct;
        }
        
        GridAccountService gridAccountService = idropCore.getConveyorService().getGridAccountService();
        
        // need to do this to retrieve plain text password
        IRODSAccount irodAccountForPswd = null;
        
        // not needed if collecting password from form
//        try {
//            irodAccountForPswd = gridAccountService.irodsAccountForGridAccount(gridAccount);
//        } catch (ConveyorExecutionException ex) {
//            Logger.getLogger(EditGridInfoDialog.class.getName()).log(
//                    Level.SEVERE, null, ex);
//            JOptionPane.showMessageDialog(
//                this,
//                "Update of grid account failed. Could not store password.",
//                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
//        }
        
        try {
            acct = IRODSAccount.instance(host, port, user, password, initialPath, zone, defaultResc);
        } catch (JargonException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter grid account information. Host, port, zone, and user name are required.",
                "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            return acct;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
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
        
        return acct;
    }
    
//    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {                                                  
//        IRODSAccount acct = createIRODSAccountFromForm();
//        if (acct != null) {
//            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(this, true, idrop, acct);
//            changePasswordDialog.setLocation(
//                        (int)this.getLocation().getX(), (int)this.getLocation().getY());
//            changePasswordDialog.setVisible(true);
//            
//            // need to put new password in gridAccount in case OK button is now pressed in this dialog
//            String newPasswd = changePasswordDialog.getNewPassword();
//            if (newPasswd != null) {
//                acct.setPassword(newPasswd);
//                try {
//                this.gridAccount = 
//                        idropCore.getConveyorService().getGridAccountService().findGridAccountByIRODSAccount(acct);
//            } catch (ConveyorExecutionException ex) {
//                Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
//                        null, ex);         
//                MessageManager.showError(this, "Cannot retrieve grid account information", "Change Password");
//            }
//            }
//        }
//
//    } 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDefaultResource = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtInitialPath = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cbAuthScheme = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaComment = new javax.swing.JTextArea();
        txtPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtVerifyPassword = new javax.swing.JPasswordField();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(430, 460));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setPreferredSize(new java.awt.Dimension(394, 406));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 4, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(382, 370));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel1, gridBagConstraints);

        lblHost.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblHost.text")); // NOI18N
        lblHost.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(lblHost, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel2.text")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(29, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel2, gridBagConstraints);

        txtPort.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.txtPort.text")); // NOI18N
        txtPort.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtPort, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel3, gridBagConstraints);

        lblZone.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblZone.text")); // NOI18N
        lblZone.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(lblZone, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel4, gridBagConstraints);

        lblUser.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.lblUser.text")); // NOI18N
        lblUser.setPreferredSize(new java.awt.Dimension(0, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(lblUser, gridBagConstraints);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel6, gridBagConstraints);

        txtDefaultResource.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.txtDefaultResource.text")); // NOI18N
        txtDefaultResource.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtDefaultResource, gridBagConstraints);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel7, gridBagConstraints);

        txtInitialPath.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.txtInitialPath.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtInitialPath, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(cbAuthScheme, gridBagConstraints);

        jLabel9.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel9, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(224, 84));

        textareaComment.setColumns(20);
        textareaComment.setRows(5);
        jScrollPane1.setViewportView(textareaComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        txtPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.txtPassword.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(txtPassword, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel10.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel10, gridBagConstraints);

        txtVerifyPassword.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.txtVerifyPassword.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(txtVerifyPassword, gridBagConstraints);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 4, 1));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 60));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel5.setPreferredSize(new java.awt.Dimension(190, 39));

        btnCancel.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel5.add(btnCancel);

        btnOK.setText(org.openide.util.NbBundle.getMessage(EditGridInfoDialog.class, "EditGridInfoDialog.btnOK.text")); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        jPanel5.add(btnOK);

        jPanel3.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel2.add(jPanel3, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
    
        IRODSAccount gridInfo = createIRODSAccountFromForm();
        if (gridInfo != null) {
            GridAccountService gridAccountService = idropCore.getConveyorService().getGridAccountService();

            // now add authorization scheme to gridaccount
            AuthScheme scheme = (AuthScheme) cbAuthScheme.getSelectedItem();
            if ((scheme != null) && (!(scheme.getTextValue().isEmpty()))) {
                gridInfo.setAuthenticationScheme(scheme);
            }

            // now see if there is a comment to add to gridaccount
            String comment = "";
            if ((textareaComment.getText() != null) || (!textareaComment.getText().isEmpty())) {
                comment = textareaComment.getText().trim();
            }

            try {
                gridAccountService.addOrUpdateGridAccountBasedOnIRODSAccount(gridInfo);
                // use this when Mike adds comment to addOrUpdateGridAccountBasedOnIRODSAccount()
                // gridAccountService.addOrUpdateGridAccountBasedOnIRODSAccount(gridInfo, comment);
            } catch (PassPhraseInvalidException ex) {
                gridInfo = null;
                Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                    this,
                    "Update of grid account failed. Pass phrase is invalid.",
                    "Edit Grid Account", JOptionPane.ERROR_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                gridInfo = null;
                Logger.getLogger(EditGridInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            this.dispose();
        }
    }//GEN-LAST:event_btnOKActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cbAuthScheme;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblZone;
    private javax.swing.JTextArea textareaComment;
    private javax.swing.JTextField txtDefaultResource;
    private javax.swing.JTextField txtInitialPath;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPort;
    private javax.swing.JPasswordField txtVerifyPassword;
    // End of variables declaration//GEN-END:variables
}
