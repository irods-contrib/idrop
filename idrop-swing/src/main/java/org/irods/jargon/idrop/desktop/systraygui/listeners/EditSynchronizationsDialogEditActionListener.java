package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.TransferDAOException;
import org.irods.jargon.transfer.dao.domain.Synchronization;

public class EditSynchronizationsDialogEditActionListener implements ActionListener {

    private final TransferDAOManager transferDAOMgr = TransferDAOManager.getInstance();

    private IDROPDesktop desktop;

    public EditSynchronizationsDialogEditActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int index = desktop.editSynchronizationsDialogList.getSelectedIndex();
        if (index != -1) {

            Object item = desktop.editSynchronizationsDialogListModel.getElementAt(index);

            SynchronizationDAO synchronizationDAO = transferDAOMgr.getTransferDAOBean().getSynchronizationDAO();

            try {
                Synchronization synchronization = synchronizationDAO.findByName(item.toString());
                desktop.editSynchronizationDialogNameTextField.setText(synchronization.getName());
                desktop.editSynchronizationDialogDeviceNameTextField.setEnabled(false);
                
                Preferences prefs = desktop.getiDropCore().getPreferences();
                desktop.editSynchronizationDialogDeviceNameTextField.setText(prefs.get(PREFERENCE_KEY_DEVICE_NAME, ""));

                desktop.editSynchronizationDialogLocalPathTextField.setText(synchronization.getLocalSynchDirectory());
                desktop.editSynchronizationDialogRemotePathTextField.setText(synchronization.getIrodsSynchDirectory());
                // desktop.editSynchronizationDialogFrequencyComboBox.setSelectedItem(synchronization.getFrequencyType()
                // .getReadableName());
            } catch (TransferDAOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(desktop.editSynchronizationsDialog, e1.getCause().getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            desktop.editSynchronizationDialog.setVisible(true);
        }

    }

}
