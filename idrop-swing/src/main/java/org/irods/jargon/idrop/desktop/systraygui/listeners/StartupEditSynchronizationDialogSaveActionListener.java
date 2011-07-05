package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.TransferDAOException;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.dao.domain.SynchronizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

public class StartupEditSynchronizationDialogSaveActionListener implements ActionListener {

    private final Logger log = LoggerFactory.getLogger(StartupEditSynchronizationDialogSaveActionListener.class);

    private final TransferDAOManager transferDAOMgr = TransferDAOManager.getInstance();

    private IDROPSplashWindow splash;

    public StartupEditSynchronizationDialogSaveActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
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
            JOptionPane.showMessageDialog(splash.editSynchronizationDialog, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = splash.editSynchronizationDialogNameTextField.getText();
        String deviceName = splash.editSynchronizationDialogDeviceNameTextField.getText();
        String localPath = splash.editSynchronizationDialogLocalPathTextField.getText();
        String remotePath = splash.editSynchronizationDialogRemotePathTextField.getText();
        // String frequency = desktop.editSynchronizationDialogFrequencyComboBox.getSelectedItem().toString();

        Synchronization synch = new Synchronization();
        synch.setName(name);

        IDROPCore core = splash.getDesktop().getiDropCore();
        IRODSAccount account = core.getIrodsAccount();

        String host = account.getHost();
        synch.setIrodsHostName(host);

        int port = account.getPort();
        synch.setIrodsPort(port);

        String username = account.getUserName();
        synch.setIrodsUserName(username);

        String password = account.getPassword();
        synch.setIrodsPassword(password);

        String zone = account.getZone();
        synch.setIrodsZone(zone);

        synch.setLocalSynchDirectory(localPath);
        synch.setIrodsSynchDirectory(remotePath);
        synch.setSynchronizationMode(SynchronizationType.ONE_WAY_LOCAL_TO_IRODS);
        synch.setCreatedAt(new Date());

        // frequency: for (FrequencyType ft : FrequencyType.values()) {
        // if (ft.getReadableName().equals(frequency)) {
        // synch.setFrequencyType(ft);
        // break frequency;
        // }
        // }

//        try {
//            // save device name on server
//            SynchPropertiesService synchPropService = new SynchPropertiesServiceImpl(core.getIRODSAccessObjectFactory(), account);
//            synchPropService.addSynchDeviceForUserAndIrodsAbsolutePath(username, deviceName, remotePath, localPath);
//        } catch (DuplicateDataException e) {
//            log.error("DuplicateDataException", e);
//            MessageUtil.showError(splash.editSynchronizationDialog, "Device name is not unique", "Error");
//            return;
//        } catch (JargonException e) {
//            e.printStackTrace();
//        }

        try {
            // save synch locally
            SynchronizationDAO synchronizationDAO = transferDAOMgr.getTransferDAOBean().getSynchronizationDAO();
            synchronizationDAO.save(synch);
        } catch (TransferDAOException e) {
            e.printStackTrace();
        }

        Preferences preferences = splash.getDesktop().getiDropCore().getPreferences();
        preferences.put(PREFERENCE_KEY_DEVICE_NAME, deviceName);
        splash.editSynchronizationDialog.setVisible(false);
    }

    private ValidationResult validateLoginForm() {
        ValidationResult result = new ValidationResult();

        if (StringUtils.isEmpty(splash.editSynchronizationDialogNameTextField.getText())) {
            result.addError("Name is required");
        }

        if (StringUtils.isEmpty(splash.editSynchronizationDialogDeviceNameTextField.getText())) {
            result.addError("Device Name is required");
        }

        if (StringUtils.isEmpty(splash.editSynchronizationDialogLocalPathTextField.getText())) {
            result.addError("Local Path is required");
        }

        if (StringUtils.isEmpty(splash.editSynchronizationDialogRemotePathTextField.getText())) {
            result.addError("Remote Path is required");
        }

        // if (desktop.editSynchronizationDialogFrequencyComboBox.getSelectedIndex() == 0) {
        // result.addError("Please choose a valid frequency");
        // }

        return result;
    }

}
