/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.QueueManagerService;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.TransferItem;

/**
 * 
 * @author lisa
 */
public class ItemListPagingTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7671632111053433938L;
	private final int pageSize;
	private int pageOffset;
	private final Long transferId;
	private List<TransferItem> items;
	private final QueueManagerService qms;

	public ItemListPagingTableModel(final int pageSize, final Long transferId,
			final QueueManagerService qms) throws ConveyorExecutionException {

		this.pageSize = pageSize;
		this.transferId = transferId;
		this.qms = qms;
		pageOffset = 0;

		// get list of initial items
		items = qms.getNextTransferItems(transferId, 0, pageSize);
	}

	// Return values appropriate for the visible table part.
	@Override
	public int getRowCount() {
		// return Math.min(pageSize, data.length);
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	// Work only on the visible part of the table.
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		// int realRow = row + (pageOffset * pageSize);
		// return data[realRow].getValueAt(col);
		if (rowIndex >= getRowCount()) {
			throw new IdropRuntimeException("row unavailable, out of bounds");
		}

		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}

		TransferItem item = items.get(rowIndex);

		// translate indexes to object values

		// 0 = source path
		if (columnIndex == 0) {
			return item.getSourceFileAbsolutePath();
		}

		// 1 = target path
		if (columnIndex == 1) {
			return item.getTargetFileAbsolutePath();
		}

		// 2 = size
		if (columnIndex == 2) {
			return item.getLengthInBytes();
		}

		if (columnIndex == 3) {
			return item.isError();
		}

		throw new IdropRuntimeException("unknown column");
	}

	@Override
	public String getColumnName(final int columnIndex) {
		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}
		// translate indexes to object values

		// 0 = Source Path
		if (columnIndex == 0) {
			return "Source Path";
		}

		// 1 = Destination Path
		if (columnIndex == 1) {
			return "Destination Path";
		}

		// 2 = Size
		if (columnIndex == 2) {
			return "Size (bytes)";
		}

		// 3 - error
		if (columnIndex == 3) {
			return "Error?";
		}

		throw new IdropRuntimeException("unknown column");
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {

		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}
		return (getValueAt(0, columnIndex).getClass());
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	// Update the page offset
	public void pageDown() throws ConveyorExecutionException {
		// make sure that there might be more data
		if (items.size() >= pageSize) {
			pageOffset += pageSize;
			refreshData();
		}
	}

	// Update the page offset.
	public void pageUp() throws ConveyorExecutionException {
		// make sure not going beyond beginning of dataset
		if (pageOffset >= pageSize) {
			pageOffset -= pageSize;
			refreshData();
		}
	}

	private void refreshData() throws ConveyorExecutionException {
		items = qms.getNextTransferItems(transferId, pageOffset, pageSize);
		fireTableDataChanged();
	}
}
