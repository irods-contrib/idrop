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

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Object value, final int index, final boolean isSelected,
            final boolean hasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, hasFocus);
        if (value != null && value instanceof String) {
            Icon icon = UIManager.getIcon("FileView.directoryIcon");
            label.setIcon(icon);
            label.setBorder(BorderFactory.createEmptyBorder(0, index * 8, 0, 0));
        }
        return (label);
    }
}
