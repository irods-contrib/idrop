package org.irods.jargon.idrop.desktop.systraygui.components;

import static org.irods.jargon.core.query.CollectionAndDataObjectListingEntry.ObjectType.DATA_OBJECT;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

/**
 * 
 * @author jdr0887
 *
 */
public class RemoteFSChooserListCellRenderer extends DefaultListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
     * boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean hasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        if (value instanceof CollectionAndDataObjectListingEntry) {
            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) value;

            Icon icon = null;
            if (!DATA_OBJECT.equals(entry.getObjectType())) {
                icon = UIManager.getIcon("FileView.directoryIcon");
            } else {
                icon = UIManager.getIcon("FileView.fileIcon");
            }

            label.setIcon(icon);
        } else {
            label.setIcon(null);
        }
        return (label);
    }

}
