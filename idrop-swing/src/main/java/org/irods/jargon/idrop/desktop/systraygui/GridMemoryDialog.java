/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.irods.jargon.conveyor.core.ConveyorBusyException;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.GridAccountService;
import org.irods.jargon.core.connection.AuthScheme;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.auth.AuthResponse;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.GridInfoTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.MetadataTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.GridAccount;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class GridMemoryDialog extends javax.swing.JDialog implements
        ListSelectionListener {
    
    private GridMemoryDialog dialog;
    private IDROPCore idropCore;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(MetadataTableModel.class);

    /**
     * Creates new form GridMemoryDialog
     */
    public GridMemoryDialog(java.awt.Frame parent, boolean modal, IDROPCore idropCore) {
        super(parent, modal);
        initComponents();
        this.idropCore = idropCore;
        makeTextAreaLikeLabel();
        initGridInfoTable();
        
        this.getRootPane().setDefaultButton(btnLogin);
    }
    
    private void makeTextAreaLikeLabel() {
        
        textAreaInfo.setEditable(false);  
        textAreaInfo.setCursor(null);  
        textAreaInfo.setOpaque(false);  
        textAreaInfo.setFocusable(false);  
        textAreaInfo.setFont(UIManager.getFont("Label.font"));      
        textAreaInfo.setWrapStyleWord(true);  
        textAreaInfo.setLineWrap(true);
    }
    
    private void initGridInfoTable() {
        this.dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {      
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    GridAccountService gridAccountService = idropCore.getConveyorService().getGridAccountService();
                    List<GridAccount> gridAccounts = null;
                    gridAccounts = gridAccountService.findAll();

                    GridInfoTableModel gridInfoTableModel = new GridInfoTableModel(gridAccounts);

                    tableGridInfo.setModel(gridInfoTableModel);
                    tableGridInfo.getSelectionModel().addListSelectionListener(dialog);
                    tableGridInfo.validate();
                } catch (ConveyorExecutionException ex) {
                    Logger.getLogger(GridMemoryDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
                
                tableGridInfo.addMouseListener(new MouseAdapter() {  
                    public void mouseClicked(MouseEvent evt) {  
                        if (evt.getClickCount() == 2) {  
                            Point pnt = evt.getPoint();  
                            int row = tableGridInfo.rowAtPoint(pnt);
                            GridInfoTableModel model = (GridInfoTableModel) tableGridInfo.getModel();
                            GridAccount gridTableData = (GridAccount)model.getRow(row);
                            GridAccount gridAccount = getStoredGridAccountFromGridTableData(gridTableData);
                            EditGridInfoDialog editGridInfoDialog = new EditGridInfoDialog(
                                null, true, idropCore, gridAccount);

                            editGridInfoDialog.setLocation(
                                (int)dialog.getLocation().getX(), (int)dialog.getLocation().getY());
                            editGridInfoDialog.setVisible(true);
                        }    
                    }  
                });
                
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    private void updateGridInfoDeleteBtnStatus(int selectedRowCount) {
        // delete button should only be enabled when there is a tableGridInfo selection
        btnDeleteGridInfo.setEnabled(selectedRowCount > 0);
    }
    
    private void updateLoginBtnStatus(int selectedRowCount) {
        // delete button should only be enabled when there is a tableGridInfo selection
        btnLogin.setEnabled(selectedRowCount > 0);
    }
    
    
    private boolean processLogin() {
        
        IRODSAccount irodsAccount = null;
        GridAccount loginAccount = null;
        
        // get selected grid account
        GridInfoTableModel tm = (GridInfoTableModel)tableGridInfo.getModel();
        int row = tableGridInfo.getSelectedRow();
        GridAccount gridTableData = tm.getRow(row);
        loginAccount = getStoredGridAccountFromGridTableData(gridTableData);

        if (loginAccount != null) {
            try {
                irodsAccount = idropCore.getConveyorService().getGridAccountService().irodsAccountForGridAccount(loginAccount);
            } catch (ConveyorExecutionException ex) {
                Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                 null, ex);         
                MessageManager.showError(this, "Cannot retrieve irods account from selected grid account", "Login Error");
            }

            AuthScheme scheme = loginAccount.getAuthScheme();
            if ((scheme != null) && (scheme.equals(AuthScheme.PAM.name()))) {
                irodsAccount.setAuthenticationScheme(AuthScheme.PAM);
            } 

            IRODSFileSystem irodsFileSystem = null;

            /*
             * getting userAO will attempt the login
             */

            try {
                irodsFileSystem = idropCore.getIrodsFileSystem();
                   AuthResponse authResponse = irodsFileSystem.getIRODSAccessObjectFactory().authenticateIRODSAccount(irodsAccount);
                    idropCore.setIrodsAccount(authResponse.getAuthenticatedIRODSAccount());
                    try {
                        idropCore.getIdropConfigurationService().saveLogin(irodsAccount);
                    } catch (IdropException ex) {
                        throw new IdropRuntimeException("error saving irodsAccount", ex);
                    }
                    this.dispose();
                } catch (JargonException ex) {
                    if (ex.getMessage().indexOf("Connection refused") > -1) {
                        Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                        MessageManager.showError(this, "Cannot connect to the server, is it down?", "Login Error");

                        return false;
                    } else if (ex.getMessage().indexOf("Connection reset") > -1) {
                        Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                        MessageManager.showError(this, "Cannot connect to the server, is it down?", "Login Error");

                        return false;
                    } else if (ex.getMessage().indexOf("io exception opening socket") > -1) {
                        Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                        MessageManager.showError(this, "Cannot connect to the server, is it down?", "Login Error");

                        return false;
                    } else {
                        Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                        MessageManager.showError(this, "login error - unable to log in, or invalid user id", "Login Error");

                        return false;
                    }
                } finally {
                    if (irodsFileSystem != null) {
                        irodsFileSystem.closeAndEatExceptions();
                }
            }
        }
        else {
            MessageManager.showError(this, "Cannot connect to the server, is grid account valid?", "Login Error");
            return false;
        }
        
        return true;
    }
    
    // Use the Grid Account data retrieved from the GridInfoTable to retrieve
    // the full record from the DB
    GridAccount getStoredGridAccountFromGridTableData(GridAccount gridTableData) {
        IRODSAccount irodsAccount = null;
        GridAccount storedGridAccount = null;
        
        if ( gridTableData != null) {
            try {
                irodsAccount = IRODSAccount.instance(gridTableData.getHost(), 0,
                        gridTableData.getUserName(), new String(), new String(),
                        gridTableData.getZone(), new String());
            } catch (JargonException ex) {
                Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                            null, ex);
                MessageManager.showError(this, "Cannot retrieve grid account information", "Retrieve Grid Account Information");
            }
            
            try {
                storedGridAccount = 
                        idropCore.getConveyorService().getGridAccountService().findGridAccountByIRODSAccount(irodsAccount);
            } catch (ConveyorExecutionException ex) {
                Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                        null, ex);         
                MessageManager.showError(this, "Cannot retrieve grid account information", "Retrieve Grid Account Information");
            }
        }
        
        return storedGridAccount; 
    }
    
    
    // ListSelectionListener methods
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        int selectedRowCount = 0;
        
        if (!lse.getValueIsAdjusting()) {
            selectedRowCount = tableGridInfo.getSelectedRowCount();
            updateGridInfoDeleteBtnStatus(selectedRowCount);
            updateLoginBtnStatus(selectedRowCount);
        } 
    }
    // end ListSelectionListener methods
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlGridInfoTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableGridInfo = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        btnAddGridInfo = new javax.swing.JButton();
        btnDeleteGridInfo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        textAreaInfo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(580, 350));

        jPanel1.setPreferredSize(new java.awt.Dimension(580, 230));
        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlGridInfoTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10));
        pnlGridInfoTable.setPreferredSize(new java.awt.Dimension(600, 150));
        pnlGridInfoTable.setLayout(new java.awt.BorderLayout());

        tableGridInfo.setPreferredSize(new java.awt.Dimension(100, 64));
        jScrollPane3.setViewportView(tableGridInfo);

        pnlGridInfoTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.setPreferredSize(new java.awt.Dimension(568, 40));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel8.setPreferredSize(new java.awt.Dimension(100, 44));

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 36, Short.MAX_VALUE)
        );

        jPanel7.add(jPanel8, java.awt.BorderLayout.EAST);

        jPanel9.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        btnAddGridInfo.setText(org.openide.util.NbBundle.getMessage(GridMemoryDialog.class, "GridMemoryDialog.btnAddGridInfo.text")); // NOI18N
        btnAddGridInfo.setPreferredSize(new java.awt.Dimension(22, 24));
        btnAddGridInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGridInfoActionPerformed(evt);
            }
        });
        jPanel16.add(btnAddGridInfo);

        btnDeleteGridInfo.setText(org.openide.util.NbBundle.getMessage(GridMemoryDialog.class, "GridMemoryDialog.btnDeleteGridInfo.text")); // NOI18N
        btnDeleteGridInfo.setEnabled(false);
        btnDeleteGridInfo.setPreferredSize(new java.awt.Dimension(22, 24));
        btnDeleteGridInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteGridInfoActionPerformed(evt);
            }
        });
        jPanel16.add(btnDeleteGridInfo);

        jPanel9.add(jPanel16, java.awt.BorderLayout.WEST);

        jPanel7.add(jPanel9, java.awt.BorderLayout.WEST);

        pnlGridInfoTable.add(jPanel7, java.awt.BorderLayout.SOUTH);

        jPanel1.add(pnlGridInfoTable, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(180, 50));
        jPanel2.setPreferredSize(new java.awt.Dimension(609, 50));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel6.setPreferredSize(new java.awt.Dimension(40, 46));

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 46, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.WEST);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(GridMemoryDialog.class, "GridMemoryDialog.btnCancel.text_1")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnLogin.setText(org.openide.util.NbBundle.getMessage(GridMemoryDialog.class, "GridMemoryDialog.btnLogin.text")); // NOI18N
        btnLogin.setEnabled(false);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(372, Short.MAX_VALUE)
                .add(btnCancel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnLogin)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnLogin))
                .addContainerGap())
        );

        jPanel2.add(jPanel10, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 10, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(609, 70));
        jPanel3.setLayout(new java.awt.BorderLayout());

        textAreaInfo.setBackground(new java.awt.Color(238, 238, 238));
        textAreaInfo.setColumns(20);
        textAreaInfo.setRows(5);
        textAreaInfo.setText(org.openide.util.NbBundle.getMessage(GridMemoryDialog.class, "GridMemoryDialog.textAreaInfo.text")); // NOI18N
        jPanel3.add(textAreaInfo, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddGridInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGridInfoActionPerformed
        CreateGridInfoDialog createGridInfoDialog = new CreateGridInfoDialog(
            null, true, idropCore);

        createGridInfoDialog.setLocation(
            (int)this.getLocation().getX(), (int)this.getLocation().getY());
        createGridInfoDialog.setVisible(true);

        IRODSAccount irodsAccount = createGridInfoDialog.getGridInfo();

        // first remove this user's entry from table if there is one
        if (irodsAccount != null) {
            try {
                GridInfoTableModel tm = (GridInfoTableModel)tableGridInfo.getModel();
                tm.deleteRow(irodsAccount);

                // now add to table
                tm.addRow(irodsAccount);
            } catch (JargonException ex) {
                Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                            null, ex);
                MessageManager.showError(this, "Addition of grid account failed", "Create Grid Account");
            }
        }
    }//GEN-LAST:event_btnAddGridInfoActionPerformed

    private void btnDeleteGridInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteGridInfoActionPerformed

        int ans = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete a grid account?",
                "Delete Grid Account",
                JOptionPane.YES_NO_OPTION);
        
        if (ans == JOptionPane.YES_OPTION) {
        
            int[] selectedRows = tableGridInfo.getSelectedRows();
            int numRowsSelected = selectedRows.length;

            // have to remove rows in reverse
            for(int i=numRowsSelected-1; i>=0; i--) {
                int selectedRow = selectedRows[i];
                if (selectedRow >= 0) {
                    try {
                        GridInfoTableModel model = (GridInfoTableModel) tableGridInfo.getModel();
                        try {
                            // delete grid account from service
                            idropCore.getConveyorService().getGridAccountService().deleteGridAccount((GridAccount)model.getRow(selectedRow));

                            // then remove from table
                            model.deleteRow(selectedRow);

                        } catch (ConveyorBusyException ex) {
                            Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                            MessageManager.showError(this, "Transfer for this grid account is currently in progess.\nPlease try again later.", "Delete Grid Account");
                        } catch (ConveyorExecutionException ex) {
                            Logger.getLogger(GridMemoryDialog.class.getName()).log(Level.SEVERE,
                                null, ex);
                            MessageManager.showError(this, "Deletion of grid account failed", "Delete Grid Account");
                        }

                    } catch (JargonException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnDeleteGridInfoActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        if(processLogin()) {
            this.dispose();
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddGridInfo;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteGridInfo;
    private javax.swing.JButton btnLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlGridInfoTable;
    private javax.swing.JTable tableGridInfo;
    private javax.swing.JTextArea textAreaInfo;
    // End of variables declaration//GEN-END:variables

}
