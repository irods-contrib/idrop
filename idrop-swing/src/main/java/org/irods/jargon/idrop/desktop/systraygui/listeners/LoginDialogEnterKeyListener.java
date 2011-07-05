package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class LoginDialogEnterKeyListener extends KeyAdapter {

    private IDROPSplashWindow desktop;

    public LoginDialogEnterKeyListener(IDROPSplashWindow desktop) {
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
            ActionEvent ae = new ActionEvent(desktop.loginDialogOKButton, ActionEvent.ACTION_PERFORMED, "");
            desktop.loginDialogOKActionListener.actionPerformed(ae);
        }
    }
}
