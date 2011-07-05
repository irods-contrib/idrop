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
import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupRemoteFileChooserDialogUpFolderActionListener implements ActionListener {

    private IDROPSplashWindow splash;

    public StartupRemoteFileChooserDialogUpFolderActionListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splash.remoteFileChooserDialogLookInComboBox.getItemCount() - 1; i++) {
            Object o = splash.remoteFileChooserDialogLookInComboBox.getItemAt(i);
            if (i > 1) {
                sb.append("/").append(o.toString());
            } else {
                sb.append(o.toString());
            }
        }

        String path = sb.toString();

        if (StringUtils.isNotEmpty(path)) {

            splash.remoteFileChooserDialogListModel.clear();
            splash.remoteFileChooserDialogLookInComboBox.removeAllItems();
            splash.remoteFileChooserDialogLookInComboBox.addItem("/");

            CollectionAndDataObjectListingEntry entry = new CollectionAndDataObjectListingEntry();
            entry.setPathOrName(sb.toString());
            splash.remoteFileChooserDialogFileNameTextField.setText(entry.getPathOrName());

            String[] parentEntries = entry.getFormattedAbsolutePath().split("/");

            for (int i = 0; i < parentEntries.length; i++) {
                if (StringUtils.isNotEmpty(parentEntries[i])) {
                    splash.remoteFileChooserDialogLookInComboBox.addItem(parentEntries[i]);
                }
            }
            splash.remoteFileChooserDialogLookInComboBox.setSelectedIndex(splash.remoteFileChooserDialogLookInComboBox
                    .getItemCount() - 1);

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
