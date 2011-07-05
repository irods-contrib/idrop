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

public class StartupRemoteFileChooserDialogLookInActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupRemoteFileChooserDialogLookInActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        Object o = splash.remoteFileChooserDialogLookInComboBox.getSelectedItem();

        if (o instanceof CollectionAndDataObjectListingEntry) {

            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) o;
            splash.remoteFileChooserDialogListModel.clear();

            try {

                IRODSFileSystem irodsFS = splash.getDesktop().getiDropCore().getIrodsFileSystem();
                IRODSAccessObjectFactory irodsAOFactory = irodsFS.getIRODSAccessObjectFactory();
                CollectionAndDataObjectListAndSearchAO collectionAO = irodsAOFactory
                        .getCollectionAndDataObjectListAndSearchAO(splash.getDesktop().getiDropCore().getIrodsAccount());
                List<CollectionAndDataObjectListingEntry> childCache = collectionAO
                        .listDataObjectsAndCollectionsUnderPath(entry.getPathOrName());

                if (childCache != null) {

                    for (int i = 0; i < childCache.size(); i++) {
                        CollectionAndDataObjectListingEntry childEntry = childCache.get(i);
                        splash.remoteFileChooserDialogListModel.addElement(childEntry);
                    }

                }
            } catch (JargonException e1) {
                e1.printStackTrace();
            } catch (SecurityException se) {
            }

        }
    }

}
