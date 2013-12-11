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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1773323142549578964L;
	private iDrop idropGUI;
	private IRODSTree irodsTree;
	private IRODSOutlineModel irodsFileSystemModel;
	public static org.slf4j.Logger log = LoggerFactory
			.getLogger(IRODSTree.class);

	/**
	 * Creates new form CopyMoveDialog
	 */
	public CopyMoveDialog(final java.awt.Frame parent, final boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public CopyMoveDialog(final iDrop parent, final boolean modal,
			final IRODSTree irodsTree) {
		super(parent, modal);
		initComponents();

		idropGUI = parent;
		this.irodsTree = irodsTree;
		irodsFileSystemModel = (IRODSOutlineModel) irodsTree.getModel();

		initSourcesFiles();
		setCopyMoveButtonsState();
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
		IRODSFile ifile = null;
		// final List<File> sourceFiles = new ArrayList<File>();
		for (int idx = idxStart; idx <= idxEnd; idx++) {
			if (selectionModel.isSelectedIndex(idx)) {
				try {
					IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
							.getValueAt(idx, 0);
					ifile = irodsFS.getIRODSFileForPath(selectedNode
							.getFullPath());
					// rule out "/"
					String path = ifile.getAbsolutePath();
					if ((path != null) && (!path.equals("/"))) {
						txtCurrentParent.append(path + "\n");
					}
				} catch (IdropException ex) {
					Exceptions.printStackTrace(ex);
				}
			}
		}
	}

	private void setCopyMoveButtonsState() {
		boolean state = ((txtNewLocation.getText().length() > 0) && (txtCurrentParent
				.getText().length() > 0));
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
					thisDialog.setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));

					DataTransferOperations dataTransferOperations;
					try {
						dataTransferOperations = idropGUI
								.getiDropCore()
								.getIRODSAccessObjectFactory()
								.getDataTransferOperations(
										idropGUI.getIrodsAccount());
					} catch (Exception e) {
						idropGUI.getiDropCore().closeIRODSConnection(
								idropGUI.getIrodsAccount());
						thisDialog.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						throw new IdropRuntimeException(e);
					}

					List<IRODSFile> filesThatHadOverwriteError = new ArrayList<IRODSFile>();

					if (sourceFiles.length == 1) {
						// IRODSFile irodsFile = null;
						log.info("processing the move/copy for one file:{}",
								sourceFiles[0]);
						try {
							irodsFile = idropGUI.getiDropCore()
									.getIRODSFileFactoryForLoggedInAccount()
									.instanceIRODSFile(sourceFiles[0]);
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
							log.error("Coll already exists", ex);
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
								irodsFile = idropGUI
										.getiDropCore()
										.getIRODSFileFactoryForLoggedInAccount()
										.instanceIRODSFile(sourceFileEntry);
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
								// FIXME: fix in jargon core to differentiate!
								log.error("coll already exists", ex);
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
					thisDialog.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
			IRODSFile targetFile = idropGUI.getiDropCore()
					.getIRODSFileFactoryForLoggedInAccount()
					.instanceIRODSFile(targetAbsolutePath);

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
		IRODSNode sourceNode = (IRODSNode) sourceNodePath
				.getLastPathComponent();
		irodsFileSystemModel.notifyFileShouldBeRemoved(sourceNode);
	}

	private void processACopyOfAnIndividualFile(
			final DataTransferOperations dataTransferOperations,
			final IRODSFile sourceFile, final String targetAbsolutePath)
			throws IdropException {
		try {
			dataTransferOperations.copy(sourceFile.getAbsolutePath(), idropGUI
					.getiDropCore().getIrodsAccount()
					.getDefaultStorageResource(), targetAbsolutePath, null,
					null);
			// idropGUI.getiDropCore().getTransferManager().enqueueACopy(sourceFile.getAbsolutePath(),
			// sourceFile.getResource(), targetAbsolutePath,
			// idropGUI.getiDropCore().getIrodsAccount());

		} catch (JargonException ex) {
			log.error("jargon exception", ex);
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

	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		pnlFolderData = new javax.swing.JPanel();
		pnlCurrentParent = new javax.swing.JPanel();
		lblCurrentParentLabel = new java.awt.Label();
		scrollCurrentParent = new javax.swing.JScrollPane();
		txtCurrentParent = new javax.swing.JTextArea();
		lblNewDiretoryName = new java.awt.Label();
		txtNewLocation = new javax.swing.JTextField();
		btnBrowse = new javax.swing.JButton();
		pnlBottom = new javax.swing.JPanel();
		btnCancel = new javax.swing.JButton();
		btnMove = new javax.swing.JButton();
		btnCopy = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(org.openide.util.NbBundle.getMessage(CopyMoveDialog.class,
				"CopyMoveDialog.title")); // NOI18N
		setPreferredSize(new java.awt.Dimension(600, 300));

		pnlFolderData.setPreferredSize(new java.awt.Dimension(540, 240));
		pnlFolderData.setLayout(new java.awt.BorderLayout());

		pnlCurrentParent.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				10, 4, 10, 4));
		pnlCurrentParent.setPreferredSize(new java.awt.Dimension(500, 190));
		pnlCurrentParent.setLayout(new java.awt.GridBagLayout());

		lblCurrentParentLabel.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class,
				"CopyMoveDialog.lblCurrentParentLabel.text")); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
		pnlCurrentParent.add(lblCurrentParentLabel, gridBagConstraints);

		scrollCurrentParent.setPreferredSize(new java.awt.Dimension(360, 100));

		txtCurrentParent.setEditable(false);
		txtCurrentParent.setColumns(20);
		txtCurrentParent.setRows(5);
		txtCurrentParent.setWrapStyleWord(true);
		txtCurrentParent.setFocusable(false);
		txtCurrentParent.setPreferredSize(new java.awt.Dimension(290, 90));
		scrollCurrentParent.setViewportView(txtCurrentParent);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		pnlCurrentParent.add(scrollCurrentParent, gridBagConstraints);

		lblNewDiretoryName
				.setText(org.openide.util.NbBundle.getMessage(
						CopyMoveDialog.class,
						"CopyMoveDialog.lblNewDiretoryName.text")); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
		pnlCurrentParent.add(lblNewDiretoryName, gridBagConstraints);

		txtNewLocation.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.txtNewLocation.text")); // NOI18N
		txtNewLocation.setPreferredSize(new java.awt.Dimension(360, 28));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		pnlCurrentParent.add(txtNewLocation, gridBagConstraints);

		btnBrowse
				.setIcon(new javax.swing.ImageIcon(
						getClass()
								.getResource(
										"/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_144_folder_open.png"))); // NOI18N
		btnBrowse.setMnemonic('b');
		btnBrowse.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnBrowse.text")); // NOI18N
		btnBrowse.setToolTipText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnBrowse.toolTipText")); // NOI18N
		btnBrowse.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				btnBrowseActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		pnlCurrentParent.add(btnBrowse, gridBagConstraints);

		pnlFolderData.add(pnlCurrentParent, java.awt.BorderLayout.CENTER);

		pnlBottom.setPreferredSize(new java.awt.Dimension(708, 40));
		pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		btnCancel
				.setIcon(new javax.swing.ImageIcon(
						getClass()
								.getResource(
										"/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
		btnCancel.setMnemonic('C');
		btnCancel.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnCancel.text")); // NOI18N
		btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnCancel.toolTipText")); // NOI18N
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});
		pnlBottom.add(btnCancel);

		btnMove.setIcon(new javax.swing.ImageIcon(
				getClass()
						.getResource(
								"/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_318_more_items.png"))); // NOI18N
		btnMove.setMnemonic('m');
		btnMove.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnMove.text")); // NOI18N
		btnMove.setToolTipText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnMove.toolTipText")); // NOI18N
		btnMove.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				btnMoveActionPerformed(evt);
			}
		});
		pnlBottom.add(btnMove);

		btnCopy.setIcon(new javax.swing.ImageIcon(
				getClass()
						.getResource(
								"/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_318_more_items.png"))); // NOI18N
		btnCopy.setMnemonic('c');
		btnCopy.setText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnCopy.text")); // NOI18N
		btnCopy.setToolTipText(org.openide.util.NbBundle.getMessage(
				CopyMoveDialog.class, "CopyMoveDialog.btnCopy.toolTipText")); // NOI18N
		btnCopy.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				btnCopyActionPerformed(evt);
			}
		});
		pnlBottom.add(btnCopy);

		pnlFolderData.add(pnlBottom, java.awt.BorderLayout.SOUTH);

		getContentPane().add(pnlFolderData, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
		dispose();
	}// GEN-LAST:event_btnCancelActionPerformed

	private void btnMoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveActionPerformed
		processMoveOrCopy(false);
	}// GEN-LAST:event_btnMoveActionPerformed

	private void btnBrowseActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBrowseActionPerformed
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
			txtNewLocation.setText(selectedPath);
		}
		setCopyMoveButtonsState();
	}// GEN-LAST:event_btnBrowseActionPerformed

	private void btnCopyActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCopyActionPerformed
		processMoveOrCopy(true);
	}// GEN-LAST:event_btnCopyActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnBrowse;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnCopy;
	private javax.swing.JButton btnMove;
	private java.awt.Label lblCurrentParentLabel;
	private java.awt.Label lblNewDiretoryName;
	private javax.swing.JPanel pnlBottom;
	private javax.swing.JPanel pnlCurrentParent;
	private javax.swing.JPanel pnlFolderData;
	private javax.swing.JScrollPane scrollCurrentParent;
	private javax.swing.JTextArea txtCurrentParent;
	private javax.swing.JTextField txtNewLocation;
	// End of variables declaration//GEN-END:variables
}
