/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * custom cell renderer for transfer manager item table
 *
 * @author Mike Conway
 */
public class ItemListPagingTableModelCustomCellRenderer extends JCheckBox implements TableCellRenderer {

    /**
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            //super.setBackground(table.getSelectionBackground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected((value != null && ((Boolean) value).booleanValue()));
        return this;

    }
}
