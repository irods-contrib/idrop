package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

/**
 * 
 * @author jdr0887
 * 
 */
public class StartupPreferencesDialogDefaultLocalDirectoryActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupPreferencesDialogDefaultLocalDirectoryActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    public void actionPerformed(ActionEvent e) {

        splash.preferencesDialogDefaultLocalDirectoryFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        splash.preferencesDialogDefaultLocalDirectoryFileChooser.setCurrentDirectory(new File(System
                .getProperty("user.home")));
        splash.preferencesDialogDefaultLocalDirectoryFileChooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }

        });
        int response = splash.preferencesDialogDefaultLocalDirectoryFileChooser
                .showSaveDialog(splash.preferencesDialog);
        if (response == JFileChooser.APPROVE_OPTION) {
            File file = splash.preferencesDialogDefaultLocalDirectoryFileChooser.getSelectedFile();
            if (file != null && file.isDirectory()) {
                splash.preferencesDialogDefaultLocalDirectoryTextField.setText(file.getAbsolutePath());
            }
        }

    }
}
