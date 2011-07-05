package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class ChangePasswordDialogEnterKeyListener extends KeyAdapter {

    private IDROPDesktop desktop;

    public ChangePasswordDialogEnterKeyListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            ActionEvent ae = new ActionEvent(desktop.changePasswordDialogSaveButton, ActionEvent.ACTION_PERFORMED, "");
            desktop.changePasswordDialogSaveActionListener.actionPerformed(ae);
        }
    }
}
