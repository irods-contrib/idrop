/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.User;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.PermissionsTableModel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class EditPermissionsDialog extends javax.swing.JDialog {

    private final IRODSFileSystem irodsFileSystem;
    private final IRODSAccount irodsAccount;
    private final boolean isCollection;
    private final int selectedRow;
    private final String selectedObjectFullPath;
    private final PermissionsTableModel tableModel;
    private final UserFilePermission permissionToUpdate;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(EditMetaDataDialog.class);
    
    /**
     * Creates new form EditPermissionsDialog
     */
    public EditPermissionsDialog(final javax.swing.JDialog parent,
            final boolean modal,
            int selectedRow,
            String selectedObjectFullPath,
            UserFilePermission permissionToUpdate,
            boolean isCollection,
            final IRODSFileSystem irodsFileSystem,
            final IRODSAccount irodsAccount,
            PermissionsTableModel model) {
        
        super(parent, modal);
        initComponents();
        
        this.irodsFileSystem = irodsFileSystem;
        this.irodsAccount = irodsAccount;
        this.selectedRow = selectedRow;
        this.isCollection = isCollection;
        this.selectedObjectFullPath = selectedObjectFullPath;
        this.tableModel = model;
        this.permissionToUpdate = permissionToUpdate;
        
        initData();
    }
    
    private void initData() {
        
        cbPermissionsPermission.addItem("READ");
        cbPermissionsPermission.addItem("WRITE");
        cbPermissionsPermission.addItem("OWN");
        
//        List<User> users = null;
//        try {
//            UserAO userAO = irodsFileSystem.getIRODSAccessObjectFactory()
//                    .getUserAO(irodsAccount);
//            users = userAO.findAll();
//            Collections.sort(users, new Comparator<User>() {
//                @Override
//                public int compare(final User object1, final User object2) {
//                    return object1.getName().compareTo(object2.getName());
//                }
//            });
//
//            for (User user : users) {
//                cbPermissionsUserName.addItem(user.getNameWithZone());
//            }
//        } catch (JargonException ex) {
//            log.error("cannot retrieve irods users list", ex);
//            JOptionPane.showMessageDialog(this, "Cannot retrieve list of iRODS users",
//                    "Edit Permissions", JOptionPane.PLAIN_MESSAGE);
//        }
        
//        cbPermissionsUserName.setSelectedItem(permissionToUpdate.getNameWithZone());
        lblUserName.setText(permissionToUpdate.getNameWithZone());
        cbPermissionsPermission.setSelectedItem(permissionToUpdate.getFilePermissionEnum().toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        pnlPermissionEdit = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        cbPermissionsPermission = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnPermissionsCancel = new javax.swing.JButton();
        btnPermissionsUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(460, 180));

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlPermissionEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 8, 20, 8));
        pnlPermissionEdit.setPreferredSize(new java.awt.Dimension(527, 200));
        pnlPermissionEdit.setLayout(new java.awt.BorderLayout());

        jLabel31.setText(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.jLabel31.text")); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel3.add(jLabel31);

        lblUserName.setText(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.lblUserName.text")); // NOI18N
        lblUserName.setPreferredSize(new java.awt.Dimension(260, 16));
        jPanel3.add(lblUserName);

        pnlPermissionEdit.add(jPanel3, java.awt.BorderLayout.NORTH);

        jLabel32.setText(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.jLabel32.text")); // NOI18N
        jLabel32.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel4.add(jLabel32);

        cbPermissionsPermission.setPreferredSize(new java.awt.Dimension(260, 27));
        jPanel4.add(cbPermissionsPermission);

        pnlPermissionEdit.add(jPanel4, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlPermissionEdit, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 6, 1));
        jPanel1.setPreferredSize(new java.awt.Dimension(525, 40));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(200, 34));
        jPanel2.setRequestFocusEnabled(false);

        btnPermissionsCancel.setText(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.btnPermissionsCancel.text")); // NOI18N
        btnPermissionsCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPermissionsCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnPermissionsCancel);

        btnPermissionsUpdate.setText(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.btnPermissionsUpdate.text")); // NOI18N
        btnPermissionsUpdate.setActionCommand(org.openide.util.NbBundle.getMessage(EditPermissionsDialog.class, "EditPermissionsDialog.btnPermissionsUpdate.actionCommand")); // NOI18N
        btnPermissionsUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPermissionsUpdateActionPerformed(evt);
            }
        });
        jPanel2.add(btnPermissionsUpdate);

        jPanel1.add(jPanel2, java.awt.BorderLayout.EAST);

        pnlMain.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPermissionsCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPermissionsCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnPermissionsCancelActionPerformed

    private void btnPermissionsUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPermissionsUpdateActionPerformed
        String selectedPermission = (String) cbPermissionsPermission
                .getSelectedItem();
        String tmpSelectedUser = lblUserName.getText();
        String selectedUser = null;

        // probably have to remove #zone from user name
        int idx = tmpSelectedUser.indexOf("#");
        if (idx >= 0) {
            selectedUser = tmpSelectedUser.substring(0, idx);
        } else {
            selectedUser = tmpSelectedUser;
        }
        
        // set updated permission type
        UserFilePermission newPermission = new UserFilePermission(
                        permissionToUpdate.getUserName(),
                        permissionToUpdate.getUserId(),
			FilePermissionEnum.valueOf(selectedPermission),
			permissionToUpdate.getUserType(),
                        permissionToUpdate.getUserZone());

        try {
//            UserAO userAO = irodsFileSystem.getIRODSAccessObjectFactory()
//                    .getUserAO(irodsAccount);
//            User user = userAO.findByName(tmpSelectedUser);
//
//            UserFilePermission newPermission = new UserFilePermission(selectedUser,
//                    user.getId(),
//                    FilePermissionEnum.valueOf(selectedPermission),
//                    user.getUserType(), user.getZone());
            CollectionAO collectionAO = irodsFileSystem
                    .getIRODSAccessObjectFactory().getCollectionAO(
                    irodsAccount);
            DataObjectAO dataObjectAO = irodsFileSystem
                    .getIRODSAccessObjectFactory().getDataObjectAO(
                    irodsAccount);

            if (newPermission.getFilePermissionEnum() == FilePermissionEnum.READ) {
                if (isCollection) {
                    collectionAO.setAccessPermissionRead(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName(), true);
                } else {
                    dataObjectAO.setAccessPermissionRead(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName());
                }
            } else if (newPermission.getFilePermissionEnum() == FilePermissionEnum.WRITE) {
                if (isCollection) {
                    collectionAO.setAccessPermissionWrite(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName(), true);
                } else {
                    dataObjectAO.setAccessPermissionWrite(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName());
                }
            } else if (newPermission.getFilePermissionEnum() == FilePermissionEnum.OWN) {
                if (isCollection) {
                    collectionAO.setAccessPermissionOwn(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName(), true);
                } else {
                    dataObjectAO.setAccessPermissionOwn(
                            newPermission.getUserZone(),
                            selectedObjectFullPath,
                            newPermission.getUserName());
                }
            }
            
            tableModel.updateRow(selectedRow, newPermission);
            
            JOptionPane.showMessageDialog(this,
                "Permission Updated Successfully",
                "Update Permissions", JOptionPane.PLAIN_MESSAGE);
            
        } catch (JargonException ex) {
            log.error("cannot update permissions", ex);
            JOptionPane.showMessageDialog(this, "Permission Update Failed",
                    "Update Permissions", JOptionPane.PLAIN_MESSAGE);
        }
        
        this.dispose();
    }//GEN-LAST:event_btnPermissionsUpdateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPermissionsCancel;
    private javax.swing.JButton btnPermissionsUpdate;
    private javax.swing.JComboBox cbPermissionsPermission;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPermissionEdit;
    // End of variables declaration//GEN-END:variables
}
