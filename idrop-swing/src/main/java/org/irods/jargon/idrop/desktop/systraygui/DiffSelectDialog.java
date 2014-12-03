/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.datautils.tree.DiffTreePostProcessor;
import org.irods.jargon.datautils.tree.FileTreeDiffUtility;
import org.irods.jargon.datautils.tree.FileTreeDiffUtilityImpl;
import org.irods.jargon.datautils.tree.FileTreeModel;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.utils.MessageUtil;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.DiffViewData;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.finder.IRODSFinderDialog;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class DiffSelectDialog extends javax.swing.JDialog {

    private final IDROPCore idropCore;
    private final iDrop idropGui;
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(SynchronizationDialog.class);
    /**
     * Creates new form AddSynchronizationDialog
     */
    public DiffSelectDialog(Tools2Dialog parent, iDrop idropGui, boolean modal, IDROPCore idropCore) {
        super(parent, modal);
        
        this.idropCore = idropCore;
        this.idropGui = idropGui;
        
        initComponents();
        setTextBoxListeners();
        populateDiffData();
    }
    
    
    
    private void populateDiffData() {
        
        // populate local path, if any is selected
        TreePath localPath = idropGui.getFileTree().getSelectionPath();
        if (localPath != null) {
            LocalFileNode selectedFileNode = (LocalFileNode) idropGui.getFileTree()
                            .getSelectionPath().getLastPathComponent();
            File targetPath = (File) selectedFileNode.getUserObject();
            final String localAbsPath = targetPath.getAbsolutePath();
            txtLocalPath.setText(localAbsPath);
            final File localFile = new File(localAbsPath);
            
            log.info("local path for diff:{}", localAbsPath);
        }

        // look for iRODS absolute path for the right hand side of the diff
        IRODSFileService irodsFS;
        try {
                irodsFS = new IRODSFileService(idropGui.getiDropCore()
                                .irodsAccount(), idropGui.getiDropCore()
                                .getIrodsFileSystem());
        } catch (Exception ex) {

                log.error("cannot create irods file service", ex);
                MessageUtil.showError(this,
                                "Cannot create iRODS file Service, see exception log",
                                MessageUtil.ERROR_MESSAGE);
                dispose();
                return;
        }

        final String irodsAbsPath;
        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) idropGui
                        .getIrodsTree().getModel();
        ListSelectionModel selectionModel = idropGui.getIrodsTree()
                        .getSelectionModel();
        int idx = selectionModel.getLeadSelectionIndex();
        IRODSFile ifile;
        // make sure there is a selected node
        if (idx >= 0) {

            try {
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(idx, 0);
                if (selectedNode == null) {
                        return;
                }
                ifile = irodsFS.getIRODSFileForPath(selectedNode.getFullPath());

                // rule out "/" and choose parent if file is not a directory
                String path = ifile.getAbsolutePath();
                if (ifile.isFile()) {
                        path = ifile.getParent();
                }
                if ((path != null) && (!path.equals("/"))) {
                        irodsAbsPath = path;
                } else {
                        irodsAbsPath = "/";
                }
                txtIrodsPath.setText(ifile.getAbsolutePath());
                log.info("irods path for diff:{}", ifile.getAbsolutePath());
                    
                // if this is a dataObject (not collection) get checksum (if computed)
                // or compute checksum dynamically
                try {
                    String checksum = getDataObjectChecksum(ifile);
                    if (checksum != null) {
                        txtChecksum.setText(checksum);
                    }
                } catch (JargonException ex) {
                    // TODO: error message/log here
                    //dispose();
                }

            } catch (IdropException ex) {
                MessageUtil.showError(this, ex.getMessage(), MessageUtil.ERROR_MESSAGE);
                dispose();
            }
    
        }
    }
    
    private void setTextBoxListeners() {
        txtLocalPath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setDiffButtonEnabledProp();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                setDiffButtonEnabledProp();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                setDiffButtonEnabledProp();
            }
        });
        
        txtIrodsPath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateChecksum();
                setDiffButtonEnabledProp();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChecksum();
                setDiffButtonEnabledProp();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateChecksum();
                setDiffButtonEnabledProp();
            }
        });
    }
    
    private String getDataObjectChecksum(IRODSFile file) throws JargonException {
        String checksum = null;
        DataObjectAO dataObjectAO = null;
        
        IRODSAccessObjectFactory aoFactory = idropCore.getIRODSAccessObjectFactory();
        dataObjectAO = aoFactory.getDataObjectAO(idropCore.irodsAccount());
        checksum = dataObjectAO.computeMD5ChecksumOnDataObject(file);
        
        return checksum;
    }
    
    private void updateChecksum() {
        String irodsPath = txtIrodsPath.getText();   
        IRODSFileService irodsFS;
        IRODSFile ifile;

        if ((irodsPath != null) && (irodsPath.length() > 0)) {
            try {
                irodsFS = new IRODSFileService(idropGui.getiDropCore()
                        .irodsAccount(), idropGui.getiDropCore()
                        .getIrodsFileSystem());
                ifile = irodsFS.getIRODSFileForPath(irodsPath);
                String checksum = getDataObjectChecksum(ifile);
                if (checksum != null) {
                    txtChecksum.setText(checksum);
                }
            } catch (JargonException ex) {
                // TODO: error message/log here
                //dispose();
            } catch (IdropException iex) {
                // TODO: error message/log here
                //dispose();
            }
        }   
    }
    
    private void setDiffButtonEnabledProp() {
        String irodsPath = txtIrodsPath.getText();
        String localPath = txtLocalPath.getText();
        if ((irodsPath != null) && 
            (irodsPath.length() > 0) &&
            (localPath != null) &&
            (localPath.length() > 0)) {
            btnSave.setEnabled(true);
        }
        else {
            btnSave.setEnabled(false);
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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtLocalPath = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtIrodsPath = new javax.swing.JTextField();
        btnLocalDirectory = new javax.swing.JButton();
        btnIrodsDirectory = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtChecksum = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(500, 270));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        txtLocalPath.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.txtLocalPath.text")); // NOI18N
        txtLocalPath.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        jPanel1.add(txtLocalPath, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel3, gridBagConstraints);

        txtIrodsPath.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.txtIrodsPath.text")); // NOI18N
        txtIrodsPath.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.1;
        jPanel1.add(txtIrodsPath, gridBagConstraints);

        btnLocalDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_144_folder_open.png"))); // NOI18N
        btnLocalDirectory.setMnemonic('l');
        btnLocalDirectory.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnLocalDirectory.text")); // NOI18N
        btnLocalDirectory.setToolTipText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnLocalDirectory.toolTipText")); // NOI18N
        btnLocalDirectory.setMaximumSize(new java.awt.Dimension(143, 31));
        btnLocalDirectory.setMinimumSize(new java.awt.Dimension(143, 31));
        btnLocalDirectory.setPreferredSize(new java.awt.Dimension(100, 34));
        btnLocalDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocalDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(btnLocalDirectory, gridBagConstraints);

        btnIrodsDirectory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_144_folder_open.png"))); // NOI18N
        btnIrodsDirectory.setMnemonic('i');
        btnIrodsDirectory.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnIrodsDirectory.text")); // NOI18N
        btnIrodsDirectory.setToolTipText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnIrodsDirectory.toolTipText")); // NOI18N
        btnIrodsDirectory.setPreferredSize(new java.awt.Dimension(100, 34));
        btnIrodsDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIrodsDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(btnIrodsDirectory, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.jLabel2.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.3;
        jPanel1.add(jLabel2, gridBagConstraints);

        txtChecksum.setEditable(false);
        txtChecksum.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.txtChecksum.text")); // NOI18N
        txtChecksum.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.3;
        jPanel1.add(txtChecksum, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        jPanel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.jPanel1.AccessibleContext.accessibleName")); // NOI18N

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 5));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnCancel.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnCancel.text")); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(82, 42));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnSave.setText(org.openide.util.NbBundle.getMessage(DiffSelectDialog.class, "DiffSelectDialog.btnSave.text")); // NOI18N
        btnSave.setEnabled(false);
        btnSave.setPreferredSize(new java.awt.Dimension(82, 42));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLocalDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocalDirectoryActionPerformed
        log.info("btnLocalDirectoryActionPerformed");

        final JFileChooser localFileChooser = new JFileChooser();
        localFileChooser.setMultiSelectionEnabled(false);
        localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        localFileChooser.setDialogTitle("Select local directory diff target");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (toolkit.getScreenSize().width - localFileChooser
                    .getWidth()) / 2;
        int y = (toolkit.getScreenSize().height - localFileChooser
                    .getHeight()) / 2;
        localFileChooser.setLocation(x, y);
        final int returnVal = localFileChooser.showOpenDialog(this);

        final DiffSelectDialog dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String localPath = localFileChooser.getSelectedFile()
                    .getAbsolutePath();
                    txtLocalPath.setText(MiscIRODSUtils.abbreviateFileName(localPath));
                    txtLocalPath.setToolTipText(localPath);
                }

            }
        });
    }//GEN-LAST:event_btnLocalDirectoryActionPerformed

    private void btnIrodsDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrodsDirectoryActionPerformed

        log.info("btnIrodsDirectoryActionPerformed");

        final DiffSelectDialog thisDialog = this;
        final IDROPCore thisIdropCore = idropCore;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                String homeDir = MiscIRODSUtils.buildIRODSUserHomeForAccountUsingDefaultScheme(thisIdropCore.irodsAccount());
                IRODSFinderDialog irodsFinder = new IRODSFinderDialog(null, false,
                    thisIdropCore, thisIdropCore.irodsAccount(), homeDir);
                irodsFinder.setTitle("Select iRODS collection diff target");
                irodsFinder
                .setSelectionType(IRODSFinderDialog.SelectionType.COLLS_ONLY_SELECTION_MODE);
                irodsFinder.setLocation((int) thisDialog.getLocation().getX(), (int) thisDialog
                    .getLocation().getY());
                irodsFinder.setVisible(true);

                String selectedPath = irodsFinder.getSelectedAbsolutePath();
                if (selectedPath != null) {
                    txtIrodsPath.setText(MiscIRODSUtils.abbreviateFileName(selectedPath));
                    txtIrodsPath.setToolTipText(selectedPath);
                }
            }
        });
    }//GEN-LAST:event_btnIrodsDirectoryActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        log.info("doing diff");
        // look for selected local and iRODS files

        final String localAbsPath = txtLocalPath.getText();
        final File localFile = new File(localAbsPath);
        log.info("local path for diff:{}", localAbsPath);

        IRODSFileService irodsFS;
        IRODSFile ifile;
        final String irodsAbsPath = txtIrodsPath.getText();
        try {
                irodsFS = new IRODSFileService(idropGui.getiDropCore()
                                .irodsAccount(), idropGui.getiDropCore()
                                .getIrodsFileSystem());
                ifile = irodsFS.getIRODSFileForPath(irodsAbsPath);
        } catch (Exception ex) {

                log.error("cannot create irods file service", ex);
                MessageUtil.showError(this,
                                "Cannot create iRODS file Service, see exception log",
                                MessageUtil.ERROR_MESSAGE);
                dispose();
                return;
        }
        log.info("irods path for diff:{}", ifile.getAbsolutePath());

        final DiffSelectDialog thisDialog = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                    thisDialog.dispose();
                    idropGui.setCursor(Cursor
                                    .getPredefinedCursor(Cursor.WAIT_CURSOR));
                    FileTreeDiffUtility fileTreeDiffUtility = new FileTreeDiffUtilityImpl(
                                    idropGui.getiDropCore().irodsAccount(), idropGui
                                                    .getiDropCore().getIRODSAccessObjectFactory());
                    try {
                            FileTreeModel diffModel = fileTreeDiffUtility
                                            .generateDiffLocalToIRODS(localFile, irodsAbsPath,
                                                            0L, 0L);
                            DiffTreePostProcessor postProcessor = new DiffTreePostProcessor();
                            postProcessor.postProcessFileTreeModel(diffModel);

                            log.info("diffModel:{}", diffModel);
                            DiffViewData diffViewData = new DiffViewData();
                            diffViewData.setFileTreeModel(diffModel);
                            diffViewData.setIrodsAbsolutePath(irodsAbsPath);
                            diffViewData.setLocalAbsolutePath(localAbsPath);
                            DiffViewDialog diffViewDialog = new DiffViewDialog(
                                            thisDialog.idropGui, true, diffViewData);
                            diffViewDialog.setVisible(true);
                    } catch (JargonException ex) {
                            log.error("Error generating diff", ex);
                            MessageUtil.showError(
                                            thisDialog,
                                            "An error occurred generating the diff:\n"
                                                            + ex.getMessage(),
                                            MessageUtil.ERROR_MESSAGE);
                            thisDialog.dispose();
                    } finally {
                            idropGui.setCursor(Cursor
                                            .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
            }
        });

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed

        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnIrodsDirectory;
    private javax.swing.JButton btnLocalDirectory;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtChecksum;
    private javax.swing.JTextField txtIrodsPath;
    private javax.swing.JTextField txtLocalPath;
    // End of variables declaration//GEN-END:variables
}
