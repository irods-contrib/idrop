package org.irods.jargon.idrop.lite;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public class UploadTableCancelRenderer extends DefaultTableCellRenderer {

	ImageIcon cancelIcon = null;
	
	public UploadTableCancelRenderer(ImageIcon image) {
		this.cancelIcon = image;
		setHorizontalAlignment(JLabel.CENTER);
	}
    //public CancelRenderer() { super(); }

    public void setValue(Object value) {
    	if(cancelIcon != null) {
	        setIcon(cancelIcon);
	    }
    }

}
