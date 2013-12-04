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
 * @author Mike Conway
 */
public class TransferManagerTableModelCustomCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
       
        String val = (String) o;
        
        if (val.equals("ERROR")) {
            this.setForeground(Color.RED);
        } else if (val.equals("WARNING")) {
            this.setForeground(Color.YELLOW);
        } else {
            this.setForeground(Color.GREEN);
        }
        
        this.setText(val);
        return this;
        
    }
    
}
