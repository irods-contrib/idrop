package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.idrop.desktop.systraygui.util.MessageUtil;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

/**
 * 
 * @author jdr0887
 * 
 */
public class ChangePasswordSaveActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public ChangePasswordSaveActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {
        ValidationResult validationResults = validateLoginForm();

        if (validationResults.hasErrors()) {
            String msg = "";
            List<ValidationMessage> errors = validationResults.getErrors();
            for (ValidationMessage error : errors) {
                msg += error.formattedText() + "\n";
            }
            JOptionPane.showMessageDialog(desktop.changePasswordDialog, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = new String(desktop.changePasswordDialogPasswordPasswordField.getPassword());

        try {
            IRODSAccount irodsAccount = desktop.getiDropCore().getIrodsAccount();
            // List<Transfer> recentQueue = idrop.getTransferManager().getRecentQueue();
            List<LocalIRODSTransfer> recentQueue = null;
            if (recentQueue != null) {
                for (LocalIRODSTransfer transfer : recentQueue) {
                    if (transfer.getTransferHost().equals(irodsAccount.getHost())
                            && transfer.getTransferZone().equals(irodsAccount.getZone())
                            && transfer.getTransferUserName().equals(irodsAccount.getUserName())) {
                        // FIXME: right now, do not differentiate transfers that are comlete, this is a hack right now
                        // if (localIRODSTransfer.getTransferState().equals(localIRODSTransfer.TRANSFER_STATE_COMPLETE))
                        // {
                        // log.info("matched transfer was complete, ignored");
                        // } else {
                        MessageUtil
                                .showMessage(
                                        desktop.changePasswordDialog,
                                        "Transfers for this account are pending, this account can not be changed until completed and purged",
                                        "Transfer Pending");
                        return;
                        // }
                    }
                }
            }

            IRODSFileSystem irodsFileSystem = desktop.getiDropCore().getIrodsFileSystem();
            IRODSAccessObjectFactory irodsAOFactory = irodsFileSystem.getIRODSAccessObjectFactory();
            UserAO userAO = irodsAOFactory.getUserAO(desktop.getiDropCore().getIrodsAccount());
            userAO.changeAUserPasswordByThatUser(irodsAccount.getUserName(), irodsAccount.getPassword(), password);
            IRODSAccount newAccount = new IRODSAccount(irodsAccount.getHost(), irodsAccount.getPort(),
                    irodsAccount.getUserName(), password, irodsAccount.getHomeDirectory(), irodsAccount.getZone(),
                    irodsAccount.getDefaultStorageResource());
            desktop.getiDropCore().setIrodsAccount(newAccount);
            desktop.changePasswordDialogPasswordPasswordField.setText("");
            desktop.changePasswordDialogPasswordConfirmPasswordField.setText("");

        } catch (JargonException ex) {
        } finally {
            // idrop.getIrodsFileSystem().closeAndEatExceptions(idrop.getIrodsAccount());
        }

        MessageUtil.showMessage(desktop.changePasswordDialog, "Password was changed", "Password Changed");

    }

    private ValidationResult validateLoginForm() {
        ValidationResult result = new ValidationResult();

        String password = new String(desktop.changePasswordDialogPasswordPasswordField.getPassword());
        String passwordConfirm = new String(desktop.changePasswordDialogPasswordConfirmPasswordField.getPassword());

        if (StringUtils.isEmpty(password)) {
            desktop.changePasswordDialogPasswordPasswordField.setBackground(Color.red);
        }

        if (StringUtils.isEmpty(passwordConfirm)) {
            desktop.changePasswordDialogPasswordConfirmPasswordField.setBackground(Color.red);
        }

        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(passwordConfirm)) {
            result.addError("New or confirm password is missing");
        }

        if (!password.equals(passwordConfirm)) {
            desktop.changePasswordDialogPasswordPasswordField.setBackground(Color.red);
            desktop.changePasswordDialogPasswordConfirmPasswordField.setBackground(Color.red);
            result.addError("Passwords do not match");
        }

        return result;
    }

}
