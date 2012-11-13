/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
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
public class DownloadDialog extends javax.swing.JDialog {
    
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
        else {
            // next see if can find some get history in the transfer queue
            try {
                List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore().getTransferManager().getRecentQueue();
                
                // assuming most recent first
                for (LocalIRODSTransfer transfer: transfers) {
                    if (transfer.getTransferType() == TransferType.GET) {
                        target = transfer.getLocalAbsolutePath();
                        break;
                    }
                }
            } catch (JargonException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
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
        IRODSFile ifile = null;
        //final List<File> sourceFiles = new ArrayList<File>();
        for (int idx = idxStart; idx <= idxEnd; idx++) {
            if (selectionModel.isSelectedIndex(idx)) {
                try {
                    IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(idx, 0);
                    ifile = irodsFS.getIRODSFileForPath(selectedNode.getFullPath());
                    // rule out "/"
                    String path = ifile.getAbsolutePath();
                    if ((path != null) && (!path.equals("/"))) {
                        txtareaDownloadSourceList.append(path);
                    }
                } catch (IdropException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
    private void executeDownload() {
        
        idropGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        final String targetPath = txtDownloadTarget.getText();
        final String sourceFiles[] = txtareaDownloadSourceList.getText().split("\n");


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
                                   (txtareaDownloadSourceList.getText().length() > 0)));
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
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDownloadTarget = new javax.swing.JTextField();
        btnBrowseDownloadTarget = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtareaDownloadSourceList = new javax.swing.JTextArea();
        btnBrowseDownloadSource = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnDownloadNow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.title")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.jLabel1.text")); // NOI18N
        jPanel4.add(jLabel1);

        txtDownloadTarget.setEditable(false);
        txtDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.txtDownloadTarget.text")); // NOI18N
        jPanel4.add(txtDownloadTarget);

        btnBrowseDownloadTarget.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnBrowseDownloadTarget.text")); // NOI18N
        btnBrowseDownloadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDownloadTargetActionPerformed(evt);
            }
        });
        jPanel4.add(btnBrowseDownloadTarget);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.jLabel2.text")); // NOI18N
        jPanel3.add(jLabel2);

        txtareaDownloadSourceList.setEditable(false);
        txtareaDownloadSourceList.setColumns(20);
        txtareaDownloadSourceList.setRows(5);
        jScrollPane2.setViewportView(txtareaDownloadSourceList);

        jPanel3.add(jScrollPane2);

        btnBrowseDownloadSource.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnBrowseDownloadSource.text")); // NOI18N
        btnBrowseDownloadSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDownloadSourceActionPerformed(evt);
            }
        });
        jPanel3.add(btnBrowseDownloadSource);

        jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel);

        btnDownloadNow.setText(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.btnDownloadNow.text")); // NOI18N
        btnDownloadNow.setEnabled(false);
        btnDownloadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadNowActionPerformed(evt);
            }
        });
        jPanel2.add(btnDownloadNow);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DownloadDialog.class, "DownloadDialog.AccessibleContext.accessibleName")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnDownloadNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadNowActionPerformed
        executeDownload();
        this.dispose();
    }//GEN-LAST:event_btnDownloadNowActionPerformed

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

    private void btnBrowseDownloadSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseDownloadSourceActionPerformed
        IRODSFinderDialog irodsFinder = new IRODSFinderDialog(
                idropGUI, false, idropGUI.getiDropCore());
        irodsFinder.setTitle("Select iRODS Files and Collections for download");
        irodsFinder.setSelectionType(IRODSFinderDialog.SelectionType.OBJS_AND_COLLS_SELECTION_MODE);
        irodsFinder.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        irodsFinder.setVisible(true);
        
        String selectedPath = irodsFinder.getSelectedAbsolutePath();
        if (selectedPath != null) {
            txtareaDownloadSourceList.setText(selectedPath);
        }
        setDownloadButtonState();
    }//GEN-LAST:event_btnBrowseDownloadSourceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseDownloadSource;
    private javax.swing.JButton btnBrowseDownloadTarget;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDownloadNow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtDownloadTarget;
    private javax.swing.JTextArea txtareaDownloadSourceList;
    // End of variables declaration//GEN-END:variables
}
