package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEFAULT_LOCAL_DIR;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_HIDDEN_FILES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_UI;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_SPLASH;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class PreferencesDialogSaveActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public PreferencesDialogSaveActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {

        boolean showUI = desktop.preferencesDialogShowUICheckBox.isSelected();
        desktop.getiDropCore().getPreferences().putBoolean(PREFERENCE_KEY_SHOW_UI, showUI ? true : false);

        boolean showSplash = desktop.preferencesDialogShowSplashScreenCheckBox.isSelected();
        desktop.getiDropCore().getPreferences().putBoolean(PREFERENCE_KEY_SHOW_SPLASH, showSplash ? true : false);

        String defaultLocalDir = desktop.preferencesDialogDefaultLocalDirectoryTextField.getText();
        desktop.getiDropCore().getPreferences().put(PREFERENCE_KEY_DEFAULT_LOCAL_DIR, defaultLocalDir);

        boolean showHiddenFiles = desktop.preferencesDialogShowHiddenFilesCheckBox.isSelected();
        desktop.getiDropCore().getPreferences()
                .putBoolean(PREFERENCE_KEY_SHOW_HIDDEN_FILES, showHiddenFiles ? true : false);

        desktop.preferencesDialog.setVisible(false);
    }

}
