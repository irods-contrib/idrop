/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Dialog;
import java.util.List;
import javax.swing.JDialog;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferManagerTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class TransferManagerDialog extends javax.swing.JDialog {
    
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
        refreshTableView();
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
                    tblTransfers.setModel(new TransferManagerTableModel(transferQueue));
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
        tblTransfers.setModel(new TransferManagerTableModel(transfers));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TransferManagerDialog.class, "TransferManagerDialog.title")); // NOI18N

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTable.setLayout(new java.awt.BorderLayout());

        tblTransfers.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(tblTransfers);

        pnlTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlTable, java.awt.BorderLayout.CENTER);

        org.jdesktop.layout.GroupLayout pnlButtonsLayout = new org.jdesktop.layout.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 799, Short.MAX_VALUE)
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        pnlMain.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblTransfers;
    // End of variables declaration//GEN-END:variables

}
