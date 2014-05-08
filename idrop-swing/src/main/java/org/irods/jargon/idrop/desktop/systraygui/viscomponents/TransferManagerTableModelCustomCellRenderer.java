/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * custom cell renderer for transfer manager table
 * 
 * @author Mike Conway
 */
public class TransferManagerTableModelCustomCellRenderer extends
		DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7551616281032851307L;

	@Override
	public Component getTableCellRendererComponent(final JTable jtable,
			final Object o, final boolean bln, final boolean bln1, final int i,
			final int i1) {

		Component component = super.getTableCellRendererComponent(jtable, o,
				bln, bln1, i, i1);
		String val = ((String) o).trim();

		if (val.equals("ERROR")) {
			component.setForeground(Color.RED);
		} else if (val.equals("WARNING")) {
			component.setForeground(Color.YELLOW);
		} else {
			component.setForeground(Color.GREEN);
		}

		return component;

	}

}
