package org.irods.jargon.idrop.desktop.systraygui.components;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * 
 * @author jdr0887
 *
 */
public class RemoteFileChooserDialogLookInComboBoxRender extends DefaultListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean hasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        if (value != null && value instanceof String) {
            Icon icon = UIManager.getIcon("FileView.directoryIcon");
            label.setIcon(icon);
            label.setBorder(BorderFactory.createEmptyBorder(0, index * 8, 0, 0));
        }
        return (label);
    }

}
