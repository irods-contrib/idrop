package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class UploadTableProgressBar extends JProgressBar implements TableCellRenderer {
	
	public UploadTableProgressBar(){
	    super(0, 100);
	    setValue(0);
	    setString("0%");
	    setStringPainted(true);
	    setBorder(new EmptyBorder(new Insets(3, 3, 4, 3)));
	    
	  }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value != null) {
			Integer ival = (Integer)value;
//			if(ival == 100) {
//					return "Done";
//			}
			setValue(ival);
			setString(ival.toString().concat("%"));
		}
		else {
			setValue(0);
		    setString("0%");
		}
		
		return this;
	}
	
	public boolean isDisplayable() {
		// This does the trick. It makes sure animation is always performed
		return true;
	} 
	
//	public void repaint() {
//		// If you have access to the table you can force repaint like this.
//		//Otherwize, you could trigger repaint in a timer at some interval
//		table.repaint();
//	} 

}
