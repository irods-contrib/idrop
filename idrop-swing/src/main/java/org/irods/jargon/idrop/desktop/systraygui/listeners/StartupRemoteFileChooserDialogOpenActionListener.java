package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupRemoteFileChooserDialogOpenActionListener implements ActionListener {

    private IDROPSplashWindow desktop;

    public StartupRemoteFileChooserDialogOpenActionListener(IDROPSplashWindow desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktop.editSynchronizationDialogRemotePathTextField.setText(desktop.remoteFileChooserDialogFileNameTextField.getText());
        desktop.remoteFileChooserDialog.setVisible(false);
    }

}
