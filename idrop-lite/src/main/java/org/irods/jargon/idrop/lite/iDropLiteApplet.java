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
import java.awt.dnd.DropTarget;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
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
public class iDropLiteApplet extends javax.swing.JApplet implements TransferStatusCallbackListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);
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

    /** Initializes the applet NewJApplet */
    public void init() {
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
    
    
    // NEED T) REFACTOR ALL OF THE FOLLOWING DUPLICATION!!!!!!!!
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
    	//set FillsViewportHeight so user can drop onto an empty table
    	tblUploadTable.setFillsViewportHeight(true);
    	
    	tblUploadTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	tblUploadTable.setDropMode(DropMode.INSERT_ROWS);
    	tblUploadTable.setDragEnabled(true);
    	tblUploadTable.setTransferHandler(new UploadTableTransferHandler());
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

    	this.pbTransferStatus.setMaximum((int) ts.getTotalSize());
    	this.pbTransferStatus.setValue((int) ts.getBytesTransfered());
    	
    	this.pbUploadTransferStatus.setMaximum((int) ts.getTotalSize());
    	this.pbUploadTransferStatus.setValue((int) ts.getBytesTransfered());
    	//this.pbTransferStatus.setMaximum((int) ts.getTotalFilesToTransfer());
    	//this.pbTransferStatus.setValue((int) ts.getTotalFilesTransferredSoFar());
    	log.info("transfer status callback to iDropLiteApplet:{}", ts);

    	java.awt.EventQueue.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    			// on initiation, clear and reset the status bar info
    			/*
            	lblTransferFilesCounts.setText("Files: " + ts.getTotalFilesTransferredSoFar() + " / "
                    + ts.getTotalFilesToTransfer());
            	lblTransferByteCounts.setText("Current File (kb):" + (ts.getBytesTransfered() / 1024) + " / "
                    + (ts.getTotalSize() / 1024));
    			 */
    			lblTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
    			
    			lblUploadTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
    		}
    	});

	}


	@Override
	public void overallStatusCallback(final TransferStatus ts) {

		IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree.getModel();
        try {
            irodsTreeModel.notifyCompletionOfOperation(irodsTree, ts);
            // if a get callback on completion, notify the local tree model
            if (ts.getTransferType() == TransferStatus.TransferType.GET
                    && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                ((LocalFileSystemModel) getFileTree().getModel()).notifyCompletionOfOperation(getFileTree(), ts);
            }
        } catch (IdropException ex) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
            this.showIdropException(ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // on initiation, clear and reset the status bar info
            	/*
                lblTransferType.setText(ts.getTransferType().name());
                lblTransferFilesCounts.setText("Files: " + ts.getTotalFilesTransferredSoFar() + " / "
                        + ts.getTotalFilesToTransfer());
                lblTransferByteCounts.setText("Bytes (kb):" + (ts.getBytesTransfered() / 1024) + " / "
                        + (ts.getTotalSize() / 1024));
                */
            	lblTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
            	
            	lblUploadTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
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

    private void bntRefreshIrodsTreeActionPerformed(java.awt.event.ActionEvent evt) {
    	// FIX ME: get current view of irods tree and pass to buildTargetTree
        buildTargetTree();
    }
    

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
        pnlMainStatus = new javax.swing.JPanel();
        lblTransferFileName = new javax.swing.JLabel();
        pbTransferStatus = new javax.swing.JProgressBar();
        pnlOperationMode2 = new javax.swing.JPanel();
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
        btnUploadMove = new javax.swing.JButton();
        pnlUploadTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblUploadTable = new javax.swing.JTable();
        pnlIRODSUploadDest = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtIRODSUploadDest = new javax.swing.JTextField();
        pnlUploadToolbar = new javax.swing.JPanel();
        pnlUploadToolStatus = new javax.swing.JPanel();
        lblUploadTransferFileName = new javax.swing.JLabel();
        pbUploadTransferStatus = new javax.swing.JProgressBar();
        btnUploadBeginImport = new javax.swing.JButton();
        btnUploadCancel = new javax.swing.JButton();
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
            .add(0, 350, Short.MAX_VALUE)
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
                .addContainerGap(141, Short.MAX_VALUE))
        );
        pnlIrodsSearchLayout.setVerticalGroup(
            pnlIrodsSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIrodsSearchLayout.createSequentialGroup()
                .add(84, 84, 84)
                .add(jLabel1)
                .addContainerGap(194, Short.MAX_VALUE))
        );

        tabIrodsViews.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlIrodsSearch.TabConstraints.tabTitle"), pnlIrodsSearch); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIrodsTreeLayout = new org.jdesktop.layout.GroupLayout(pnlIrodsTree);
        pnlIrodsTree.setLayout(pnlIrodsTreeLayout);
        pnlIrodsTreeLayout.setHorizontalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
        );
        pnlIrodsTreeLayout.setVerticalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );

        pnlSplitPaneLocalRemote.setRightComponent(pnlIrodsTree);

        pnlMainTrees.add(pnlSplitPaneLocalRemote);

        pnlMain.add(pnlMainTrees, java.awt.BorderLayout.CENTER);

        pnlMainStatus.setMinimumSize(new java.awt.Dimension(250, 30));
        pnlMainStatus.setPreferredSize(new java.awt.Dimension(700, 34));
        pnlMainStatus.setLayout(new java.awt.GridBagLayout());

        lblTransferFileName.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblTransferFileName.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferFileName.text")); // NOI18N
        lblTransferFileName.setMinimumSize(new java.awt.Dimension(100, 16));
        lblTransferFileName.setPreferredSize(new java.awt.Dimension(250, 16));
        lblTransferFileName.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlMainStatus.add(lblTransferFileName, gridBagConstraints);

        pbTransferStatus.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pbTransferStatus.setMinimumSize(new java.awt.Dimension(100, 24));
        pbTransferStatus.setPreferredSize(new java.awt.Dimension(300, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        pnlMainStatus.add(pbTransferStatus, gridBagConstraints);

        pnlMain.add(pnlMainStatus, java.awt.BorderLayout.SOUTH);

        pnlTabbedMain.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlMain.TabConstraints.tabTitle"), pnlMain); // NOI18N

        pnlOperationMode2.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlOperationMode2.setPreferredSize(new java.awt.Dimension(700, 450));
        pnlOperationMode2.setLayout(new java.awt.BorderLayout());

        pnlUploadTrees.setMinimumSize(new java.awt.Dimension(250, 160));
        pnlUploadTrees.setPreferredSize(new java.awt.Dimension(700, 385));
        pnlUploadTrees.setLayout(new java.awt.GridBagLayout());

        pnlUploadLocalTree.setMinimumSize(new java.awt.Dimension(0, 0));
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
            .add(0, 289, Short.MAX_VALUE)
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
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadLocalTree, gridBagConstraints);

        pnlUploadCenterTools.setMaximumSize(new java.awt.Dimension(65, 32767));
        pnlUploadCenterTools.setMinimumSize(new java.awt.Dimension(30, 100));
        pnlUploadCenterTools.setPreferredSize(new java.awt.Dimension(50, 380));
        pnlUploadCenterTools.setRequestFocusEnabled(false);
        pnlUploadCenterTools.setLayout(new java.awt.BorderLayout());

        btnUploadMove.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadMove.text")); // NOI18N
        btnUploadMove.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnUploadMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadMoveActionPerformed(evt);
            }
        });
        pnlUploadCenterTools.add(btnUploadMove, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadCenterTools, gridBagConstraints);

        pnlUploadTable.setPreferredSize(new java.awt.Dimension(310, 380));
        pnlUploadTable.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(275, 380));

        tblUploadTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "Import?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
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
        tblUploadTable.setShowGrid(true);
        jScrollPane3.setViewportView(tblUploadTable);

        pnlUploadTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        pnlIRODSUploadDest.setLayout(new java.awt.BorderLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel2.text")); // NOI18N
        pnlIRODSUploadDest.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        txtIRODSUploadDest.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.txtIRODSUploadDest.text")); // NOI18N
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
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadTable, gridBagConstraints);

        pnlOperationMode2.add(pnlUploadTrees, java.awt.BorderLayout.CENTER);

        pnlUploadToolbar.setMaximumSize(new java.awt.Dimension(32767, 60));
        pnlUploadToolbar.setMinimumSize(new java.awt.Dimension(250, 30));
        pnlUploadToolbar.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlUploadToolbar.setLayout(new java.awt.GridBagLayout());

        pnlUploadToolStatus.setPreferredSize(new java.awt.Dimension(500, 34));
        pnlUploadToolStatus.setRequestFocusEnabled(false);
        pnlUploadToolStatus.setLayout(new java.awt.GridBagLayout());

        lblUploadTransferFileName.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblUploadTransferFileName.text")); // NOI18N
        lblUploadTransferFileName.setMaximumSize(new java.awt.Dimension(4000, 18));
        lblUploadTransferFileName.setMinimumSize(new java.awt.Dimension(100, 18));
        lblUploadTransferFileName.setPreferredSize(new java.awt.Dimension(250, 18));
        lblUploadTransferFileName.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlUploadToolStatus.add(lblUploadTransferFileName, gridBagConstraints);

        pbUploadTransferStatus.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 136;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 6.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        pnlUploadToolStatus.add(pbUploadTransferStatus, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 140.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        pnlUploadToolbar.add(pnlUploadToolStatus, gridBagConstraints);

        btnUploadBeginImport.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadBeginImport.text")); // NOI18N
        btnUploadBeginImport.setEnabled(false);
        btnUploadBeginImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadBeginImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 50.0;
        pnlUploadToolbar.add(btnUploadBeginImport, gridBagConstraints);

        btnUploadCancel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadCancel.text")); // NOI18N
        btnUploadCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.6;
        pnlUploadToolbar.add(btnUploadCancel, gridBagConstraints);

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
        	Object [] rowData = new Object[2];
        	rowData[0] = LocalFileUtils.makeLocalFilePath(path);
        	rowData[1] = Boolean.TRUE;
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
    	
    	// first collect target IRODS path
      
// iPlant decided they wanted IRODS destination path sent in as applet parameter instead of using IRODS finder dialog
//            IRODSFinderDialog finderDialog = new IRODSFinderDialog(true, iDropCore);
//            finderDialog.setVisible(true);
//            String targetPath = finderDialog.getSelectedAbsolutePath();
//            if(targetPath != null) {
//            	//then do stuff
//            	log.info("upload drop target selected:{}", targetPath);
//            }
//            finderDialog.dispose();
        	
        // make sure IRODS destination is legal
        String targetPath = txtIRODSUploadDest.getText();

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
            
        // clear the status bars and text
        this.pbTransferStatus.setValue(0);
        this.pbUploadTransferStatus.setValue(0);
        this.lblTransferFileName.setText("");
        this.lblUploadTransferFileName.setText("");
        
        // now go through and process selected import files from table
        try {
            int rows = tblUploadTable.getRowCount();
            int [] rowsToRemove;
            rowsToRemove = new int[rows];
            int idx = 0;
            
            for(int row=0; row<rows; row++) {
            	if((Boolean)tblUploadTable.getValueAt(row, 1)) {
            		String fileToImport = (String)tblUploadTable.getValueAt(row, 0);
            		log.info("uploading local file:{}", fileToImport);
            		iDropCore.getTransferManager().putOperation(fileToImport,
                			targetPath, iDropCore.getIrodsAccount().getDefaultStorageResource(),
                			this, null);
            		rowsToRemove[idx] = row;
            		idx++;
            	}
            	else {
            		rowsToRemove[idx] = -1;
            		idx++;
            	}
            }
            
            // now delete all rows that were imported
            // must do it backwards because table gets updated as rows get removed
            DefaultTableModel tm = (DefaultTableModel)tblUploadTable.getModel();
            int total = rowsToRemove.length;
            for(int i=(total-1); i>=0; i--) {
            	if(rowsToRemove[i] >= 0 ) {
            		tm.removeRow(rowsToRemove[i]);
            	}
            }
            
        } catch (Exception e) {
            log.error("exception choosings iRODS file");
            throw new IdropRuntimeException("exception choosing irods file", e);
        } finally {
            iDropCore.getIrodsFileSystem().closeAndEatExceptions();
        }
        
    }//GEN-LAST:event_btnUploadBeginImportActionPerformed

    private void btnUploadCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadCancelActionPerformed
    	DefaultTableModel tm = (DefaultTableModel)tblUploadTable.getModel();
    	tm.getDataVector().removeAllElements();
    	tm.fireTableDataChanged();
    }//GEN-LAST:event_btnUploadCancelActionPerformed

    private void btnUploadLocalRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadLocalRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUploadLocalRefreshActionPerformed

    private void txtIRODSUploadDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIRODSUploadDestActionPerformed
        btnUploadBeginImport.setEnabled(txtIRODSUploadDest.getText().length() > 0);
    }//GEN-LAST:event_txtIRODSUploadDestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIrodsTreeRefresh;
    private javax.swing.JButton btnLocalRefresh;
    private javax.swing.JToggleButton btnToggleLocalView;
    private javax.swing.JButton btnUploadBeginImport;
    private javax.swing.JButton btnUploadCancel;
    private javax.swing.JButton btnUploadLocalRefresh;
    private javax.swing.JButton btnUploadMove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblTransferFileName;
    private javax.swing.JLabel lblUploadTransferFileName;
    private javax.swing.JList lstLocalDrives;
    private javax.swing.JList lstUploadLocalDrives;
    private javax.swing.JProgressBar pbTransferStatus;
    private javax.swing.JProgressBar pbUploadTransferStatus;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlIRODSUploadDest;
    private javax.swing.JPanel pnlIrodsSearch;
    private javax.swing.JPanel pnlIrodsTree;
    private javax.swing.JPanel pnlIrodsTreeView;
    private javax.swing.JPanel pnlIrodsTreeViewButtons;
    private javax.swing.JPanel pnlLocalRefreshButton;
    private javax.swing.JPanel pnlLocalRoots;
    private javax.swing.JPanel pnlLocalToggleSizer;
    private javax.swing.JPanel pnlLocalTree;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlMainStatus;
    private javax.swing.JPanel pnlMainToolBar;
    private javax.swing.JPanel pnlMainTrees;
    private javax.swing.JPanel pnlOperationMode2;
    private javax.swing.JPanel pnlOperationMode3;
    private javax.swing.JPanel pnlPlaceholder;
    private javax.swing.JPanel pnlProgressTable;
    private javax.swing.JPanel pnlSearchSizer;
    private javax.swing.JSplitPane pnlSplitPaneLocalRemote;
    private javax.swing.JTabbedPane pnlTabbedMain;
    private javax.swing.JPanel pnlToolBarSizer;
    private javax.swing.JPanel pnlUploadCenterTools;
    private javax.swing.JPanel pnlUploadLocalDrivesFiller;
    private javax.swing.JPanel pnlUploadLocalTree;
    private javax.swing.JPanel pnlUploadRefreshButton;
    private javax.swing.JPanel pnlUploadRoots;
    private javax.swing.JPanel pnlUploadTable;
    private javax.swing.JPanel pnlUploadToolStatus;
    private javax.swing.JPanel pnlUploadToolbar;
    private javax.swing.JPanel pnlUploadTrees;
    private javax.swing.JScrollPane scrIrodsTreeView;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JScrollPane scrollUploadLocalDrives;
    private javax.swing.JScrollPane scrollUploadLocalTree;
    private javax.swing.JTabbedPane tabIrodsViews;
    private javax.swing.JTable tblProgress;
    private javax.swing.JTable tblUploadTable;
    private javax.swing.JTextField txtIRODSUploadDest;
    // End of variables declaration//GEN-END:variables

}