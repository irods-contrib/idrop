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
    public Object getValueFor(final Object node, final int column) {
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
    public Class getColumnClass(final int i) {
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
    public boolean isCellEditable(final Object o, final int i) {
        return false;
    }

    @Override
    public void setValueFor(final Object o, final int i, final Object o1) {
    }

    @Override
    public String getColumnName(final int i) {
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
