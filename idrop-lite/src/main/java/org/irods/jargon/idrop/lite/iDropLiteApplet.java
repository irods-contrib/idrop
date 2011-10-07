/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJApplet.java
 *
 * Created on Jul 13, 2011, 11:52:59 AM
 */

package org.irods.jargon.idrop.lite;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.datautils.datacache.DataCacheServiceImpl;
import org.irods.jargon.idrop.lite.finder.IRODSFinderDialog;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;
import org.netbeans.swing.outline.Outline;

import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class iDropLiteApplet extends javax.swing.JApplet implements TransferStatusCallbackListener, TableModelListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);
    private iDropLiteApplet applet;
    private final Integer defaultLoginMode = -1;
    private iDropLiteCore iDropCore = null;
    private IRODSAccount irodsAccount = null;
    private LocalFileTree fileTree = null;
    private LocalFileTree fileUploadTree = null;
    private IRODSTree irodsTree = null;
    private Integer mode;
    private String host;
    private Integer port;
    private String zone;
    private String user;
    private String defaultStorageResource;
    private String tempPswd;
    private String absPath;
    private String uploadDest;
    IRODSFileSystem irodsFileSystem = null;
    private LocalFileSystemModel localFileModel = null;
    private LocalFileSystemModel localUploadFileModel = null;
    private Boolean transferInProgress = false;
    private String currentUploadFile = null;
    private ImageIcon cancelIcon;

    /** Initializes the applet NewJApplet */
    public void init() {
    	this.applet = this;
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                	getAppletParams();         
                    if(doStartup()) {
                    	initComponents();
                    	doPostInitWork();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void getAppletParams() {

    	try {
    		this.mode = Integer.parseInt(getParameter("mode"));
    	} catch (Exception ex) {
            this.mode = defaultLoginMode;
        }
    	try {
    		this.defaultStorageResource = getParameter("defaultStorageResource");
    	} catch (Exception ex) {
    		this.defaultStorageResource = "";
        }
    	try {
    		this.uploadDest = getParameter("uploadDest");
    	} catch (Exception ex) {
    		this.uploadDest = "";
        }

    	try {
    		this.host = getParameter("host");
    		this.port = Integer.parseInt(getParameter("port"));
    		this.user = getParameter("user");
    		this.zone = getParameter("zone");
    		//this.defaultStorageResource = getParameter("defaultStorageResource");
    		this.tempPswd = getParameter("password");
    		this.absPath = getParameter("absPath");

            log.debug("creating account with applet params");
            log.info("mode:{}", mode);
            log.info("host:{}", host);
            log.info("port:{}", port);
            log.info("user:{}", user);
            log.info("zone:{}", zone);
            log.info("resource:{}", defaultStorageResource);
            log.info("absPath:{}", absPath);
            log.info("upload destination:{}", uploadDest);
        } catch (Exception ex) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
            showIdropException(ex);
        }

    }

    private boolean retrievePermAccount()
    {
    	String pswd = null;

    	DataCacheServiceImpl dataCache = new DataCacheServiceImpl();
    	try {
			dataCache.setIrodsAccessObjectFactory(irodsFileSystem.getIRODSAccessObjectFactory());
		} catch (JargonException e1) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e1);
		}
    	dataCache.setIrodsAccount(irodsAccount);

    	try {
    		log.info("sending user name and key user:{}", user);
			pswd = dataCache.retrieveStringValueFromCache(user, tempPswd);
			irodsFileSystem.closeAndEatExceptions();
			this.irodsAccount = new IRODSAccount(host, port, user, pswd, absPath, zone, defaultStorageResource);
		} catch (JargonException e2) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e2);
			return false;
		}

		return true;
    }

    private boolean createPermAccount()
    {
    	this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);

    	return true;
    }


    private boolean processLogin()  {

    	// do different logins depending on which mode is used
    	// 0 - Hard-coded permanent password - just use this password to create and IRODS Account
    	// 1 - Temporary password supplied - use this password to retrieve permanent password from cache file in cacheServiceTempDir

    	switch(this.mode) {

    	case 1:
    		log.info("processLogin: retrieving permanent password...");
    		if(!retrievePermAccount()) {
    			showMessageFromOperation("Temporary Password Mode: login error - unable to log in, or invalid user id");
    			return false;
    		}
    		break;
    	case 0:
    		log.info("processLogin: creating account with provided permanent password...");
    		if(!createPermAccount()) {
    			showMessageFromOperation("Permanent Password Mode: login error - unable to log in, or invalid user id");
    			return false;
    		}
    		break;
    	default:
    		showMessageFromOperation("Unsupported Login Mode");
    		return false;

    	}


        try {

            final UserAO userAO = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
            iDropCore.setIrodsAccount(irodsAccount);
            iDropCore.setIrodsFileSystem(irodsFileSystem);
        } catch (JargonException ex) {
            if (ex.getMessage().indexOf("Connection refused") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else if (ex.getMessage().indexOf("Connection reset") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else if (ex.getMessage().indexOf("io exception opening socket") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("login error - unable to log in, or invalid user id");
                return false;
            }
        } finally {
            if (irodsFileSystem != null) {
                try {
                    irodsFileSystem.close();
                } catch (JargonException ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }


    protected boolean doStartup() {

        log.info("initiating startup sequence...");

        log.info("creating irods file system instance...");
        try {
			irodsFileSystem = IRODSFileSystem.instance();
		} catch (JargonException ex) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
		}

        log.info("creating temporary irods account...");
        this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);

        log.info("creating idropCore...");
        iDropCore = new iDropLiteCore();

        if(!processLogin()) {
        	return false;
        }

        buildTargetTree();
        setUpLocalFileSelectTree();
        setUpUploadLocalFileSelectTree();

        try {

        	DataTransferOperations dataTransferOperations = irodsFileSystem.getIRODSAccessObjectFactory()
        	.getDataTransferOperations(irodsAccount);
        	iDropCore.setTransferManager(dataTransferOperations);
        }
        catch(JargonException ex) {
        	Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
			iDropCore.setTransferControlBlock(DefaultTransferControlBlock.instance());
		} catch (JargonException ex) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
		}

        return true;
    }
    
    private void doPostInitWork() {
    	populateUploadDestination();
    	setupUploadTable();
    }

    public void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final iDropLiteApplet gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();

                log.info("using root path, no login preset");
                root.setPathOrName("/");

                log.info("building new iRODS tree");
                try {
                    if (irodsTree == null) {
                        irodsTree = new IRODSTree(gui);
                        IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(),
                                getiDropCore().getIrodsFileSystem(), irodsTree);
                        irodsTree.setRefreshingTree(true);
                        //irodsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    }
                    IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(), getiDropCore().getIrodsFileSystem(),
                            irodsTree);

                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(rootNode, getIrodsAccount());
                    IRODSOutlineModel mdl = new IRODSOutlineModel(gui, irodsFileSystemModel, new IRODSRowModel(), true,
                            "File System");
                    irodsTree.setModel(mdl);

                    /*
                     * IrodsTreeListenerForBuildingInfoPanel treeListener = new
                     * IrodsTreeListenerForBuildingInfoPanel(gui); irodsTree.addTreeExpansionListener(treeListener);
                     * irodsTree.addTreeSelectionListener(treeListener); // preset to display root tree node
                     * irodsTree.setSelectionRow(0);
                     */
                } catch (Exception ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                }

                scrIrodsTreeView.setViewportView(getTreeStagingResource());
                try {
                    TreePath selectedPath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, absPath);
                    irodsTree.expandPath(selectedPath);
                    //irodsTree.getSelectionModel().setSelectionInterval(10, 12);
                    Rectangle rect = irodsTree.getPathBounds(selectedPath);
                    scrIrodsTreeView.getViewport().scrollRectToVisible(rect);
                }
                catch(IdropException ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*
                 * TreePath currentPath;
                 *
                 * if (currentPaths != null) { while (currentPaths.hasMoreElements()) { currentPath = (TreePath)
                 * currentPaths.nextElement(); log.debug("expanding tree path:{}", currentPath);
                 * irodsTree.expandPath(currentPath); } }
                 */
                irodsTree.setRefreshingTree(false);

                getiDropCore().getIrodsFileSystem().closeAndEatExceptions(iDropCore.getIrodsAccount());
            }
        });
    }
    
    
    // NEED TO REFACTOR ALL OF THE FOLLOWING DUPLICATION!!!!!!!!
    private void setUpLocalFileSelectTree() {

        /*
         * build a list of the roots (e.g. drives on windows systems). If there
         * is only one, use it as the basis for the file model, otherwise,
         * display an additional panel listing the other roots, and build the
         * tree for the first drive encountered.
         */

        if (fileTree != null) {
            log.info("file tree already initialized");
            return;
        }

        log.info("building tree to look at local file system");
        final iDropLiteApplet gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                initializeLocalFileTreeModel(null);
                fileTree = new LocalFileTree(localFileModel, gui);
                lstLocalDrives.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {

                            @Override
                            public void valueChanged(final ListSelectionEvent e) {
                                if (e.getValueIsAdjusting()) {
                                    return;
                                }

                                log.debug("new local file system model");
                                log.debug("selection event:{}", e);
                                Object selectedItem = lstLocalDrives.getSelectedValue();
                                initializeLocalFileTreeModelWhenDriveIsSelected(selectedItem);

                            }
                        });
                scrollLocalFileTree.setViewportView(fileTree);
                pnlLocalTree.add(scrollLocalFileTree,
                        java.awt.BorderLayout.CENTER);
                pnlLocalTree.setVisible(false);
            }
        });

    }
    
    private void setUpUploadLocalFileSelectTree() {

        /*
         * build a list of the roots (e.g. drives on windows systems). If there
         * is only one, use it as the basis for the file model, otherwise,
         * display an additional panel listing the other roots, and build the
         * tree for the first drive encountered.
         */

        if (fileUploadTree != null) {
            log.info("file upload tree already initialized");
            return;
        }

        log.info("building upload tree to look at local file system");
        final iDropLiteApplet gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                initializeUploadLocalFileTreeModel(null);
                fileUploadTree = new LocalFileTree(localUploadFileModel, gui);
                lstUploadLocalDrives.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {

                            @Override
                            public void valueChanged(final ListSelectionEvent e) {
                                if (e.getValueIsAdjusting()) {
                                    return;
                                }

                                log.debug("new uload local file system model");
                                log.debug("uload selection event:{}", e);
                                Object selectedItem = lstUploadLocalDrives.getSelectedValue();
                                initializeUploadLocalFileTreeModelWhenDriveIsSelected(selectedItem);

                            }
                        });
                scrollUploadLocalTree.setViewportView(fileUploadTree);
                pnlUploadLocalTree.add(scrollUploadLocalTree,
                        java.awt.BorderLayout.CENTER);
                pnlUploadLocalTree.setVisible(true);
            }
        });

    }
    
    private void initializeLocalFileTreeModelWhenDriveIsSelected(
            final Object selectedDrive) {
        if (selectedDrive == null) {
            log.debug("selected drive is null, use the first one");
            lstLocalDrives.setSelectedIndex(0);

            localFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File((String) lstLocalDrives.getSelectedValue())));

            fileTree.setModel(localFileModel);
        } else {
            log.debug(
                    "selected drive is not null, create new root based on selection",
                    selectedDrive);
            lstLocalDrives.setSelectedValue(selectedDrive, true);
            localFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File((String) selectedDrive)));
            fileTree.setModel(localFileModel);

        }

        scrollLocalDrives.setVisible(true);
    }

    private void initializeLocalFileTreeModel(final Object selectedDrive) {
        List<String> roots = LocalFileUtils.listFileRootsForSystem();

        if (roots.isEmpty()) {
            IdropException ie = new IdropException(
                    "unable to find any roots on the local file system");
            log.error("error building roots on local file system", ie);
            showIdropException(ie);
            return;
        } else if (roots.size() == 1) {
            scrollLocalDrives.setVisible(false);
            localFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File(roots.get(0))));

        } else {
            DefaultListModel listModel = new DefaultListModel();
            for (String root : roots) {
                listModel.addElement(root);
            }

            lstLocalDrives.setModel(listModel);

            scrollLocalDrives.setVisible(true);
        }
    }
    
    private void initializeUploadLocalFileTreeModelWhenDriveIsSelected(
            final Object selectedDrive) {
        if (selectedDrive == null) {
            log.debug("selected drive is null, use the first one");
            lstUploadLocalDrives.setSelectedIndex(0);

            localUploadFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File((String) lstUploadLocalDrives.getSelectedValue())));

            fileUploadTree.setModel(localUploadFileModel);
        } else {
            log.debug(
                    "selected drive is not null, create new root based on selection",
                    selectedDrive);
            lstUploadLocalDrives.setSelectedValue(selectedDrive, true);
            localUploadFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File((String) selectedDrive)));
            fileUploadTree.setModel(localUploadFileModel);

        }

        scrollUploadLocalDrives.setVisible(true);
    }

    private void initializeUploadLocalFileTreeModel(final Object selectedDrive) {
        List<String> roots = LocalFileUtils.listFileRootsForSystem();

        if (roots.isEmpty()) {
            IdropException ie = new IdropException(
                    "unable to find any roots on the local file system");
            log.error("error building roots on local file system", ie);
            showIdropException(ie);
            return;
        } else if (roots.size() == 1) {
            scrollUploadLocalDrives.setVisible(false);
            localUploadFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File(roots.get(0))));

        } else {
            DefaultListModel listModel = new DefaultListModel();
            for (String root : roots) {
                listModel.addElement(root);
            }

            lstUploadLocalDrives.setModel(listModel);

            scrollUploadLocalDrives.setVisible(true);
        }
    }
    
    private void populateUploadDestination() {
    	if((uploadDest != null) && (uploadDest.length() > 0)) {
    		txtIRODSUploadDest.setText(uploadDest);
    		btnUploadBeginImport.setEnabled(true);
    	}
    }
    
    private void setupUploadTable() {
    	
    	// load table cancel icon
        java.net.URL imgURL = getClass().getResource("cancel.gif");
	    if (imgURL != null) {
	        cancelIcon = new ImageIcon(imgURL, "image used to denote cancel or remove table entry");
	    }
	    else {
	    	log.error("cannot find image: cancel.gif for Upload Table");
	    }
    	//set FillsViewportHeight so user can drop onto an empty table
    	tblUploadTable.setFillsViewportHeight(true);
    	tblUploadTable.setShowGrid(true);
        tblUploadTable.setShowVerticalLines(false);
        tblUploadTable.getColumnModel().getColumn(2).setPreferredWidth(6);
    	
    	tblUploadTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	tblUploadTable.setDropMode(DropMode.INSERT_ROWS);
    	tblUploadTable.setDragEnabled(true);
    	UploadTableTransferHandler tth = new UploadTableTransferHandler();
    	tth.setGUI(this);
    	tblUploadTable.getModel().addTableModelListener(applet);
    	tblUploadTable.setTransferHandler(tth);
    	
    	// add rendered for progress bars in second column
    	tblUploadTable.getColumnModel().getColumn(1).setCellRenderer(new UploadTableProgressBar());
    	tblUploadTable.getColumnModel().getColumn(2).setCellRenderer(new UploadTableCancelRenderer(cancelIcon));
    	
    }

    public IRODSAccount getIrodsAccount() {
        synchronized (this) {
            return this.iDropCore.getIrodsAccount();
        }
    }


    public void showIdropException(Exception idropException) {
        JOptionPane.showMessageDialog(this, idropException.getMessage(), "iDROP Exception", JOptionPane.WARNING_MESSAGE);
    }

    public void showMessageFromOperation(final String messageFromOperation) {

        final iDropLiteApplet thisIdropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(thisIdropGui, messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
	public void statusCallback(final TransferStatus ts) {
    	log.info("transfer status callback to iDropLiteApplet:{}", ts);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            	log.info("in statusCallback thread");
            	
            	//String file = ts.getSourceFileAbsolutePath();
            	int tableRow = -1;
            	if(currentUploadFile != null) {
            		tableRow = getUploadTableProgressRow(currentUploadFile);
            	}
        		
                if (ts.isIntraFileStatusReport()) {

                    // intra file reports update the progress bar
                    lblTransferByteCounts.setText("Current File (kb):"
                            + (ts.getBytesTransfered() / 1024) + " / "
                            + (ts.getTotalSize() / 1024));
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum((int) ts.getTotalSize());
                    progressIntraFile.setValue((int) ts.getBytesTransfered());
                    
                 // if uploading from mode 2 table
                    if((tableRow >= 0) && (ts.getTotalSize() > 0)) {
                    	float bt = ts.getBytesTransfered() * 100;
                    	float tot = ts.getTotalSize();
            			float percentDone = bt/tot;
            			tblUploadTable.getModel().setValueAt((int)percentDone, tableRow, 1);
                    }

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {

                    // start of a file operation
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum((int) ts.getTotalSize());
                    progressIntraFile.setValue(0);
                    lblCurrentFile.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
                    
                    //currentUploadFile = ts.getSourceFileAbsolutePath();
                    if(currentUploadFile != null) {
                		tableRow = getUploadTableProgressRow(currentUploadFile);
                	}
                    if((tableRow >= 0)) {
            			tblUploadTable.getModel().setValueAt(0, tableRow, 1);
                    }


                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {

                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum(10);
                    progressIntraFile.setValue(10);
                    lblTransferByteCounts.setText("Current File (kb):"
                            + (ts.getTotalSize() / 1024) + " / "
                            + (ts.getTotalSize() / 1024));
                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
                    lblTransferFilesCounts.setText("Files: "
                            + ts.getTotalFilesTransferredSoFar() + " / "
                            + ts.getTotalFilesToTransfer());
                    
                    // if uploading from mode 2 table
                    if((tableRow >= 0) && (ts.getTotalSize() > 0)) {
                    	float bt = ts.getBytesTransfered() * 100;
                    	float tot = ts.getTotalSize();
                    	float percentDone = bt/tot;
                    	tblUploadTable.getModel().setValueAt((int)percentDone, tableRow, 1);
                    }

                } else {

                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
                    lblTransferFilesCounts.setText("Files: "
                            + ts.getTotalFilesTransferredSoFar() + " / "
                            + ts.getTotalFilesToTransfer());
                    lblTransferByteCounts.setText("Current File (kb):"
                            + (ts.getBytesTransfered() / 1024) + " / "
                            + (ts.getTotalSize() / 1024));

                    lblCurrentFile.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
                }
            }
        });
    }

	@Override
	public void overallStatusCallback(final TransferStatus ts) {
		
		final IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree.getModel();
        final iDropLiteApplet idropGui = this;
        idropGui.setTransferInProgress(true);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	int tableRow = -1;
            	if(currentUploadFile != null) {
            		tableRow = getUploadTableProgressRow(currentUploadFile);
            	}
            	
                /* 
                 * Handle appropriate tree notifications, so some filtering to prevent notifications when for a different host/zone
                 */
                if (ts.getTransferType() == TransferStatus.TransferType.SYNCH || ts.getTransferType() == TransferStatus.TransferType.REPLICATE) {
                    log.info("no need to notify tree for synch or replicate");
                } else if (ts.getTransferType() == TransferStatus.TransferType.GET
                        && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                    try {
                        ((LocalFileSystemModel) idropGui.getFileTree().getModel()).notifyCompletionOfOperation(idropGui.getFileTree(), ts);

                    } catch (IdropException ex) {
                        log.error("error on tree notify after operation", ex);
                        throw new IdropRuntimeException("error processing overall status callback", ex);
                    }
                } else if (ts.getTransferType() == TransferStatus.TransferType.COPY || ts.getTransferType() == TransferStatus.TransferType.PUT) {
                    if (ts.getTransferZone().equals(
                            iDropCore.getIrodsAccount().getZone()) && ts.getTransferHost().equals(iDropCore.getIrodsAccount().getHost())) {
                        try {
                            // should leave PUT, and COPY
                            irodsTreeModel.notifyCompletionOfOperation(irodsTree, ts);
                        } catch (IdropException ex) {
                            log.error("error on tree notify after operation", ex);
                            throw new IdropRuntimeException("error processing overall status callback", ex);
                        }
                    }

                }

                /*
                 * Handle progress bar and messages.  These are cleared on overall initiation
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    clearProgressBar();
                    // on initiation, clear and reset the status bar info
                    lblTransferType.setText(ts.getTransferType().name());
                    lblTransferFilesCounts.setText("Files: "
                            + ts.getTotalFilesTransferredSoFar() + " / "
                            + ts.getTotalFilesToTransfer());
                    lblTransferByteCounts.setText("Bytes (kb):"
                            + (ts.getBytesTransfered() / 1024) + " / "
                            + (ts.getTotalSize() / 1024));
                    lblCurrentFile.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
                    transferStatusProgressBar.setMinimum(0);
                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(0);
                    
                    currentUploadFile = ts.getSourceFileAbsolutePath();
                    enableUploadButtons(false);
                }
                
                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                	if(tableRow >= 0) {
                		//tblUploadTable.getModel().setValueAt(false, tableRow, 1);
                	}
                	currentUploadFile = null;
                	idropGui.setTransferInProgress(false);
                    enableUploadButtons(true);
                }

                /*
                 * Handle any text messages
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    lblTransferStatusMessage.setText("Synchronization Initializing");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_GENERATION) {
                    lblTransferStatusMessage.setText("Synchronization looking for updates");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_STEP) {
                    lblTransferStatusMessage.setText("Synchronizing differences");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
                    lblTransferStatusMessage.setText("Synchronization complete");
                } else if (ts.getTransferEnclosingType() == TransferStatus.TransferType.SYNCH) {
                    lblTransferStatusMessage.setText("Transfer to synchronize local and iRODS");
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION) {
                    // initiation not within a synch
                    lblTransferStatusMessage.setText("Processing a " + ts.getTransferType().name() + " operation");
                }
            }
        });

	}
	
	private void clearProgressBar() {
        lblTransferType.setText("");
        lblTransferFilesCounts.setText("Files:   /    ");

        lblTransferByteCounts.setText("Bytes (kb):  /   ");

        lblCurrentFile.setText("");
        transferStatusProgressBar.setMinimum(0);
        transferStatusProgressBar.setMaximum(100);
        transferStatusProgressBar.setValue(0);
    }
	
	public void setTransferInProgress(Boolean state) {
		this.transferInProgress = state;
	}
	
	public Boolean isTransferInProgress() {
		return this.transferInProgress;
	}
	
	public void cancelTransfer() {
		java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            	iDropCore.getTransferControlBlock().setCancelled(true);
            }
        });
	}

	private final String abbreviateFileName(final String fileName) {

        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }

        StringBuilder sb = new StringBuilder();
        if (fileName.length() < 100) {
            sb.append(fileName);
        } else {
            // gt 100 bytes, redact
            sb.append(fileName.substring(0, 50));
            sb.append(" ... ");
            sb.append(fileName.substring(fileName.length() - 50));
        }

        return sb.toString();

    }
	
	private int getUploadTableProgressRow(String filename) {
		int row=-1;
		int numRows = tblUploadTable.getModel().getRowCount();
		for(int i=0; i<numRows; i++) {
			if((String)tblUploadTable.getModel().getValueAt(i, 0) == filename) {
				row = i;
				break;
			}
		}
		
		return row;
	}
	
	private void enableUploadButtons(Boolean state) {
		btnUploadBeginImport.setEnabled(state);
	    btnUploadCancel.setEnabled(state);
	    btnUploadMove.setEnabled(state);
	    btnBrowseIRODSUploadDest.setEnabled(state);
	}
	
	public void setTotalFileUpload(int total) {
		String phrase = "Total Files To Upload: ";
		
		if(total > 0) {
			Integer itotal = new Integer(total);
			lblUploadTotalFiles.setText(phrase.concat(itotal.toString()));
		}
		else {
			lblUploadTotalFiles.setText(phrase);
		}
	}
	
	public void setTotalSizeUpload(long total) {
		String phrase = "Total File Size: ";
		String postfix = " (kb)";
		if(total > 0) {
			Long ltotal = new Long(total/1024);
			lblUploadTotalSize.setText(phrase.concat(ltotal.toString()).concat(postfix));
		}
		else {
			lblUploadTotalSize.setText(phrase);
		}
	}
	
	public void updateFileStats(DefaultTableModel tm) {

   	 	int numRows = tm.getRowCount();
   	 	long totalSize = 0;
   	 	int totalFiles = 0;
   	 
   	 	for(int i=0; i<numRows; i++) {
   	 		// only count if it is currently checked for upload
   	 		//if((Boolean)tm.getValueAt(i, 1)) {
   	 			String fileName = (String)tm.getValueAt(i, 0);
   	 			if(fileName != null) {
   	 				File file = new File(fileName);
   	 				if(file.exists()) {
   	 					if(file.isDirectory()) {
   	 						totalFiles+=FileUtils.listFiles(file, null, true).size();
   	 						totalSize+=FileUtils.sizeOfDirectory(file);
   	 					}
   	 					else {
   	 						totalFiles++;
   	 						totalSize+=file.length();
   	 					}
   	 				}
   	 			}
   	 		//}
   	 	}
   	 	setTotalFileUpload(totalFiles);
   	 	setTotalSizeUpload(totalSize);
    }
	
	@Override
	public void tableChanged(TableModelEvent tme) {
		int type = tme.getType();
		int row = tme.getFirstRow();
		int column = tme.getColumn();
		DefaultTableModel tm = (DefaultTableModel)tblUploadTable.getModel();
		
		if(type == TableModelEvent.UPDATE && column == 2) {
			//log.info("canceling or removing??");
			if(isTransferInProgress()) {
				this.cancelTransfer();
			}
			else {
				tm.removeRow(row);
				tm.fireTableRowsDeleted(row, row);
			}
		}
		else {
			updateFileStats(tm);	
		}
	}

    public IRODSTree getIrodsTree() {
        return irodsTree;
    }

    public iDropLiteCore getiDropCore() {
        return iDropCore;
    }

    public LocalFileTree getFileTree() {
        return fileTree;
    }

    public Outline getTreeStagingResource() {
        return irodsTree;
    }
     

//    private void bntRefreshIrodsTreeActionPerformed(java.awt.event.ActionEvent evt) {
//    	// FIX ME: get current view of irods tree and pass to buildTargetTree
//        buildTargetTree();
//    }
    

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlTabbedMain = new javax.swing.JTabbedPane();
        pnlMain = new javax.swing.JPanel();
        pnlMainToolBar = new javax.swing.JPanel();
        pnlToolBarSizer = new javax.swing.JPanel();
        pnlLocalToggleSizer = new javax.swing.JPanel();
        btnToggleLocalView = new javax.swing.JToggleButton();
        pnlSearchSizer = new javax.swing.JPanel();
        pnlPlaceholder = new javax.swing.JPanel();
        pnlMainTrees = new javax.swing.JPanel();
        pnlSplitPaneLocalRemote = new javax.swing.JSplitPane();
        pnlLocalTree = new javax.swing.JPanel();
        pnlLocalRoots = new javax.swing.JPanel();
        pnlLocalRefreshButton = new javax.swing.JPanel();
        btnLocalRefresh = new javax.swing.JButton();
        scrollLocalDrives = new javax.swing.JScrollPane();
        lstLocalDrives = new javax.swing.JList();
        pnlDrivesFiller = new javax.swing.JPanel();
        scrollLocalFileTree = new javax.swing.JScrollPane();
        pnlIrodsTree = new javax.swing.JPanel();
        tabIrodsViews = new javax.swing.JTabbedPane();
        pnlIrodsTreeView = new javax.swing.JPanel();
        pnlIrodsTreeViewButtons = new javax.swing.JPanel();
        btnIrodsTreeRefresh = new javax.swing.JButton();
        scrIrodsTreeView = new javax.swing.JScrollPane();
        pnlIrodsSearch = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlIdropBottom = new javax.swing.JPanel();
        pnlTransferOverview = new javax.swing.JPanel();
        pnlTransferStatus = new javax.swing.JPanel();
        lblTransferStatusMessage = new javax.swing.JLabel();
        pnlTransferType = new javax.swing.JPanel();
        lblTransferTypeLabel = new javax.swing.JLabel();
        lblTransferType = new javax.swing.JLabel();
        pnlTransferFileCounts = new javax.swing.JPanel();
        lblTransferFilesCounts = new javax.swing.JLabel();
        pnlTransferByteCounts = new javax.swing.JPanel();
        lblTransferByteCounts = new javax.swing.JLabel();
        progressIntraFile = new javax.swing.JProgressBar();
        pnlTransferFileInfo = new javax.swing.JPanel();
        lblCurrentFileLabel = new javax.swing.JLabel();
        lblCurrentFile = new javax.swing.JLabel();
        transferStatusProgressBar = new javax.swing.JProgressBar();
        pnlOperationMode2 = new javax.swing.JPanel();
        pnlTitlleBar = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnlUploadTrees = new javax.swing.JPanel();
        pnlUploadLocalTree = new javax.swing.JPanel();
        pnlUploadRoots = new javax.swing.JPanel();
        scrollUploadLocalDrives = new javax.swing.JScrollPane();
        lstUploadLocalDrives = new javax.swing.JList();
        pnlUploadRefreshButton = new javax.swing.JPanel();
        btnUploadLocalRefresh = new javax.swing.JButton();
        pnlUploadLocalDrivesFiller = new javax.swing.JPanel();
        scrollUploadLocalTree = new javax.swing.JScrollPane();
        pnlUploadCenterTools = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnUploadMove = new javax.swing.JButton();
        btnUploadCancel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        pnlUploadTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblUploadTable = new javax.swing.JTable();
        pnlIRODSUploadDest = new javax.swing.JPanel();
        pnlIRODSUploadBrowse = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnBrowseIRODSUploadDest = new javax.swing.JButton();
        txtIRODSUploadDest = new javax.swing.JTextField();
        pnlUploadToolbar = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlUploadToolStatus = new javax.swing.JPanel();
        lblUploadTotalFiles = new javax.swing.JLabel();
        lblUploadTotalSize = new javax.swing.JLabel();
        btnUploadBeginImport = new javax.swing.JButton();
        pnlOperationMode3 = new javax.swing.JPanel();
        pnlProgressTable = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProgress = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(250, 200));
        setPreferredSize(new java.awt.Dimension(700, 450));

        pnlTabbedMain.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        pnlTabbedMain.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlTabbedMain.setPreferredSize(new java.awt.Dimension(700, 450));

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlMain.setPreferredSize(new java.awt.Dimension(700, 450));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlMainToolBar.setMinimumSize(new java.awt.Dimension(250, 30));
        pnlMainToolBar.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlMainToolBar.setLayout(new java.awt.BorderLayout());

        pnlToolBarSizer.setPreferredSize(new java.awt.Dimension(632, 50));
        pnlToolBarSizer.setSize(new java.awt.Dimension(100, 50));
        pnlToolBarSizer.setLayout(new java.awt.BorderLayout());

        pnlLocalToggleSizer.setPreferredSize(new java.awt.Dimension(150, 50));

        btnToggleLocalView.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnToggleLocalView.text")); // NOI18N
        btnToggleLocalView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleLocalViewActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlLocalToggleSizerLayout = new org.jdesktop.layout.GroupLayout(pnlLocalToggleSizer);
        pnlLocalToggleSizer.setLayout(pnlLocalToggleSizerLayout);
        pnlLocalToggleSizerLayout.setHorizontalGroup(
            pnlLocalToggleSizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlLocalToggleSizerLayout.createSequentialGroup()
                .addContainerGap()
                .add(btnToggleLocalView)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlLocalToggleSizerLayout.setVerticalGroup(
            pnlLocalToggleSizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlLocalToggleSizerLayout.createSequentialGroup()
                .add(btnToggleLocalView)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pnlToolBarSizer.add(pnlLocalToggleSizer, java.awt.BorderLayout.WEST);

        pnlSearchSizer.setPreferredSize(new java.awt.Dimension(300, 50));

        org.jdesktop.layout.GroupLayout pnlSearchSizerLayout = new org.jdesktop.layout.GroupLayout(pnlSearchSizer);
        pnlSearchSizer.setLayout(pnlSearchSizerLayout);
        pnlSearchSizerLayout.setHorizontalGroup(
            pnlSearchSizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 396, Short.MAX_VALUE)
        );
        pnlSearchSizerLayout.setVerticalGroup(
            pnlSearchSizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );

        pnlToolBarSizer.add(pnlSearchSizer, java.awt.BorderLayout.CENTER);

        pnlPlaceholder.setPreferredSize(new java.awt.Dimension(150, 50));

        org.jdesktop.layout.GroupLayout pnlPlaceholderLayout = new org.jdesktop.layout.GroupLayout(pnlPlaceholder);
        pnlPlaceholder.setLayout(pnlPlaceholderLayout);
        pnlPlaceholderLayout.setHorizontalGroup(
            pnlPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        pnlPlaceholderLayout.setVerticalGroup(
            pnlPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );

        pnlToolBarSizer.add(pnlPlaceholder, java.awt.BorderLayout.EAST);

        pnlMainToolBar.add(pnlToolBarSizer, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlMainToolBar, java.awt.BorderLayout.NORTH);

        pnlMainTrees.setMinimumSize(new java.awt.Dimension(250, 150));
        pnlMainTrees.setPreferredSize(new java.awt.Dimension(700, 375));
        pnlMainTrees.setLayout(new javax.swing.BoxLayout(pnlMainTrees, javax.swing.BoxLayout.LINE_AXIS));

        pnlSplitPaneLocalRemote.setDividerLocation(300);
        pnlSplitPaneLocalRemote.setMinimumSize(new java.awt.Dimension(250, 150));
        pnlSplitPaneLocalRemote.setPreferredSize(new java.awt.Dimension(700, 375));

        pnlLocalTree.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlLocalTree.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalTree.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlLocalTree.setLayout(new java.awt.BorderLayout());

        pnlLocalRoots.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalRoots.setPreferredSize(new java.awt.Dimension(295, 100));
        pnlLocalRoots.setRequestFocusEnabled(false);
        pnlLocalRoots.setLayout(new java.awt.BorderLayout());

        pnlLocalRefreshButton.setMaximumSize(new java.awt.Dimension(1000, 34));
        pnlLocalRefreshButton.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalRefreshButton.setPreferredSize(new java.awt.Dimension(0, 34));
        pnlLocalRefreshButton.setRequestFocusEnabled(false);

        btnLocalRefresh.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnLocalRefresh.text")); // NOI18N
        pnlLocalRefreshButton.add(btnLocalRefresh);

        pnlLocalRoots.add(pnlLocalRefreshButton, java.awt.BorderLayout.NORTH);

        scrollLocalDrives.setMaximumSize(null);
        scrollLocalDrives.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollLocalDrives.setPreferredSize(new java.awt.Dimension(285, 61));
        scrollLocalDrives.setRequestFocusEnabled(false);

        lstLocalDrives.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstLocalDrives.setMaximumSize(null);
        lstLocalDrives.setPreferredSize(new java.awt.Dimension(100, 75));
        lstLocalDrives.setVisibleRowCount(4);
        scrollLocalDrives.setViewportView(lstLocalDrives);

        pnlLocalRoots.add(scrollLocalDrives, java.awt.BorderLayout.CENTER);

        pnlDrivesFiller.setPreferredSize(new java.awt.Dimension(292, 5));

        org.jdesktop.layout.GroupLayout pnlDrivesFillerLayout = new org.jdesktop.layout.GroupLayout(pnlDrivesFiller);
        pnlDrivesFiller.setLayout(pnlDrivesFillerLayout);
        pnlDrivesFillerLayout.setHorizontalGroup(
            pnlDrivesFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 292, Short.MAX_VALUE)
        );
        pnlDrivesFillerLayout.setVerticalGroup(
            pnlDrivesFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 5, Short.MAX_VALUE)
        );

        pnlLocalRoots.add(pnlDrivesFiller, java.awt.BorderLayout.SOUTH);

        pnlLocalTree.add(pnlLocalRoots, java.awt.BorderLayout.NORTH);

        scrollLocalFileTree.setMaximumSize(null);
        scrollLocalFileTree.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollLocalFileTree.setPreferredSize(new java.awt.Dimension(298, 400));
        scrollLocalFileTree.setRequestFocusEnabled(false);
        pnlLocalTree.add(scrollLocalFileTree, java.awt.BorderLayout.CENTER);

        pnlSplitPaneLocalRemote.setLeftComponent(pnlLocalTree);

        pnlIrodsTree.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        pnlIrodsTreeView.setLayout(new java.awt.BorderLayout());

        btnIrodsTreeRefresh.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnIrodsTreeRefresh.text")); // NOI18N
        btnIrodsTreeRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIrodsTreeRefreshActionPerformed(evt);
            }
        });
        pnlIrodsTreeViewButtons.add(btnIrodsTreeRefresh);

        pnlIrodsTreeView.add(pnlIrodsTreeViewButtons, java.awt.BorderLayout.NORTH);
        pnlIrodsTreeView.add(scrIrodsTreeView, java.awt.BorderLayout.CENTER);

        tabIrodsViews.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlIrodsTreeView.TabConstraints.tabTitle"), pnlIrodsTreeView); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIrodsSearchLayout = new org.jdesktop.layout.GroupLayout(pnlIrodsSearch);
        pnlIrodsSearch.setLayout(pnlIrodsSearchLayout);
        pnlIrodsSearchLayout.setHorizontalGroup(
            pnlIrodsSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIrodsSearchLayout.createSequentialGroup()
                .add(83, 83, 83)
                .add(jLabel1)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        pnlIrodsSearchLayout.setVerticalGroup(
            pnlIrodsSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIrodsSearchLayout.createSequentialGroup()
                .add(84, 84, 84)
                .add(jLabel1)
                .addContainerGap(176, Short.MAX_VALUE))
        );

        tabIrodsViews.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlIrodsSearch.TabConstraints.tabTitle"), pnlIrodsSearch); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIrodsTreeLayout = new org.jdesktop.layout.GroupLayout(pnlIrodsTree);
        pnlIrodsTree.setLayout(pnlIrodsTreeLayout);
        pnlIrodsTreeLayout.setHorizontalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        );
        pnlIrodsTreeLayout.setVerticalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        pnlSplitPaneLocalRemote.setRightComponent(pnlIrodsTree);

        pnlMainTrees.add(pnlSplitPaneLocalRemote);

        pnlMain.add(pnlMainTrees, java.awt.BorderLayout.CENTER);

        pnlIdropBottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlIdropBottom.setToolTipText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlIdropBottom.toolTipText")); // NOI18N
        pnlIdropBottom.setLayout(new java.awt.GridBagLayout());

        pnlTransferOverview.setLayout(new java.awt.BorderLayout());

        pnlTransferStatus.setLayout(new java.awt.GridBagLayout());

        lblTransferStatusMessage.setForeground(new java.awt.Color(0, 0, 255));
        pnlTransferStatus.add(lblTransferStatusMessage, new java.awt.GridBagConstraints());

        lblTransferTypeLabel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferTypeLabel.text")); // NOI18N
        pnlTransferType.add(lblTransferTypeLabel);

        lblTransferType.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferType.text")); // NOI18N
        pnlTransferType.add(lblTransferType);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlTransferStatus.add(pnlTransferType, gridBagConstraints);

        lblTransferFilesCounts.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferFilesCounts.text")); // NOI18N
        pnlTransferFileCounts.add(lblTransferFilesCounts);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlTransferStatus.add(pnlTransferFileCounts, gridBagConstraints);

        pnlTransferByteCounts.setLayout(new java.awt.GridBagLayout());

        lblTransferByteCounts.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferByteCounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlTransferByteCounts.add(lblTransferByteCounts, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlTransferByteCounts.add(progressIntraFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlTransferStatus.add(pnlTransferByteCounts, gridBagConstraints);

        pnlTransferOverview.add(pnlTransferStatus, java.awt.BorderLayout.NORTH);

        pnlTransferFileInfo.setLayout(new java.awt.GridBagLayout());

        lblCurrentFileLabel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblCurrentFileLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferFileInfo.add(lblCurrentFileLabel, gridBagConstraints);

        lblCurrentFile.setMaximumSize(new java.awt.Dimension(999, 999));
        lblCurrentFile.setMinimumSize(new java.awt.Dimension(30, 10));
        lblCurrentFile.setPreferredSize(new java.awt.Dimension(300, 20));
        pnlTransferFileInfo.add(lblCurrentFile, new java.awt.GridBagConstraints());

        pnlTransferOverview.add(pnlTransferFileInfo, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 35;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlIdropBottom.add(pnlTransferOverview, gridBagConstraints);

        transferStatusProgressBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        transferStatusProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8.0;
        pnlIdropBottom.add(transferStatusProgressBar, gridBagConstraints);

        pnlMain.add(pnlIdropBottom, java.awt.BorderLayout.SOUTH);

        pnlTabbedMain.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlMain.TabConstraints.tabTitle"), pnlMain); // NOI18N

        pnlOperationMode2.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlOperationMode2.setPreferredSize(new java.awt.Dimension(700, 450));
        pnlOperationMode2.setLayout(new java.awt.BorderLayout());

        pnlTitlleBar.setPreferredSize(new java.awt.Dimension(700, 24));
        pnlTitlleBar.setLayout(new java.awt.GridBagLayout());

        jPanel5.setMinimumSize(new java.awt.Dimension(150, 27));
        jPanel5.setPreferredSize(new java.awt.Dimension(285, 20));

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel3.text")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 17));
        jPanel5.add(jLabel3);
        jLabel3.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel3.AccessibleContext.accessibleName_1")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel5, gridBagConstraints);

        jPanel7.setMinimumSize(new java.awt.Dimension(80, 0));
        jPanel7.setPreferredSize(new java.awt.Dimension(80, 24));

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 24, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel7, gridBagConstraints);

        jPanel6.setMinimumSize(new java.awt.Dimension(252, 27));
        jPanel6.setPreferredSize(new java.awt.Dimension(310, 17));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel4.text")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(200, 17));
        jPanel6.add(jLabel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.6;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel6, gridBagConstraints);

        pnlOperationMode2.add(pnlTitlleBar, java.awt.BorderLayout.NORTH);

        pnlUploadTrees.setMinimumSize(new java.awt.Dimension(250, 160));
        pnlUploadTrees.setPreferredSize(new java.awt.Dimension(700, 385));
        pnlUploadTrees.setLayout(new java.awt.GridBagLayout());

        pnlUploadLocalTree.setMinimumSize(new java.awt.Dimension(150, 0));
        pnlUploadLocalTree.setPreferredSize(new java.awt.Dimension(285, 380));
        pnlUploadLocalTree.setLayout(new java.awt.BorderLayout());

        pnlUploadRoots.setMinimumSize(new java.awt.Dimension(280, 35));
        pnlUploadRoots.setPreferredSize(new java.awt.Dimension(280, 100));
        pnlUploadRoots.setRequestFocusEnabled(false);
        pnlUploadRoots.setLayout(new java.awt.BorderLayout());

        scrollUploadLocalDrives.setBorder(null);
        scrollUploadLocalDrives.setMaximumSize(null);
        scrollUploadLocalDrives.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollUploadLocalDrives.setPreferredSize(new java.awt.Dimension(256, 61));

        lstUploadLocalDrives.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lstUploadLocalDrives.setMaximumSize(null);
        lstUploadLocalDrives.setPreferredSize(new java.awt.Dimension(256, 100));
        lstUploadLocalDrives.setVisibleRowCount(4);
        scrollUploadLocalDrives.setViewportView(lstUploadLocalDrives);

        pnlUploadRoots.add(scrollUploadLocalDrives, java.awt.BorderLayout.CENTER);

        pnlUploadRefreshButton.setMaximumSize(new java.awt.Dimension(1000, 34));
        pnlUploadRefreshButton.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlUploadRefreshButton.setPreferredSize(new java.awt.Dimension(101, 34));
        pnlUploadRefreshButton.setRequestFocusEnabled(false);

        btnUploadLocalRefresh.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadLocalRefresh.text")); // NOI18N
        btnUploadLocalRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadLocalRefreshActionPerformed(evt);
            }
        });
        pnlUploadRefreshButton.add(btnUploadLocalRefresh);

        pnlUploadRoots.add(pnlUploadRefreshButton, java.awt.BorderLayout.NORTH);

        pnlUploadLocalDrivesFiller.setMinimumSize(new java.awt.Dimension(10, 5));
        pnlUploadLocalDrivesFiller.setPreferredSize(new java.awt.Dimension(10, 5));

        org.jdesktop.layout.GroupLayout pnlUploadLocalDrivesFillerLayout = new org.jdesktop.layout.GroupLayout(pnlUploadLocalDrivesFiller);
        pnlUploadLocalDrivesFiller.setLayout(pnlUploadLocalDrivesFillerLayout);
        pnlUploadLocalDrivesFillerLayout.setHorizontalGroup(
            pnlUploadLocalDrivesFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 298, Short.MAX_VALUE)
        );
        pnlUploadLocalDrivesFillerLayout.setVerticalGroup(
            pnlUploadLocalDrivesFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 5, Short.MAX_VALUE)
        );

        pnlUploadRoots.add(pnlUploadLocalDrivesFiller, java.awt.BorderLayout.SOUTH);

        pnlUploadLocalTree.add(pnlUploadRoots, java.awt.BorderLayout.NORTH);

        scrollUploadLocalTree.setMaximumSize(null);
        scrollUploadLocalTree.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollUploadLocalTree.setPreferredSize(new java.awt.Dimension(283, 400));
        pnlUploadLocalTree.add(scrollUploadLocalTree, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadLocalTree, gridBagConstraints);

        pnlUploadCenterTools.setAutoscrolls(true);
        pnlUploadCenterTools.setMaximumSize(new java.awt.Dimension(80, 32767));
        pnlUploadCenterTools.setMinimumSize(new java.awt.Dimension(80, 100));
        pnlUploadCenterTools.setPreferredSize(new java.awt.Dimension(80, 380));
        pnlUploadCenterTools.setRequestFocusEnabled(false);
        pnlUploadCenterTools.setLayout(new java.awt.GridBagLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(80, 100));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlUploadCenterTools.add(jPanel2, gridBagConstraints);

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 80));
        jPanel3.setMinimumSize(new java.awt.Dimension(80, 80));
        jPanel3.setPreferredSize(new java.awt.Dimension(80, 80));
        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        btnUploadMove.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadMove.text")); // NOI18N
        btnUploadMove.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnUploadMove.setMaximumSize(new java.awt.Dimension(75, 50));
        btnUploadMove.setMinimumSize(new java.awt.Dimension(75, 50));
        btnUploadMove.setPreferredSize(new java.awt.Dimension(80, 40));
        btnUploadMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadMoveActionPerformed(evt);
            }
        });
        jPanel3.add(btnUploadMove);

        btnUploadCancel.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        btnUploadCancel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadCancel.text")); // NOI18N
        btnUploadCancel.setMinimumSize(new java.awt.Dimension(50, 40));
        btnUploadCancel.setPreferredSize(new java.awt.Dimension(80, 40));
        btnUploadCancel.setRequestFocusEnabled(false);
        btnUploadCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadCancelActionPerformed(evt);
            }
        });
        jPanel3.add(btnUploadCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlUploadCenterTools.add(jPanel3, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(80, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(80, 100));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlUploadCenterTools.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadCenterTools, gridBagConstraints);

        pnlUploadTable.setPreferredSize(new java.awt.Dimension(310, 380));
        pnlUploadTable.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(275, 370));

        tblUploadTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "Upload Status", "Cancel/Remove"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUploadTable.setBounds(new java.awt.Rectangle(0, 0, 350, 64));
        tblUploadTable.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        tblUploadTable.setGridColor(new java.awt.Color(204, 204, 204));
        tblUploadTable.setPreferredSize(new java.awt.Dimension(285, 380));
        tblUploadTable.setRowMargin(2);
        tblUploadTable.setShowGrid(false);
        jScrollPane3.setViewportView(tblUploadTable);

        pnlUploadTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        pnlIRODSUploadDest.setPreferredSize(new java.awt.Dimension(163, 54));
        pnlIRODSUploadDest.setLayout(new java.awt.BorderLayout());

        pnlIRODSUploadBrowse.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnlIRODSUploadBrowse.add(jLabel2, java.awt.BorderLayout.WEST);

        btnBrowseIRODSUploadDest.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnBrowseIRODSUploadDest.text")); // NOI18N
        btnBrowseIRODSUploadDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseIRODSUploadDestActionPerformed(evt);
            }
        });
        pnlIRODSUploadBrowse.add(btnBrowseIRODSUploadDest, java.awt.BorderLayout.EAST);

        pnlIRODSUploadDest.add(pnlIRODSUploadBrowse, java.awt.BorderLayout.NORTH);

        txtIRODSUploadDest.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.txtIRODSUploadDest.text")); // NOI18N
        txtIRODSUploadDest.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtIRODSUploadDest.setDragEnabled(false);
        txtIRODSUploadDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIRODSUploadDestActionPerformed(evt);
            }
        });
        pnlIRODSUploadDest.add(txtIRODSUploadDest, java.awt.BorderLayout.CENTER);

        pnlUploadTable.add(pnlIRODSUploadDest, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.6;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadTable, gridBagConstraints);

        pnlOperationMode2.add(pnlUploadTrees, java.awt.BorderLayout.CENTER);

        pnlUploadToolbar.setMaximumSize(new java.awt.Dimension(32767, 60));
        pnlUploadToolbar.setMinimumSize(new java.awt.Dimension(250, 30));
        pnlUploadToolbar.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlUploadToolbar.setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(215, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(275, 40));
        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(new java.awt.GridLayout(2, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.2;
        pnlUploadToolbar.add(jPanel1, gridBagConstraints);

        pnlUploadToolStatus.setMinimumSize(new java.awt.Dimension(175, 32));
        pnlUploadToolStatus.setPreferredSize(new java.awt.Dimension(175, 34));
        pnlUploadToolStatus.setRequestFocusEnabled(false);
        pnlUploadToolStatus.setLayout(new java.awt.BorderLayout());

        lblUploadTotalFiles.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblUploadTotalFiles.text")); // NOI18N
        pnlUploadToolStatus.add(lblUploadTotalFiles, java.awt.BorderLayout.NORTH);

        lblUploadTotalSize.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblUploadTotalSize.text")); // NOI18N
        pnlUploadToolStatus.add(lblUploadTotalSize, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        pnlUploadToolbar.add(pnlUploadToolStatus, gridBagConstraints);

        btnUploadBeginImport.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadBeginImport.text")); // NOI18N
        btnUploadBeginImport.setEnabled(false);
        btnUploadBeginImport.setMaximumSize(new java.awt.Dimension(115, 29));
        btnUploadBeginImport.setMinimumSize(new java.awt.Dimension(115, 29));
        btnUploadBeginImport.setPreferredSize(new java.awt.Dimension(115, 29));
        btnUploadBeginImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadBeginImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        pnlUploadToolbar.add(btnUploadBeginImport, gridBagConstraints);

        pnlOperationMode2.add(pnlUploadToolbar, java.awt.BorderLayout.SOUTH);

        pnlTabbedMain.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlOperationMode2.TabConstraints.tabTitle"), pnlOperationMode2); // NOI18N

        pnlOperationMode3.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlOperationMode3.setPreferredSize(new java.awt.Dimension(700, 450));
        pnlOperationMode3.setLayout(new java.awt.BorderLayout());

        pnlProgressTable.setLayout(new java.awt.BorderLayout());

        tblProgress.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProgress.setShowGrid(true);
        jScrollPane4.setViewportView(tblProgress);

        pnlProgressTable.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        pnlOperationMode3.add(pnlProgressTable, java.awt.BorderLayout.CENTER);

        pnlTabbedMain.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlOperationMode3.TabConstraints.tabTitle"), pnlOperationMode3); // NOI18N

        getContentPane().add(pnlTabbedMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnIrodsTreeRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrodsTreeRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIrodsTreeRefreshActionPerformed

    private void btnUploadMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadMoveActionPerformed
        
        TreePath [] paths = fileUploadTree.getSelectionPaths();
        for(TreePath path: paths) {
        	DefaultTableModel tm = (DefaultTableModel)tblUploadTable.getModel();
        	Object [] rowData = new Object[3];
        	rowData[0] = LocalFileUtils.makeLocalFilePath(path);
        	rowData[1] = 0;
        	rowData[2] = Boolean.TRUE;
        	tm.addRow(rowData);
        }

    }//GEN-LAST:event_btnUploadMoveActionPerformed

    private void btnToggleLocalViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleLocalViewActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                pnlLocalTree.setVisible(btnToggleLocalView.isSelected());
                if (pnlLocalTree.isVisible()) {
                    pnlSplitPaneLocalRemote.setDividerLocation(0.3d);
                }
            }
        });
    }//GEN-LAST:event_btnToggleLocalViewActionPerformed

    private void btnUploadBeginImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadBeginImportActionPerformed
    
    	PutTransferRunner currentTransferRunner = null;
    	final List<File> sourceFiles = new ArrayList<File>();
    	
        // make sure IRODS destination is legal
        final String targetPath = txtIRODSUploadDest.getText();

        try {
        	IRODSFileService irodsFS = new IRODSFileService(iDropCore.getIrodsAccount(), IRODSFileSystem.instance());
        	IRODSFile ifile = irodsFS.getIRODSFileForPath(targetPath);
        	if(!ifile.isDirectory()) {
        		JOptionPane.showMessageDialog(this, "Please enter a valid IRODS destination for upload.");
        		return;
        	}
        }
        catch(Exception ex)  {
        	JOptionPane.showMessageDialog(this, "Please enter a valid IRODS destination for upload.");
        	return;
        }
        
        // now go through and process selected import files from table
        if(!isTransferInProgress()) {
        	
        	// collect list of files in the table
            int rows = tblUploadTable.getRowCount();            
            for(int row=0; row<rows; row++) {
            	// only select files checked for import
            	//if((Boolean)tblUploadTable.getValueAt(row, 1)) {
            		sourceFiles.add(new File((String)tblUploadTable.getValueAt(row, 0)));
            	//}
            }
            	
            // set Upload button test to Cancel
            try {
                currentTransferRunner = new PutTransferRunner(applet, targetPath, sourceFiles, iDropCore.getTransferControlBlock());
                final Thread transferThread = new Thread(currentTransferRunner);
                log.info("launching transfer thread");
                transferThread.start();
            } catch (Exception e) {
            	log.error("exception choosings iRODS file");
                throw new IdropRuntimeException("exception choosing irods file", e);
            } finally {
            	iDropCore.getIrodsFileSystem().closeAndEatExceptions();
            }
        }
        else {
        	// message??
        }
        
    }//GEN-LAST:event_btnUploadBeginImportActionPerformed

    private void btnUploadCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadCancelActionPerformed
    	DefaultTableModel tm = (DefaultTableModel)tblUploadTable.getModel();
    	int numRows = tm.getRowCount();
    	tm.getDataVector().removeAllElements();
    	if(numRows > 0) {
    		tm.getDataVector().removeAllElements();
    		tm.fireTableRowsDeleted(0, numRows-1);
    	}
    }//GEN-LAST:event_btnUploadCancelActionPerformed

    private void btnUploadLocalRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadLocalRefreshActionPerformed
    	setUpUploadLocalFileSelectTree();
    }//GEN-LAST:event_btnUploadLocalRefreshActionPerformed

    private void txtIRODSUploadDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIRODSUploadDestActionPerformed
        btnUploadBeginImport.setEnabled(txtIRODSUploadDest.getText().length() > 0);
    }//GEN-LAST:event_txtIRODSUploadDestActionPerformed

    private void btnBrowseIRODSUploadDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseIRODSUploadDestActionPerformed
    	IRODSFinderDialog finderDialog = new IRODSFinderDialog(true, iDropCore);
        finderDialog.setVisible(true);
        String targetPath = finderDialog.getSelectedAbsolutePath();
        if(targetPath != null) {
            //then do stuff
            log.info("upload drop target selected:{}", targetPath);
            txtIRODSUploadDest.setText(targetPath);
            btnUploadBeginImport.setEnabled(true);
        }
        finderDialog.dispose();
    }//GEN-LAST:event_btnBrowseIRODSUploadDestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseIRODSUploadDest;
    private javax.swing.JButton btnIrodsTreeRefresh;
    private javax.swing.JButton btnLocalRefresh;
    private javax.swing.JToggleButton btnToggleLocalView;
    private javax.swing.JButton btnUploadBeginImport;
    private javax.swing.JButton btnUploadCancel;
    private javax.swing.JButton btnUploadLocalRefresh;
    private javax.swing.JButton btnUploadMove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCurrentFile;
    private javax.swing.JLabel lblCurrentFileLabel;
    private javax.swing.JLabel lblTransferByteCounts;
    private javax.swing.JLabel lblTransferFilesCounts;
    private javax.swing.JLabel lblTransferStatusMessage;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JLabel lblTransferTypeLabel;
    private javax.swing.JLabel lblUploadTotalFiles;
    private javax.swing.JLabel lblUploadTotalSize;
    private javax.swing.JList lstLocalDrives;
    private javax.swing.JList lstUploadLocalDrives;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlIRODSUploadBrowse;
    private javax.swing.JPanel pnlIRODSUploadDest;
    private javax.swing.JPanel pnlIdropBottom;
    private javax.swing.JPanel pnlIrodsSearch;
    private javax.swing.JPanel pnlIrodsTree;
    private javax.swing.JPanel pnlIrodsTreeView;
    private javax.swing.JPanel pnlIrodsTreeViewButtons;
    private javax.swing.JPanel pnlLocalRefreshButton;
    private javax.swing.JPanel pnlLocalRoots;
    private javax.swing.JPanel pnlLocalToggleSizer;
    private javax.swing.JPanel pnlLocalTree;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlMainToolBar;
    private javax.swing.JPanel pnlMainTrees;
    private javax.swing.JPanel pnlOperationMode2;
    private javax.swing.JPanel pnlOperationMode3;
    private javax.swing.JPanel pnlPlaceholder;
    private javax.swing.JPanel pnlProgressTable;
    private javax.swing.JPanel pnlSearchSizer;
    private javax.swing.JSplitPane pnlSplitPaneLocalRemote;
    private javax.swing.JTabbedPane pnlTabbedMain;
    private javax.swing.JPanel pnlTitlleBar;
    private javax.swing.JPanel pnlToolBarSizer;
    private javax.swing.JPanel pnlTransferByteCounts;
    private javax.swing.JPanel pnlTransferFileCounts;
    private javax.swing.JPanel pnlTransferFileInfo;
    private javax.swing.JPanel pnlTransferOverview;
    private javax.swing.JPanel pnlTransferStatus;
    private javax.swing.JPanel pnlTransferType;
    private javax.swing.JPanel pnlUploadCenterTools;
    private javax.swing.JPanel pnlUploadLocalDrivesFiller;
    private javax.swing.JPanel pnlUploadLocalTree;
    private javax.swing.JPanel pnlUploadRefreshButton;
    private javax.swing.JPanel pnlUploadRoots;
    private javax.swing.JPanel pnlUploadTable;
    private javax.swing.JPanel pnlUploadToolStatus;
    private javax.swing.JPanel pnlUploadToolbar;
    private javax.swing.JPanel pnlUploadTrees;
    private javax.swing.JProgressBar progressIntraFile;
    private javax.swing.JScrollPane scrIrodsTreeView;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JScrollPane scrollUploadLocalDrives;
    private javax.swing.JScrollPane scrollUploadLocalTree;
    private javax.swing.JTabbedPane tabIrodsViews;
    private javax.swing.JTable tblProgress;
    private javax.swing.JTable tblUploadTable;
    private javax.swing.JProgressBar transferStatusProgressBar;
    private javax.swing.JTextField txtIRODSUploadDest;
    // End of variables declaration//GEN-END:variables

}
