package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class UploadFromURLTablePlusMinusRenderer extends
		DefaultTableCellRenderer {
	
	ImageIcon plus = null;
	ImageIcon minus = null;
	public static final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

	
	public UploadFromURLTablePlusMinusRenderer(ImageIcon plus, ImageIcon minus) {
		super();
		this.plus = plus;
		this.minus = minus;
		setHorizontalAlignment(JLabel.CENTER);
	}

    public void setValue(Object value) {
    	if(value instanceof Boolean) {
    		if((Boolean)value) {
    			if(plus != null) {
    				setIcon(plus);
    			}
    		}
    		else {
    			if(minus != null) {
    				setIcon(minus);
    			}
    		}
    	}
    }

}
