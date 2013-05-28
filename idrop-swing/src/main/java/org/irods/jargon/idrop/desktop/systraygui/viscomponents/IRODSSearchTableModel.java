package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Model for a JTable that represents iRODS files and collections using the
 * <code>CollectionAndDataObjectListingEntry</code> domain object.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSSearchTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7271103263452379537L;
	private List<CollectionAndDataObjectListingEntry> listingEntries = new ArrayList<CollectionAndDataObjectListingEntry>();
	private static final List<String> columnNames = new ArrayList<String>();
	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(IRODSSearchTableModel.class);

	static {
		columnNames.add("Type");
		columnNames.add("Parent");
		columnNames.add("Name");
		columnNames.add("Created At");
		columnNames.add("Modified At");
	}

	public IRODSSearchTableModel() {
		this(new ArrayList<CollectionAndDataObjectListingEntry>());
	}

	public IRODSSearchTableModel(
			final List<CollectionAndDataObjectListingEntry> entries) {
		log.info("in default constructor with table model:{}", entries);
		listingEntries = entries;
	}

	// type, parent, name, created, modified
	@Override
	public Class<?> getColumnClass(final int i) {
		Class clazz = null;
		switch (i) {
		case 0:
			clazz = CollectionAndDataObjectListingEntry.ObjectType.class;
			break;
		case 1:
			clazz = String.class;
			break;
		case 2:
			clazz = String.class;
			break;
		case 3:
			clazz = Date.class;
			break;
		case 4:
			clazz = Date.class;
			break;
		default:
			throw new IdropRuntimeException(
					"unknown column, cannot determine class");
		}
		return clazz;
	}

	public List<CollectionAndDataObjectListingEntry> getEntries() {
		return listingEntries;
	}

	public void setEntries(
			final List<CollectionAndDataObjectListingEntry> entries) {
		listingEntries = entries;

	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(final int i) {
		return columnNames.get(i);
	}

	@Override
	public int getRowCount() {
		return listingEntries.size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		CollectionAndDataObjectListingEntry entry = listingEntries.get(row);
		if (entry == null) {
			throw new IdropRuntimeException("null entry for row number:" + row);
		}

		if (column < 0 || column > 5) {
			throw new IllegalArgumentException("invalid column number:"
					+ column);
		}

		/*
		 * cols 0: type 1: parent 2: name 3: created 4: modified
		 */
		Object returnedVal = null;

		switch (column) {
		case (0):
			returnedVal = entry.getObjectType();
			break;
		case (1):
			returnedVal = entry.getParentPath();
			break;
		case (2):
			if (entry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
				returnedVal = MiscIRODSUtils
						.getLastPathComponentForGiveAbsolutePath(entry
								.getPathOrName());
			} else {
				returnedVal = entry.getPathOrName();
			}
			break;
		case (3):
			returnedVal = entry.getCreatedAt();
			break;
		case (4):
			returnedVal = entry.getModifiedAt();
			break;
		}

		return returnedVal;

	}
}
