package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.irods.jargon.core.transfer.TransferStatus;

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
//		if(value.getClass().getName().equals("TransferStatus")) {
//			TransferStatus ts = (TransferStatus)value;
//			float bt = ts.getBytesTransfered() * 100;
//            float tot = ts.getTotalSize();
//            float percentDone = bt / tot;
//            Integer ival = (int)percentDone;
//            
//            setValue(ival);
//            // find out if this is a file or folder
//            if(!ts.isIntraFileStatusReport()) {
//            	String progressString = "File " + ts.getTotalFilesTransferredSoFar() + " of " + ts.getTotalFilesToTransfer() + " complete";
//            	setString(progressString);
//            }
//            else {
//            	//setString(ival.toString().concat("%"));
//            }      
//		}
//		if(value != null) {
//			Integer ival = (Integer)value;
//			
//			setValue(ival);
//			setString(ival.toString().concat("%"));
//		}
//		else {
//			setValue(0);
//		    setString("0%");
//		}
		Boolean transferInProgress = ((iDropLiteApplet)table.getTopLevelAncestor()).isTransferInProgress();
		
		setFont(new java.awt.Font("Lucida Grande", 0, 12));
		
		if((value != null) && (value instanceof TransferProgressInfo)) {
			TransferProgressInfo tpInfo = (TransferProgressInfo)value;
			Integer ival = tpInfo.getPercentDone();
			setValue(ival);
			
			// check to see if this a folder and if files have already started to be transferred
			Boolean isFolder = (Boolean) table.getModel().getValueAt(row, 4);
			int filesToTransfer = tpInfo.getTotalFilesToTransfer();
			int soFar = tpInfo.getTotalFilesTransferredSoFar();
			if((isFolder) && (ival > 0)) {
				//if((transferInProgress) && (soFar > 0)) {
				//if(!tpInfo.isIntraFile) {
					String pbText = "File " + soFar + " of " + filesToTransfer + " complete";
					setString(pbText);
				//}
			}
			else {
				setString(ival.toString().concat("%"));
			}
		}
//		else {
//			setValue(0);
//		    setString("0%");
//		}
		
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
