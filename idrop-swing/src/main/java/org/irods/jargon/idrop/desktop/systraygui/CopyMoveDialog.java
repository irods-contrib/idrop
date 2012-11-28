/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonFileOrCollAlreadyExistsException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class CopyMoveDialog extends javax.swing.JDialog {
    
    
    private iDrop idropGUI;
    private IRODSTree irodsTree;
    private IRODSOutlineModel irodsFileSystemModel;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);

    /**
     * Creates new form CopyMoveDialog
     */
    public CopyMoveDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CopyMoveDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree) {
        super(parent, modal);
        initComponents();
        
        this.idropGUI = parent;
        this.irodsTree = irodsTree;
        this.irodsFileSystemModel = (IRODSOutlineModel) irodsTree.getModel();
        
        initSourcesFiles();
        setCopyMoveButtonsState();
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
                        txtCurrentParent.append(path  + "\n");
                    }
                } catch (IdropException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
    private void setCopyMoveButtonsState() {
        boolean state = ((txtNewLocation.getText().length() > 0) &&
                         (txtCurrentParent.getText().length() > 0));
        btnCopy.setEnabled(state);
        btnMove.setEnabled(state);
    }
    
    private void processMoveOrCopy(final boolean isCopy) {
        // add the new folder to irods, add to the tree, and scroll the tree
        // into view
        final CopyMoveDialog thisDialog = this;
        final String targetAbsolutePath = txtNewLocation.getText();
        final String sourceFiles[] = txtCurrentParent.getText().split("\n");
        
        log.info("processing move or copy");
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                IRODSFile irodsFile = null;
                try {
                    log.info("processing move of a file in iRODS tree");
                    thisDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    DataTransferOperations dataTransferOperations;
                    try {
                        dataTransferOperations = idropGUI.getiDropCore().getIRODSAccessObjectFactory().getDataTransferOperations(
                                idropGUI.getIrodsAccount());
                    } catch (Exception e) {
                        idropGUI.getiDropCore().closeIRODSConnection(
                                idropGUI.getIrodsAccount());
                        thisDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        throw new IdropRuntimeException(e);
                    }

                    List<IRODSFile> filesThatHadOverwriteError = new ArrayList<IRODSFile>();

                    if (sourceFiles.length == 1) {
                        //IRODSFile irodsFile = null;
                        log.info("processing the move/copy for one file:{}",
                                sourceFiles[0]);
                        try {
                            irodsFile = 
                                idropGUI.getiDropCore().getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(sourceFiles[0]);
                            if (isCopy) {
                                processACopyOfAnIndividualFile(
                                        dataTransferOperations, irodsFile,
                                        targetAbsolutePath);
                            } else {
                                processAMoveOfAnIndividualFile(
                                        dataTransferOperations, irodsFile,
                                        targetAbsolutePath);
                            }
                        } catch (JargonFileOrCollAlreadyExistsException ex) {
                            Logger.getLogger(
                                    MoveOrCopyiRODSDialog.class.getName()).log(
                                    Level.SEVERE, null, ex);
                            filesThatHadOverwriteError.add(irodsFile);
                        } catch (JargonException je) {
                            if (je.getMessage().indexOf("-834000") > -1
                                    || je.getMessage().indexOf("-833000") > -1) {
                                filesThatHadOverwriteError.add(irodsFile);
                            } else {
                                throw new IdropException(je);
                            }
                        }
                    } else if (sourceFiles.length > 1) {
                        log.info("processing move/copy of multiple files");
                        for (String sourceFileEntry : sourceFiles) {
                            
                            try {
                                irodsFile = 
                                idropGUI.getiDropCore().getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(sourceFileEntry);
                                if (isCopy) {
                                    processACopyOfAnIndividualFile(
                                            dataTransferOperations,
                                            irodsFile, targetAbsolutePath);
                                } else {
                                    processAMoveOfAnIndividualFile(
                                            dataTransferOperations,
                                            irodsFile, targetAbsolutePath);
                                }
                            } catch (JargonFileOrCollAlreadyExistsException ex) {
                                // FIXME: fix in jargon core to differentiate!
                                Logger.getLogger(
                                        MoveOrCopyiRODSDialog.class.getName()).log(Level.SEVERE, null, ex);
                                filesThatHadOverwriteError.add(irodsFile);
                            } catch (JargonException je) {
                                if (je.getMessage().indexOf("-834000") > -1
                                        || je.getMessage().indexOf("-833000") > -1) {
                                    filesThatHadOverwriteError.add(irodsFile);
                                } else {
                                    throw new IdropException(je);
                                }
                            }
                        }
                    }

                    log.debug("move done");
                    if (!isCopy) {
                        if (filesThatHadOverwriteError.isEmpty()) {
                            idropGUI.showMessageFromOperation("irods move processed");
                        } else {
                            idropGUI.showMessageFromOperation("irods move processed, some files were not moved as files of the same name already existed");
                        }
                    } else {
                        idropGUI.showMessageFromOperation("The file copy operation has been placed on the work queue");
                    }
                    thisDialog.dispose();

                } catch (IdropException ex) {
                    Logger.getLogger(IRODSTree.class.getName()).log(
                            Level.SEVERE, null, ex);
                    idropGUI.showIdropException(ex);
                    return;
                } finally {
                    thisDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    idropGUI.getiDropCore().closeIRODSConnection(
                            idropGUI.getIrodsAccount());
                }
            }
        });
    }
    
    private void processAMoveOfAnIndividualFile(
            final DataTransferOperations dataTransferOperations,
            final IRODSFile sourceFile, final String targetAbsolutePath)
            throws JargonFileOrCollAlreadyExistsException, IdropException {

        try {
            
             boolean isFile = sourceFile.isFile();
            IRODSFile targetFile = idropGUI.getiDropCore().getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(targetAbsolutePath);
            
            dataTransferOperations.move(sourceFile.getAbsolutePath(),
                    targetAbsolutePath);

             String targetPathForNotify = null;
            if (isFile) {
                log.debug("source file is a file, do a move");

                if (targetFile.isDirectory()) {
                    targetPathForNotify = targetFile.getAbsolutePath() + "/"
                            + sourceFile.getName();
                } else {
                    targetPathForNotify = targetFile.getAbsolutePath();
                }

                irodsFileSystemModel.notifyFileShouldBeAdded(irodsTree,
                        targetPathForNotify);

            } else {
                log.debug("source file is a collection, reparent it");
                   targetPathForNotify = targetFile.getAbsolutePath() + "/"
                            + sourceFile.getName();

                irodsFileSystemModel.notifyFileShouldBeAdded(irodsTree,
                        targetPathForNotify);
            }
        } catch (JargonFileOrCollAlreadyExistsException fcae) {
            throw fcae;
        } catch (JargonException je) {
            throw new IdropException(je);
        }

        TreePath sourceNodePath = TreeUtils.buildTreePathForIrodsAbsolutePath(
                irodsTree, sourceFile.getAbsolutePath());
        if (sourceNodePath == null) {
            log.info("could not find tree path for source node, ignore");
            return;
        }
        IRODSNode sourceNode = (IRODSNode) sourceNodePath.getLastPathComponent();
        irodsFileSystemModel.notifyFileShouldBeRemoved(sourceNode);
    }

    private void processACopyOfAnIndividualFile(
            final DataTransferOperations dataTransferOperations,
            final IRODSFile sourceFile, final String targetAbsolutePath)
            throws IdropException {
        try {
            dataTransferOperations.copy(sourceFile.getAbsolutePath(),
                    idropGUI.getiDropCore().getIrodsAccount().getDefaultStorageResource(),
                    targetAbsolutePath,
                    null,
                    null);
            //idropGUI.getiDropCore().getTransferManager().enqueueACopy(sourceFile.getAbsolutePath(),
                    //sourceFile.getResource(), targetAbsolutePath,
                    //idropGUI.getiDropCore().getIrodsAccount());

        } catch (JargonException ex) {
            Logger.getLogger(MoveOrCopyiRODSDialog.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new IdropException(
                    "unable to copy file due to JargonException", ex);
        }

        // notifications are done at completion of transfer using status
        // callbacks
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFolderData = new javax.swing.JPanel();
        pnlCurrentParent = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblCurrentParent = new java.awt.Label();
        scrollCurrentParent = new javax.swing.JScrollPane();
        txtCurrentParent = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        lblNewDiretoryName = new java.awt.Label();
        txtNewLocation = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        pnlBottom = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnMove = new javax.swing.JButton();
        btnCopy = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(590, 260));

        pnlFolderData.setPreferredSize(new java.awt.Dimension(540, 240));
        pnlFolderData.setLayout(new java.awt.BorderLayout());

        pnlCurrentParent.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 4, 10, 4));
        pnlCurrentParent.setPreferredSize(new java.awt.Dimension(500, 190));
        pnlCurrentParent.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(500, 125));

        lblCurrentParent.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.lblCurrentParent.text")); // NOI18N

        scrollCurrentParent.setPreferredSize(new java.awt.Dimension(360, 100));

        txtCurrentParent.setEditable(false);
        txtCurrentParent.setColumns(20);
        txtCurrentParent.setRows(5);
        txtCurrentParent.setWrapStyleWord(true);
        txtCurrentParent.setFocusable(false);
        txtCurrentParent.setPreferredSize(new java.awt.Dimension(290, 90));
        scrollCurrentParent.setViewportView(txtCurrentParent);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblCurrentParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrollCurrentParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 426, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(45, 45, 45)
                .add(lblCurrentParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(scrollCurrentParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnlCurrentParent.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setPreferredSize(new java.awt.Dimension(500, 60));
        jPanel2.setRequestFocusEnabled(false);

        lblNewDiretoryName.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.lblNewDiretoryName.text")); // NOI18N

        txtNewLocation.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.txtNewLocation.text")); // NOI18N
        txtNewLocation.setPreferredSize(new java.awt.Dimension(360, 28));

        btnBrowse.setLabel(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.btnBrowse.label")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblNewDiretoryName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(txtNewLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 320, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(30, 30, 30))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(9, 9, 9)
                .add(lblNewDiretoryName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel2Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtNewLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnBrowse)))
        );

        pnlCurrentParent.add(jPanel2, java.awt.BorderLayout.SOUTH);

        pnlFolderData.add(pnlCurrentParent, java.awt.BorderLayout.CENTER);

        pnlBottom.setPreferredSize(new java.awt.Dimension(708, 40));

        btnCancel.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnMove.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.btnMove.text")); // NOI18N
        btnMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveActionPerformed(evt);
            }
        });

        btnCopy.setText(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class, "CopyMoveDialog.btnCopy.text")); // NOI18N
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlBottomLayout = new org.jdesktop.layout.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlBottomLayout.createSequentialGroup()
                .add(334, 334, 451)
                .add(btnCancel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnMove)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCopy)
                .addContainerGap())
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlBottomLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(pnlBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnMove)
                    .add(btnCopy)))
        );

        pnlFolderData.add(pnlBottom, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFolderData, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveActionPerformed
        processMoveOrCopy(false);
    }//GEN-LAST:event_btnMoveActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        IRODSFinderDialog irodsFinder = new IRODSFinderDialog(
            idropGUI, false, idropGUI.getiDropCore());
        irodsFinder.setTitle("Select iRODS Collection Upload Target");
        irodsFinder.setSelectionType(IRODSFinderDialog.SelectionType.COLLS_ONLY_SELECTION_MODE);
        irodsFinder.setLocation(
                (int)this.getLocation().getX(), (int)this.getLocation().getY());
        irodsFinder.setVisible(true);

        String selectedPath = irodsFinder.getSelectedAbsolutePath();
        if (selectedPath != null) {
            txtNewLocation.setText(selectedPath);
        }
        setCopyMoveButtonsState();
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        processMoveOrCopy(true);
    }//GEN-LAST:event_btnCopyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnMove;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private java.awt.Label lblCurrentParent;
    private java.awt.Label lblNewDiretoryName;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlCurrentParent;
    private javax.swing.JPanel pnlFolderData;
    private javax.swing.JScrollPane scrollCurrentParent;
    private javax.swing.JTextArea txtCurrentParent;
    private javax.swing.JTextField txtNewLocation;
    // End of variables declaration//GEN-END:variables
}
