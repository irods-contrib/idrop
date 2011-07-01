package org.irods.jargon.idrop.desktop.systraygui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.TransferDAOException;
import org.irods.jargon.transfer.dao.domain.Synchronization;

public class EditSynchronizationsDialogDeleteActionListener implements ActionListener {

    private final TransferDAOManager transferDAOMgr = TransferDAOManager.getInstance();

    private IDROPDesktop desktop;

    public EditSynchronizationsDialogDeleteActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SynchronizationDAO synchronizationDAO = transferDAOMgr.getTransferDAOBean().getSynchronizationDAO();
        int[] indices = desktop.editSynchronizationsDialogList.getSelectedIndices();
        for (int index : indices) {
            if (index != -1) {
                Object item = desktop.editSynchronizationsDialogListModel.getElementAt(index);
                try {
                    Synchronization synch = synchronizationDAO.findByName(item.toString());
                    synchronizationDAO.delete(synch);
                } catch (TransferDAOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(desktop.editSynchronizationsDialog, e1.getCause().getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                desktop.editSynchronizationsDialogListModel.remove(index);
            }
        }

    }

}
