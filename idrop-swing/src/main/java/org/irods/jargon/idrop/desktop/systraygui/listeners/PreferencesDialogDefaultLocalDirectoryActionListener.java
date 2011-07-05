package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class PreferencesDialogDefaultLocalDirectoryActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public PreferencesDialogDefaultLocalDirectoryActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {

        desktop.preferencesDialogDefaultLocalDirectoryFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        desktop.preferencesDialogDefaultLocalDirectoryFileChooser.setCurrentDirectory(new File(System
                .getProperty("user.home")));
        desktop.preferencesDialogDefaultLocalDirectoryFileChooser.addChoosableFileFilter(new FileFilter() {

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
        int response = desktop.preferencesDialogDefaultLocalDirectoryFileChooser
                .showSaveDialog(desktop.preferencesDialog);
        if (response == JFileChooser.APPROVE_OPTION) {
            File file = desktop.preferencesDialogDefaultLocalDirectoryFileChooser.getSelectedFile();
            if (file != null && file.isDirectory()) {
                desktop.preferencesDialogDefaultLocalDirectoryTextField.setText(file.getAbsolutePath());
            }
        }

    }
}
