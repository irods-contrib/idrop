package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.domain.User;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lisa
 */
public class PermissionsTableModel extends AbstractTableModel {

	/**
     *
     */
	private static final long serialVersionUID = -1321576471314258457L;
	List<UserFilePermission> permissions;
	List<UserFilePermission> origPermissions;
	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(MetadataTableModel.class);

	public PermissionsTableModel(final List<UserFilePermission> permissions) {
		if (permissions == null) {
			throw new IdropRuntimeException("null permissions");
		}
		this.permissions = permissions;
		resetOriginalPermissionList();
	}

	@Override
	public int getRowCount() {
		return permissions.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (rowIndex >= getRowCount()) {
			throw new IdropRuntimeException("row unavailable, out of bounds");
		}

		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}

		UserFilePermission permission = permissions.get(rowIndex);

		// translate indexes to object values

		// 0 = attribute

		if (columnIndex == 0) {
			return permission.getNameWithZone();
		}

		// 1 = value

		if (columnIndex == 1) {
			return permission.getFilePermissionEnum().name();
		}

		throw new IdropRuntimeException("unknown column");
	}

	@Override
	public void setValueAt(final Object value, final int row, final int column) {
		if (column == 1) {
			UserFilePermission permission = permissions.get(row);
			permission.setFilePermissionEnum(FilePermissionEnum
					.valueOf((String) value));
			fireTableDataChanged();
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {

		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}
		return (getValueAt(0, columnIndex).getClass());
	}

	@Override
	public String getColumnName(final int columnIndex) {
		if (columnIndex >= getColumnCount()) {
			throw new IdropRuntimeException("column unavailable, out of bounds");
		}

		// translate indexes to object values

		// 0 = user name

		if (columnIndex == 0) {
			return "User Name";
		}

		// 1 = share permissions

		if (columnIndex == 1) {
			return "Share Permissions";
		}

		throw new IdropRuntimeException("unknown column");
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		// all cells false
		return false;
	}

	public void addRow(final User user, final FilePermissionEnum permissionEnum)
			throws JargonException {
		UserFilePermission permission = new UserFilePermission(user.getName(),
				user.getId(), permissionEnum, user.getUserType(),
				user.getZone());
		permissions.add(permission);
		fireTableDataChanged();
	}

	public void deleteRow(final User user) throws JargonException {
		// UserFilePermission permission = new UserFilePermission(
		// user.getName(),
		// user.getId(),
		// permissionEnum,
		// user.getUserType(),
		// user.getZone());

		// see if we can find this user in the table, don't complain if we can't
		String userName = user.getNameWithZone();
		for (int idx = 0; idx < getRowCount(); idx++) {
			if (userName.equals(getValueAt(idx, 0))) {
				permissions.remove(idx);
				fireTableDataChanged();
			}
		}
	}

	public void deleteRow(final int idx) {

		permissions.remove(idx);
		fireTableDataChanged();
	}

	public UserFilePermission getRow(final int row) {
		return permissions.get(row);
	}

	public void updateRow(final int row, final UserFilePermission permission) {
		permissions.set(row, permission);
		fireTableDataChanged();
	}

	public UserFilePermission[] getPermissionsToDelete() {

		Set<UserFilePermission> permissionsToDeleteSet = new HashSet<UserFilePermission>(
				origPermissions);
		permissionsToDeleteSet.removeAll(permissions);
		UserFilePermission[] permissionsToDelete = permissionsToDeleteSet
				.toArray(new UserFilePermission[0]);

		return permissionsToDelete;
	}

	public UserFilePermission[] getPermissionsToAdd() {

		Set<UserFilePermission> permissionsToAddSet = new HashSet<UserFilePermission>(
				permissions);
		permissionsToAddSet.removeAll(origPermissions);
		UserFilePermission[] permissionsToAdd = permissionsToAddSet
				.toArray(new UserFilePermission[0]);

		return permissionsToAdd;
	}

	public void resetOriginalPermissionList() {
		origPermissions = new ArrayList(permissions);
	}
}
