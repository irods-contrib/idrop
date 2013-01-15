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
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class DownloadDialog extends javax.swing.JDialog implements ListSelectionListener {
    
    iDrop idropGUI;
    IRODSTree irodsTree;
    LocalFileTree localFileTree;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);

    /**
     * Creates new form DownloadDialog
     */
    public DownloadDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public DownloadDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree,
            final LocalFileTree localFileTree) {
        super(parent, modal);
        initComponents();
        
        this.idropGUI = parent;
        this.irodsTree = irodsTree;
        this.localFileTree = localFileTree;
        this.btnDeleteDownloadFile.setEnabled(false);
        tblFilesToDownload.getSelectionModel().addListSelectionListener(this);
        
        initDownloadTarget();
        initSourcesFiles();
        setDownloadButtonState();
    }
    
    private void initDownloadTarget() {
        
        String target = "";
        
        //first check to see if a download target is selected in the local file tree
        TreePath treePath = localFileTree.getSelectionPath();
        if(treePath != null) {
            LocalFileNode selectedFileNode = (LocalFileNode) localFileTree.getSelectionPath().getLastPathComponent();
            File targetPath = (File) selectedFileNode.getUserObject();
            if (targetPath.isDirectory()) {
                target = targetPath.getAbsolutePath();
            }
        }
//        else {
//            // next see if can find some get history in the transfer queue
//            try {
//                List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore().getTransferManager().getRecentQueue();
//                
//                // assuming most recent first
//                for (LocalIRODSTransfer transfer: transfers) {
//                    if (transfer.getTransferType() == TransferType.GET) {
//                        target = transfer.getLocalAbsolutePath();
//                        break;
//                    }
//                }
//            } catch (JargonException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
        
       txtDownloadTarget.setText(target);
    }
    
    private void initSourcesFiles() {
        //check for selected objects and/or collections to download
        // get iRODS File Service
        IRODSFileService irodsFS = null;
        try {
            irodsFS = new IRODSFileService(idropGUI.getiDropCore().getIrodsAccount(),
                    idropGUI.getiDropCore().getIrodsFileSystem());
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, "Cannot access iRODS file system for get.");
            log.error("cannot create irods file service");
            return;
        }

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree.getModel();
        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idxStart = selectionModel.getMinSelectionIndex();
        int idxEnd = selectionModel.getMaxSelectionIndex();

        // now collect all selected nodes
        List<IRODSFile> ifiles = new ArrayList<IRODSFile>();

        for (int idx = idxStart; idx <= idxEnd; idx++) {
            if (selectionModel.isSelectedIndex(idx)) {
                try {
                    IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(idx, 0);
                    ifiles.add(irodsFS.getIRODSFileForPath(selectedNode.getFullPath()));
                } catch (IdropException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        
        setFilesToDownload(ifiles);
    }
    
    private void executeDownload() {
        
        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        final String targetPath = txtDownloadTarget.getText();
        final String sourceFiles[] = getFilesToDownload();


        // process as a get
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (String transferFile : sourceFiles) {
                    log.info("initiating a transfer of iRODS file:{}",
                        transferFile);
                    log.info("transfer to local file:{}",
                        targetPath);
                    try {
                        idropGUI.getiDropCore().getTransferManager().enqueueAGet(
                                    transferFile,
                                    targetPath,
                                    "", idropGUI.getIrodsAccount());
                    } catch (JargonException ex) {
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
        btnDownloadNow.setEnabled(((txtDownloadTarget.getText().length() > 0) &&
                                   (tblFilesToDownload.getModel().getRowCount() > 0)));
    }
    
    private void setFilesToDownload(List<IRODSFile> ifiles) {
        
        DefaultTableModel model = (DefaultTableModel) tblFilesToDownload.getModel();
        
        for(int i=0; i<ifiles.size(); i++) {
            String filePath = ifiles.get(i).getAbsolutePath();
            model.addRow(new Object[] {filePath});
        }
    }
    
    private String[] getFilesToDownload() {
        
        int numFiles = 0;
        DefaultTableModel model = (DefaultTableModel) tblFilesToDownload.getModel();
        numFiles = model.getRowCount();
        String[] filesToDownload = new String[numFiles];
        
        for (int i=0; i<numFiles; i++) {
            filesToDownload[i] = (String) model.getValueAt(i, 0);
        }
        
        return filesToDownload;
    }
    
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting() == false) {
            btnDeleteDownloadFile.setEnabled(tblFilesToDownload.getSelectedRow() >= 0);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFilesToDownload = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        btnAddDownloadFile = new javax.swing.JButton();
        btnDeleteDownloadFile = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDownloadTarget = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        btnBrowseDownloadTarget = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        btnUseLocaLHome = new javax.swing.JButton();
        btnUseLastDownload = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        btnDownloadNow = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 420));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 350));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 4, 1, 4));
        jPanel3.setPreferredSize(new java.awt.Dimension(303, 250));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.jLabel2.text")); // NOI18N
        jPanel3.add(jLabel2, java.awt.BorderLayout.NORTH);

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
        jScrollPane1.setViewportView(tblFilesToDownload);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel6.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        btnAddDownloadFile.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnAddDownloadFile.text")); // NOI18N
        btnAddDownloadFile.setPreferredSize(new java.awt.Dimension(22, 24));
        btnAddDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDownloadFileActionPerformed(evt);
            }
        });
        jPanel16.add(btnAddDownloadFile);

        btnDeleteDownloadFile.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDeleteDownloadFile.text")); // NOI18N
        btnDeleteDownloadFile.setPreferredSize(new java.awt.Dimension(22, 24));
        btnDeleteDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDownloadFileActionPerformed(evt);
            }
        });
        jPanel16.add(btnDeleteDownloadFile);

        jPanel6.add(jPanel16, java.awt.BorderLayout.WEST);

        jPanel3.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setPreferredSize(new java.awt.Dimension(240, 76));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(600, 32));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel11.setPreferredSize(new java.awt.Dimension(400, 40));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.jLabel1.text")); // NOI18N
        jPanel11.add(jLabel1);

        txtDownloadTarget.setEditable(false);
        txtDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.txtDownloadTarget.text")); // NOI18N
        txtDownloadTarget.setPreferredSize(new java.awt.Dimension(220, 28));
        txtDownloadTarget.setRequestFocusEnabled(false);
        jPanel11.add(txtDownloadTarget);

        jPanel9.add(jPanel11, java.awt.BorderLayout.WEST);

        jPanel12.setPreferredSize(new java.awt.Dimension(200, 40));

        btnBrowseDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnBrowseDownloadTarget.text")); // NOI18N
        btnBrowseDownloadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDownloadTargetActionPerformed(evt);
            }
        });
        jPanel12.add(btnBrowseDownloadTarget);

        jPanel9.add(jPanel12, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel9, java.awt.BorderLayout.NORTH);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        jPanel10.setPreferredSize(new java.awt.Dimension(600, 40));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel13.setPreferredSize(new java.awt.Dimension(100, 40));
        jPanel10.add(jPanel13, java.awt.BorderLayout.WEST);

        jPanel14.setPreferredSize(new java.awt.Dimension(430, 40));
        jPanel14.setRequestFocusEnabled(false);

        btnUseLocaLHome.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnUseLocaLHome.text")); // NOI18N
        btnUseLocaLHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLocaLHomeActionPerformed(evt);
            }
        });
        jPanel14.add(btnUseLocaLHome);

        btnUseLastDownload.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnUseLastDownload.text")); // NOI18N
        btnUseLastDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLastDownloadActionPerformed(evt);
            }
        });
        jPanel14.add(btnUseLastDownload);

        jPanel10.add(jPanel14, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel4, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnDownloadNow.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDownloadNow.text")); // NOI18N
        btnDownloadNow.setEnabled(false);
        btnDownloadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadNowActionPerformed(evt);
            }
        });
        jPanel15.add(btnDownloadNow);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel15.add(btnCancel);

        jPanel2.add(jPanel15, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.AccessibleContext.accessibleName")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseDownloadTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseDownloadTargetActionPerformed
        
        JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(false);
        localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        localFileChooser.setDialogTitle("Select Download Target");
        localFileChooser.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        int returnVal = localFileChooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String downloadPath = localFileChooser.getSelectedFile().getAbsolutePath();
            txtDownloadTarget.setText(downloadPath);
            setDownloadButtonState();
        }
    }//GEN-LAST:event_btnBrowseDownloadTargetActionPerformed

    private void btnUseLocaLHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseLocaLHomeActionPerformed
        String target = System.getProperty("user.home");

        if (target != null) {
            txtDownloadTarget.setText(target);
        }
        setDownloadButtonState();
    }//GEN-LAST:event_btnUseLocaLHomeActionPerformed

    private void btnUseLastDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseLastDownloadActionPerformed
        String target = "";
        // see if can find some get history in the transfer queue
        try {
            List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore().getTransferManager().getRecentQueue();
                
            // assuming most recent first
            for (LocalIRODSTransfer transfer: transfers) {
                // must check to match type, user, host, zone, & resource
                if ((transfer.getTransferType() == TransferType.GET) && 
                    (transfer.getTransferUserName().equals(idropGUI.getiDropCore().getIrodsAccount().getUserName())) &&
                    (transfer.getTransferZone().equals(idropGUI.getiDropCore().getIrodsAccount().getZone())) &&
                    (transfer.getTransferPort() == idropGUI.getiDropCore().getIrodsAccount().getPort()) &&
                    (transfer.getTransferHost().equals(idropGUI.getiDropCore().getIrodsAccount().getHost()))) {
                    target = transfer.getLocalAbsolutePath();
                    break;
                }
            }
        } catch (JargonException ex) {
                Exceptions.printStackTrace(ex);
        }
        if (target != null) {
            txtDownloadTarget.setText(target);
        }
        setDownloadButtonState();
    }//GEN-LAST:event_btnUseLastDownloadActionPerformed

    private void btnAddDownloadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDownloadFileActionPerformed
        IRODSFinderDialog irodsFinder = new IRODSFinderDialog(
                idropGUI, false, idropGUI.getiDropCore());
        irodsFinder.setTitle("Select iRODS Files and Collections for download");
        irodsFinder.setSelectionType(IRODSFinderDialog.SelectionType.OBJS_AND_COLLS_SELECTION_MODE);
        irodsFinder.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        irodsFinder.setVisible(true);
        
        List<String> selectedPaths = irodsFinder.getSelectedAbsolutePaths();
        for (String selectedPath: selectedPaths) {
            if (selectedPath != null) {
                DefaultTableModel model = (DefaultTableModel) tblFilesToDownload.getModel();
                model.addRow(new Object[] {selectedPath});
            }
        }
        setDownloadButtonState();
    }//GEN-LAST:event_btnAddDownloadFileActionPerformed

    private void btnDeleteDownloadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteDownloadFileActionPerformed

        int[] selectedRows = tblFilesToDownload.getSelectedRows();
        int numRowsSelected = selectedRows.length;
        
        // have to remove rows in reverse
        for(int i=numRowsSelected-1; i>=0; i--) {
        //for (int selectedRow: selectedRows) {
            int selectedRow = selectedRows[i];
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) tblFilesToDownload.getModel();
                model.removeRow(selectedRow);
            }
        }
    }//GEN-LAST:event_btnDeleteDownloadFileActionPerformed

    private void btnDownloadNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadNowActionPerformed
        executeDownload();
        this.dispose();
    }//GEN-LAST:event_btnDownloadNowActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDownloadFile;
    private javax.swing.JButton btnBrowseDownloadTarget;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteDownloadFile;
    private javax.swing.JButton btnDownloadNow;
    private javax.swing.JButton btnUseLastDownload;
    private javax.swing.JButton btnUseLocaLHome;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFilesToDownload;
    private javax.swing.JTextField txtDownloadTarget;
    // End of variables declaration//GEN-END:variables

}
