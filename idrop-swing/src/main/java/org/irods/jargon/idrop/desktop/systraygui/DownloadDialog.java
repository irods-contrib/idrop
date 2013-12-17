/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.QueueManagerService;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class DownloadDialog extends javax.swing.JDialog implements
        ListSelectionListener {

    /**
     *
     */
    private static final long serialVersionUID = -204997338708996297L;
    iDrop idropGUI;
    IRODSTree irodsTree;
    LocalFileTree localFileTree;
    String downloadTarget;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(IRODSTree.class);

    /**
     * Creates new form DownloadDialog
     */
    public DownloadDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public DownloadDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree, final LocalFileTree localFileTree) {
        super(parent, modal);
        initComponents();

        idropGUI = parent;
        this.irodsTree = irodsTree;
        this.localFileTree = localFileTree;
        btnDeleteDownloadFile.setEnabled(false);
        tblFilesToDownload.getSelectionModel().addListSelectionListener(this);

        initDownloadTarget();
        initSourcesFiles();
        setDownloadButtonState();
    }

    private void initDownloadTarget() {

        String target = "";

        // first check to see if a download target is selected in the local file
        // tree
        TreePath treePath = localFileTree.getSelectionPath();
        if (treePath != null) {
            LocalFileNode selectedFileNode = (LocalFileNode) localFileTree
                    .getSelectionPath().getLastPathComponent();
            File targetPath = (File) selectedFileNode.getUserObject();
            if (targetPath.isDirectory()) {
                target = targetPath.getAbsolutePath();
            }
        }

        downloadTarget = target;
        lblDownloadTarget.setText(MiscIRODSUtils.abbreviateFileName(target));
        downloadTarget = target;
    }

    private void initSourcesFiles() {
        // check for selected objects and/or collections to download
        // get iRODS File Service
        IRODSFileService irodsFS = null;
        try {
            irodsFS = new IRODSFileService(idropGUI.getiDropCore()
                    .getIrodsAccount(), idropGUI.getiDropCore()
                    .getIrodsFileSystem());
        } catch (Exception ex) {
            // JOptionPane.showMessageDialog(this,
            // "Cannot access iRODS file system for get.");
            log.error("cannot create irods file service");
            return;
        }

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree
                .getModel();
        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idxStart = selectionModel.getMinSelectionIndex();
        int idxEnd = selectionModel.getMaxSelectionIndex();

        // now collect all selected nodes
        List<IRODSFile> ifiles = new ArrayList<IRODSFile>();

        for (int idx = idxStart; idx <= idxEnd; idx++) {
            if (selectionModel.isSelectedIndex(idx)) {
                try {
                    IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                            .getValueAt(idx, 0);
                    ifiles.add(irodsFS.getIRODSFileForPath(selectedNode
                            .getFullPath()));
                } catch (IdropException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        setFilesToDownload(ifiles);
    }

    private void executeDownload() {

        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final String targetPath = lblDownloadTarget.getText();
        final String sourceFiles[] = getFilesToDownload();

        // process as a get
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (String transferFile : sourceFiles) {
                    log.info("initiating a transfer of iRODS file:{}",
                            transferFile);
                    log.info("transfer to local file:{}", targetPath);

                    try {
                        QueueManagerService qms = idropGUI.getiDropCore()
                                .getConveyorService().getQueueManagerService();

                        qms.enqueueTransferOperation(transferFile, targetPath,
                                idropGUI.getiDropCore().getIrodsAccount(),
                                TransferType.GET);
                    } catch (ConveyorExecutionException ex) {
                        java.util.logging.Logger.getLogger(
                                LocalFileTree.class.getName()).log(
                                java.util.logging.Level.SEVERE, null, ex);
                        idropGUI.showIdropException(ex);
                    }

                }
            }
        });
        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void setDownloadButtonState() {
        btnDownloadNow
                .setEnabled(((lblDownloadTarget.getText().length() > 0) && (tblFilesToDownload
                .getModel().getRowCount() > 0)));
    }

    private void setFilesToDownload(final List<IRODSFile> ifiles) {

        DefaultTableModel model = (DefaultTableModel) tblFilesToDownload
                .getModel();

        for (int i = 0; i < ifiles.size(); i++) {
            String filePath = ifiles.get(i).getAbsolutePath();
            model.addRow(new Object[]{filePath});
        }
    }

    private String[] getFilesToDownload() {

        int numFiles = 0;
        DefaultTableModel model = (DefaultTableModel) tblFilesToDownload
                .getModel();
        numFiles = model.getRowCount();
        String[] filesToDownload = new String[numFiles];

        for (int i = 0; i < numFiles; i++) {
            filesToDownload[i] = (String) model.getValueAt(i, 0);
        }

        return filesToDownload;
    }

    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting() == false) {
            btnDeleteDownloadFile.setEnabled(tblFilesToDownload
                    .getSelectedRow() >= 0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlFileSection = new javax.swing.JPanel();
        pnlDownloadFileContainer = new javax.swing.JPanel();
        lblFilesHeader = new javax.swing.JLabel();
        scrollPanelFilesToDownload = new javax.swing.JScrollPane();
        tblFilesToDownload = new javax.swing.JTable();
        pnlAddDelete = new javax.swing.JPanel();
        btnAddDownloadFile = new javax.swing.JButton();
        btnDeleteDownloadFile = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblDownloadTargetLabel = new javax.swing.JLabel();
        lblDownloadTarget = new javax.swing.JLabel();
        btnBrowseDownloadTarget = new javax.swing.JButton();
        btnUseLocaLHome = new javax.swing.JButton();
        pnlUploadDownloadButtons = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnDownloadNow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 600));

        pnlFileSection.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pnlFileSection.setPreferredSize(new java.awt.Dimension(600, 350));
        pnlFileSection.setLayout(new java.awt.BorderLayout());

        pnlDownloadFileContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 4, 1, 4));
        pnlDownloadFileContainer.setPreferredSize(new java.awt.Dimension(303, 250));
        pnlDownloadFileContainer.setLayout(new java.awt.BorderLayout());

        lblFilesHeader.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.lblFilesHeader.text")); // NOI18N
        pnlDownloadFileContainer.add(lblFilesHeader, java.awt.BorderLayout.NORTH);

        tblFilesToDownload.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPanelFilesToDownload.setViewportView(tblFilesToDownload);

        pnlDownloadFileContainer.add(scrollPanelFilesToDownload, java.awt.BorderLayout.CENTER);

        pnlAddDelete.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddDownloadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_190_circle_plus.png"))); // NOI18N
        btnAddDownloadFile.setMnemonic('+');
        btnAddDownloadFile.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnAddDownloadFile.text")); // NOI18N
        btnAddDownloadFile.setToolTipText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnAddDownloadFile.toolTipText")); // NOI18N
        btnAddDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDownloadFileActionPerformed(evt);
            }
        });
        pnlAddDelete.add(btnAddDownloadFile);

        btnDeleteDownloadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnDeleteDownloadFile.setMnemonic('-');
        btnDeleteDownloadFile.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDeleteDownloadFile.text")); // NOI18N
        btnDeleteDownloadFile.setToolTipText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDeleteDownloadFile.toolTipText")); // NOI18N
        btnDeleteDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDownloadFileActionPerformed(evt);
            }
        });
        pnlAddDelete.add(btnDeleteDownloadFile);

        pnlDownloadFileContainer.add(pnlAddDelete, java.awt.BorderLayout.SOUTH);

        pnlFileSection.add(pnlDownloadFileContainer, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName(""); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lblDownloadTargetLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDownloadTargetLabel.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.lblDownloadTargetLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel4.add(lblDownloadTargetLabel, gridBagConstraints);

        lblDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.lblDownloadTarget.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel4.add(lblDownloadTarget, gridBagConstraints);

        btnBrowseDownloadTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_148_folder_flag.png"))); // NOI18N
        btnBrowseDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnBrowseDownloadTarget.text")); // NOI18N
        btnBrowseDownloadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDownloadTargetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(btnBrowseDownloadTarget, gridBagConstraints);

        btnUseLocaLHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_020_home.png"))); // NOI18N
        btnUseLocaLHome.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnUseLocaLHome.text")); // NOI18N
        btnUseLocaLHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLocaLHomeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(btnUseLocaLHome, gridBagConstraints);

        pnlFileSection.add(jPanel4, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFileSection, java.awt.BorderLayout.CENTER);

        pnlUploadDownloadButtons.setMinimumSize(new java.awt.Dimension(200, 80));
        pnlUploadDownloadButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_197_remove.png"))); // NOI18N
        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.setMargin(null);
        btnCancel.setMaximumSize(null);
        btnCancel.setMinimumSize(null);
        btnCancel.setPreferredSize(null);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlUploadDownloadButtons.add(btnCancel);

        btnDownloadNow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_181_download_alt.png"))); // NOI18N
        btnDownloadNow.setMnemonic('d');
        btnDownloadNow.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDownloadNow.text")); // NOI18N
        btnDownloadNow.setToolTipText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDownloadNow.toolTipText")); // NOI18N
        btnDownloadNow.setEnabled(false);
        btnDownloadNow.setMargin(null);
        btnDownloadNow.setMaximumSize(null);
        btnDownloadNow.setMinimumSize(null);
        btnDownloadNow.setPreferredSize(null);
        btnDownloadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadNowActionPerformed(evt);
            }
        });
        pnlUploadDownloadButtons.add(btnDownloadNow);

        getContentPane().add(pnlUploadDownloadButtons, java.awt.BorderLayout.SOUTH);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.AccessibleContext.accessibleName")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseDownloadTargetActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBrowseDownloadTargetActionPerformed

        JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(false);
        localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        localFileChooser.setDialogTitle("Select Download Target");
        localFileChooser.setLocation((int) this.getLocation().getX(),
                (int) this.getLocation().getY());
        int returnVal = localFileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String downloadPath = localFileChooser.getSelectedFile()
                    .getAbsolutePath();
            lblDownloadTarget.setText(MiscIRODSUtils.abbreviateFileName(downloadPath));
            lblDownloadTarget.setToolTipText(downloadPath);
            this.downloadTarget = downloadPath;
            setDownloadButtonState();
        }
    }// GEN-LAST:event_btnBrowseDownloadTargetActionPerformed

    private void btnUseLocaLHomeActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUseLocaLHomeActionPerformed
        String target = System.getProperty("user.home");

        if (target != null) {
            lblDownloadTarget.setText(MiscIRODSUtils.abbreviateFileName(target));
            lblDownloadTarget.setToolTipText(target);
            this.downloadTarget = target;
        }
        setDownloadButtonState();
    }// GEN-LAST:event_btnUseLocaLHomeActionPerformed

    private void btnUseLastDownloadActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUseLastDownloadActionPerformed
        String target = "";
        // see if can find some get history in the transfer queue
		/*
         * FIXME:conveyor try { List<LocalIRODSTransfer> transfers =
         * idropGUI.getiDropCore() .getTransferManager().getRecentQueue();
         * 
         * // assuming most recent first for (LocalIRODSTransfer transfer :
         * transfers) { // must check to match type, user, host, zone, &
         * resource if ((transfer.getTransferType() == TransferType.GET) &&
         * (transfer.getTransferUserName() .equals(idropGUI.getiDropCore()
         * .getIrodsAccount().getUserName())) &&
         * (transfer.getTransferZone().equals(idropGUI
         * .getiDropCore().getIrodsAccount().getZone())) &&
         * (transfer.getTransferPort() == idropGUI
         * .getiDropCore().getIrodsAccount().getPort()) &&
         * (transfer.getTransferHost().equals(idropGUI
         * .getiDropCore().getIrodsAccount().getHost()))) { target =
         * transfer.getLocalAbsolutePath(); break; } } } catch (JargonException
         * ex) { Exceptions.printStackTrace(ex); }
         */
        if (target != null) {
            lblDownloadTarget.setText(target);
        }
        setDownloadButtonState();
    }// GEN-LAST:event_btnUseLastDownloadActionPerformed

    private void btnAddDownloadFileActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddDownloadFileActionPerformed
        IRODSFinderDialog irodsFinder = new IRODSFinderDialog(idropGUI, false,
                idropGUI.getiDropCore());
        irodsFinder.setTitle("Select iRODS Files and Collections for download");
        irodsFinder
                .setSelectionType(IRODSFinderDialog.SelectionType.OBJS_AND_COLLS_SELECTION_MODE);
        irodsFinder.setLocation((int) this.getLocation().getX(), (int) this
                .getLocation().getY());
        irodsFinder.setVisible(true);

        List<String> selectedPaths = irodsFinder.getSelectedAbsolutePaths();
        for (String selectedPath : selectedPaths) {
            if (selectedPath != null) {
                DefaultTableModel model = (DefaultTableModel) tblFilesToDownload
                        .getModel();
                model.addRow(new Object[]{selectedPath});
            }
        }
        setDownloadButtonState();
    }// GEN-LAST:event_btnAddDownloadFileActionPerformed

    private void btnDeleteDownloadFileActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteDownloadFileActionPerformed

        int[] selectedRows = tblFilesToDownload.getSelectedRows();
        int numRowsSelected = selectedRows.length;

        // have to remove rows in reverse
        for (int i = numRowsSelected - 1; i >= 0; i--) {
            // for (int selectedRow: selectedRows) {
            int selectedRow = selectedRows[i];
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) tblFilesToDownload
                        .getModel();
                model.removeRow(selectedRow);
            }
        }
        setDownloadButtonState();

    }// GEN-LAST:event_btnDeleteDownloadFileActionPerformed

    private void btnDownloadNowActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDownloadNowActionPerformed
        executeDownload();
        dispose();
    }// GEN-LAST:event_btnDownloadNowActionPerformed

    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }// GEN-LAST:event_btnCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDownloadFile;
    private javax.swing.JButton btnBrowseDownloadTarget;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteDownloadFile;
    private javax.swing.JButton btnDownloadNow;
    private javax.swing.JButton btnUseLocaLHome;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblDownloadTarget;
    private javax.swing.JLabel lblDownloadTargetLabel;
    private javax.swing.JLabel lblFilesHeader;
    private javax.swing.JPanel pnlAddDelete;
    private javax.swing.JPanel pnlDownloadFileContainer;
    private javax.swing.JPanel pnlFileSection;
    private javax.swing.JPanel pnlUploadDownloadButtons;
    private javax.swing.JScrollPane scrollPanelFilesToDownload;
    private javax.swing.JTable tblFilesToDownload;
    // End of variables declaration//GEN-END:variables
}
