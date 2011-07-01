package org.irods.jargon.idrop.desktop.systraygui.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * 
 * @author jdr0887
 * 
 */
public class MessageUtil {

    public static void showError(Component rootComponent, String message, String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component rootComponent, String message, String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showMessage(Component rootComponent, String message, String title) {
        JOptionPane.showMessageDialog(rootComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirm(Component rootComponent, String message, String title) {
        return showConfirm(rootComponent, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirm(Component rootComponent, String message, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(rootComponent, message, title, optionType, messageType);
    }
}
