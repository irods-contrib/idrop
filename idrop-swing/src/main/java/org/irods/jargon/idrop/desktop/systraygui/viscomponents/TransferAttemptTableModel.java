/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import javax.swing.table.AbstractTableModel;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;

/**
 *
 * @author Mike
 */
public class TransferAttemptTableModel extends AbstractTableModel {

    @Override
    public String getColumnName(int i) {
       
        switch (i) {
            case 0:
                return "Start";
            case 1:
               return "End";
            case 2:
               return "Skipped";
            case 3:
                return "Transferred";
            case 4:
               return "Errors";
            case 5:
               return "Message";
            default:
                return "";
        }
    }

    private final Transfer transfer;

    public TransferAttemptTableModel(final Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("null transfer");
        }

        this.transfer = transfer;

    }

    @Override
    public int getRowCount() {
        return transfer.getTransferAttempts().size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }
    
    

    @Override
    public Object getValueAt(int row, int col) {

        // start end status total_skipped, total_transferred, total_error


        if (row > getRowCount() - 1) {
            throw new IllegalArgumentException("no row at given index");
        }

        switch (col) {
            case 0:
                return transfer.getTransferAttempts().get(row).getAttemptStart();
            case 1:
                return transfer.getTransferAttempts().get(row).getAttemptEnd();
            case 2:
                return transfer.getTransferAttempts().get(row).getTotalFilesSkippedSoFar();
            case 3:
                return transfer.getTransferAttempts().get(row).getTotalFilesTransferredSoFar() - transfer.getTransferAttempts().get(row).getTotalFilesSkippedSoFar();
            case 4:
                return transfer.getTransferAttempts().get(row).getTotalFilesErrorSoFar();
            case 5:
                return transfer.getTransferAttempts().get(row).getErrorMessage();
            default:
                return null;
        }
    }
    
    public TransferAttempt getTransferAttemptAtRow(final int i) {
        if (i < 0 || i > transfer.getTransferAttempts().size() - 1) {
            throw new IllegalArgumentException("no transfer at given index");
        }
        
        return transfer.getTransferAttempts().get(i);
        
    }
    
}