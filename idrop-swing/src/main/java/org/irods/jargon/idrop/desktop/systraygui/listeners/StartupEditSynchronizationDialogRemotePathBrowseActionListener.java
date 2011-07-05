package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupEditSynchronizationDialogRemotePathBrowseActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupEditSynchronizationDialogRemotePathBrowseActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        CollectionAndDataObjectListingEntry rootEntry = new CollectionAndDataObjectListingEntry();
        rootEntry.setPathOrName("/");
        splash.remoteFileChooserDialog.setLocationRelativeTo(splash.editSynchronizationDialog);
        splash.remoteFileChooserDialogListModel.clear();
        splash.remoteFileChooserDialogLookInComboBox.removeAllItems();
        splash.remoteFileChooserDialogLookInComboBox.addItem("/");

        try {

            IRODSFileSystem irodsFS = splash.getDesktop().getiDropCore().getIrodsFileSystem();
            IRODSAccessObjectFactory irodsAOFactory = irodsFS.getIRODSAccessObjectFactory();
            CollectionAndDataObjectListAndSearchAO collectionAO = irodsAOFactory
                    .getCollectionAndDataObjectListAndSearchAO(splash.getDesktop().getiDropCore().getIrodsAccount());
            List<CollectionAndDataObjectListingEntry> childCache = collectionAO
                    .listDataObjectsAndCollectionsUnderPath(rootEntry.getPathOrName());

            if (childCache != null) {

                for (int i = 0; i < childCache.size(); i++) {
                    CollectionAndDataObjectListingEntry childEntry = childCache.get(i);
                    splash.remoteFileChooserDialogListModel.addElement(childEntry);
                }

            }
            splash.remoteFileChooserDialog.setVisible(true);
        } catch (JargonException e1) {
            e1.printStackTrace();
        } catch (SecurityException se) {
        }

    }

}
