package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

/**
 * 
 * @author jdr0887
 * 
 */
public class StartupPreferencesDialogCancelActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupPreferencesDialogCancelActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    public void actionPerformed(ActionEvent e) {
        if (splash.preferencesDialog != null) {
            splash.preferencesDialog.setVisible(false);
        }
    }

}
