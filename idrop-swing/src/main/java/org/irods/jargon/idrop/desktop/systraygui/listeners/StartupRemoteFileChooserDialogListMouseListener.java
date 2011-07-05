package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.IDROPSplashWindow;

public class StartupRemoteFileChooserDialogListMouseListener extends MouseAdapter {

    private IDROPSplashWindow splash;

    public StartupRemoteFileChooserDialogListMouseListener(IDROPSplashWindow splash) {
        super();
        this.splash = splash;
    }

    @Override
    public void mousePressed(MouseEvent me) {

        if (me.getClickCount() == 2) {
            doDoubleClickStuff(me);
        } else {
            doSingleClickStuff(me);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getClickCount() == 2) {
            doDoubleClickStuff(me);
        } else {
            doSingleClickStuff(me);
        }
    }

    private void doSingleClickStuff(MouseEvent me) {

        Object o = splash.remoteFileChooserDialogList.getSelectedValue();

        if (o instanceof CollectionAndDataObjectListingEntry) {

            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) o;
            splash.remoteFileChooserDialogFileNameTextField.setText(entry.getPathOrName());

        }

    }

    private void doDoubleClickStuff(MouseEvent me) {

        Object o = splash.remoteFileChooserDialogList.getSelectedValue();

        if (o instanceof CollectionAndDataObjectListingEntry) {

            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) o;
            splash.remoteFileChooserDialogListModel.clear();
            splash.remoteFileChooserDialogLookInComboBox.removeAllItems();
            splash.remoteFileChooserDialogLookInComboBox.addItem("/");

            String[] parentEntries = entry.getFormattedAbsolutePath().split("/");

            for (int i = 0; i < parentEntries.length; i++) {
                if (StringUtils.isNotEmpty(parentEntries[i])) {
                    splash.remoteFileChooserDialogLookInComboBox.addItem(parentEntries[i]);
                }
            }
            splash.remoteFileChooserDialogLookInComboBox.setSelectedIndex(parentEntries.length - 1);

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
