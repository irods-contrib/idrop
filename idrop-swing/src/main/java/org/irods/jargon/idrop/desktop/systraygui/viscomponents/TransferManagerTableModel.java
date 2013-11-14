/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class TransferManagerTableModel extends AbstractTableModel { //extends DefaultTableModel {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(TransferManagerTableModel.class);
    private List<Transfer> transfers = null;
    private final IDROPCore idropCore;

    public TransferManagerTableModel(
            IDROPCore idropCore,
            final List<Transfer> transfers) {
        if (transfers == null) {
            throw new IdropRuntimeException("null transfers");
        }

        this.transfers = transfers;
        this.idropCore = idropCore;
    }
    
    @Override
    public Class<?> getColumnClass(final int columnIndex) {

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values

        // 0 = create date

        if (columnIndex == 0) {
            return Date.class;
        }

        // 1 = status

        if (columnIndex == 1) {
            return String.class;
        }


        // 2 = type

        if (columnIndex == 2) {
            return String.class;
        }
        
        // 3 = grid account zone

        if (columnIndex == 3) {
            return String.class;
        }

        // 4 = source path

        if (columnIndex == 4) {
            return String.class;
        }

        // 5 = target path

        if (columnIndex == 5) {
            return String.class;
        }
        
        // 6 = summary

        if (columnIndex == 6) {
            return String.class;
        }

        throw new IdropRuntimeException("unknown column");
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values

        // 0 = create date

        if (columnIndex == 0) {
            return "Created Date";
        }

        // 1 = status

        if (columnIndex == 1) {
            return "Status";
        }

        // 2 = type

        if (columnIndex == 2) {
            return "Operation";
        }
        
        // 3 = grid account zone

        if (columnIndex == 3) {
            return "Zone";
        }

        // 4 = source path

        if (columnIndex == 4) {
            return "Source";
        }

        // 5 = target path

        if (columnIndex == 5) {
            return "Destination";
        }
        
        // 6 = summary

        if (columnIndex == 6) {
            return "Summary";
        }

        throw new IdropRuntimeException("unknown column");
    }


    @Override
    public synchronized int getRowCount() {
        if (transfers == null) {
            return 0;
        } else {
            return transfers.size();
        }
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public synchronized Object getValueAt(final int rowIndex,
            final int columnIndex) {

        if (rowIndex >= getRowCount()) {
            throw new IdropRuntimeException("row unavailable, out of bounds");
        }

        if (columnIndex > getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        Transfer transfer = transfers.get(rowIndex);

        // translate indexes to object values

        // 0 = create date

        if (columnIndex == 0) {
            return transfer.getCreatedAt();
        }

        // 1 = status

        if (columnIndex == 1) {
            return transfer.getTransferState().name();
        }

        // 2 = type

        if (columnIndex == 2) {
            return transfer.getTransferType().name();
        }
        
        // 3 = grid account zone

        if (columnIndex == 3) {
            return transfer.getGridAccount().getZone();
        }

        // 4 = source path

        String path = null;
        if (columnIndex == 4) {
            switch (transfer.getTransferType()) {
                case GET:
                    path = IDropUtils.abbreviateFileName(transfer.getIrodsAbsolutePath());
                    break;
                case PUT:
                case REPLICATE:
                    path = IDropUtils.abbreviateFileName(transfer.getLocalAbsolutePath());
                    break;
                case COPY:
                    path = IDropUtils.abbreviateFileName(transfer.getLocalAbsolutePath());
                    break;
                case SYNCH:
                    path = IDropUtils.abbreviateFileName(transfer.getLocalAbsolutePath());
                    break;
                default:
                    log.error(
                            "unable to build details for transfer with transfer type of:{}",
                            transfer.getTransferType());
                    path = "";
                    break;
            }
            return path;
        }

        // 5 = target path
        if (columnIndex == 5) {
            switch (transfer.getTransferType()) {
                case GET:
                    path = IDropUtils.abbreviateFileName(transfer.getLocalAbsolutePath());
                    break;
                case PUT:
                    path = IDropUtils.abbreviateFileName(transfer.getIrodsAbsolutePath());
                    break;
                case REPLICATE:
                    path = "";
                    break;
                       case COPY:
                    path = IDropUtils.abbreviateFileName(transfer.getIrodsAbsolutePath());
                    break;
                case SYNCH:
                    path = IDropUtils.abbreviateFileName(transfer.getIrodsAbsolutePath()); // FIXME: should really be a get/put at transfer item level
                    break;
                default:
                    log.error(
                            "unable to build details for transfer with transfer type of:{}",
                            transfer.getTransferType());
                    path = "";
                    break;
            }
            return path;
        }
        
        // 6 = summary
        
        if (columnIndex == 6) {
            
            StringBuilder summary = new StringBuilder();
            try {
                TransferAttempt attempt = null;
                Transfer transferWithChildren = idropCore.getConveyorService().getQueueManagerService().initializeGivenTransferByLoadingChildren(transfer);
                TransferAttempt attempts[] = new TransferAttempt[transferWithChildren.getTransferAttempts().size()];
		attempts = transferWithChildren.getTransferAttempts().toArray(attempts);
                
                // get last attempt
                int numOfAttempts = attempts.length;
                if (numOfAttempts > 0) {
                    attempt = attempts[numOfAttempts - 1];
                    summary.append("Completed transfer of ");
                    summary.append(attempt.getTotalFilesTransferredSoFar());
                    summary.append(" out of ");
                    summary.append(attempt.getTotalFilesCount());
                    summary.append(" files, in ");
                    summary.append(numOfAttempts);
                    if (numOfAttempts == 1) {
                        summary.append(" attempt.");
                    }
                    else {
                        summary.append(" attempts.");
                    }
                }
                    
            } catch (ConveyorExecutionException ex) {
                Exceptions.printStackTrace(ex); // FIXME: do somethin else here
            }
            finally {
                return summary.toString();
            }
        }
        
        // 7 = transfer id

        if (columnIndex == 7) {
            return transfer.getId();
        }

        throw new IdropRuntimeException("unknown column");

    }

    public synchronized Transfer getTransferAtRow(final int rowIndex) {
        if (transfers == null) {
            log.warn("attempt to access a null model");
            return null;
        }

        if (rowIndex >= transfers.size()) {
            log.warn("attempt to access a row that does not exist");
            return null;
        }

        return transfers.get(rowIndex);
    }
    
    public synchronized List<Transfer> getTransfers() {
        return this.transfers;
    }
    
    public synchronized void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }
    
}
