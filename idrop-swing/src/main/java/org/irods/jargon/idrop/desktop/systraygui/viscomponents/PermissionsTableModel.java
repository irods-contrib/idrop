package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;

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
    
    List<UserFilePermission> permissions;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(MetadataTableModel.class);
    
    public PermissionsTableModel(List<UserFilePermission> permissions) {
        if (permissions == null) {
            throw new IdropRuntimeException("null permissions");
        }
        this.permissions = permissions;
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
    public Class<?> getColumnClass(final int columnIndex) {

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values
        // 0 = user name

        if (columnIndex == 0) {
            return String.class;
        }

        // 1 = share permission

        if (columnIndex == 1) {
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
    
    public void addRow(User user, FilePermissionEnum permissionEnum) throws JargonException {
        UserFilePermission permission = new UserFilePermission(
                user.getName(),
                user.getId(),
                permissionEnum,
                user.getUserType(),
                user.getZone());
        permissions.add(permission);
        fireTableDataChanged();
    }
    
    public void deleteRow(User user) throws JargonException {
//        UserFilePermission permission = new UserFilePermission(
//                user.getName(),
//                user.getId(),
//                permissionEnum,
//                user.getUserType(),
//                user.getZone());
        
        // see if we can find this user in the table, don't complain if we can't
        String userName = user.getNameWithZone();
        for (int idx=0; idx < this.getRowCount(); idx++) {
            if (userName.equals(this.getValueAt(idx, 0))) {
                permissions.remove(idx);
                fireTableDataChanged();
            }
        }
    }
    
}
