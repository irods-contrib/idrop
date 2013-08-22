/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.QueueManagerService;

import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
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
public class UploadDialog extends javax.swing.JDialog implements
		ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -635863371072845882L;
	iDrop idropGUI;
	IRODSTree irodsTree;
	LocalFileTree localFileTree;
	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(IRODSTree.class);

	/**
	 * Creates new form UploadDialog
	 */
	public UploadDialog(final java.awt.Frame parent, final boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public UploadDialog(final iDrop parent, final boolean modal,
			final IRODSTree irodsTree, final LocalFileTree localFileTree) {
		super(parent, modal);
		initComponents();

		idropGUI = parent;
		this.irodsTree = irodsTree;
		this.localFileTree = localFileTree;
		btnDeleteUploadFile.setEnabled(false);
		tblFilesToUpload.getSelectionModel().addListSelectionListener(this);

		initUploadTarget();
		// initSourcesFiles();
		setUploadButtonState();
	}

	private void initUploadTarget() {

		String target = "";

		// check for selected collection to use for upload target
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
		int idx = selectionModel.getLeadSelectionIndex();

		// make sure there is a selected node
		if (idx >= 0) {
			IRODSFile ifile = null;
			try {
				IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
						.getValueAt(idx, 0);
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
		
		txtUploadTarget.setText(target);
	}

	// seems to be impossible to preselect files in filechooser component
	// don't do this for now
	// private void initSourcesFiles() {
	//
	// //check for selected files and/or folders to upload
	// TreeSelectionModel selectionModel = localFileTree.getSelectionModel();
	// LocalFileSystemModel fileSystemModel = (LocalFileSystemModel)
	// localFileTree.getModel();
	//
	// TreePath[] selectionPaths = selectionModel.getSelectionPaths();
	//
	// // now select these paths in the file chooser
	// if ( selectionPaths != null) {
	// LocalFileNode sourceNode;
	// for (TreePath selectionPath : selectionPaths) {
	// sourceNode = (LocalFileNode) selectionPath.getLastPathComponent();
	// File file = (File) sourceNode.getUserObject();
	// //txtareaUploadSourceList.append(file.getAbsolutePath() + "\n");
	// localChooser.setSelectedFile(file);
	// }
	// }
	//
	// }

	private void setUploadButtonState() {
		btnUploadNow
				.setEnabled(((txtUploadTarget.getText().length() > 0) && (tblFilesToUpload
						.getModel().getRowCount() > 0)));
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
                    
                    try {
                            QueueManagerService qms = idropGUI.getiDropCore().getConveyorService().getQueueManagerService();
                            qms.enqueueTransferOperation(
                                targetPath,
                                transferFile,
                                idropGUI.getiDropCore().getIrodsAccount(),
                                TransferType.PUT);
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
    
    
    private String[] getFilesToUpload() {

		int numFiles = 0;
		DefaultTableModel model = (DefaultTableModel) tblFilesToUpload
				.getModel();
		numFiles = model.getRowCount();
		String[] filesToUpload = new String[numFiles];

		for (int i = 0; i < numFiles; i++) {
			filesToUpload[i] = (String) model.getValueAt(i, 0);
		}

		return filesToUpload;
	}

	private void setFilesToUpload(final File[] files) {

		DefaultTableModel model = (DefaultTableModel) tblFilesToUpload
				.getModel();

		for (File file : files) {
			String filePath = file.getAbsolutePath();
			model.addRow(new Object[] { filePath });
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent lse) {
		if (lse.getValueIsAdjusting() == false) {
			btnDeleteUploadFile
					.setEnabled(tblFilesToUpload.getSelectedRow() >= 0);
		}
	}

	// private void
	// btnBrowseUploadSourceActionPerformed(java.awt.event.ActionEvent evt) {
	//
	// JFileChooser localFileChooser = new JFileChooser();
	// localFileChooser.setMultiSelectionEnabled(true);
	// localFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	// localFileChooser.setDialogTitle("Select Files and Collections to Upload");
	// localFileChooser.setLocation(
	// (int)this.getLocation().getX(), (int)this.getLocation().getY());
	// int returnVal = localFileChooser.showOpenDialog(this);
	//
	// if (returnVal == JFileChooser.APPROVE_OPTION) {
	// File uploadFiles[] = localFileChooser.getSelectedFiles();
	// for (File uploadFile: uploadFiles) {
	// txtareaUploadSourceList.append(uploadFile.getAbsolutePath() + "\n");
	// }
	// setUploadButtonState();
	// }
	// }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new javax.swing.JPanel();
        pnlTargetLocations = new javax.swing.JPanel();
        lblUploadTargetLocation = new javax.swing.JLabel();
        txtUploadTarget = new javax.swing.JTextField();
        btnUseIrodsHome = new javax.swing.JButton();
        btnUseLastUpload = new javax.swing.JButton();
        pnlFilesToUpload = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        scrollPanelFilesToUpload = new javax.swing.JScrollPane();
        tblFilesToUpload = new javax.swing.JTable();
        pnlBottomButtons = new javax.swing.JPanel();
        btnAddUploadFile = new javax.swing.JButton();
        btnDeleteUploadFile = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnUploadNow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 400));

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pnlMain.setPreferredSize(new java.awt.Dimension(600, 400));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTargetLocations.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTargetLocations.setPreferredSize(new java.awt.Dimension(945, 76));
        pnlTargetLocations.setLayout(new java.awt.GridBagLayout());

        lblUploadTargetLocation.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.lblUploadTargetLocation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTargetLocations.add(lblUploadTargetLocation, gridBagConstraints);

        txtUploadTarget.setEditable(false);
        txtUploadTarget.setColumns(100);
        txtUploadTarget.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.txtUploadTarget.text")); // NOI18N
        txtUploadTarget.setMinimumSize(new java.awt.Dimension(200, 20));
        txtUploadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseUploadTargetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlTargetLocations.add(txtUploadTarget, gridBagConstraints);

        btnUseIrodsHome.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseIrodsHome.text")); // NOI18N
        btnUseIrodsHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseIrodsHomeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlTargetLocations.add(btnUseIrodsHome, gridBagConstraints);

        btnUseLastUpload.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUseLastUpload.text")); // NOI18N
        btnUseLastUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLastUploadActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlTargetLocations.add(btnUseLastUpload, gridBagConstraints);

        pnlMain.add(pnlTargetLocations, java.awt.BorderLayout.NORTH);

        pnlFilesToUpload.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 4, 1, 4));
        pnlFilesToUpload.setPreferredSize(new java.awt.Dimension(462, 250));
        pnlFilesToUpload.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.jLabel2.text")); // NOI18N
        pnlFilesToUpload.add(jLabel2, java.awt.BorderLayout.NORTH);

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
        scrollPanelFilesToUpload.setViewportView(tblFilesToUpload);

        pnlFilesToUpload.add(scrollPanelFilesToUpload, java.awt.BorderLayout.CENTER);

        pnlBottomButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddUploadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_190_circle_plus.png"))); // NOI18N
        btnAddUploadFile.setMnemonic('+');
        btnAddUploadFile.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnAddUploadFile.text")); // NOI18N
        btnAddUploadFile.setToolTipText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnAddUploadFile.toolTipText")); // NOI18N
        btnAddUploadFile.setMargin(null);
        btnAddUploadFile.setMaximumSize(null);
        btnAddUploadFile.setMinimumSize(null);
        btnAddUploadFile.setPreferredSize(null);
        btnAddUploadFile.setRequestFocusEnabled(false);
        btnAddUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUploadFileActionPerformed(evt);
            }
        });
        pnlBottomButtons.add(btnAddUploadFile);

        btnDeleteUploadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnDeleteUploadFile.setMnemonic('-');
        btnDeleteUploadFile.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnDeleteUploadFile.text")); // NOI18N
        btnDeleteUploadFile.setToolTipText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnDeleteUploadFile.toolTipText")); // NOI18N
        btnDeleteUploadFile.setMargin(null);
        btnDeleteUploadFile.setMaximumSize(null);
        btnDeleteUploadFile.setMinimumSize(null);
        btnDeleteUploadFile.setPreferredSize(null);
        btnDeleteUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUploadFileActionPerformed(evt);
            }
        });
        pnlBottomButtons.add(btnDeleteUploadFile);

        pnlFilesToUpload.add(pnlBottomButtons, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlFilesToUpload, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_197_remove.png"))); // NOI18N
        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel12.add(btnCancel);

        btnUploadNow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_415_disk_open.png"))); // NOI18N
        btnUploadNow.setMnemonic('u');
        btnUploadNow.setText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUploadNow.text")); // NOI18N
        btnUploadNow.setToolTipText(org.openide.util.NbBundle.getMessage(UploadDialog.class, "UploadDialog.btnUploadNow.toolTipText")); // NOI18N
        btnUploadNow.setEnabled(false);
        btnUploadNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadNowActionPerformed(evt);
            }
        });
        jPanel12.add(btnUploadNow);

        jPanel2.add(jPanel12, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btnBrowseUploadTargetActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBrowseUploadTargetActionPerformed

		IRODSFinderDialog irodsFinder = new IRODSFinderDialog(idropGUI, false,
				idropGUI.getiDropCore());
		irodsFinder.setTitle("Select iRODS Collection Upload Target");
		irodsFinder
				.setSelectionType(IRODSFinderDialog.SelectionType.COLLS_ONLY_SELECTION_MODE);
		irodsFinder.setLocation((int) this.getLocation().getX(), (int) this
				.getLocation().getY());
		irodsFinder.setVisible(true);

		String selectedPath = irodsFinder.getSelectedAbsolutePath();
		if (selectedPath != null) {
			txtUploadTarget.setText(selectedPath);
		}
		setUploadButtonState();
	}// GEN-LAST:event_btnBrowseUploadTargetActionPerformed

	private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
		dispose();
	}// GEN-LAST:event_btnCancelActionPerformed

	private void btnUploadNowActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUploadNowActionPerformed
		executeUpload();
		dispose();
	}// GEN-LAST:event_btnUploadNowActionPerformed

	private void btnUseIrodsHomeActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUseIrodsHomeActionPerformed
		String target = "";
		if (idropGUI.getiDropCore().getIrodsAccount().isAnonymousAccount()) {
			target = MiscIRODSUtils.computePublicDirectory(idropGUI
					.getiDropCore().getIrodsAccount());
		} else {
			target = MiscIRODSUtils
					.computeHomeDirectoryForIRODSAccount(idropGUI
							.getiDropCore().getIrodsAccount());
		}
		if (target != null) {
			txtUploadTarget.setText(target);
		}
		setUploadButtonState();
	}// GEN-LAST:event_btnUseIrodsHomeActionPerformed

	private void btnUseLastUploadActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUseLastUploadActionPerformed
		String target = "";
		// see if can find some get history in the transfer queue
                /* FIXME: conveyor
		try {
			List<LocalIRODSTransfer> transfers = idropGUI.getiDropCore()
					.getTransferManager().getRecentQueue();

			// assuming most recent first
			for (LocalIRODSTransfer transfer : transfers) {
				// must check to match type, user, host, zone, & port
				if ((transfer.getTransferType() == TransferType.PUT)
						&& (transfer.getTransferUserName()
								.equals(idropGUI.getiDropCore()
										.getIrodsAccount().getUserName()))
						&& (transfer.getTransferZone().equals(idropGUI
								.getiDropCore().getIrodsAccount().getZone()))
						&& (transfer.getTransferPort() == idropGUI
								.getiDropCore().getIrodsAccount().getPort())
						&& (transfer.getTransferHost().equals(idropGUI
								.getiDropCore().getIrodsAccount().getHost()))) {
					target = transfer.getIrodsAbsolutePath();
					break;
				}
			}
		} catch (JargonException ex) {
			Exceptions.printStackTrace(ex);
		}*/
		if (target != null) {
			txtUploadTarget.setText(target);
		}
		setUploadButtonState();
	}// GEN-LAST:event_btnUseLastUploadActionPerformed

	private void btnAddUploadFileActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddUploadFileActionPerformed
		JFileChooser localFileChooser = new JFileChooser();
		localFileChooser.setMultiSelectionEnabled(true);
		localFileChooser
				.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		localFileChooser.setDialogTitle("Select Files to Upload");
		localFileChooser.setLocation((int) this.getLocation().getX(),
				(int) this.getLocation().getY());
		int returnVal = localFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] filesToUpload = localFileChooser.getSelectedFiles();
			setFilesToUpload(filesToUpload);
			setUploadButtonState();
		}
	}// GEN-LAST:event_btnAddUploadFileActionPerformed

	private void btnDeleteUploadFileActionPerformed(
			final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteUploadFileActionPerformed

		int[] selectedRows = tblFilesToUpload.getSelectedRows();
		int numRowsSelected = selectedRows.length;

		// have to remove rows in reverse
		for (int i = numRowsSelected - 1; i >= 0; i--) {
			// for (int selectedRow: selectedRows) {
			int selectedRow = selectedRows[i];
			if (selectedRow >= 0) {
				DefaultTableModel model = (DefaultTableModel) tblFilesToUpload
						.getModel();
				model.removeRow(selectedRow);
			}
		}
	}// GEN-LAST:event_btnDeleteUploadFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUploadFile;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteUploadFile;
    private javax.swing.JButton btnUploadNow;
    private javax.swing.JButton btnUseIrodsHome;
    private javax.swing.JButton btnUseLastUpload;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblUploadTargetLocation;
    private javax.swing.JPanel pnlBottomButtons;
    private javax.swing.JPanel pnlFilesToUpload;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTargetLocations;
    private javax.swing.JScrollPane scrollPanelFilesToUpload;
    private javax.swing.JTable tblFilesToUpload;
    private javax.swing.JTextField txtUploadTarget;
    // End of variables declaration//GEN-END:variables

}

