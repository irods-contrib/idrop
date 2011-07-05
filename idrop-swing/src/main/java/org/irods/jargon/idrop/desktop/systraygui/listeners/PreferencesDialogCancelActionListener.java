package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class PreferencesDialogCancelActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public PreferencesDialogCancelActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {
        desktop.preferencesDialog.setVisible(false);
    }

}
