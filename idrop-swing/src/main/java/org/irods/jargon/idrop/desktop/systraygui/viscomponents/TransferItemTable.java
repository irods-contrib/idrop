/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Special JTable subclass with appropriate cell renderers
 *
 * @author Mike Conway
 */
public class TransferItemTable extends JTable {

    /**
     *
     */
    private static final long serialVersionUID = -395062818571167946L;
    private ItemListPagingTableModelCustomCellRenderer renderer = new ItemListPagingTableModelCustomCellRenderer();

    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {

        if (column == 3) {
            return renderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }
}
