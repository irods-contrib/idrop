package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.TransferDAOException;
import org.irods.jargon.transfer.dao.domain.Synchronization;

/**
 * 
 * @author jdr0887
 * 
 */
public class EditSynchronizationsMenuActionListener implements ActionListener {

    private final TransferDAOManager transferDAOMgr = TransferDAOManager.getInstance();

    private IDROPDesktop desktop;

    public EditSynchronizationsMenuActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {

        SynchronizationDAO synchronizationDAO = transferDAOMgr.getTransferDAOBean().getSynchronizationDAO();

        try {
            desktop.editSynchronizationsDialogListModel.clear();
            desktop.editSynchronizationsDialogListModel.removeAllElements();
            List<Synchronization> synchList = synchronizationDAO.findAll();

            for (Synchronization synch : synchList) {
                desktop.editSynchronizationsDialogListModel.addElement(synch.getName());
            }

        } catch (TransferDAOException e1) {
            e1.printStackTrace();
        }

        desktop.editSynchronizationsDialog.setVisible(true);
    }

}
