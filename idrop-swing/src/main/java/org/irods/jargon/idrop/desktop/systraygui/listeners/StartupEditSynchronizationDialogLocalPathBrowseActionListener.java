package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupEditSynchronizationDialogLocalPathBrowseActionListener implements ActionListener {

    private IDROPSplashWindow desktop;

    public StartupEditSynchronizationDialogLocalPathBrowseActionListener(IDROPSplashWindow desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        desktop.editSynchronizationDialogLocalPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        desktop.editSynchronizationDialogLocalPathFileChooser.setCurrentDirectory(new File(System
                .getProperty("user.home")));
        desktop.editSynchronizationDialogLocalPathFileChooser.addChoosableFileFilter(new FileFilter() {

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
        int response = desktop.editSynchronizationDialogLocalPathFileChooser
                .showOpenDialog(desktop.editSynchronizationDialog);
        if (response == JFileChooser.APPROVE_OPTION) {
            File file = desktop.editSynchronizationDialogLocalPathFileChooser.getSelectedFile();
            if (file != null && file.isDirectory()) {
                desktop.editSynchronizationDialogLocalPathTextField.setText(file.getAbsolutePath());
            }
        }

    }

}
