package org.irods.jargon.idrop.desktop.systraygui.listeners;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class ChangePasswordDialogWindowListener extends WindowAdapter {

    private IDROPDesktop desktop;

    public ChangePasswordDialogWindowListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        desktop.mainFrame.requestFocus();
    }

}
