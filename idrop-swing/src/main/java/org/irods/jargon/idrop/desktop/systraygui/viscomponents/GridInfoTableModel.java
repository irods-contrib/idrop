/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.GridAccount;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class GridInfoTableModel extends AbstractTableModel {
    
    public static org.slf4j.Logger log = LoggerFactory.getLogger(GridInfoTableModel.class);

    @Override
    public Class<?> getColumnClass(final int columnIndex) {

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values
        // 0 = user name

        if (columnIndex == 0) {
            return String.class;
        }

        // 1 = host

        if (columnIndex == 1) {
            return String.class;
        }

        // 2 = port

        if (columnIndex == 2) {
            return int.class;
        }
        
        // 3 = zone
        
        if (columnIndex == 3) {
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

        // 0 = attribute

        if (columnIndex == 0) {
            return "User Name";
        }

        // 1 = value

        if (columnIndex == 1) {
            return "Host";
        }

        // 2 = units

        if (columnIndex == 2) {
            return "Port";
        }
        
        // 3 = zone

        if (columnIndex == 3) {
            return "Zone";
        }

        throw new IdropRuntimeException("unknown column");
    }
    private List<GridAccount> gridAccounts = null;

    public GridInfoTableModel(
            final List<GridAccount> gridAccounts) {
        if (gridAccounts == null) {
            throw new IdropRuntimeException("null gridAccounts");
        }

        this.gridAccounts = gridAccounts;
    }

    @Override
    public int getRowCount() {
        return gridAccounts.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        if (rowIndex >= getRowCount()) {
            throw new IdropRuntimeException("row unavailable, out of bounds");
        }

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        GridAccount gridAccount = gridAccounts.get(rowIndex);

        // translate indexes to object values

        // 0 = user name

        if (columnIndex == 0) {
            return gridAccount.getUserName();
        }

        // 1 = host

        if (columnIndex == 1) {
            return gridAccount.getHost();
        }

        // 2 = port

        if (columnIndex == 2) {
            return gridAccount.getPort();
        }
        
        // 3 = zone

        if (columnIndex == 3) {
            return gridAccount.getZone();
        }

        throw new IdropRuntimeException("unknown column");

    }
    
    public void addRow(IRODSAccount irodsAccount) throws JargonException {
        GridAccount gridAccount = new GridAccount();
        
        if (! isIrodsAccountValid(irodsAccount)) {
            throw new JargonException("invalid grid account parameters");
        }
        
        gridAccount.setUserName(irodsAccount.getUserName());
        gridAccount.setHost(irodsAccount.getHost());
        gridAccount.setPort(irodsAccount.getPort());
        gridAccount.setZone(irodsAccount.getZone());
        
        gridAccounts.add(gridAccount);
        
        fireTableDataChanged();
    }
    
    public void deleteRow(int selectedRow) throws JargonException {
        gridAccounts.remove(selectedRow);
        fireTableDataChanged();
    }
    
    public void deleteRow(IRODSAccount irodsAccount) throws JargonException {
        
        if (! isIrodsAccountValid(irodsAccount)) {
            throw new JargonException("invalid grid account parameters");
        }
        
        int idx = 0;
        for (GridAccount acct: gridAccounts) {
            if ((irodsAccount.getUserName().equals(acct.getUserName()) &&
                (irodsAccount.getHost().equals(acct.getHost())) &&
                (irodsAccount.getPort() == acct.getPort()) &&
                (irodsAccount.getZone().equals(acct.getZone())))) {
                
                gridAccounts.remove(idx);
                break;
            }
            idx++;
        }

        fireTableDataChanged();
    }
    
    private boolean isIrodsAccountValid(IRODSAccount irodsAccount) {
        
        String user = irodsAccount.getUserName();
        String host = irodsAccount.getHost();
        int port = irodsAccount.getPort();
        String zone = irodsAccount.getZone();
        
        if ((user == null) || (user.isEmpty()) ||
            (host == null) || (host.isEmpty()) ||
            (port <= 0) ||
            (zone == null) || zone.isEmpty()) {
            
            return false;
        }
        else {
            return true;
        }
    }
    
}
