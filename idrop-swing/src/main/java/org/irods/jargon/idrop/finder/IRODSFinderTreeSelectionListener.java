/*
 *A listener for iRODS finder dialog tree selected
 */
package org.irods.jargon.idrop.finder;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;

import org.irods.jargon.idrop.exceptions.IdropException;
import org.slf4j.LoggerFactory;

/**
 * This object will listen to tree selection events in the iDrop Finder Dialog
 * iRODS tree, and update the Select button on the iRODS Finder dialog
 * 
 * 
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFinderTreeSelectionListener implements ListSelectionListener {

	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(IRODSFinderTreeSelectionListener.class);
	private final IRODSFinderDialog irodsFinderDialog;

	public IRODSFinderTreeSelectionListener(
			final IRODSFinderDialog irodsFinderDialog) throws IdropException {
		if (irodsFinderDialog == null) {
			throw new IdropException("null irods finder dialog");
		}

		this.irodsFinderDialog = irodsFinderDialog;

	}

	public void treeExpanded(final TreeExpansionEvent event) {
		// operation not needed, left for interface contract
	}

	public void treeCollapsed(final TreeExpansionEvent event) {
		// operation not needed, left for interface contract
	}

	@Override
	public void valueChanged(final ListSelectionEvent lse) {
		log.info("lse: {}", lse);
		irodsFinderDialog.enableButtonSelectFolder(true);
	}
}
