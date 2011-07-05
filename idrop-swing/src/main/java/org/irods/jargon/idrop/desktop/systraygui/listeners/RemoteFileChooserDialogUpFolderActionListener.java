package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class RemoteFileChooserDialogUpFolderActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public RemoteFileChooserDialogUpFolderActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < desktop.remoteFileChooserDialogLookInComboBox.getItemCount() - 1; i++) {
            Object o = desktop.remoteFileChooserDialogLookInComboBox.getItemAt(i);
            if (i > 1) {
                sb.append("/").append(o.toString());
            } else {
                sb.append(o.toString());
            }
        }

        String path = sb.toString();

        if (StringUtils.isNotEmpty(path)) {

            desktop.remoteFileChooserDialogListModel.clear();
            desktop.remoteFileChooserDialogLookInComboBox.removeAllItems();
            desktop.remoteFileChooserDialogLookInComboBox.addItem("/");

            CollectionAndDataObjectListingEntry entry = new CollectionAndDataObjectListingEntry();
            entry.setPathOrName(sb.toString());
            desktop.remoteFileChooserDialogFileNameTextField.setText(entry.getPathOrName());
            
            String[] parentEntries = entry.getFormattedAbsolutePath().split("/");

            for (int i = 0; i < parentEntries.length; i++) {
                if (StringUtils.isNotEmpty(parentEntries[i])) {
                    desktop.remoteFileChooserDialogLookInComboBox.addItem(parentEntries[i]);
                } 
            }
            desktop.remoteFileChooserDialogLookInComboBox.setSelectedIndex(desktop.remoteFileChooserDialogLookInComboBox.getItemCount() - 1);

            try {

                IRODSFileSystem irodsFS = desktop.getiDropCore().getIrodsFileSystem();
                IRODSAccessObjectFactory irodsAOFactory = irodsFS.getIRODSAccessObjectFactory();
                CollectionAndDataObjectListAndSearchAO collectionAO = irodsAOFactory
                        .getCollectionAndDataObjectListAndSearchAO(desktop.getiDropCore().getIrodsAccount());
                List<CollectionAndDataObjectListingEntry> childCache = collectionAO
                        .listDataObjectsAndCollectionsUnderPath(entry.getPathOrName());

                if (childCache != null) {

                    for (int i = 0; i < childCache.size(); i++) {
                        CollectionAndDataObjectListingEntry childEntry = childCache.get(i);
                        desktop.remoteFileChooserDialogListModel.addElement(childEntry);
                    }

                }
            } catch (JargonException e1) {
                e1.printStackTrace();
            } catch (SecurityException se) {
            }

        }

    }

}
