/*
 *A listener for iRODS tree selected
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;

import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.slf4j.LoggerFactory;

/**
 * Class to encapsulate handling of info panel. This object will listen to tree
 * selection events in the iDROP iRODS tree, and initialize the info panel.
 *
 *
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IrodsSelectionListenerForBuildingInfoPanel implements
        ListSelectionListener {

    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(IrodsSelectionListenerForBuildingInfoPanel.class);
    private final iDrop idrop;

    public IrodsSelectionListenerForBuildingInfoPanel(final iDrop idrop)
            throws IdropException {
        if (idrop == null) {
            throw new IdropException("null iDrop");
        }

        this.idrop = idrop;

    }

    public void treeExpanded(final TreeExpansionEvent event) {
        /*
         * TreePath expandedTreePath = event.getPath(); IRODSNode expandedNode =
         * (IRODSNode) expandedTreePath.getLastPathComponent(); try {
         * identifyNodeTypeAndInitializeInfoPanel(expandedNode); } catch
         * (IdropException ex) {
         * Logger.getLogger(IrodsTreeListenerForBuildingInfoPanel
         * .class.getName()).log(Level.SEVERE, null, ex); throw new
         * IdropRuntimeException
         * ("exception processing treeExpanded() event for IRODSNode selection"
         * ); }
         */
    }

    public void treeCollapsed(final TreeExpansionEvent event) {
        // operation not needed, left for interface contract
    }

    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting()) {
            return;
        }
        log.info("lse: {}", lse);
        idrop.triggerInfoPanelUpdate();
        idrop.triggerToolbarUpdate();
    }
}
