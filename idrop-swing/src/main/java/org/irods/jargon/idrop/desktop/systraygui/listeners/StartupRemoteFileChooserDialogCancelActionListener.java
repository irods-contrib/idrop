package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupRemoteFileChooserDialogCancelActionListener implements ActionListener {

    private IDROPSplashWindow desktop;

    public StartupRemoteFileChooserDialogCancelActionListener(IDROPSplashWindow desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktop.remoteFileChooserDialog.setVisible(false);
        desktop.editSynchronizationDialog.requestFocus();
    }

}
