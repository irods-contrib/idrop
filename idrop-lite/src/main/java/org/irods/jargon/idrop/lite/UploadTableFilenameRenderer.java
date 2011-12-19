package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.io.File;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.table.DefaultTableCellRenderer;

import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;

// get filename sent to renderer - determine if this is an irods or local file/folder and
// set appropriate icon (for folder or file)

public class UploadTableFilenameRenderer extends DefaultTableCellRenderer {
	
	static final int UPLOAD_MODE = 2;
	static final int DOWNLOAD_MODE = 3;
	private int mode = -1;
	
	public UploadTableFilenameRenderer(int mode) {
		super();
		this.mode = mode;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if(column == 0) { // filename column in upload/download table
            String filename = (String)value;
            Icon icon = null;
            
            switch(mode) {
          
          	case(UPLOAD_MODE) :
          		File localFile = new File(filename);
          		icon = FileSystemView.getFileSystemView().getSystemIcon(localFile);
          		break;
          		
          	case(DOWNLOAD_MODE) :
          		if(isIrodsFolder(table, row)) {
          			icon = UIManager.getIcon("FileView.directoryIcon");
          		}
          		else {
          			icon = UIManager.getIcon("FileView.fileIcon");	
          		}
          	
          		UIDefaults defs = UIManager.getDefaults();
          		int i = 0;
          		break;
            }
            
            if(icon != null) {
            	label.setIcon(icon);
            }
            label.setText(filename);
		}
		
		return label;
	}
    
    private Boolean isIrodsFolder(JTable table, int row) {
    	
    	Boolean isFolder = false;
    	
		isFolder = (Boolean) table.getModel().getValueAt(row, 4);
    	return isFolder;
    }
    
    private Boolean isLocalFolder(String filename) {
    	return false;
    }


}
