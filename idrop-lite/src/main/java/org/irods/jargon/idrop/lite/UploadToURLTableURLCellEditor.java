package org.irods.jargon.idrop.lite;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

public class UploadToURLTableURLCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	JComponent txtURL = new JTextField();
	
	@Override
	public Object getCellEditorValue() {
		return ((JTextField)txtURL).getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		((JTextComponent) txtURL).setText(value.toString());
//		((JTextComponent) txtURL).setSelectionStart(0);
//		((JTextComponent) txtURL).setSelectionEnd(value.toString().length());
		((JTextComponent) txtURL).requestFocus();
		((JTextComponent) txtURL).selectAll();
		

		return txtURL;
	}

}
