/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferManagerTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class TransferManagerDialog extends javax.swing.JDialog implements ActionListener {
    
    public static org.slf4j.Logger log = LoggerFactory.getLogger(TransferManagerTableModel.class);
    private Transfer selectedTableObject = null;
    private final iDrop idropGui;
    private final IDROPCore idropCore;

    /**
     * Creates new form TransferManagerDialog
     */    
    public TransferManagerDialog(final iDrop parent) throws ConveyorExecutionException {
        super(parent, false);
        initComponents();
        this.idropGui = parent;
        this.idropCore = parent.getiDropCore();
        initTransferTable();
        // refreshTableView();
    }
    
    public final void refreshTableView() {
        
        final TransferManagerDialog queueManagerDialog = this;
        
        final TransferManagerTableModel transferTableModel = (TransferManagerTableModel) tblTransfers.getModel();
        int selectedRow = tblTransfers.getSelectedRow();
        
        if (transferTableModel.getRowCount() > 0) {
            tblTransfers.setRowSelectionInterval(0, 0);
        }

        if (selectedRow > -1) {
            selectedTableObject = (Transfer) transferTableModel.getTransferAtRow(tblTransfers.convertRowIndexToModel(selectedRow));
        }
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                queueManagerDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                 
                        //resetDisplayFieldsAndStatus();
                        updateDetailsWithSelectedTable();
                 
                    
                } catch (Exception e) {
                    log.error("exception updating table", e);
                    MessageManager.showError(queueManagerDialog, e.getMessage(), MessageManager.TITLE_MESSAGE);
                } finally {
                    queueManagerDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                
            }
            
            private void updateDetailsWithSelectedTable() throws Exception {
                log.info("refreshing transfer table");
                
                idropCore.getConveyorService().getQueueManagerService().listAllTransfersInQueue();
                List<Transfer> transferQueue = null;
                
                transferQueue = idropCore.getConveyorService().getQueueManagerService().listAllTransfersInQueue();
                
                //queueManagerDialog.getLblHeader().setText("Current transfer queue");
               
                
                if (transferQueue != null) {
                    tblTransfers.setModel(new TransferManagerTableModel(idropCore, transferQueue));
                    int matchingRowForSelected = -1;
                    
                    if (selectedTableObject != null) {
                        // previously selected table, refresh display, first, selecting same row

                        Transfer transfer;
                        for (int i = 0; i < tblTransfers.getModel().getRowCount(); i++) {
                            transfer = transferTableModel.getTransferAtRow(i);
                            if (transfer.getId() == selectedTableObject.getId()) {
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
        tblTransfers.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(5).setPreferredWidth(280);
      
        final TransferManagerDialog tmd = this;
    
        tblTransfers.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e)
            {
                JTable table = (JTable) e.getSource();
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                row = table.convertRowIndexToModel(row);
                showPopup(e, row);
            }
            
            public void showPopup(MouseEvent e, int row) {
                List<TransferAttempt> attempts = null;
                
                JTable table = (JTable) e.getSource();
                // get id hidden in last column of table
                long transferID = (Long)table.getModel().getValueAt(row, 6);
                try {
                    Transfer transfer = idropCore.getConveyorService().getQueueManagerService().findTransferByTransferId(transferID);
                    Transfer transferWithChildren = idropCore.getConveyorService().getQueueManagerService().initializeGivenTransferByLoadingChildren(transfer);
                    attempts = transferWithChildren.getTransferAttempts();
                   
                } catch (ConveyorExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
                if (attempts.size() > 0) {
                    JPopupMenu popup = new JPopupMenu();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM, d, yyyy : h:mm:ss a");

                    for ( TransferAttempt attempt: attempts) {

                        StringBuilder menuText = new StringBuilder();
                        menuText.append("Transfer attempt start time: ");
                        menuText.append(dateFormat.format(attempt.getAttemptStart()));
                        menuText.append(",  Status: ");
                        menuText.append(attempt.getAttemptStatus());
                        menuText.append("  - Details ...");
                        JMenuItem menuItem = new JMenuItem(menuText.toString());
                        menuItem.addActionListener(tmd);
                        popup.add(menuItem);

                    }

                    popup.show(e.getComponent(), e.getX()+2, e.getY()+2);
                }
            }

        });
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
        pnlTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransfers = new javax.swing.JTable();
        pnlButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        bntClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(940, 440));

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTable.setLayout(new java.awt.BorderLayout());

        tblTransfers.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(tblTransfers);

        pnlTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlTable, java.awt.BorderLayout.CENTER);

        pnlButtons.setPreferredSize(new java.awt.Dimension(799, 40));
        pnlButtons.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 692, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );

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
        this.dispose();
    }//GEN-LAST:event_bntCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntClose;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblTransfers;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent ae) {
        String strId = ae.getActionCommand().split(",")[0];
        long transferID = Long.getLong(strId);
        // need to get selected transfer id
        //Transfer transfer = idropCore.getConveyorService().getQueueManagerService().findTransferByTransferId()    
        
    }

}
