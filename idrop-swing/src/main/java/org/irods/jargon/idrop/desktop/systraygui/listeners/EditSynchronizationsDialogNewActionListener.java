package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class EditSynchronizationsDialogNewActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public EditSynchronizationsDialogNewActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        desktop.editSynchronizationDialogNameTextField.setText("");
        desktop.editSynchronizationDialogLocalPathTextField.setText("");
        Preferences prefs = desktop.getiDropCore().getPreferences();
        desktop.editSynchronizationDialogDeviceNameTextField.setText(prefs.get(PREFERENCE_KEY_DEVICE_NAME, ""));
        desktop.editSynchronizationDialogRemotePathTextField.setText("");
        //desktop.editSynchronizationDialogFrequencyComboBox.setSelectedIndex(0);
        desktop.editSynchronizationDialog.setVisible(true);
    }

}
