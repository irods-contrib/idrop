package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

/**
 * 
 * @author jdr0887
 * 
 */
public class LoginCancelActionListener implements ActionListener {

    private IDROPSplashWindow desktop;

    public LoginCancelActionListener(IDROPSplashWindow desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {
        if (desktop.loginDialog != null) {
            desktop.loginDialog.setVisible(false);
            System.exit(0);
        }
    }

}
