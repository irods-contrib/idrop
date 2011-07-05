package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class RemoteFileChooserDialogOpenActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public RemoteFileChooserDialogOpenActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktop.editSynchronizationDialogRemotePathTextField.setText(desktop.remoteFileChooserDialogFileNameTextField.getText());
        desktop.remoteFileChooserDialog.setVisible(false);
    }

}
