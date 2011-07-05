package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class ChangePasswordMenuActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public ChangePasswordMenuActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {
        StringBuilder sb = new StringBuilder();
        IRODSAccount account = desktop.getiDropCore().getIrodsAccount();
        sb.append("irods://").append(account.getUserName()).append("@").append(account.getHost()).append(":")
                .append(account.getPort());

        desktop.changePasswordDialogCurrentAccountTextField.setText(sb.toString());
        desktop.changePasswordDialog.setVisible(true);
    }

}
