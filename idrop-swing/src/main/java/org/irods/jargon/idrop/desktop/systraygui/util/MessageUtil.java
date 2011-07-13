package org.irods.jargon.idrop.desktop.systraygui.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * 
 * @author jdr0887
 * 
 */
public class MessageUtil {

    public static void showError(final Component rootComponent,
            final String message, final String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(final Component rootComponent,
            final String message, final String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showMessage(final Component rootComponent,
            final String message, final String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirm(final Component rootComponent,
            final String message, final String title) {
        return showConfirm(rootComponent, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirm(final Component rootComponent,
            final String message, final String title, final int optionType,
            final int messageType) {
        return JOptionPane.showConfirmDialog(rootComponent, message, title,
                optionType, messageType);
    }
}
