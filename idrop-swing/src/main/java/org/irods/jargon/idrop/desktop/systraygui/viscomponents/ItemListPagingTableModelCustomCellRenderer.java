/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * custom cell renderer for transfer manager item table
 * 
 * @author Mike Conway
 */
public class ItemListPagingTableModelCustomCellRenderer extends JCheckBox
		implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4812664469645815511L;

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
	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			// super.setBackground(table.getSelectionBackground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setSelected((value != null && ((Boolean) value).booleanValue()));
                
                                        if (column == 3) {
                                            boolean boolVal = (Boolean) value;
                                            if (boolVal) {
                                                setForeground(Color.RED);
                                            } else {
                                                setForeground(Color.GREEN);
                                            }
                                        }
                                        
                                        if (column == 4) {
                                            boolean boolVal = (Boolean) value;
                                            if (boolVal) {
                                                setForeground(Color.BLUE);
                                            } else {
                                                setForeground(Color.BLACK);
                                            }
                                        }
   
		return this;
                
 
	}
}
