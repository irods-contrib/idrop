package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * 
 * @author jdr0887
 * 
 */
public class MessageManager {

	public static final String TITLE_MESSAGE = "iDrop Message";

	public static void showError(final Component rootComponent,
			final String message, final String title) {
		JOptionPane.showMessageDialog(rootComponent, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
        
        public static void showError(final Component rootComponent,
			final String message) {
		JOptionPane.showMessageDialog(rootComponent, message, TITLE_MESSAGE,
				JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(final Component rootComponent,
			final String message, final String title) {
		JOptionPane.showMessageDialog(rootComponent, message, title,
				JOptionPane.WARNING_MESSAGE);
	}
        
        public static void showWarning(final Component rootComponent,
			final String message) {
		JOptionPane.showMessageDialog(rootComponent, message, TITLE_MESSAGE,
				JOptionPane.WARNING_MESSAGE);
	}

	public static void showMessage(final Component rootComponent,
			final String message, final String title) {
		JOptionPane.showMessageDialog(rootComponent, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}
        
        public static void showMessage(final Component rootComponent,
			final String message) {
		JOptionPane.showMessageDialog(rootComponent, message, TITLE_MESSAGE,
				JOptionPane.INFORMATION_MESSAGE);
	}
}
