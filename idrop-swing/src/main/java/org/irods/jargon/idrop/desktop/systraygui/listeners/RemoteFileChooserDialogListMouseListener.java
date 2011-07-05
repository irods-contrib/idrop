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
import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

public class RemoteFileChooserDialogListMouseListener extends MouseAdapter {

    private IDROPDesktop desktop;

    public RemoteFileChooserDialogListMouseListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
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

        Object o = desktop.remoteFileChooserDialogList.getSelectedValue();

        if (o instanceof CollectionAndDataObjectListingEntry) {

            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) o;
            desktop.remoteFileChooserDialogFileNameTextField.setText(entry.getPathOrName());

        }

    }

    private void doDoubleClickStuff(MouseEvent me) {

        Object o = desktop.remoteFileChooserDialogList.getSelectedValue();

        if (o instanceof CollectionAndDataObjectListingEntry) {

            CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) o;
            desktop.remoteFileChooserDialogListModel.clear();
            desktop.remoteFileChooserDialogLookInComboBox.removeAllItems();
            desktop.remoteFileChooserDialogLookInComboBox.addItem("/");

            String[] parentEntries = entry.getFormattedAbsolutePath().split("/");

            for (int i = 0; i < parentEntries.length; i++) {
                if (StringUtils.isNotEmpty(parentEntries[i])) {
                    desktop.remoteFileChooserDialogLookInComboBox.addItem(parentEntries[i]);
                } 
            }
            desktop.remoteFileChooserDialogLookInComboBox.setSelectedIndex(parentEntries.length - 1);

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
