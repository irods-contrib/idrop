package org.irods.jargon.idrop.lite;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.irods.jargon.core.transfer.TransferControlBlock;
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

		Boolean transferInProgress = ((iDropLiteApplet)table.getTopLevelAncestor()).isTransferInProgress();
		
		setFont(new java.awt.Font("Lucida Grande", 0, 12));
		
		if((value != null) && (value instanceof TransferProgressInfo)) {
			TransferProgressInfo tpInfo = (TransferProgressInfo)value;
			Integer ival = tpInfo.getPercentDone();
			setValue(ival);
			
			// check to see if this a folder and if files have already started to be transferred
			//Boolean isFolder = (Boolean) table.getModel().getValueAt(row, 4);
			int fileType = (Integer)table.getModel().getValueAt(row, 4);
			int filesToTransfer = tpInfo.getTotalFilesToTransfer();
			int soFar = tpInfo.getTotalFilesTransferredSoFar();
			if((fileType == iDropLiteApplet.uploadFolder) && (ival > 0)) {
				if(tpInfo.isIntraFile) { // retrieve file transfer statistics not given in intraFile type callback
					TransferControlBlock tcb = ((iDropLiteApplet)table.getTopLevelAncestor()).getiDropCore().getTransferControlBlock();
					if(tcb != null) {
						soFar = tcb.getTotalFilesTransferredSoFar();
						filesToTransfer = tcb.getTotalFilesToTransfer();
					}
				}
				String pbText = "File " + soFar + " of " + filesToTransfer + " complete";
				setString(pbText);
			}
			else {
				setString(ival.toString().concat("%"));
			}
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
