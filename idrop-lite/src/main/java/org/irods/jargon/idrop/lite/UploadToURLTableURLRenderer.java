package org.irods.jargon.idrop.lite;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

public class UploadToURLTableURLRenderer extends JTextField implements
		TableCellRenderer {

	public UploadToURLTableURLRenderer() {
		super();
		setText("Insert URL");
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if(value instanceof String) {
			setText((String)value);
		}
		
		return this;
	}

}
