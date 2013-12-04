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
public class TransferManagerTable extends JTable {

    private TransferManagerTableModelCustomCellRenderer transferManagerTableModelCustomCellRenderer = new TransferManagerTableModelCustomCellRenderer();

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {


        if (column == 2) {
            return transferManagerTableModelCustomCellRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }
}
