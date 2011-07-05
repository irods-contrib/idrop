package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEFAULT_LOCAL_DIR;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_PREFERENCES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_SPLASH;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class PreferencesMenuActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public PreferencesMenuActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {

        Preferences prefs = desktop.getiDropCore().getPreferences();
        
        String deviceName = prefs.get(PREFERENCE_KEY_DEVICE_NAME, null);
        if (StringUtils.isNotEmpty(deviceName)) {
            desktop.preferencesDialogDeviceNameTextField.setText(deviceName);
        }

        String defaultLocalDirectory = prefs.get(PREFERENCE_KEY_DEFAULT_LOCAL_DIR, null);
        if (StringUtils.isNotEmpty(defaultLocalDirectory)) {
            desktop.preferencesDialogDefaultLocalDirectoryTextField.setText(defaultLocalDirectory);
        }

        boolean showPreferences = prefs.getBoolean(PREFERENCE_KEY_SHOW_PREFERENCES, true);
        desktop.preferencesDialogShowPreferencesCheckBox.setSelected(showPreferences ? true : false);

        boolean showSplash = prefs.getBoolean(PREFERENCE_KEY_SHOW_SPLASH, true);
        desktop.preferencesDialogShowSplashScreenCheckBox.setSelected(showSplash ? true : false);

        boolean showGUI = prefs.getBoolean(PREFERENCE_KEY_SHOW_UI, true);
        desktop.preferencesDialogShowUICheckBox.setSelected(showGUI);

        desktop.preferencesDialog.setLocationRelativeTo(desktop.mainFrame);
        desktop.preferencesDialog.setVisible(true);
    }

}
