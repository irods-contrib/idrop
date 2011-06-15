/*
 * PreferencesDialog.java
 *
 * Created on Nov 23, 2010, 2:58:18 PM
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.slf4j.LoggerFactory;

/**
 * Dialog that can manage preferences
 * @author mikeconway
 */
public class ChangePasswordDialog extends javax.swing.JDialog {

    private final iDrop idrop;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(ChangePasswordDialog.class);

    /** Creates new form PreferencesDialog */
    public ChangePasswordDialog(final iDrop idrop, boolean modal) {
        super(idrop, modal);
        this.idrop = idrop;
        initComponents();
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

        tabPreferences = new javax.swing.JTabbedPane();
        pnlPasswords = new javax.swing.JPanel();
        lblCurrentAccountLabel = new javax.swing.JLabel();
        lblCurrentAccount = new javax.swing.JLabel();
        lblNewPassword = new javax.swing.JLabel();
        passwdNewPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        passwdConfirmPassword = new javax.swing.JPasswordField();
        btnUpdatePassword = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("iDrop - Preferences");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(551, 400));

        tabPreferences.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPreferencesStateChanged(evt);
            }
        });

        pnlPasswords.setLayout(new java.awt.GridBagLayout());

        lblCurrentAccountLabel.setText("Current account:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPasswords.add(lblCurrentAccountLabel, gridBagConstraints);

        lblCurrentAccount.setText("XXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPasswords.add(lblCurrentAccount, gridBagConstraints);

        lblNewPassword.setText("New password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPasswords.add(lblNewPassword, gridBagConstraints);

        passwdNewPassword.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPasswords.add(passwdNewPassword, gridBagConstraints);

        lblConfirmPassword.setText("Confirm password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPasswords.add(lblConfirmPassword, gridBagConstraints);

        passwdConfirmPassword.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPasswords.add(passwdConfirmPassword, gridBagConstraints);

        btnUpdatePassword.setMnemonic('P');
        btnUpdatePassword.setText("Update Password");
        btnUpdatePassword.setToolTipText("Change the current password to the new values");
        btnUpdatePassword.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePasswordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        pnlPasswords.add(btnUpdatePassword, gridBagConstraints);

        tabPreferences.addTab("Passwords", null, pnlPasswords, "Manage passwords");

        getContentPane().add(tabPreferences, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabPreferencesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPreferencesStateChanged
        // TODO add your handling code here:
        log.debug("preferences tab state changed: {}", evt);
        if (pnlPasswords.isVisible()) {
            setUpPasswordPanel();
        }
    }//GEN-LAST:event_tabPreferencesStateChanged

    private void btnUpdatePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePasswordActionPerformed

        log.info("changing password, doing edits first");
        initializePasswordColors();

        if (passwdNewPassword.getPassword().length == 0) {
            passwdNewPassword.setBackground(Color.red);
        }

        if (passwdConfirmPassword.getPassword().length == 0) {
            passwdConfirmPassword.setBackground(Color.red);
        }

        if (passwdNewPassword.getPassword().length == 0 || passwdConfirmPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(idrop, "New or confirm password is missing");
            return;
        }

        String newPassword = new String(passwdNewPassword.getPassword());
        String confirmPassword = new String(passwdConfirmPassword.getPassword());

        if (newPassword.equals(confirmPassword)) {
            // passwords match
        } else {
            passwdNewPassword.setBackground(Color.red);
            passwdConfirmPassword.setBackground(Color.red);
            JOptionPane.showMessageDialog(idrop, "New and confirm password do not match");
            return;
        }

        log.info("edits pass, updating password");
        try {
            log.info("check queue for any jobs for the account, these have the old password.");
            IRODSAccount irodsAccount = idrop.getIrodsAccount();
            List<LocalIRODSTransfer> recentQueue = idrop.getiDropCore().getTransferManager().getRecentQueue();
            for (LocalIRODSTransfer localIRODSTransfer : recentQueue) {
                if (localIRODSTransfer.getTransferHost().equals(irodsAccount.getHost())
                        && localIRODSTransfer.getTransferZone().equals(irodsAccount.getZone())
                        && localIRODSTransfer.getTransferUserName().equals(irodsAccount.getUserName())) {
                    // FIXME: right now, do not differentiate transfers that are comlete, this is a hack right now
                    // if (localIRODSTransfer.getTransferState().equals(localIRODSTransfer.TRANSFER_STATE_COMPLETE)) {
                    //      log.info("matched transfer was complete, ignored");
                    //  } else {
                    JOptionPane.showMessageDialog(this, "Transfers for this account are pending, this account can not be changed until completed and purged");
                    return;
                    // }
                }
            }
            UserAO userAO = idrop.getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().getUserAO(idrop.getIrodsAccount());
            userAO.changeAUserPasswordByThatUser(irodsAccount.getUserName(), irodsAccount.getPassword(), newPassword);
            log.info("password changed, resetting iRODS Account");
            IRODSAccount newAccount = new IRODSAccount(
                    irodsAccount.getHost(),
                    irodsAccount.getPort(),
                    irodsAccount.getUserName(),
                    newPassword,
                    irodsAccount.getHomeDirectory(),
                    irodsAccount.getZone(),
                    irodsAccount.getDefaultStorageResource()
                    );
            idrop.setIrodsAccount(newAccount);
            JOptionPane.showMessageDialog(this, "Password was changed");
            passwdNewPassword.setText("");
            passwdConfirmPassword.setText("");
       
        } catch (JargonException ex) {
            Logger.getLogger(ChangePasswordDialog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            idrop.getiDropCore().closeIRODSConnection(idrop.getiDropCore().getIrodsAccount());
        }
    }//GEN-LAST:event_btnUpdatePasswordActionPerformed

    private void setUpPasswordPanel() {
        String acctUri = idrop.getIrodsAccount().toString();
        lblCurrentAccount.setText(acctUri);
        initializePasswordColors();
    }

    private void initializePasswordColors() {
        passwdNewPassword.setBackground(Color.white);
        passwdConfirmPassword.setBackground(Color.white);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdatePassword;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblCurrentAccount;
    private javax.swing.JLabel lblCurrentAccountLabel;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JPasswordField passwdConfirmPassword;
    private javax.swing.JPasswordField passwdNewPassword;
    private javax.swing.JPanel pnlPasswords;
    private javax.swing.JTabbedPane tabPreferences;
    // End of variables declaration//GEN-END:variables
}
