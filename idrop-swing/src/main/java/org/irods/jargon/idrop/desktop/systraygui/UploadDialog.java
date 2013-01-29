/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class UploadDialog extends javax.swing.JDialog implements ListSelectionListener {
    
    iDrop idropGUI;
    IRODSTree irodsTree;
    LocalFileTree localFileTree;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);

    /**
     * Creates new form UploadDialog
     */
    public UploadDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public UploadDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree,
            final LocalFileTree localFileTree) {
        super(parent, modal);
        initComponents();
        
        this.idropGUI = parent;
        this.irodsTree = irodsTree;
        this.localFileTree = localFileTree;
        this.btnDeleteUploadFile.setEnabled(false);
        tblFilesToUpload.getSelectionModel().addListSelectionListener(this);
        
        initUploadTarget();
//        initSourcesFiles();
        setUploadButtonState();
    }
    
    private void initUploadTarget() {
        
        String target = "";
        
        //check for selected collection to use for upload target
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
        int idx = selectionModel.getLeadSelectionIndex();

        // make sure there is a selected node
        if (idx >= 0) {
            IRODSFile ifile = null;
            try {
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(idx, 0);
                ifile = irodsFS.getIRODSFileForPath(selectedNode.getFullPath());
                
                // rule out "/" and choose parent if file is not a directory
                String path = ifile.getAbsolutePath();
                if (ifile.isFile()) {
                    path = ifile.getParent();
                }
                if ((path != null) && (!path.equals("/"))) {
                    target = path;
                }
            } catch (IdropException ex) {
                Exceptions.printStackTrace(ex);
            }
        } 
//        else {
//
//        // next see if can find some put history in the transfer queue
//        // manager and use that target location       
//            // next see if can find some get history in the transfer queue
//            try {
//                List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore().getTransferManager().getRecentQueue();
//                
//                // assuming most recent first
//                for (LocalIRODSTransfer transfer: transfers) {
//                    if (transfer.getTransferType() == TransferType.PUT) {
//                        target = transfer.getIrodsAbsolutePath();
//                        break;
//                    }
//                }
//            } catch (JargonException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
        
       txtUploadTarget.setText(target);
    }
    
    
    // seems to be impossible to preselect files in filechooser component
    // don't do this for now
//    private void initSourcesFiles() {
//        
//        //check for selected files and/or folders to upload     
//        TreeSelectionModel selectionModel = localFileTree.getSelectionModel();
//        LocalFileSystemModel fileSystemModel = (LocalFileSystemModel) localFileTree.getModel();
//
//        TreePath[] selectionPaths = selectionModel.getSelectionPaths();
//
//        // now select these paths in the file chooser
//        if ( selectionPaths != null) {
//            LocalFileNode sourceNode;
//            for (TreePath selectionPath : selectionPaths) {
//                sourceNode = (LocalFileNode) selectionPath.getLastPathComponent();
//                File file = (File) sourceNode.getUserObject();
//                //txtareaUploadSourceList.append(file.getAbsolutePath() + "\n");
//                localChooser.setSelectedFile(file);
//            }
//        }
//        
//    }
    
    private void setUploadButtonState() {
        btnUploadNow.setEnabled(((txtUploadTarget.getText().length() > 0) &&
                                 (tblFilesToUpload.getModel().getRowCount() > 0)));
    }
    
    
    private void executeUpload() {
        
        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        final String targetPath = txtUploadTarget.getText();
        //final String sourceFiles[] = txtareaUploadSourceList.getText().split("\n");
        final String sourceFiles[] = getFilesToUpload();
    
        // process as a put
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                for (String transferFile : sourceFiles) {
                    log.info("process a put from source: {}",
                            transferFile);

                    String sourceResource = idropGUI.getIrodsAccount().getDefaultStorageResource();
                    log.info("initiating put transfer");
                    
                    // FIXME: conveyor
                    /*
                    try {
                        idropGUI.getiDropCore().getTransferManager().enqueueAPut(transferFile,
                                targetPath,
                                sourceResource,
                                idropGUI.getIrodsAccount());
                    } catch (JargonException ex) {
                        java.util.logging.Logger.getLogger(
                                LocalFileTree.class.getName()).log(
                                java.util.logging.Level.SEVERE, null, ex);
                        idropGUI.showIdropException(ex);
                    }
                    * */
                }
            }
        });

        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    
    private String[] getFilesToUpload() {
        
        int numFiles = 0;
        DefaultTableModel model = (DefaultTableModel) tblFilesToUpload.getModel();
        numFiles = model.getRowCount();
        String[] filesToUpload = new String[numFiles];
        
        for (int i=0; i<numFiles; i++) {
            filesToUpload[i] = (String) model.getValueAt(i, 0);
        }
        
        return filesToUpload;
    }
    
    private void setFilesToUpload(File[] files) {
        
        DefaultTableModel model = (DefaultTableModel) tblFilesToUpload.getModel();
        
        for(int i=0; i<files.length; i++) {
            String filePath = files[i].getAbsolutePath();
            model.addRow(new Object[] {filePath});
        }
    }
    
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting() == false) {
            btnDeleteUploadFile.setEnabled(tblFilesToUpload.getSelectedRow() >= 0);
        }
    }
    
//    private void btnBrowseUploadSourceActionPerformed(java.awt.event.ActionEvent evt) {                                                      
//        
//        JFileChooser localFileChooser = new JFileChooser();
//        localFileChooser.setMultiSelectionEnabled(true);
//        localFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        localFileChooser.setDialogTitle("Select Files and Collections to Upload");
//        localFileChooser.setLocation(
//                (int)this.getLocation().getX(), (int)this.getLocation().getY());
//        int returnVal = localFileChooser.showOpenDialog(this);
//
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File uploadFiles[] = localFileChooser.getSelectedFiles();
//            for (File uploadFile: uploadFiles) {
//                txtareaUploadSourceList.append(uploadFile.getAbsolutePath() + "\n");
//            }
//            setUploadButtonState();
//        }
//    }  


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUploadTarget = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        btnBrowseUploadTarget = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        btnUseIrodsHome = new javax.swing.JButton();
        btnUseLastUpload = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFilesToUpload = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnAddUploadFile = new javax.swing.JButton();
        btnDeleteUploadFile = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        btnUploadNow = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 400));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 400));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setPreferredSize(new java.awt.Dimension(945, 76));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(100, 32));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.jLabel1.text")); // NOI18N
        jPanel7.add(jLabel1);

        txtUploadTarget.setEditable(false);
        txtUploadTarget.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.txtUploadTarget.text")); // NOI18N
        txtUploadTarget.setPreferredSize(new java.awt.Dimension(200, 28));
        txtUploadTarget.setRequestFocusEnabled(false);
        jPanel7.add(txtUploadTarget);

        jPanel9.add(jPanel7, java.awt.BorderLayout.WEST);

        btnBrowseUploadTarget.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnBrowseUploadTarget.text")); // NOI18N
        btnBrowseUploadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseUploadTargetActionPerformed(evt);
            }
        });
        jPanel8.add(btnBrowseUploadTarget);

        jPanel9.add(jPanel8, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel9, java.awt.BorderLayout.NORTH);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        jPanel10.setPreferredSize(new java.awt.Dimension(100, 40));
        jPanel10.setSize(new java.awt.Dimension(100, 32));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        btnUseIrodsHome.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseIrodsHome.text")); // NOI18N
        btnUseIrodsHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseIrodsHomeActionPerformed(evt);
            }
        });
        jPanel11.add(btnUseIrodsHome);

        btnUseLastUpload.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseLastUpload.text")); // NOI18N
        btnUseLastUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLastUploadActionPerformed(evt);
            }
        });
        jPanel11.add(btnUseLastUpload);

        jPanel10.add(jPanel11, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 4, 1, 4));
        jPanel5.setPreferredSize(new java.awt.Dimension(462, 250));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.jLabel2.text")); // NOI18N
        jPanel5.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 190));

        tblFilesToUpload.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblFilesToUpload);

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        btnAddUploadFile.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnAddUploadFile.text")); // NOI18N
        btnAddUploadFile.setPreferredSize(new java.awt.Dimension(22, 24));
        btnAddUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUploadFileActionPerformed(evt);
            }
        });
        jPanel6.add(btnAddUploadFile);

        btnDeleteUploadFile.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnDeleteUploadFile.text")); // NOI18N
        btnDeleteUploadFile.setPreferredSize(new java.awt.Dimension(22, 24));
        btnDeleteUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUploadFileActionPerformed(evt);
            }
        });
        jPanel6.add(btnDeleteUploadFile);

        jPanel3.add(jPanel6, java.awt.BorderLayout.WEST);

        jPanel5.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnUploadNow.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUploadNow.text")); // NOI18N
        btnUploadNow.setEnabled(false);
        btnUploadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadNowActionPerformed(evt);
            }
        });
        jPanel12.add(btnUploadNow);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel12.add(btnCancel);

        jPanel2.add(jPanel12, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseUploadTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseUploadTargetActionPerformed

        IRODSFinderDialog irodsFinder = new IRODSFinderDialog(
            idropGUI, false, idropGUI.getiDropCore());
        irodsFinder.setTitle("Select iRODS Collection Upload Target");
        irodsFinder.setSelectionType(IRODSFinderDialog.SelectionType.COLLS_ONLY_SELECTION_MODE);
        irodsFinder.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        irodsFinder.setVisible(true);

        String selectedPath = irodsFinder.getSelectedAbsolutePath();
        if (selectedPath != null) {
            txtUploadTarget.setText(selectedPath);
        }
        setUploadButtonState();
    }//GEN-LAST:event_btnBrowseUploadTargetActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnUploadNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadNowActionPerformed
        executeUpload();
        this.dispose();
    }//GEN-LAST:event_btnUploadNowActionPerformed

    private void btnUseIrodsHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseIrodsHomeActionPerformed
        String target = "";
        if (idropGUI.getiDropCore().getIrodsAccount().isAnonymousAccount()) {   
            target = MiscIRODSUtils.computePublicDirectory(idropGUI.getiDropCore().getIrodsAccount());
        } else {
            target = MiscIRODSUtils.computeHomeDirectoryForIRODSAccount(idropGUI.getiDropCore().getIrodsAccount());
        }
        if (target != null) {
            txtUploadTarget.setText(target);
        }
        setUploadButtonState();
    }//GEN-LAST:event_btnUseIrodsHomeActionPerformed

    private void btnUseLastUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseLastUploadActionPerformed
        String target = "";
        // see if can find some get history in the transfer queue
        // FIXME: conveyor
        /*
        try {
            List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore().getTransferManager().getRecentQueue();
                
            // assuming most recent first
            for (LocalIRODSTransfer transfer: transfers) {
                // must check to match type, user, host, zone, & port
                if ((transfer.getTransferType() == TransferType.PUT) && 
                    (transfer.getTransferUserName().equals(idropGUI.getiDropCore().getIrodsAccount().getUserName())) &&
                    (transfer.getTransferZone().equals(idropGUI.getiDropCore().getIrodsAccount().getZone())) &&
                    (transfer.getTransferPort() == idropGUI.getiDropCore().getIrodsAccount().getPort()) &&
                    (transfer.getTransferHost().equals(idropGUI.getiDropCore().getIrodsAccount().getHost()))) {
                    target = transfer.getIrodsAbsolutePath();
                    break;
                }
            }
        } catch (JargonException ex) {
                Exceptions.printStackTrace(ex);
        }
        if (target != null) {
            txtUploadTarget.setText(target);
        }
        */
        setUploadButtonState();
    }//GEN-LAST:event_btnUseLastUploadActionPerformed

    private void btnAddUploadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUploadFileActionPerformed
        JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(true);
        localFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        localFileChooser.setDialogTitle("Select Files to Upload");
        localFileChooser.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        int returnVal = localFileChooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] filesToUpload = localFileChooser.getSelectedFiles();
            setFilesToUpload(filesToUpload);
            setUploadButtonState();
        }
    }//GEN-LAST:event_btnAddUploadFileActionPerformed

    private void btnDeleteUploadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUploadFileActionPerformed
        
        int[] selectedRows = tblFilesToUpload.getSelectedRows();
        int numRowsSelected = selectedRows.length;
        
        // have to remove rows in reverse
        for(int i=numRowsSelected-1; i>=0; i--) {
        //for (int selectedRow: selectedRows) {
            int selectedRow = selectedRows[i];
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) tblFilesToUpload.getModel();
                model.removeRow(selectedRow);
            }
        }
    }//GEN-LAST:event_btnDeleteUploadFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUploadFile;
    private javax.swing.JButton btnBrowseUploadTarget;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteUploadFile;
    private javax.swing.JButton btnUploadNow;
    private javax.swing.JButton btnUseIrodsHome;
    private javax.swing.JButton btnUseLastUpload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFilesToUpload;
    private javax.swing.JTextField txtUploadTarget;
    // End of variables declaration//GEN-END:variables

}
