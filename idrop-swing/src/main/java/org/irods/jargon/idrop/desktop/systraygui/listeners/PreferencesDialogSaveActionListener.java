package org.irods.jargon.idrop.desktop.systraygui.listeners;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEFAULT_LOCAL_DIR;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_HIDDEN_FILES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop;

/**
 * 
 * @author jdr0887
 * 
 */
public class PreferencesDialogSaveActionListener implements ActionListener {

    private IDROPDesktop desktop;

    public PreferencesDialogSaveActionListener(IDROPDesktop desktop) {
        super();
        this.desktop = desktop;
    }

    public void actionPerformed(ActionEvent e) {

        boolean showUI = desktop.preferencesDialogShowUICheckBox.isSelected();
        desktop.getiDropCore().getPreferences().putBoolean(PREFERENCE_KEY_SHOW_UI, showUI ? true : false);

        String defaultLocalDir = desktop.preferencesDialogDefaultLocalDirectoryTextField.getText();
        desktop.getiDropCore().getPreferences().put(PREFERENCE_KEY_DEFAULT_LOCAL_DIR, defaultLocalDir);

        boolean showHiddenFiles = desktop.preferencesDialogShowHiddenFilesCheckBox.isSelected();
        desktop.getiDropCore().getPreferences()
                .putBoolean(PREFERENCE_KEY_SHOW_HIDDEN_FILES, showHiddenFiles ? true : false);

        // if (!showHiddenFiles && StringUtils.isNotEmpty(defaultLocalDir)) {
        // desktop.localFSTreeTableModel = new LocalFSTreeTableModel(new File(defaultLocalDir),
        // desktop.hiddenFileFilenameFilter);
        // }
        //
        // if (showHiddenFiles && StringUtils.isNotEmpty(defaultLocalDir)) {
        // desktop.localFSTreeTableModel = new LocalFSTreeTableModel(new File(defaultLocalDir));
        // }
        //
        // if (!showHiddenFiles && StringUtils.isEmpty(defaultLocalDir)) {
        // desktop.localFSTreeTableModel = new LocalFSTreeTableModel(desktop.hiddenFileFilenameFilter);
        // }
        //
        // if (showHiddenFiles && StringUtils.isEmpty(defaultLocalDir)) {
        // desktop.localFSTreeTableModel = new LocalFSTreeTableModel();
        // }

        // desktop.localFSTreeTable.setModel(new TreeTableModelAdapter(desktop.localFSTreeTableModel,
        // new TreeTableCellRenderer(desktop.localFSTreeTable, desktop.localFSTreeTableModel)));
        //
        // desktop.localFSTreeTable = new LocalFSTreeTable(desktop);
        //
        // desktop.localFSTreeTable.setDragEnabled(true);
        // desktop.localFSTreeTable.setDropMode(DropMode.INSERT_ROWS);
        // desktop.localFSTreeTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        // TransferHandler handler = new LocalFSTreeTableTransferHandler(desktop);
        // desktop.localFSTreeTable.setTransferHandler(handler);
        //
        // desktop.localFSTreeTable.getColumn("Name").setPreferredWidth(230);
        // desktop.localFSTreeTable.getColumn("Size").setPreferredWidth(70);
        // desktop.localFSTreeTable.getColumn("Type").setPreferredWidth(65);
        // desktop.localFSTreeTable.getColumn("Modified").setPreferredWidth(95);
        //
        // desktop.localFSTreeTable.setFillsViewportHeight(true);
        // desktop.localFSTreeTableScrollPane.getViewport().removeAll();
        // desktop.localFSTreeTableScrollPane.getViewport().add(desktop.localFSTreeTable);

        desktop.preferencesDialog.setVisible(false);
    }

}
