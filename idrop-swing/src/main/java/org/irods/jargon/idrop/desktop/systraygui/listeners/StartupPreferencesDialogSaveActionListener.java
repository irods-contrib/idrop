package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEFAULT_LOCAL_DIR;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_HIDDEN_FILES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_PREFERENCES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_SPLASH;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

/**
 * 
 * @author jdr0887
 * 
 */
public class StartupPreferencesDialogSaveActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupPreferencesDialogSaveActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    public void actionPerformed(ActionEvent e) {

        Preferences prefs = splash.getDesktop().getiDropCore().getPreferences();

        boolean showUI = splash.preferencesDialogShowUICheckBox.isSelected();
        prefs.putBoolean(PREFERENCE_KEY_SHOW_UI, showUI ? true : false);

        boolean showPreferences = splash.preferencesDialogShowPreferencesCheckBox.isSelected();
        prefs.putBoolean(PREFERENCE_KEY_SHOW_PREFERENCES, showPreferences ? true : false);

        boolean showSplash = splash.preferencesDialogShowSplashScreenCheckBox.isSelected();
        prefs.putBoolean(PREFERENCE_KEY_SHOW_SPLASH, showSplash ? true : false);

        String defaultLocalDir = splash.preferencesDialogDefaultLocalDirectoryTextField.getText();
        prefs.put(PREFERENCE_KEY_DEFAULT_LOCAL_DIR, defaultLocalDir);

        String deviceName = splash.preferencesDialogDeviceNameTextField.getText();
        prefs.put(PREFERENCE_KEY_DEVICE_NAME, deviceName);

        boolean showHiddenFiles = splash.preferencesDialogShowHiddenFilesCheckBox.isSelected();
        prefs.putBoolean(PREFERENCE_KEY_SHOW_HIDDEN_FILES, showHiddenFiles ? true : false);

        splash.preferencesDialog.setVisible(false);
    }

}
