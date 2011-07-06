package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_LOGIN_HOST;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_LOGIN_PORT;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_LOGIN_RESOURCE;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_LOGIN_USERNAME;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_LOGIN_ZONE;

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
import org.irods.jargon.core.pub.domain.User;
import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;
import org.irods.jargon.idrop.desktop.systraygui.util.MessageUtil;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

/**
 * 
 * @author jdr0887
 * 
 */
public class LoginOKActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public LoginOKActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    public void actionPerformed(ActionEvent e) {

        ValidationResult validationResults = validateLoginForm();

        if (validationResults.hasErrors()) {
            String msg = "";
            List<ValidationMessage> errors = validationResults.getErrors();
            for (ValidationMessage error : errors) {
                msg += error.formattedText() + "\n";
            }
            JOptionPane.showMessageDialog(splash.loginDialog, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = splash.loginDialogUsernameTextField.getText();
        String password = new String(splash.loginDialogPasswordPasswordField.getPassword());
        String host = splash.loginDialogHostTextField.getText();
        String port = splash.loginDialogPortTextField.getText();
        String resource = splash.loginDialogResourceTextField.getText();
        String zone = splash.loginDialogZoneTextField.getText();

        splash.getDesktop().getiDropCore().getPreferences().put(PREFERENCE_KEY_LOGIN_HOST, host);
        splash.getDesktop().getiDropCore().getPreferences().put(PREFERENCE_KEY_LOGIN_ZONE, zone);
        splash.getDesktop().getiDropCore().getPreferences().put(PREFERENCE_KEY_LOGIN_RESOURCE, resource);
        splash.getDesktop().getiDropCore().getPreferences().put(PREFERENCE_KEY_LOGIN_USERNAME, username);
        splash.getDesktop().getiDropCore().getPreferences().put(PREFERENCE_KEY_LOGIN_PORT, port);

        StringBuilder sb = new StringBuilder();
        sb.append('/').append(zone).append("/home/").append(username);

        IRODSAccount irodsAccount;
        try {
            irodsAccount = IRODSAccount.instance(host, Integer.valueOf(port), username, password, sb.toString(), zone,
                    resource);
        } catch (JargonException e1) {
            e1.printStackTrace();
            MessageUtil.showError(splash.loginDialog, e1.getMessage(), "IRODS Account");
            return;
        }

        IRODSFileSystem irodsFileSystem = null;
        try {
            irodsFileSystem = IRODSFileSystem.instance();
            IRODSAccessObjectFactory irodsAOFactory = irodsFileSystem.getIRODSAccessObjectFactory();
            UserAO userAO = irodsAOFactory.getUserAO(irodsAccount);
            User loggedInUser = userAO.findByName(username);
        } catch (JargonException ex) {
            if (ex.getMessage().indexOf("Connection refused") > -1) {
                MessageUtil.showError(splash.loginDialog, "Cannot connect to the server, is it down?", "Login Error");
            } else if (ex.getMessage().indexOf("Connection reset") > -1) {
                MessageUtil.showError(splash.loginDialog, "Cannot connect to the server, is it down?", "Login Error");
            } else if (ex.getMessage().indexOf("io exception opening socket") > -1) {
                MessageUtil.showError(splash.loginDialog, "Cannot connect to the server, is it down?", "Login Error");
            } else {
                MessageUtil.showError(splash.loginDialog, "login error - unable to log in, or invalid user id",
                        "Login Error");
            }
            return;
        } finally {
        }

        splash.getDesktop().getiDropCore().setIrodsFileSystem(irodsFileSystem);
        splash.getDesktop().getiDropCore().setIrodsAccount(irodsAccount);
        splash.loginDialog.setVisible(false);
    }

    private ValidationResult validateLoginForm() {
        ValidationResult result = new ValidationResult();

        if (StringUtils.isEmpty(splash.loginDialogUsernameTextField.getText())) {
            result.addError("Username is required");
        }

        if (StringUtils.isEmpty(new String(splash.loginDialogPasswordPasswordField.getPassword()))) {
            result.addError("Password is required");
        }

        if (StringUtils.isEmpty(splash.loginDialogHostTextField.getText())) {
            result.addError("Host is required");
        }

        if (StringUtils.isEmpty(splash.loginDialogResourceTextField.getText())) {
            result.addError("Resource is required");
        }

        if (StringUtils.isEmpty(splash.loginDialogZoneTextField.getText())) {
            result.addError("Zone is required");
        }

        if (StringUtils.isEmpty(splash.loginDialogPortTextField.getText())) {
            result.addError("Port is required");
        }

        if (StringUtils.isNotEmpty(splash.loginDialogPortTextField.getText())) {
            try {
                Integer.valueOf(splash.loginDialogPortTextField.getText());
            } catch (NumberFormatException e) {
                result.addError("Port is invalid");
            }
        }

        return result;
    }

}
