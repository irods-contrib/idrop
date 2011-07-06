package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupEditSynchronizationDialogCancelActionListener implements ActionListener {

    private IDROPSplashWindow desktop;

    public StartupEditSynchronizationDialogCancelActionListener(IDROPSplashWindow desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktop.editSynchronizationDialog.setVisible(false);
        //don't want to exit here...just close and move along
    }

}
