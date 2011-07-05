package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class EditSynchronizationDialogRemotePathBrowseActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public EditSynchronizationDialogRemotePathBrowseActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        CollectionAndDataObjectListingEntry rootEntry = new CollectionAndDataObjectListingEntry();
        rootEntry.setPathOrName("/");
        desktop.remoteFileChooserDialog.setLocationRelativeTo(desktop.editSynchronizationDialog);
        desktop.remoteFileChooserDialogListModel.clear();
        desktop.remoteFileChooserDialogLookInComboBox.removeAllItems();
        desktop.remoteFileChooserDialogLookInComboBox.addItem("/");

        try {

            IRODSFileSystem irodsFS = desktop.getiDropCore().getIrodsFileSystem();
            IRODSAccessObjectFactory irodsAOFactory = irodsFS.getIRODSAccessObjectFactory();
            CollectionAndDataObjectListAndSearchAO collectionAO = irodsAOFactory
                    .getCollectionAndDataObjectListAndSearchAO(desktop.getiDropCore().getIrodsAccount());
            List<CollectionAndDataObjectListingEntry> childCache = collectionAO
                    .listDataObjectsAndCollectionsUnderPath(rootEntry.getPathOrName());

            if (childCache != null) {

                for (int i = 0; i < childCache.size(); i++) {
                    CollectionAndDataObjectListingEntry childEntry = childCache.get(i);
                    desktop.remoteFileChooserDialogListModel.addElement(childEntry);
                }

            }
            desktop.remoteFileChooserDialog.setVisible(true);
        } catch (JargonException e1) {
            e1.printStackTrace();
        } catch (SecurityException se) {
        }

    }

}
