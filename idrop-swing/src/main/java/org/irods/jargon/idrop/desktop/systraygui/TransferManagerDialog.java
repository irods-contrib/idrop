/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.irods.jargon.conveyor.core.ConveyorBusyException;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferManagerTableModel;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.irods.jargon.transfer.dao.domain.TransferStateEnum;
import org.irods.jargon.transfer.dao.domain.TransferStatusEnum;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
// public class TransferManagerDialog extends javax.swing.JDialog implements ActionListener {
public class TransferManagerDialog extends javax.swing.JDialog implements ListSelectionListener {
    
    public static org.slf4j.Logger log = LoggerFactory.getLogger(TransferManagerTableModel.class);
    private Transfer selectedTableObject = null;
    private final iDrop idropGui;
    private final IDROPCore idropCore;
    private List<TransferAttempt> currentAttempts = null;

    /**
     * Creates new form TransferManagerDialog
     */    
    public TransferManagerDialog(final iDrop parent) throws ConveyorExecutionException {
        super(parent, false);
        initComponents();
        
        this.idropGui = parent;
        this.idropCore = parent.getiDropCore();
        
        initTransferTable();
    }
    
    public final void refreshTableView() {
        
        final TransferManagerDialog tmd = this;
        
        log.info("refreshing transfer table");
          
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                tmd.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                try {
                    int matchingRowForSelected = -1;
                    List<Transfer> transfers = idropCore.getConveyorService().getQueueManagerService().listAllTransfersInQueue();
                        
                    TransferManagerTableModel model = (TransferManagerTableModel)tblTransfers.getModel();
                    model.setTransfers(transfers);
                    model.fireTableDataChanged();
                    tblTransfers.revalidate();
                    
                    if (selectedTableObject != null) {
                        // previously selected table, refresh display, first, selecting same row

                        Transfer transfer;
                        for (int i = 0; i < tblTransfers.getModel().getRowCount(); i++) {
                            transfer = model.getTransferAtRow(i);
                            if (transfer.getId().longValue() == selectedTableObject.getId().longValue()) {
                                matchingRowForSelected = i;
                                break;
                            }
                        }
                        
                        if (matchingRowForSelected != -1) {
                            int selectedRowIndex = tblTransfers.convertRowIndexToView(matchingRowForSelected);
                            if (selectedRowIndex != -1) {
                                tblTransfers.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
                            }
                        }
                    }
                } catch (ConveyorExecutionException ex) {
                    log.error("exception updating transfer table", ex);
                    MessageManager.showError(tmd, ex.getMessage(), MessageManager.TITLE_MESSAGE);
                } finally {
                    tmd.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                
            }
        });
        
    }
    
    private void initTransferTable() throws ConveyorExecutionException {
        
        List<Transfer> transfers = idropCore.getConveyorService().getQueueManagerService().listAllTransfersInQueue();
        
        tblTransfers.setModel(new TransferManagerTableModel(idropCore, transfers));
        
        // make more room for summary column
        tblTransfers.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblTransfers.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblTransfers.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblTransfers.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblTransfers.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(6).setPreferredWidth(280);
      
        final TransferManagerDialog tmd = this;
        
        tblTransfers.getSelectionModel().addListSelectionListener(this);
    
//        tblTransfers.addMouseMotionListener(new MouseMotionAdapter(){
//            
//          Take out hover dropdown menu for now ...
//            public void mouseMoved(MouseEvent e)
//            {
//                JTable table = (JTable) e.getSource();
//                Point point = e.getPoint();
//                int row = table.rowAtPoint(point);
//                row = table.convertRowIndexToModel(row);
//                showPopup(e, row);
//            }
//            
//            public void showPopup(MouseEvent e, int row) {
//                List<TransferAttempt> attempts = null;
//                
//                JTable table = (JTable) e.getSource();
//                // get id hidden in last column of table
//                long transferID = (Long)table.getModel().getValueAt(row, 7);
//                try {
//                    Transfer transfer = idropCore.getConveyorService().getQueueManagerService().findTransferByTransferId(transferID);
//                    Transfer transferWithChildren = idropCore.getConveyorService().getQueueManagerService().initializeGivenTransferByLoadingChildren(transfer);
//                    attempts = transferWithChildren.getTransferAttempts();
//                    tmd.currentAttempts = attempts;
//                   
//                } catch (ConveyorExecutionException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//                
//                if (attempts.size() > 0) {
//                    JPopupMenu popup = new JPopupMenu();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM, d, yyyy : h:mm:ss a");
//                    int count =1;
//
//                    for ( TransferAttempt attempt: attempts) {
//
//                        StringBuilder menuText = new StringBuilder();
//                        menuText.append(count);
//                        menuText.append(") Transfer attempt start time: ");
//                        menuText.append(dateFormat.format(attempt.getAttemptStart()));
//                        menuText.append(",  Status: ");
//                        menuText.append(attempt.getAttemptStatus());
//                        menuText.append("  - Details ...");
//                        JMenuItem menuItem = new JMenuItem(menuText.toString());
//                        menuItem.addActionListener(tmd);
//                        popup.add(menuItem);
//                        count++;
//
//                    }
//
//                    popup.show(e.getComponent(), e.getX()+2, e.getY()+2);
//                }
//            }
//
//        });
    }
    
    private void enableTransferSpecificButtons() {
        boolean isRowSelected = (tblTransfers.getSelectedRow() != -1);
        
        // enable/disable info button if row in table is selected/deselected 
        btnTransferInfo.setEnabled(isRowSelected);
        
        // enable delete, restart, resubmit buttons if row is selected and transfer status is not PROCESSING
        btnRemoveSelected.setEnabled(isRowSelected &&
                (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnRestartSelected.setEnabled(isRowSelected &&
                (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnResubmitSelected.setEnabled(isRowSelected &&
                (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnCancel.setEnabled(isRowSelected);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if ( !lse.getValueIsAdjusting() ) {
            
           // save selected row transfer object
            int selectedRow = tblTransfers.getSelectedRow();
            if (selectedRow >=0) {
                selectedRow = tblTransfers.convertRowIndexToModel(selectedRow);
                TransferManagerTableModel model = (TransferManagerTableModel)tblTransfers.getModel();
                this.selectedTableObject = model.getTransferAtRow(selectedRow);
            }
            
            // enable appropriate buttons
            enableTransferSpecificButtons();
        }
    }
    
//    @Override
//    public void actionPerformed(ActionEvent ae) {
//        
//        // get selected attempt
//        if ( currentAttempts != null) {
//            String strIdx = ae.getActionCommand().split("[)]")[0];
//            int index = Integer.parseInt(strIdx);
//            TransferAttempt selectedAttempt = currentAttempts.get(index-1);
//            
//            // now show details dialog for transfer attempt
//        }    
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        pnlTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransfers = new javax.swing.JTable();
        pnlButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnTransferInfo = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnPurgeAll = new javax.swing.JButton();
        btnPurgeSuccessful = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnRemoveSelected = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnRestartSelected = new javax.swing.JButton();
        btnResubmitSelected = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        btnRefresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        bntClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 74));
        setPreferredSize(new java.awt.Dimension(980, 440));

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 4, 4, 4));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTable.setLayout(new java.awt.BorderLayout());

        tblTransfers.setAutoCreateRowSorter(true);
        tblTransfers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblTransfers);

        pnlTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlTable, java.awt.BorderLayout.CENTER);

        pnlButtons.setPreferredSize(new java.awt.Dimension(799, 40));
        pnlButtons.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(830, 39));

        btnTransferInfo.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnTransferInfo.text")); // NOI18N
        btnTransferInfo.setEnabled(false);
        btnTransferInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransferInfoActionPerformed(evt);
            }
        });
        jPanel1.add(btnTransferInfo);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator1);

        btnPurgeAll.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnPurgeAll.text")); // NOI18N
        btnPurgeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurgeAllActionPerformed(evt);
            }
        });
        jPanel1.add(btnPurgeAll);

        btnPurgeSuccessful.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnPurgeSuccessful.text")); // NOI18N
        jPanel1.add(btnPurgeSuccessful);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2);

        btnRemoveSelected.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnRemoveSelected.text")); // NOI18N
        btnRemoveSelected.setEnabled(false);
        btnRemoveSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSelectedActionPerformed(evt);
            }
        });
        jPanel1.add(btnRemoveSelected);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnCancel.text")); // NOI18N
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel);

        btnRestartSelected.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnRestartSelected.text")); // NOI18N
        btnRestartSelected.setEnabled(false);
        btnRestartSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartSelectedActionPerformed(evt);
            }
        });
        jPanel1.add(btnRestartSelected);

        btnResubmitSelected.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnResubmitSelected.text")); // NOI18N
        btnResubmitSelected.setEnabled(false);
        btnResubmitSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResubmitSelectedActionPerformed(evt);
            }
        });
        jPanel1.add(btnResubmitSelected);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator3);

        btnRefresh.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.btnRefresh.text")); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel1.add(btnRefresh);

        pnlButtons.add(jPanel1, java.awt.BorderLayout.CENTER);

        bntClose.setText(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.bntClose.text")); // NOI18N
        bntClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCloseActionPerformed(evt);
            }
        });
        jPanel2.add(bntClose);

        pnlButtons.add(jPanel2, java.awt.BorderLayout.EAST);

        pnlMain.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCloseActionPerformed
        idropGui.closeTransferManagerDialog();
        this.dispose();
    }//GEN-LAST:event_bntCloseActionPerformed

    private void btnPurgeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPurgeAllActionPerformed
        try {
            idropCore.getConveyorService().getQueueManagerService().purgeAllFromQueue();
        } catch (ConveyorBusyException ex) {
            log.error("exception purging all from transfer table", ex);
            MessageManager.showError(this,
                    "Transfer Queue Manager is currently busy. Please try again later.",
                    MessageManager.TITLE_MESSAGE);
        } catch (ConveyorExecutionException ex) {
            log.error("exception updating transfer table", ex);
            MessageManager.showError(this, ex.getMessage(), MessageManager.TITLE_MESSAGE);
        }
        refreshTableView();
    }//GEN-LAST:event_btnPurgeAllActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshTableView();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnTransferInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferInfoActionPerformed
        TransferInfoDialog transferInfoDialog = new TransferInfoDialog(this, selectedTableObject, idropCore);
        Toolkit tk = getToolkit();
        int x = (tk.getScreenSize().width - transferInfoDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - transferInfoDialog.getHeight()) / 2;
        transferInfoDialog.setLocation(x, y);
        transferInfoDialog.setModal(true);  
        transferInfoDialog.setVisible(true);
    }//GEN-LAST:event_btnTransferInfoActionPerformed

    private void btnRemoveSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
            idropCore.getConveyorService().getQueueManagerService().deleteTransferFromQueue(selectedTableObject);
            } catch (ConveyorBusyException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ConveyorExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            refreshTableView();
        }
    }//GEN-LAST:event_btnRemoveSelectedActionPerformed

    private void btnRestartSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestartSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore.getConveyorService().getQueueManagerService().enqueueRestartOfTransferOperation(selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error restarting transfer: {}", ex.getMessage());
                MessageManager.showError(this,
                    "Transfer Queue Manager is currently busy. Please try again later.",
                    MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error restarting transfer. Transfer may have already completed.";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg, MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }//GEN-LAST:event_btnRestartSelectedActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
         if (selectedTableObject != null) {
            try {
                idropCore.getConveyorService().getQueueManagerService().cancelTransfer(selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error restarting transfer: {}", ex.getMessage());
                MessageManager.showError(this,
                    "Transfer Queue Manager is currently busy. Please try again later.",
                    MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error cancelling transfer. ";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg, MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnResubmitSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResubmitSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore.getConveyorService().getQueueManagerService().enqueueResubmitOfTransferOperation(selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error resubmitting transfer: {}", ex.getMessage());
                MessageManager.showError(this,
                    "Transfer Queue Manager is currently busy. Please try again later.",
                    MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error resubmitting transfer. Transfer may have already completed.";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg, MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }//GEN-LAST:event_btnResubmitSelectedActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntClose;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPurgeAll;
    private javax.swing.JButton btnPurgeSuccessful;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRemoveSelected;
    private javax.swing.JButton btnRestartSelected;
    private javax.swing.JButton btnResubmitSelected;
    private javax.swing.JButton btnTransferInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblTransfers;
    // End of variables declaration//GEN-END:variables

}
