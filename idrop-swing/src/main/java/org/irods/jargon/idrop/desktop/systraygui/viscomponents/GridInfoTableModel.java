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

	/**
	 * 
	 */
	private static final long serialVersionUID = 6492635838872419711L;
	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(GridInfoTableModel.class);

	@Override
	public Class<?> getColumnClass(final int columnIndex) {

		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}

		// translate indexes to object values

		// 0 = host

		if (columnIndex == 0) {
			return String.class;
		}

		// 2 = port - Mike says we don't need this

		// if (columnIndex == 2) {
		// return int.class;
		// }

		// 1 = zone

		if (columnIndex == 1) {
			return String.class;
		}

		// 2 = user name

		if (columnIndex == 2) {
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

		// 0 = Host

		if (columnIndex == 0) {
			return "Host";
		}

		// 2 = Port

		// if (columnIndex == 2) {
		// return "Port";
		// }

		// 1 = Zone

		if (columnIndex == 1) {
			return "Zone";
		}

		// 2 = User

		if (columnIndex == 2) {
			return "User Name";
		}

		throw new IdropRuntimeException("unknown column");
	}

	private List<GridAccount> gridAccounts = null;

	public GridInfoTableModel(final List<GridAccount> gridAccounts) {
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
		return 3;
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

		// 0 = host

		if (columnIndex == 0) {
			return gridAccount.getHost();
		}

		// 2 = port

		// if (columnIndex == 2) {
		// return gridAccount.getPort();
		// }

		// 1 = zone

		if (columnIndex == 1) {
			return gridAccount.getZone();
		}

		// 2 = user name

		if (columnIndex == 2) {
			return gridAccount.getUserName();
		}

		throw new IdropRuntimeException("unknown column");

	}

	public void addRow(final IRODSAccount irodsAccount) throws JargonException {
		GridAccount gridAccount = new GridAccount();

		if (!isIrodsAccountValid(irodsAccount)) {
			throw new JargonException("invalid grid account parameters");
		}

		gridAccount.setUserName(irodsAccount.getUserName());
		gridAccount.setHost(irodsAccount.getHost());
		// gridAccount.setPort(irodsAccount.getPort());
		gridAccount.setZone(irodsAccount.getZone());
		// gridAccount.setPassword(irodsAccount.getPassword());
		// gridAccount.setDefaultResource(irodsAccount.getDefaultStorageResource());

		gridAccounts.add(gridAccount);

		fireTableDataChanged();
	}

	public void deleteRow(final int selectedRow) throws JargonException {
		gridAccounts.remove(selectedRow);
		fireTableDataChanged();
	}

	public void deleteRow(final IRODSAccount irodsAccount)
			throws JargonException {

		if (!isIrodsAccountValid(irodsAccount)) {
			throw new JargonException("invalid grid account parameters");
		}

		int idx = 0;
		for (GridAccount acct : gridAccounts) {
			if ((irodsAccount.getUserName().equals(acct.getUserName())
					&& (irodsAccount.getHost().equals(acct.getHost())) &&
			// (irodsAccount.getPort() == acct.getPort()) &&
			(irodsAccount.getZone().equals(acct.getZone())))) {

				gridAccounts.remove(idx);
				break;
			}
			idx++;
		}

		fireTableDataChanged();
	}

	private boolean isIrodsAccountValid(final IRODSAccount irodsAccount) {

		String user = irodsAccount.getUserName();
		String host = irodsAccount.getHost();
		// int port = irodsAccount.getPort();
		String zone = irodsAccount.getZone();

		if ((user == null) || (user.isEmpty()) || (host == null)
				|| (host.isEmpty()) ||
				// (port <= 0) ||
				(zone == null) || zone.isEmpty()) {

			return false;
		} else {
			return true;
		}
	}

	public GridAccount getRow(final int row) {
		if (row < 0) {
			return null;
		}
		return gridAccounts.get(row);
	}

}
