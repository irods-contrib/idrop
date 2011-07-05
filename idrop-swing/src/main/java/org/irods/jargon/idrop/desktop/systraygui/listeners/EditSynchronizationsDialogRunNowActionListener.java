package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.idrop.desktop.systraygui.util.MessageUtil;

/**
 * 
 * @author jdr0887
 * 
 */
public class EditSynchronizationsDialogRunNowActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public EditSynchronizationsDialogRunNowActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {
        int index = desktop.editSynchronizationsDialogList.getSelectedIndex();
        if (index == -1) {
            MessageUtil.showWarning(desktop.editSynchronizationsDialog, "No Synchronizations Selected", "Warning");
        } else {
            
            
        }
        
    }

}
