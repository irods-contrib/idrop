package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.Synchronization;

/**
 * Model for synchronization table
 * @author Mike Conway - DICE (www.irods.org)
 */
public class SynchConfigTableModel extends DefaultTableModel {

    private final IDROPCore idropCore;
    private List<Synchronization> synchronizations;
    private Object[] columnNames = new Object[]{"Name", "Local Path", "iRODS Path"};

    public void setSynchronizations(List<Synchronization> synchronizations) {
        this.synchronizations = synchronizations;
    }

    @Override
    public int getRowCount() {
        if (synchronizations == null) {
            return 0;
        } else {
            return synchronizations.size();
        }
    }

    @Override
    public void removeRow(int i) {
        synchronizations.remove(i);
        this.fireTableRowsDeleted(i, i);
    }

    public SynchConfigTableModel(IDROPCore idropCore, List<Synchronization> synchronizations) {
        this.idropCore = idropCore;
        this.synchronizations = synchronizations;
        this.setColumnIdentifiers(columnNames);

    }

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

    /**
     * Return the underlying domain object at the given row in the model
     * @param row
     * @return 
     */
    public Synchronization getSynchronizationAt(int row) {
        return synchronizations.get(row);
    }

    public List<Synchronization> getSynchronizations() {
        return synchronizations;
    }
}
