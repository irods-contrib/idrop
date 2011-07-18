package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Synchronization;

/**
 * Model for synchronization table
 * @author Mike Conway - DICE (www.irods.org)
 */
public class SynchConfigTableModel extends AbstractTableModel {

    @Override
    public String getColumnName(int i) {
        if (i == 0) {
            return "Name";
        } else if (i == 1) {
            return "Local";
        } else if (i == 2) {
            return "iRODS";
        } else {
            throw new IdropRuntimeException("invalid column index, cannot find name");
        }
    }
    private final IDROPCore idropCore;
    private final List<Synchronization> synchronizations;

    public SynchConfigTableModel(IDROPCore idropCore, List<Synchronization> synchronizations) {
        this.idropCore = idropCore;
        this.synchronizations = synchronizations;
    }

    @Override
    public int getRowCount() {
        return synchronizations.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Synchronization synchronization = synchronizations.get(row);

        if (col == 0) {
            return synchronization.getName();
        } else if (col == 1) {
            return synchronization.getLocalSynchDirectory();
        } else if (col == 2) {
            return synchronization.getIrodsSynchDirectory();
        } else {
            throw new IdropRuntimeException("Invalid column requested from model");
        }
    }
}
