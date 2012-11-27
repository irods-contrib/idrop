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
public class UploadDialog extends javax.swing.JDialog {
    
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
        
        createChooserListener();
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
                // rule out "/"
                String path = ifile.getAbsolutePath();
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
                                 (txtareaUploadSourceList.getText().length() > 0)));
    }
    
    private void executeUpload() {
        
        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        final String targetPath = txtUploadTarget.getText();
        final String sourceFiles[] = txtareaUploadSourceList.getText().split("\n");
    
        // process as a put
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                for (String transferFile : sourceFiles) {
                    log.info("process a put from source: {}",
                            transferFile);

                    String sourceResource = idropGUI.getIrodsAccount().getDefaultStorageResource();
                    log.info("initiating put transfer");
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
                }
            }
        });

        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void createChooserListener() {
        localChooser.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        
        if ((JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) ||
            (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(evt.getPropertyName()))) {
            // clear all items from listbox
            txtareaUploadSourceList.setText(null);
        
            if (evt.getNewValue() != null) {
                File uploadFiles[] = localChooser.getSelectedFiles();
                for (File uploadFile: uploadFiles) {
                    txtareaUploadSourceList.append(uploadFile.getAbsolutePath() + "\n");
                }
            }
            setUploadButtonState();
        }
      }
    });
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
        jLabel1 = new javax.swing.JLabel();
        txtUploadTarget = new javax.swing.JTextField();
        btnBrowseUploadTarget = new javax.swing.JButton();
        btnUseIrodsHome = new javax.swing.JButton();
        btnUseLastUpload = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        localChooser = new javax.swing.JFileChooser();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtareaUploadSourceList = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnUploadNow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.title")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 4, 16, 4));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.jLabel1.text")); // NOI18N
        jPanel4.add(jLabel1);

        txtUploadTarget.setEditable(false);
        txtUploadTarget.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.txtUploadTarget.text")); // NOI18N
        txtUploadTarget.setPreferredSize(new java.awt.Dimension(160, 28));
        txtUploadTarget.setRequestFocusEnabled(false);
        jPanel4.add(txtUploadTarget);

        btnBrowseUploadTarget.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnBrowseUploadTarget.text")); // NOI18N
        btnBrowseUploadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseUploadTargetActionPerformed(evt);
            }
        });
        jPanel4.add(btnBrowseUploadTarget);

        btnUseIrodsHome.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseIrodsHome.text")); // NOI18N
        btnUseIrodsHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseIrodsHomeActionPerformed(evt);
            }
        });
        jPanel4.add(btnUseIrodsHome);

        btnUseLastUpload.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseLastUpload.text")); // NOI18N
        btnUseLastUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLastUploadActionPerformed(evt);
            }
        });
        jPanel4.add(btnUseLastUpload);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        localChooser.setControlButtonsAreShown(false);
        localChooser.setCurrentDirectory(null);
        localChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        localChooser.setMultiSelectionEnabled(true);
        jPanel3.add(localChooser);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 4, 1, 4));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.jLabel2.text")); // NOI18N
        jPanel5.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        txtareaUploadSourceList.setEditable(false);
        txtareaUploadSourceList.setColumns(20);
        txtareaUploadSourceList.setRows(5);
        jScrollPane2.setViewportView(txtareaUploadSourceList);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel);

        btnUploadNow.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUploadNow.text")); // NOI18N
        btnUploadNow.setEnabled(false);
        btnUploadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadNowActionPerformed(evt);
            }
        });
        jPanel2.add(btnUploadNow);

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
        setUploadButtonState();
    }//GEN-LAST:event_btnUseLastUploadActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseUploadTarget;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnUploadNow;
    private javax.swing.JButton btnUseIrodsHome;
    private javax.swing.JButton btnUseLastUpload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JFileChooser localChooser;
    private javax.swing.JTextField txtUploadTarget;
    private javax.swing.JTextArea txtareaUploadSourceList;
    // End of variables declaration//GEN-END:variables
}
