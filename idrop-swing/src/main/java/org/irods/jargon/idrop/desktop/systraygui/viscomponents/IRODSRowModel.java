/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.Date;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.netbeans.swing.outline.RowModel;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSRowModel implements RowModel {

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueFor(Object node, int column) {
        IRODSNode f = (IRODSNode) node;
        CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) f.getUserObject();
        switch (column) {

            case 0:
                return entry.getDataSize();
            case 1:
                return entry.getModifiedAt();
            default:
                assert false;
        }
        return null;
    }

    @Override
    public Class getColumnClass(int i) {
        switch (i) {

            case 0:
                return Long.class;
            case 1:
                return Date.class;
            default:
                assert false;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    @Override
    public void setValueFor(Object o, int i, Object o1) {
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "size";
            case 1:
                return "last modified";
            default:
                assert false;
        }
        return null;
    }
}
