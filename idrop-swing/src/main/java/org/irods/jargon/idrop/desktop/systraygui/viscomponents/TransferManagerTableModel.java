/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class TransferManagerTableModel extends DefaultTableModel {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(TransferManagerTableModel.class);
    private List<Transfer> transfers = null;

    public TransferManagerTableModel(
            final List<Transfer> transfers) {
        if (transfers == null) {
            throw new IdropRuntimeException("null transfers");
        }

        this.transfers = transfers;
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

        // 3 = source path

        if (columnIndex == 3) {
            return String.class;
        }

        // 4 = target path

        if (columnIndex == 4) {
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

        // 3 = source path

        if (columnIndex == 3) {
            return "Source";
        }

        // 4 = target path

        if (columnIndex == 4) {
            return "Destination";
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
        return 5;
    }

    @Override
    public synchronized Object getValueAt(final int rowIndex,
            final int columnIndex) {

        if (rowIndex >= getRowCount()) {
            throw new IdropRuntimeException("row unavailable, out of bounds");
        }

        if (columnIndex >= getColumnCount()) {
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

        // 3 = source path

        String path = null;
        if (columnIndex == 3) {
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

        // 4 = target path
        if (columnIndex == 4) {
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
    
}
