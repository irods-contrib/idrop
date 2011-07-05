package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.TransferDAOException;
import org.irods.jargon.transfer.dao.domain.FrequencyType;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.dao.domain.SynchronizationType;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

public class EditSynchronizationDialogSaveActionListener implements ActionListener {

    private final TransferDAOManager transferDAOMgr = TransferDAOManager.getInstance();

    private IDROPDesktop desktop;

    public EditSynchronizationDialogSaveActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        ValidationResult validationResults = validateLoginForm();

        if (validationResults.hasErrors()) {
            String msg = "";
            List<ValidationMessage> errors = validationResults.getErrors();
            for (ValidationMessage error : errors) {
                msg += error.formattedText() + "\n";
            }
            JOptionPane.showMessageDialog(desktop.editSynchronizationDialog, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = desktop.editSynchronizationDialogNameTextField.getText();
        String localPath = desktop.editSynchronizationDialogLocalPathTextField.getText();
        String remotePath = desktop.editSynchronizationDialogRemotePathTextField.getText();
        String frequency = desktop.editSynchronizationDialogFrequencyComboBox.getSelectedItem().toString();

        Synchronization synch = new Synchronization();
        synch.setName(name);
        synch.setIrodsHostName(desktop.getiDropCore().getIrodsAccount().getHost());
        synch.setIrodsPort(desktop.getiDropCore().getIrodsAccount().getPort());
        synch.setIrodsUserName(desktop.getiDropCore().getIrodsAccount().getUserName());
        synch.setIrodsPassword(desktop.getiDropCore().getIrodsAccount().getPassword());
        synch.setIrodsZone(desktop.getiDropCore().getIrodsAccount().getZone());
        synch.setLocalSynchDirectory(localPath);
        synch.setIrodsSynchDirectory(remotePath);
        synch.setSynchronizationMode(SynchronizationType.ONE_WAY_LOCAL_TO_IRODS);
        synch.setCreatedAt(new Date());

        frequency: for (FrequencyType ft : FrequencyType.values()) {
            if (ft.getReadableName().equals(frequency)) {
                synch.setFrequencyType(ft);
                break frequency;
            }
        }

        try {
            SynchronizationDAO synchronizationDAO = transferDAOMgr.getTransferDAOBean().getSynchronizationDAO();
            synchronizationDAO.save(synch);
        } catch (TransferDAOException e) {
            e.printStackTrace();
        }
        desktop.editSynchronizationsDialogListModel.addElement(name);
        desktop.editSynchronizationDialog.setVisible(false);
        desktop.editSynchronizationsDialog.requestFocus();
    }

    private ValidationResult validateLoginForm() {
        ValidationResult result = new ValidationResult();

        if (StringUtils.isEmpty(desktop.editSynchronizationDialogNameTextField.getText())) {
            result.addError("Name is required");
        }

        if (StringUtils.isEmpty(desktop.editSynchronizationDialogLocalPathTextField.getText())) {
            result.addError("Local Path is required");
        }

        if (StringUtils.isEmpty(desktop.editSynchronizationDialogRemotePathTextField.getText())) {
            result.addError("Remote Path is required");
        }

        if (desktop.editSynchronizationDialogFrequencyComboBox.getSelectedIndex() == 0) {
            result.addError("Please choose a valid frequency");
        }

        return result;
    }

}
