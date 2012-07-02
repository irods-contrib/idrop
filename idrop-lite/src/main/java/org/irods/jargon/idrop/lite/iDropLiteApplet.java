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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;
import org.irods.jargon.datautils.connection.TempPasswordCachingProtocolManager;
import org.irods.jargon.datautils.datacache.DataCacheServiceFactory;
import org.irods.jargon.datautils.datacache.DataCacheServiceFactoryImpl;
import org.irods.jargon.datautils.datacache.DataCacheServiceImpl;
import org.irods.jargon.datautils.shoppingcart.FileShoppingCart;
import org.irods.jargon.datautils.shoppingcart.ShoppingCartEntry;
import org.irods.jargon.datautils.shoppingcart.ShoppingCartService;
import org.irods.jargon.datautils.shoppingcart.ShoppingCartServiceImpl;
import org.irods.jargon.idrop.lite.finder.IRODSFinderDialog;
import org.netbeans.swing.outline.Outline;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class iDropLiteApplet extends javax.swing.JApplet implements TransferStatusCallbackListener, TableModelListener, ComponentListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);
    private iDropLiteApplet applet;
    private final Integer defaultLoginMode = -1;
    private iDropLiteCore iDropCore = null;
    private IRODSAccount irodsAccount = null;
    private LocalFileTree fileTree = null;
    private LocalFileTree fileUploadTree = null;
    private IRODSTree irodsTree = null;
    private Integer mode;
    private Integer displayMode;
    private String host;
    private Integer port;
    private String zone;
    private String user;
    private String defaultStorageResource;
    private String tempPswd;
    private String absPath;
    private String uploadDest;
    private String key;
    IRODSFileSystem irodsFileSystem = null;
    private LocalFileSystemModel localFileModel = null;
    private LocalFileSystemModel localUploadFileModel = null;
    private final JFileChooser dlgLocalFileChooser = new JFileChooser();
    private Boolean transferInProgress = false;
    private Boolean transferCancelled = false;
    private String currentUploadFile = null;
    private int filesInTable = 0;
    private int filesInTableProcessed = 0;
    private ImageIcon cancelIcon;
    // different files types for Upload Table
    static final int uploadFile = 1;
    static final int uploadFolder = 2;
    static final int uploadURL = 3;

    /** Initializes the applet NewJApplet */
    public void init() {
        this.applet = this;
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                    log.info("iDropLiteApplet version is:{}", Version.VERSION);
                    getAppletParams();
                    if (doStartup()) {
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
            this.tempPswd = getParameter("password");
            
            if(getParameter("absPath") == null) {
            	this.absPath = "/" + this.zone + "/" + this.user;
            	log.info("no absPath parameter provided, set to default: {}", this.absPath);
            }
            else {
            	this.absPath = getParameter("absPath");
            }

            if (getParameter("displayMode") == null) {
                this.displayMode = 0;
                log.info("normal (all modes) display mode");
            } else {
                this.displayMode = Integer.valueOf(getParameter("displayMode"));
                log.info("setting display mode to {}", displayMode);
            }

            // param for mode 3 (shopping cart)
            if (getParameter("key") == null) {
                log.info("shopping cart key is NOT set");
                key = "undefined";
            } else {
                log.info("shopping cart key IS set");
                this.key = getParameter("key");
            }

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

    private boolean retrievePermAccount() {
        String pswd = null;

        DataCacheServiceImpl dataCache = new DataCacheServiceImpl();
        try {
            dataCache.setIrodsAccessObjectFactory(irodsFileSystem.getIRODSAccessObjectFactory());
        } catch (JargonException e1) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e1);
        }
        log.info("creating temporary irods account...");
        IRODSAccount tmpAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);
        dataCache.setIrodsAccount(tmpAccount);

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

    private boolean createPermAccount() {
        this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);

        return true;
    }

    private boolean processLogin() {

        // do different logins depending on which mode is used
        // 0 - Hard-coded permanent password - just use this password to create and IRODS Account
        // 1 - Temporary password supplied - use this password to retrieve permanent password from cache file in cacheServiceTempDir
        // 2 - Temporary password only mode

        switch (this.mode) {

            case 1:
                showMessageFromOperation("Connection mode - 1 is not currently supported. Please configure another connection mode.");
                return false;
//				Disable mode 1 for now
//                log.info("processLogin: retrieving permanent password...");
//                if (!retrievePermAccount()) {
//                    showMessageFromOperation("Temporary Password Mode: login error - unable to log in, or invalid user id");
//                    return false;
//                }
//                break;
            case 0:
                log.info("processLogin: creating account with provided permanent password...");
                if (!createPermAccount()) {
                    showMessageFromOperation("Permanent Password Mode: login error - unable to log in, or invalid user id");
                    return false;
                }
                break;

            case 2:
                log.info("processLogin: using temp-only with cache");

                if (!tempOnlyAccount()) {
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
                System.exit(0); // added for weird applet lifecycle behavior in windows browser
                //showMessageFromOperation("login error - unable to log in, or invalid user id");
                //return false;
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
//
//        log.info("creating temporary irods account...");
//        this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);

        log.info("creating idropCore...");
        iDropCore = new iDropLiteCore();

        if (!processLogin()) {
            return false;
        }
        
        // set Look and Feel
        String lookAndFeel = iDropCore.setLookAndFeel();
        log.info("setting look and feel: {}", lookAndFeel);

        if (this.displayMode != 3) {
            buildTargetTree();
        }

        setUpLocalFileSelectTree();
        setUpUploadLocalFileSelectTree();

        try {

            DataTransferOperations dataTransferOperations = irodsFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(irodsAccount);
            iDropCore.setTransferManager(dataTransferOperations);
        } catch (JargonException ex) {
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

        CardLayout cl = (CardLayout) (testCardPanel.getLayout());

        switch (displayMode) {
            case 1:
                log.info(">>>>>>>>> local/irods display mode 1");
                cl.show(testCardPanel, "card2");
                break;
            case 2:
                log.info(">>>>>>>>>upload picker display mode");
                // need to make upload destination text box background invisible here since
                // Netbeans GUI builder will not preserve correct setting
                txtIRODSUploadDest.setBackground(new Color(0, 0, 0, 0));
                txtIRODSUploadDest.setOpaque(false);
                
                cl.show(testCardPanel, "card3");
                populateUploadDestination();
                setupProgressTable();
                break;
            case 3:
                log.info(">>>>>>>>>shopping cart display mode 3");
                // need to make download destination text box background invisible here since
                // Netbeans GUI builder will not preserve correct setting
                txtDownloadTarget.setBackground(new Color(0, 0, 0, 0));
                txtDownloadTarget.setOpaque(false);
                
                dlgLocalFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                cl.show(testCardPanel, "card4");
                setupProgressTable();
                populateDownloadTableWithCartContents();
                setupForIdropWebMode();
                break;
            default:
                log.info(">>>>>>>>> no display mode, show local/rods display mode 1");
                this.displayMode = 1;
                cl.show(testCardPanel, "card2");
        }

    }

    public void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final iDropLiteApplet gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                IRODSOutlineModel mdl = null;
                log.info("building new iRODS tree");
                try {
                    if (getTreeStagingResource() != null) {
                        reloadExistingTree();
                    } else {
                        loadNewTree();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE,
                            null, ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(
                            iDropCore.getIrodsAccount());
                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            /**
             * A tree has not been previosly loaded, establish the root (strict ACLs?  Login preset?)
             */
            private void loadNewTree() throws JargonException, IdropException {
                IRODSOutlineModel mdl;
                TreePath[] currentPaths = null;
                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();
                if (gui.absPath == null || gui.absPath.isEmpty()) {
                    log.info("using home directory");
                    StringBuilder sb = new StringBuilder();
                    sb.append("/");
                    sb.append(getIrodsAccount().getZone());
                    sb.append("/");
                    sb.append("home");
                    root.setParentPath(sb.toString());
                    root.setPathOrName(getIrodsAccount().getHomeDirectory());
                    root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                } else {
                    String basePath = gui.absPath;
                    log.info("base path set to:{}", basePath);
                    root.setPathOrName(basePath);
                    root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                }

                irodsTree = new IRODSTree(gui);
                IRODSNode rootNode = new IRODSNode(root,
                        getIrodsAccount(), getiDropCore().getIrodsFileSystem(), irodsTree);
                irodsTree.setRefreshingTree(true);
                IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                        rootNode, getIrodsAccount());
                mdl = new IRODSOutlineModel(gui,
                        irodsFileSystemModel, new IRODSRowModel(), true,
                        "File System");
                irodsTree.setModel(mdl);
                gui.scrIrodsTreeView.setViewportView(irodsTree);
            }

            /**
             * A tree already exists so use the current information to reload
             */
            private void reloadExistingTree() throws IdropException, JargonException {
                IRODSNode currentRoot = (IRODSNode) irodsTree.getOutlineModel().getRoot();
                log.debug("current tree root:{}", currentRoot);
                TreePath rootPath = TreeUtils.getPath(currentRoot);
                TreePath[] currentPaths = irodsTree.getOutlineModel().getTreePathSupport().getExpandedDescendants(rootPath);
                log.info("expanded paths:{}", currentPaths);
                   gui.scrIrodsTreeView.getViewport().removeAll();
                irodsTree = null;
                irodsTree = new IRODSTree(gui);
                CollectionAndDataObjectListingEntry currentEntry = (CollectionAndDataObjectListingEntry) currentRoot.getUserObject();
                IRODSNode rootNode = new IRODSNode(currentEntry,
                        getIrodsAccount(), getiDropCore().getIrodsFileSystem(), irodsTree);
                irodsTree.setRefreshingTree(true);
                IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                        rootNode, getIrodsAccount());
                IRODSOutlineModel mdl = new IRODSOutlineModel(gui,
                        irodsFileSystemModel, new IRODSRowModel(), true,
                        "File System");

                irodsTree.setModel(mdl);
                   gui.scrIrodsTreeView.setViewportView(irodsTree);
                if (currentPaths != null) {
                    IRODSNode irodsNode = null;
                    TreePath pathOfExpandingNode = null;
                    CollectionAndDataObjectListingEntry expandedEntry = null;
                    log.info("looking to re-expand paths...");
                    for (TreePath treePath : currentPaths) {
                        irodsNode = (IRODSNode) treePath.getLastPathComponent();
                        expandedEntry = (CollectionAndDataObjectListingEntry) irodsNode.getUserObject();
                        irodsNode = (IRODSNode) TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, expandedEntry.getFormattedAbsolutePath()).getLastPathComponent();
                        irodsNode.getChildCount();
                        TreePath pathInNew = TreeUtils.getPath(irodsNode);
                        irodsTree.collapsePath(pathInNew);
                        irodsTree.expandPath(pathInNew);
                    }
                }
            }
        });
    }
    
       /**
     * Establish base path (checking if strict acl's are in place
     * @return <code>String</code> with the base path for the tree
     * @throws JargonException 
     */
    private synchronized String getBasePath() throws JargonException {
        String myBase = this.getiDropCore().getBasePath();
        if (myBase == null) {
            EnvironmentalInfoAO environmentalInfoAO = this.getiDropCore().getIRODSAccessObjectFactory().getEnvironmentalInfoAO(getiDropCore().getIrodsAccount());
            boolean isStrict = environmentalInfoAO.isStrictACLs();
            log.info("is strict?:{}", isStrict);
            if (isStrict) {
                myBase = "/"
                        + getiDropCore().getIrodsAccount().getZone() + "/home/"
                        + getiDropCore().getIrodsAccount().getUserName();
            } else {
                myBase = "/";
            }
        }

        return myBase;

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
            pnlLocalRoots.setPreferredSize(new java.awt.Dimension(295, 39));
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
            pnlUploadRoots.setPreferredSize(new java.awt.Dimension(295, 54));
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
        if ((uploadDest != null) && (uploadDest.length() > 0)) {
            txtIRODSUploadDest.setText(uploadDest);
            btnUploadBeginImport.setEnabled(true);
        }
    }

    private void setupProgressTable() {

        // load table cancel icon
        java.net.URL imgURL = getClass().getResource("/cancel.gif");

        if (imgURL != null) {
            cancelIcon = new ImageIcon(imgURL, "image used to denote cancel or remove table entry");
        } else {
            log.error("cannot find image: cancel.gif for Upload Table");
        }

        //set FillsViewportHeight so user can drop onto an empty table
        tblUploadTable1.setFillsViewportHeight(true);
        tblUploadTable1.setShowGrid(true);
        tblUploadTable1.setShowVerticalLines(false);
        tblUploadTable1.getColumnModel().getColumn(3).setPreferredWidth(6);
        tblUploadTable1.setRowHeight(24);

        tblUploadTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (displayMode == 2) { // do some special stuff for Upload Mode
            tblUploadTable1.setDropMode(DropMode.INSERT_ROWS);
            tblUploadTable1.setDragEnabled(true);
            UploadTableTransferHandler tth = new UploadTableTransferHandler();
            tth.setGUI(this);
            tblUploadTable1.setTransferHandler(tth);
            tblUploadTable1.getColumnModel().getColumn(1).setWidth(0);
            tblUploadTable1.getColumnModel().getColumn(1).setMinWidth(0);
            tblUploadTable1.getColumnModel().getColumn(1).setMaxWidth(0);
            tblUploadTable1.getColumnModel().getColumn(1).setPreferredWidth(0);
        } else {
            tblUploadTable1.getColumnModel().getColumn(1).setPreferredWidth(10);
            pnlDownloadProgressTable.add(jScrollPane5, java.awt.BorderLayout.CENTER);
        }
        // hide the isFolder indicator column
        tblUploadTable1.getColumnModel().getColumn(4).setWidth(0);
        tblUploadTable1.getColumnModel().getColumn(4).setMinWidth(0);
        tblUploadTable1.getColumnModel().getColumn(4).setMaxWidth(0);
        tblUploadTable1.getColumnModel().getColumn(4).setPreferredWidth(0);

        tblUploadTable1.getModel().addTableModelListener(applet);

        // add renderer for file name in first column
        tblUploadTable1.getColumnModel().getColumn(0).setCellRenderer(new UploadTableFilenameRenderer(this.displayMode));
        // add rendered for progress bars in second column
        tblUploadTable1.getColumnModel().getColumn(2).setCellRenderer(new UploadTableProgressBar());
        // add renderer for cancel icon
        tblUploadTable1.getColumnModel().getColumn(3).setCellRenderer(new UploadTableCancelRenderer(cancelIcon));

        // save Table Model for later use
        this.iDropCore.setUploadTableModel((DefaultTableModel) tblUploadTable1.getModel());
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
                JOptionPane.showMessageDialog(iDropCore.findAppletParentFrame(thisIdropGui), messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
    public void statusCallback(final TransferStatus ts) {
        log.info("transfer status callback to iDropLiteApplet:{}", ts);
        final iDropLiteApplet idrop = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                log.info("in statusCallback thread");

                //String file = ts.getSourceFileAbsolutePath();
                int tableRow = -1;
                if (currentUploadFile != null) {
                    tableRow = getUploadTableProgressRow(currentUploadFile);
                }

                if (ts.getTransferState() == TransferStatus.TransferState.FAILURE) {
                    // an error occurs, stop the transfer
                    log.error("error occurred in transfer: {}", ts);
                    if (ts.getTransferException() == null) {
                        idrop.showMessageFromOperation("An error occurred in the transfer, this transfer will be cancelled");
                    } else {
                        idrop.showIdropException(ts.getTransferException());
                    }
                    idrop.getiDropCore().getTransferControlBlock().setCancelled(true);
                    // reset process transfer flags
                    if (isTransferInProgress()) {
                        setTransferCancelled(true);
                        setTransferInProgress(false);
                    }
                    
                } else if (ts.isIntraFileStatusReport()) {

                    // intra file reports update the progress bar
                    lblTransferByteCounts.setText("Current File (kb):"
                            + (ts.getBytesTransfered() / 1024) + " / "
                            + (ts.getTotalSize() / 1024));
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum((int) ts.getTotalSize());
                    progressIntraFile.setValue((int) ts.getBytesTransfered());

                    // if uploading from mode 2 table
                    if ((tableRow >= 0) && (ts.getTotalSize() > 0)) {
                        float bt = ts.getBytesTransfered() * 100;
                        float tot = ts.getTotalSize();
                        float percentDone = bt / tot;
                        TransferProgressInfo tpi = new TransferProgressInfo(ts.getTotalSize(), ts.getBytesTransfered(), 0, 0, true);
                        tblUploadTable1.getModel().setValueAt(tpi, tableRow, 2);
                    }

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {

                    // start of a file operation
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum((int) ts.getTotalSize());
                    progressIntraFile.setValue(0);
                    lblCurrentFile.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));

                    // need to do this because an IN_PROGRESS_START_FILE message is sent first that says
                    // all of the bytes have been transferred which of course is incorrect, so must ignore
                    // that first message
                    if (!(ts.getTotalSize() == ts.getBytesTransfered())) {
                        if (currentUploadFile != null) {
                            tableRow = getUploadTableProgressRow(currentUploadFile);
                        }
                        if ((tableRow >= 0)) {
                            TransferProgressInfo tpi = new TransferProgressInfo(ts.getTotalSize(), ts.getBytesTransfered(),
                                    ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar());
                            tblUploadTable1.getModel().setValueAt(tpi, tableRow, 2);
                        }
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
                    pbIdropWebModeDownloadProgress.setMaximum(ts.getTotalFilesToTransfer());
                    pbIdropWebModeDownloadProgress.setValue(ts.getTotalFilesTransferredSoFar());
                    lblTransferFilesCounts.setText("Files: "
                            + ts.getTotalFilesTransferredSoFar() + " / "
                            + ts.getTotalFilesToTransfer());

                    // if uploading from mode 2 table
                    if ((tableRow >= 0) && (ts.getTotalSize() > 0)) {
                        float bt = ts.getBytesTransfered() * 100;
                        float tot = ts.getTotalSize();
                        float percentDone = bt / tot;
                        //tblUploadTable1.getModel().setValueAt((int) percentDone, tableRow, 2);
                        TransferProgressInfo tpi = new TransferProgressInfo(ts.getTotalSize(), ts.getBytesTransfered(),
                                ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar());
                        tblUploadTable1.getModel().setValueAt(tpi, tableRow, 2);
                    }

                } else {

                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
                    pbIdropWebModeDownloadProgress.setMaximum(ts.getTotalFilesToTransfer());
                    pbIdropWebModeDownloadProgress.setValue(ts.getTotalFilesTransferredSoFar());
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

//        final IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree.getModel();
        final iDropLiteApplet idropGui = this;
        idropGui.setTransferInProgress(true);

        log.info("transfer OVERALL status callback to iDropLiteApplet:{}", ts);
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                log.info("in overallStatusCallback thread");

                int tableRow = -1;
                if (currentUploadFile != null) {
                    tableRow = getUploadTableProgressRow(currentUploadFile);
                }

                /* 
                 * Handle appropriate tree notifications, so some filtering to prevent notifications when for a different host/zone
                 */
                if (ts.getTransferType() == TransferStatus.TransferType.SYNCH || ts.getTransferType() == TransferStatus.TransferType.REPLICATE) {
                    log.info("no need to notify tree for synch or replicate");
                } else if (ts.getTransferType() == TransferStatus.TransferType.GET
                        && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION
                        && idropGui.displayMode == 1) {
                    try {
                        ((LocalFileSystemModel) idropGui.getFileTree().getModel()).notifyCompletionOfOperation(idropGui.getFileTree(), ts);

                    } catch (IdropException ex) {
                        log.error("error on tree notify after operation", ex);
                        throw new IdropRuntimeException("error processing overall status callback", ex);
                    }
                } else if (ts.getTransferType() == TransferStatus.TransferType.COPY || ts.getTransferType() == TransferStatus.TransferType.PUT) {
                    if (ts.getTransferZone().equals(
                            iDropCore.getIrodsAccount().getZone()) && ts.getTransferHost().equals(iDropCore.getIrodsAccount().getHost())) {
                        // only do this for displayMode 1 - local and irods trees
                        if (idropGui.displayMode == 1) {
                            try {
                                // should leave PUT, and COPY
                                final IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) idropGui.irodsTree.getModel();
                                irodsTreeModel.notifyCompletionOfOperation(idropGui.irodsTree, ts);
                            } catch (IdropException ex) {
                                log.error("error on tree notify after operation", ex);
                                throw new IdropRuntimeException("error processing overall status callback", ex);
                            }
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
                    pbIdropWebModeDownloadProgress.setMinimum(0);
                    pbIdropWebModeDownloadProgress.setMaximum(ts.getTotalFilesToTransfer());
                    pbIdropWebModeDownloadProgress.setValue(0);

                    if ((tableRow >= 0)) {
                        TransferProgressInfo tpi = new TransferProgressInfo();
                        tblUploadTable1.getModel().setValueAt(tpi, tableRow, 2);
                    }

                    currentUploadFile = ts.getSourceFileAbsolutePath();
                    enableUploadButtons(false);
                }

                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                    if (tableRow >= 0) {
                        //tblUploadTable.getModel().setValueAt(false, tableRow, 2);
                    }
                    currentUploadFile = null;
                    idropGui.setTransferInProgress(false);
                    enableUploadButtons(true);
                    // for Bulk Upload and Shopping Carts modes, display message when file transfer is done
                    if (idropGui.displayMode == 2 || idropGui.displayMode == 3) {
                        idropGui.filesInTableProcessed++;
                        if (idropGui.filesInTableProcessed >= idropGui.filesInTable) {
                            showMessageFromOperation("Transfer Completed");
                            idropGui.filesInTable = 0;
                            idropGui.filesInTableProcessed = 0;
                        }
                    }
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

        pbIdropWebModeDownloadProgress.setMinimum(0);
        pbIdropWebModeDownloadProgress.setMaximum(100);
        pbIdropWebModeDownloadProgress.setValue(0);
    }

    public void setTransferInProgress(Boolean state) {
        this.transferInProgress = state;
    }

    public Boolean isTransferInProgress() {
        return this.transferInProgress;
    }

    // these may not be needed any more because the overall cancel
    // feature was removed by iPlant
    public void setTransferCancelled(Boolean state) {
        this.transferCancelled = state;
    }

    public Boolean isTransferCancelled() {
        return this.transferCancelled;
    }

    public void cancelTransfer() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                iDropCore.getTransferControlBlock().setCancelled(true);
            }
        });
    }
    // not needed? end

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
        int row = -1;
        int numRows = tblUploadTable1.getModel().getRowCount();
        for (int i = 0; i < numRows; i++) {
            String name = (String) tblUploadTable1.getModel().getValueAt(i, 0);
            log.info("filename is: {} row name is: {}", filename, name);
            if (filename.equals(name)) {
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
        btnUploadUrl.setEnabled(state);
    }

    public void setTotalFileUpload(int total) {
        String phrase = "Total Files To Upload: ";

        if (total > 0) {
            Integer itotal = new Integer(total);
            lblUploadTotalFiles.setText(phrase.concat(itotal.toString()));
        } else {
            lblUploadTotalFiles.setText(phrase);
        }
    }

    public void setTotalSizeUpload(long total) {
        String phrase = "Total File Size: ";
        String postfix = " (kb)";
        if (total > 0) {
            Long ltotal = new Long(total / 1024);
            lblUploadTotalSize.setText(phrase.concat(ltotal.toString()).concat(postfix));
        } else {
            lblUploadTotalSize.setText(phrase);
        }
    }

    public void updateFileStats(DefaultTableModel tm) {

        int numRows = tm.getRowCount();
        long totalSize = 0;
        int totalFiles = 0;
        URL url = null;
        URLConnection connection = null;

        for (int i = 0; i < numRows; i++) {

            String fileName = (String) tm.getValueAt(i, 0);
            if (fileName != null) {

                // check to see if this is an URL
                int fileType = (Integer) tm.getValueAt(i, 4);
                if (fileType == iDropLiteApplet.uploadURL) {
                    // do url stuff
                    try {
                        url = new URL(fileName);
                        connection = url.openConnection();
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    totalFiles++;
                    totalSize += connection.getContentLength();
                } else {
                    // do file stuff
                    File file = new File(fileName);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            totalFiles += FileUtils.listFiles(file, null, true).size();
                            totalSize += FileUtils.sizeOfDirectory(file);
                        } else {
                            totalFiles++;
                            totalSize += file.length();
                        }
                    }
                }
            }
        }
        setTotalFileUpload(totalFiles);
        setTotalSizeUpload(totalSize);
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        int type = tme.getType();
        int row = tme.getFirstRow();
        int column = tme.getColumn();
        DefaultTableModel tm = (DefaultTableModel) tblUploadTable1.getModel();

        if (type == TableModelEvent.UPDATE && column == 3) {
            //log.info("canceling or removing??");
            if (isTransferInProgress()) {
                this.cancelTransfer();
            } else {
                tm.removeRow(row);
                tm.fireTableRowsDeleted(row, row);
            }
        } else {
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

    private void collectDownloadTarget() {
        int ret = dlgLocalFileChooser.showOpenDialog(iDropCore.findAppletParentFrame(this));
        //int ret = dlgLocalFileChooser.showSaveDialog(this.applet); // update this so you can create a folder???
        if (ret == JFileChooser.APPROVE_OPTION) {
            File path = dlgLocalFileChooser.getSelectedFile();
            if (path.getPath() != null) {
                txtDownloadTarget.setText(path.getPath());
                txtIdropWebModeDownloadTarget.setText(path.getPath());
            }
        }
    }

    private boolean tempOnlyAccount() {
        String pswd = null;

        try {
            log.info("creating a shared (cached) temp account connection");
            this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);
            TempPasswordCachingProtocolManager manager = new TempPasswordCachingProtocolManager(
                    irodsAccount);
            irodsFileSystem = new IRODSFileSystem(manager);
            log.info("irodsFileSystem updated to utilize cache");

        } catch (JargonException e2) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e2);
            return false;
        }

        return true;
    }

    private List<String> getCartFiles() {
        List<String> cartContents = new ArrayList<String>();
        FileShoppingCart cart = null;

        log.info("retrieving cart shopping cart contents");

        DataCacheServiceFactory dataCacheServiceFactory;
        try {
            dataCacheServiceFactory = new DataCacheServiceFactoryImpl(
                    irodsFileSystem.getIRODSAccessObjectFactory());
            ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl(
                    iDropCore.getIrodsFileSystem().getIRODSAccessObjectFactory(), iDropCore.getIrodsAccount(),
                    dataCacheServiceFactory);
            log.info("getting cart as logged in user, key: {}", this.key);
            if ((this.mode == 2) && (this.key != null) && (!this.key.isEmpty()) && (this.key != "undefined")) {
            	cart = shoppingCartService.retreiveShoppingCartAsLoggedInUser(this.key);
            }
            else {
            	log.error("could not create shopping cart");
                showMessageFromOperation("Cannot retrieve files from shopping cart");
                return cartContents;
            }

            // for testing
            //cart = FileShoppingCart.instance();
            //ShoppingCartEntry entry = ShoppingCartEntry.instance("/test1/home/test1/this_file.txt");
            //cart.addAnItem(entry);
            //
        } catch (JargonException e) {
            log.error("could not create shopping cart");
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e);
            showMessageFromOperation("Cannot retrieve files from shopping cart");
        }

        if (cart.hasItems()) {
            cartContents = cart.getShoppingCartFileList();
        }

        log.info("returning contents of shopping cart {}", cartContents);
        return cartContents;
    }

    private void populateDownloadTableWithCartContents() {
        long fileSize = 0;
        int fileType = iDropLiteApplet.uploadFile;

        List<String> cartFiles = getCartFiles();

        for (String cf : cartFiles) {
            DefaultTableModel tm = (DefaultTableModel) tblUploadTable1.getModel();
            Object[] rowData = new Object[5];
            rowData[0] = cf;
            try {
                IRODSFileService irodsFS = new IRODSFileService(iDropCore.getIrodsAccount(), iDropCore.getIrodsFileSystem());
                IRODSFile ifile = irodsFS.getIRODSFileForPath(cf);
                if (ifile.isDirectory()) {
                    fileType = iDropLiteApplet.uploadFolder;
                }
                fileSize = ifile.length();
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("cannot retrieve irods file size for display in download table");
                fileSize = 0;
            }
            rowData[1] = fileSize;
            rowData[2] = 0;
            rowData[3] = Boolean.TRUE;
            rowData[4] = fileType;
            tm.addRow(rowData);
        }
    }

    private void setupForIdropWebMode() {
        // set up listener to capture resize of applet - so if it gets really small it will switch to
        // iDrop Web mode for shopping cart mode
        this.addComponentListener(this);
        checkForIdropWebMode();
    }

    private void checkForIdropWebMode() {
        int width = this.getWidth();
        int height = this.getHeight();
        CardLayout cl = (CardLayout) (testCardPanel.getLayout());

        if (width < 350 && height < 250) {
            String target = txtDownloadTarget.getText();
            if (target.length() > 0) {
                txtIdropWebModeDownloadTarget.setText(target);
            }
            cl.show(testCardPanel, "card5"); // iDrop Web small mode
        } else {
            String target = txtIdropWebModeDownloadTarget.getText();
            if (target.length() > 0) {
                txtDownloadTarget.setText(target);
            }
            cl.show(testCardPanel, "card4"); // regular shopping cart mode
        }
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        checkForIdropWebMode();
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    private void executeDownload() {
        GetTransferRunner currentTransferRunner = null;
        final List<File> sourceFiles = new ArrayList<File>();

        // make sure local destination is legal
        final String targetPath = txtDownloadTarget.getText();
        log.info("download destination is: {}", targetPath);

        try {
            if (!new File(targetPath).exists()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid local destination for download.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid local destination for download.");
            return;
        }

        // now go through and process selected import files from table
        if (!isTransferInProgress()) {

            IRODSFileService irodsFS = null;
            try {
                irodsFS = new IRODSFileService(iDropCore.getIrodsAccount(), iDropCore.getIrodsFileSystem());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot access iRODS file system for get.");
                log.error("cannot create irods file service");
                return;
            }

            // collect list of files in the table
            int rows = tblUploadTable1.getRowCount();
            for (int row = 0; row < rows; row++) {
                IRODSFile ifile = null;
                try {
                    ifile = irodsFS.getIRODSFileForPath((String) tblUploadTable1.getValueAt(row, 0));
                    sourceFiles.add((File) ifile);
                } catch (Exception ex) {
                    log.error("cannot access irods file for get: {}", (String) tblUploadTable1.getValueAt(row, 0));
                    ex.printStackTrace();
                }
            }
            try {
                // process a get
                this.filesInTable = rows; // reset to 0 in overall status callback when all files have been transferred
                //currentTransferRunner = new GetTransferRunner(applet, targetPath, sourceFiles, iDropCore.getTransferControlBlock());
                currentTransferRunner = new GetTransferRunner(applet, targetPath, sourceFiles);
                final Thread transferThread = new Thread(currentTransferRunner);
                log.info("launching transfer thread");
                // close so that transfer thread can grab account
                irodsFileSystem.closeAndEatExceptions();
                transferThread.start();
            } catch (Exception e) {
                log.error("exception choosings local file");
                throw new IdropRuntimeException("exception choosing locL file", e);
            } finally {
                iDropCore.getIrodsFileSystem().closeAndEatExceptions();
            }
        }
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

        pnllSeems2BneededForCorrectResizing = new javax.swing.JPanel();
        testCardPanel = new javax.swing.JPanel();
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
        jLabel5 = new javax.swing.JLabel();
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
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pnlUploadTrees = new javax.swing.JPanel();
        pnlUploadLocalTree = new javax.swing.JPanel();
        pnlUploadRoots = new javax.swing.JPanel();
        scrollUploadLocalDrives = new javax.swing.JScrollPane();
        lstUploadLocalDrives = new javax.swing.JList();
        pnlUploadRefreshButton = new javax.swing.JPanel();
        btnUploadLocalRefresh = new javax.swing.JButton();
        btnUploadMove = new javax.swing.JButton();
        btnUploadUrl = new javax.swing.JButton();
        pnlUploadLocalDrivesFiller = new javax.swing.JPanel();
        scrollUploadLocalTree = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        pnlUploadCenterTools = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        pnlUploadTable = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblUploadTable1 = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        pnlUploadToolStatus = new javax.swing.JPanel();
        lblUploadTotalFiles = new javax.swing.JLabel();
        lblUploadTotalSize = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        btnUploadCancel = new javax.swing.JButton();
        btnUploadBeginImport = new javax.swing.JButton();
        pnlIRODSUploadBrowse = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btnBrowseIRODSUploadDest = new javax.swing.JButton();
        txtIRODSUploadDest = new javax.swing.JTextField();
        pnlUploadToolbar = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        pnlOperationMode3 = new javax.swing.JPanel();
        pnlDownloadModeTarget = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDownloadTarget = new javax.swing.JTextField();
        btnBrowseDownloadTarget = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        pnlDownloadProgressTable = new javax.swing.JPanel();
        pnlDownloadButtons = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        bntBeginDownload = new javax.swing.JButton();
        pnlIdropWebMode = new javax.swing.JPanel();
        pnlIdropWebModeDownloadTarget = new javax.swing.JPanel();
        pnlIdropWebModeText = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlIdropWebModeDownloadDest = new javax.swing.JPanel();
        txtIdropWebModeDownloadTarget = new javax.swing.JTextField();
        btnIdropWebModeTargetBrowse = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        pnlIdropWebModeProgressBar = new javax.swing.JPanel();
        pbIdropWebModeDownloadProgress = new javax.swing.JProgressBar();
        pnlIdropWebModeBeginDownload = new javax.swing.JPanel();
        btnIdropWebModeBeginDownload = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(250, 200));
        setPreferredSize(new java.awt.Dimension(700, 490));

        pnllSeems2BneededForCorrectResizing.setPreferredSize(new java.awt.Dimension(720, 490));
        pnllSeems2BneededForCorrectResizing.setLayout(new java.awt.GridLayout(1, 1));

        testCardPanel.setPreferredSize(new java.awt.Dimension(710, 490));
        testCardPanel.setLayout(new java.awt.CardLayout());

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlMain.setPreferredSize(new java.awt.Dimension(700, 490));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlMainToolBar.setMinimumSize(new java.awt.Dimension(250, 30));
        pnlMainToolBar.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlMainToolBar.setLayout(new java.awt.BorderLayout());

        pnlToolBarSizer.setPreferredSize(new java.awt.Dimension(632, 50));
        pnlToolBarSizer.setSize(new java.awt.Dimension(100, 50));
        pnlToolBarSizer.setLayout(new java.awt.BorderLayout());

        pnlLocalToggleSizer.setPreferredSize(new java.awt.Dimension(150, 50));

        btnToggleLocalView.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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
                .addContainerGap(11, Short.MAX_VALUE))
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
            .add(0, 392, Short.MAX_VALUE)
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

        pnlMainTrees.setLayout(new javax.swing.BoxLayout(pnlMainTrees, javax.swing.BoxLayout.LINE_AXIS));

        pnlSplitPaneLocalRemote.setDividerLocation(300);
        pnlSplitPaneLocalRemote.setMinimumSize(new java.awt.Dimension(250, 150));
        pnlSplitPaneLocalRemote.setPreferredSize(new java.awt.Dimension(700, 375));

        pnlLocalTree.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlLocalTree.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlLocalTree.setLayout(new java.awt.BorderLayout());

        pnlLocalRoots.setPreferredSize(new java.awt.Dimension(295, 100));
        pnlLocalRoots.setRequestFocusEnabled(false);
        pnlLocalRoots.setLayout(new java.awt.BorderLayout());

        pnlLocalRefreshButton.setMaximumSize(new java.awt.Dimension(1000, 34));
        pnlLocalRefreshButton.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalRefreshButton.setPreferredSize(new java.awt.Dimension(0, 34));
        pnlLocalRefreshButton.setRequestFocusEnabled(false);

        btnLocalRefresh.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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

        tabIrodsViews.setFont(new java.awt.Font("Lucida Grande", 0, 12));

        pnlIrodsTreeView.setLayout(new java.awt.BorderLayout());

        btnIrodsTreeRefresh.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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

        jLabel5.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel5.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIrodsSearchLayout = new org.jdesktop.layout.GroupLayout(pnlIrodsSearch);
        pnlIrodsSearch.setLayout(pnlIrodsSearchLayout);
        pnlIrodsSearchLayout.setHorizontalGroup(
            pnlIrodsSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIrodsSearchLayout.createSequentialGroup()
                .add(83, 83, 83)
                .add(jLabel5)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        pnlIrodsSearchLayout.setVerticalGroup(
            pnlIrodsSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIrodsSearchLayout.createSequentialGroup()
                .add(84, 84, 84)
                .add(jLabel5)
                .addContainerGap(168, Short.MAX_VALUE))
        );

        tabIrodsViews.addTab(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.pnlIrodsSearch.TabConstraints.tabTitle"), pnlIrodsSearch); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIrodsTreeLayout = new org.jdesktop.layout.GroupLayout(pnlIrodsTree);
        pnlIrodsTree.setLayout(pnlIrodsTreeLayout);
        pnlIrodsTreeLayout.setHorizontalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        pnlIrodsTreeLayout.setVerticalGroup(
            pnlIrodsTreeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
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

        lblTransferTypeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblTransferTypeLabel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferTypeLabel.text")); // NOI18N
        pnlTransferType.add(lblTransferTypeLabel);

        lblTransferType.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblTransferType.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferType.text")); // NOI18N
        pnlTransferType.add(lblTransferType);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlTransferStatus.add(pnlTransferType, gridBagConstraints);

        lblTransferFilesCounts.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblTransferFilesCounts.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferFilesCounts.text")); // NOI18N
        pnlTransferFileCounts.add(lblTransferFilesCounts);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlTransferStatus.add(pnlTransferFileCounts, gridBagConstraints);

        pnlTransferByteCounts.setLayout(new java.awt.GridBagLayout());

        lblTransferByteCounts.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblTransferByteCounts.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblTransferByteCounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlTransferByteCounts.add(lblTransferByteCounts, gridBagConstraints);

        progressIntraFile.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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

        lblCurrentFileLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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

        transferStatusProgressBar.setFont(new java.awt.Font("Lucida Grande", 0, 12));
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

        testCardPanel.add(pnlMain, "card2");

        pnlOperationMode2.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlOperationMode2.setPreferredSize(new java.awt.Dimension(700, 490));
        pnlOperationMode2.setLayout(new java.awt.BorderLayout());

        pnlTitlleBar.setPreferredSize(new java.awt.Dimension(700, 24));
        pnlTitlleBar.setLayout(new java.awt.GridBagLayout());

        jPanel8.setMinimumSize(new java.awt.Dimension(200, 27));
        jPanel8.setPreferredSize(new java.awt.Dimension(285, 20));
        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel6.text")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(150, 17));
        jPanel8.add(jLabel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel8, gridBagConstraints);

        jPanel9.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanel9.setPreferredSize(new java.awt.Dimension(10, 24));

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 12, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 24, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel9, gridBagConstraints);

        jPanel10.setMinimumSize(new java.awt.Dimension(252, 20));
        jPanel10.setPreferredSize(new java.awt.Dimension(310, 20));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel7.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel7.text")); // NOI18N
        jPanel10.add(jLabel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.6;
        gridBagConstraints.weighty = 0.2;
        pnlTitlleBar.add(jPanel10, gridBagConstraints);

        pnlOperationMode2.add(pnlTitlleBar, java.awt.BorderLayout.NORTH);

        pnlUploadTrees.setMinimumSize(new java.awt.Dimension(250, 160));
        pnlUploadTrees.setPreferredSize(new java.awt.Dimension(700, 425));
        pnlUploadTrees.setLayout(new java.awt.GridBagLayout());

        pnlUploadLocalTree.setMinimumSize(new java.awt.Dimension(150, 0));
        pnlUploadLocalTree.setPreferredSize(new java.awt.Dimension(285, 420));
        pnlUploadLocalTree.setLayout(new java.awt.BorderLayout());

        pnlUploadRoots.setMinimumSize(new java.awt.Dimension(280, 35));
        pnlUploadRoots.setPreferredSize(new java.awt.Dimension(280, 120));
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
        pnlUploadRefreshButton.setPreferredSize(new java.awt.Dimension(101, 54));
        pnlUploadRefreshButton.setRequestFocusEnabled(false);
        pnlUploadRefreshButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 6));

        btnUploadLocalRefresh.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnUploadLocalRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/lite/images/icon_idroplite_refresh.png"))); // NOI18N
        btnUploadLocalRefresh.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadLocalRefresh.text")); // NOI18N
        btnUploadLocalRefresh.setMaximumSize(new java.awt.Dimension(94, 34));
        btnUploadLocalRefresh.setMinimumSize(new java.awt.Dimension(94, 34));
        btnUploadLocalRefresh.setPreferredSize(new java.awt.Dimension(94, 34));
        btnUploadLocalRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadLocalRefreshActionPerformed(evt);
            }
        });
        pnlUploadRefreshButton.add(btnUploadLocalRefresh);

        btnUploadMove.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnUploadMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/lite/images/icon_idroplite_addtoqueue.png"))); // NOI18N
        btnUploadMove.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadMove.text")); // NOI18N
        btnUploadMove.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnUploadMove.setMaximumSize(new java.awt.Dimension(75, 34));
        btnUploadMove.setMinimumSize(new java.awt.Dimension(75, 34));
        btnUploadMove.setPreferredSize(new java.awt.Dimension(200, 34));
        btnUploadMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadMoveActionPerformed(evt);
            }
        });
        pnlUploadRefreshButton.add(btnUploadMove);

        btnUploadUrl.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        btnUploadUrl.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadUrl.text")); // NOI18N
        btnUploadUrl.setEnabled(false);
        btnUploadUrl.setMaximumSize(new java.awt.Dimension(0, 0));
        btnUploadUrl.setMinimumSize(new java.awt.Dimension(0, 0));
        btnUploadUrl.setPreferredSize(new java.awt.Dimension(0, 0));
        btnUploadUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadUrlActionPerformed(evt);
            }
        });
        pnlUploadRefreshButton.add(btnUploadUrl);

        pnlUploadRoots.add(pnlUploadRefreshButton, java.awt.BorderLayout.NORTH);

        pnlUploadLocalDrivesFiller.setMinimumSize(new java.awt.Dimension(10, 5));

        org.jdesktop.layout.GroupLayout pnlUploadLocalDrivesFillerLayout = new org.jdesktop.layout.GroupLayout(pnlUploadLocalDrivesFiller);
        pnlUploadLocalDrivesFiller.setLayout(pnlUploadLocalDrivesFillerLayout);
        pnlUploadLocalDrivesFillerLayout.setHorizontalGroup(
            pnlUploadLocalDrivesFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 272, Short.MAX_VALUE)
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

        jPanel5.setPreferredSize(new java.awt.Dimension(283, 60));

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 272, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 60, Short.MAX_VALUE)
        );

        pnlUploadLocalTree.add(jPanel5, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadLocalTree, gridBagConstraints);

        pnlUploadCenterTools.setAutoscrolls(true);
        pnlUploadCenterTools.setMaximumSize(new java.awt.Dimension(10, 32767));
        pnlUploadCenterTools.setMinimumSize(new java.awt.Dimension(10, 100));
        pnlUploadCenterTools.setPreferredSize(new java.awt.Dimension(10, 440));
        pnlUploadCenterTools.setRequestFocusEnabled(false);
        pnlUploadCenterTools.setLayout(new java.awt.GridBagLayout());

        jPanel13.setMinimumSize(new java.awt.Dimension(80, 0));
        jPanel13.setPreferredSize(new java.awt.Dimension(80, 100));

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlUploadCenterTools.add(jPanel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        pnlUploadTrees.add(pnlUploadCenterTools, gridBagConstraints);

        pnlUploadTable.setPreferredSize(new java.awt.Dimension(310, 420));
        pnlUploadTable.setLayout(new java.awt.BorderLayout());

        jScrollPane5.setPreferredSize(new java.awt.Dimension(275, 370));

        tblUploadTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "File/Folder Size", "Progress", "Cancel/Remove", "File Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUploadTable1.setBounds(new java.awt.Rectangle(0, 0, 350, 64));
        tblUploadTable1.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        tblUploadTable1.setGridColor(new java.awt.Color(204, 204, 204));
        tblUploadTable1.setPreferredSize(new java.awt.Dimension(285, 380));
        tblUploadTable1.setRowMargin(2);
        tblUploadTable1.setShowGrid(false);
        jScrollPane5.setViewportView(tblUploadTable1);

        pnlUploadTable.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        jPanel14.setPreferredSize(new java.awt.Dimension(320, 60));
        jPanel14.setLayout(new java.awt.BorderLayout());

        pnlUploadToolStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pnlUploadToolStatus.setMinimumSize(new java.awt.Dimension(210, 40));
        pnlUploadToolStatus.setPreferredSize(new java.awt.Dimension(200, 40));
        pnlUploadToolStatus.setRequestFocusEnabled(false);
        pnlUploadToolStatus.setLayout(new java.awt.BorderLayout());

        lblUploadTotalFiles.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblUploadTotalFiles.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblUploadTotalFiles.text")); // NOI18N
        lblUploadTotalFiles.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 0, 0));
        lblUploadTotalFiles.setMaximumSize(new java.awt.Dimension(122, 24));
        lblUploadTotalFiles.setMinimumSize(new java.awt.Dimension(122, 24));
        lblUploadTotalFiles.setPreferredSize(new java.awt.Dimension(122, 30));
        pnlUploadToolStatus.add(lblUploadTotalFiles, java.awt.BorderLayout.NORTH);

        lblUploadTotalSize.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        lblUploadTotalSize.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.lblUploadTotalSize.text")); // NOI18N
        lblUploadTotalSize.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        lblUploadTotalSize.setPreferredSize(new java.awt.Dimension(83, 30));
        pnlUploadToolStatus.add(lblUploadTotalSize, java.awt.BorderLayout.SOUTH);

        jPanel14.add(pnlUploadToolStatus, java.awt.BorderLayout.WEST);

        jPanel17.setMinimumSize(new java.awt.Dimension(100, 40));
        jPanel17.setPreferredSize(new java.awt.Dimension(240, 40));
        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 12));

        btnUploadCancel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnUploadCancel.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadCancel.text")); // NOI18N
        btnUploadCancel.setMaximumSize(new java.awt.Dimension(116, 34));
        btnUploadCancel.setMinimumSize(new java.awt.Dimension(116, 34));
        btnUploadCancel.setPreferredSize(new java.awt.Dimension(116, 38));
        btnUploadCancel.setRequestFocusEnabled(false);
        btnUploadCancel.setRolloverEnabled(true);
        btnUploadCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadCancelActionPerformed(evt);
            }
        });
        jPanel17.add(btnUploadCancel);

        btnUploadBeginImport.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnUploadBeginImport.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnUploadBeginImport.text")); // NOI18N
        btnUploadBeginImport.setEnabled(false);
        btnUploadBeginImport.setMaximumSize(new java.awt.Dimension(96, 34));
        btnUploadBeginImport.setMinimumSize(new java.awt.Dimension(96, 34));
        btnUploadBeginImport.setPreferredSize(new java.awt.Dimension(96, 38));
        btnUploadBeginImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadBeginImportActionPerformed(evt);
            }
        });
        jPanel17.add(btnUploadBeginImport);

        jPanel14.add(jPanel17, java.awt.BorderLayout.EAST);

        pnlUploadTable.add(jPanel14, java.awt.BorderLayout.PAGE_END);

        pnlIRODSUploadBrowse.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 4, 12, 2));
        pnlIRODSUploadBrowse.setPreferredSize(new java.awt.Dimension(215, 54));
        pnlIRODSUploadBrowse.setLayout(new java.awt.BorderLayout());

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel8.text")); // NOI18N
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel8.setMaximumSize(new java.awt.Dimension(64, 34));
        jLabel8.setMinimumSize(new java.awt.Dimension(64, 34));
        jLabel8.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnlIRODSUploadBrowse.add(jLabel8, java.awt.BorderLayout.WEST);

        btnBrowseIRODSUploadDest.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnBrowseIRODSUploadDest.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnBrowseIRODSUploadDest.text")); // NOI18N
        btnBrowseIRODSUploadDest.setMaximumSize(new java.awt.Dimension(155, 30));
        btnBrowseIRODSUploadDest.setMinimumSize(new java.awt.Dimension(155, 30));
        btnBrowseIRODSUploadDest.setPreferredSize(new java.awt.Dimension(140, 30));
        btnBrowseIRODSUploadDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseIRODSUploadDestActionPerformed(evt);
            }
        });
        pnlIRODSUploadBrowse.add(btnBrowseIRODSUploadDest, java.awt.BorderLayout.EAST);

        txtIRODSUploadDest.setEditable(false);
        txtIRODSUploadDest.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        txtIRODSUploadDest.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.txtIRODSUploadDest.text")); // NOI18N
        txtIRODSUploadDest.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        txtIRODSUploadDest.setDragEnabled(false);
        txtIRODSUploadDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIRODSUploadDestActionPerformed(evt);
            }
        });
        pnlIRODSUploadBrowse.add(txtIRODSUploadDest, java.awt.BorderLayout.CENTER);

        pnlUploadTable.add(pnlIRODSUploadBrowse, java.awt.BorderLayout.NORTH);

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
        pnlUploadToolbar.setPreferredSize(new java.awt.Dimension(700, 2));
        pnlUploadToolbar.setLayout(new java.awt.GridBagLayout());

        jPanel7.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel7.setPreferredSize(new java.awt.Dimension(310, 40));
        jPanel7.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.6;
        gridBagConstraints.weighty = 0.2;
        pnlUploadToolbar.add(jPanel7, gridBagConstraints);

        pnlOperationMode2.add(pnlUploadToolbar, java.awt.BorderLayout.SOUTH);

        testCardPanel.add(pnlOperationMode2, "card3");

        pnlOperationMode3.setMinimumSize(new java.awt.Dimension(250, 200));
        pnlOperationMode3.setPreferredSize(new java.awt.Dimension(700, 450));
        pnlOperationMode3.setLayout(new java.awt.BorderLayout());

        pnlDownloadModeTarget.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlDownloadModeTarget.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(450, 38));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel2.text")); // NOI18N
        jPanel1.add(jLabel2, java.awt.BorderLayout.WEST);

        txtDownloadTarget.setEditable(false);
        txtDownloadTarget.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        txtDownloadTarget.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.txtDownloadTarget.text")); // NOI18N
        txtDownloadTarget.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtDownloadTarget.setPreferredSize(new java.awt.Dimension(20, 28));
        jPanel1.add(txtDownloadTarget, java.awt.BorderLayout.CENTER);

        btnBrowseDownloadTarget.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        btnBrowseDownloadTarget.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnBrowseDownloadTarget.text")); // NOI18N
        btnBrowseDownloadTarget.setPreferredSize(new java.awt.Dimension(140, 30));
        btnBrowseDownloadTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDownloadTargetActionPerformed(evt);
            }
        });
        jPanel1.add(btnBrowseDownloadTarget, java.awt.BorderLayout.EAST);

        pnlDownloadModeTarget.add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel2.setPreferredSize(new java.awt.Dimension(100, 38));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );

        pnlDownloadModeTarget.add(jPanel2, java.awt.BorderLayout.EAST);

        pnlOperationMode3.add(pnlDownloadModeTarget, java.awt.BorderLayout.PAGE_START);

        pnlDownloadProgressTable.setLayout(new java.awt.BorderLayout());
        pnlOperationMode3.add(pnlDownloadProgressTable, java.awt.BorderLayout.CENTER);

        pnlDownloadButtons.setPreferredSize(new java.awt.Dimension(700, 40));
        pnlDownloadButtons.setLayout(new java.awt.BorderLayout());
        pnlDownloadButtons.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 8, 4));
        jPanel4.setPreferredSize(new java.awt.Dimension(160, 40));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        bntBeginDownload.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        bntBeginDownload.setLabel(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.bntBeginDownload.label")); // NOI18N
        bntBeginDownload.setMaximumSize(new java.awt.Dimension(101, 30));
        bntBeginDownload.setMinimumSize(new java.awt.Dimension(101, 30));
        bntBeginDownload.setPreferredSize(new java.awt.Dimension(101, 33));
        bntBeginDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntBeginDownloadActionPerformed(evt);
            }
        });
        jPanel4.add(bntBeginDownload);

        pnlDownloadButtons.add(jPanel4, java.awt.BorderLayout.EAST);

        pnlOperationMode3.add(pnlDownloadButtons, java.awt.BorderLayout.PAGE_END);

        testCardPanel.add(pnlOperationMode3, "card4");

        pnlIdropWebMode.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlIdropWebMode.setLayout(new java.awt.BorderLayout());

        pnlIdropWebModeDownloadTarget.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pnlIdropWebModeDownloadTarget.setPreferredSize(new java.awt.Dimension(700, 68));
        pnlIdropWebModeDownloadTarget.setRequestFocusEnabled(false);
        pnlIdropWebModeDownloadTarget.setLayout(new java.awt.BorderLayout());

        pnlIdropWebModeText.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pnlIdropWebModeText.setPreferredSize(new java.awt.Dimension(682, 30));
        pnlIdropWebModeText.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.jLabel1.text")); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(57, 18));
        jLabel1.setPreferredSize(new java.awt.Dimension(350, 26));
        pnlIdropWebModeText.add(jLabel1, java.awt.BorderLayout.CENTER);

        pnlIdropWebModeDownloadTarget.add(pnlIdropWebModeText, java.awt.BorderLayout.NORTH);

        pnlIdropWebModeDownloadDest.setPreferredSize(new java.awt.Dimension(682, 26));
        pnlIdropWebModeDownloadDest.setLayout(new java.awt.BorderLayout());

        txtIdropWebModeDownloadTarget.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.txtIdropWebModeDownloadTarget.text")); // NOI18N
        txtIdropWebModeDownloadTarget.setMinimumSize(new java.awt.Dimension(14, 24));
        txtIdropWebModeDownloadTarget.setPreferredSize(new java.awt.Dimension(400, 24));
        txtIdropWebModeDownloadTarget.setRequestFocusEnabled(false);
        pnlIdropWebModeDownloadDest.add(txtIdropWebModeDownloadTarget, java.awt.BorderLayout.CENTER);

        btnIdropWebModeTargetBrowse.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnIdropWebModeTargetBrowse.text")); // NOI18N
        btnIdropWebModeTargetBrowse.setMinimumSize(new java.awt.Dimension(88, 24));
        btnIdropWebModeTargetBrowse.setPreferredSize(new java.awt.Dimension(80, 24));
        btnIdropWebModeTargetBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdropWebModeTargetBrowseActionPerformed(evt);
            }
        });
        pnlIdropWebModeDownloadDest.add(btnIdropWebModeTargetBrowse, java.awt.BorderLayout.EAST);

        pnlIdropWebModeDownloadTarget.add(pnlIdropWebModeDownloadDest, java.awt.BorderLayout.SOUTH);

        pnlIdropWebMode.add(pnlIdropWebModeDownloadTarget, java.awt.BorderLayout.NORTH);

        jPanel6.setPreferredSize(new java.awt.Dimension(700, 60));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel16.setPreferredSize(new java.awt.Dimension(700, 10));

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 686, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel16, java.awt.BorderLayout.NORTH);

        pnlIdropWebModeProgressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 10, 15));
        pnlIdropWebModeProgressBar.setMinimumSize(new java.awt.Dimension(40, 24));
        pnlIdropWebModeProgressBar.setPreferredSize(new java.awt.Dimension(700, 24));
        pnlIdropWebModeProgressBar.setLayout(new java.awt.BorderLayout());

        pbIdropWebModeDownloadProgress.setStringPainted(true);
        pnlIdropWebModeProgressBar.add(pbIdropWebModeDownloadProgress, java.awt.BorderLayout.CENTER);

        jPanel6.add(pnlIdropWebModeProgressBar, java.awt.BorderLayout.CENTER);

        pnlIdropWebMode.add(jPanel6, java.awt.BorderLayout.CENTER);

        pnlIdropWebModeBeginDownload.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlIdropWebModeBeginDownload.setMinimumSize(new java.awt.Dimension(151, 28));
        pnlIdropWebModeBeginDownload.setPreferredSize(new java.awt.Dimension(700, 30));
        pnlIdropWebModeBeginDownload.setLayout(new java.awt.BorderLayout());

        btnIdropWebModeBeginDownload.setText(org.openide.util.NbBundle.getMessage(iDropLiteApplet.class, "iDropLiteApplet.btnIdropWebModeBeginDownload.text")); // NOI18N
        btnIdropWebModeBeginDownload.setMinimumSize(new java.awt.Dimension(145, 26));
        btnIdropWebModeBeginDownload.setPreferredSize(new java.awt.Dimension(134, 24));
        btnIdropWebModeBeginDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdropWebModeBeginDownloadActionPerformed(evt);
            }
        });
        pnlIdropWebModeBeginDownload.add(btnIdropWebModeBeginDownload, java.awt.BorderLayout.EAST);

        pnlIdropWebMode.add(pnlIdropWebModeBeginDownload, java.awt.BorderLayout.SOUTH);

        testCardPanel.add(pnlIdropWebMode, "card5");

        pnllSeems2BneededForCorrectResizing.add(testCardPanel);

        getContentPane().add(pnllSeems2BneededForCorrectResizing, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnIrodsTreeRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrodsTreeRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIrodsTreeRefreshActionPerformed

    private void btnUploadLocalRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadLocalRefreshActionPerformed
        setUpUploadLocalFileSelectTree();
    }//GEN-LAST:event_btnUploadLocalRefreshActionPerformed

    private void btnUploadMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadMoveActionPerformed

        int fileType = iDropLiteApplet.uploadFile;
        TreePath[] paths = fileUploadTree.getSelectionPaths();
        Object selectedDrive = lstUploadLocalDrives.getSelectedValue();
        if (paths != null) {
            for (TreePath path : paths) {
                DefaultTableModel tm = (DefaultTableModel) tblUploadTable1.getModel();
                // need to add selected drive to make file transfer work properly in windows browser
                String filePath = LocalFileUtils.makeLocalFilePath(path, selectedDrive);
                File localFile = new File(filePath);
                if (localFile.isDirectory()) {
                    fileType = iDropLiteApplet.uploadFolder;
                }
                Object[] rowData = new Object[5];
                rowData[0] = filePath;
                rowData[1] = 0;
                //rowData[2] = 0;
                rowData[2] = new TransferProgressInfo(this.displayMode);
                rowData[3] = Boolean.TRUE;
                rowData[4] = fileType;
                tm.addRow(rowData);
            }
        }

    }//GEN-LAST:event_btnUploadMoveActionPerformed

    private void btnUploadCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadCancelActionPerformed
        DefaultTableModel tm = (DefaultTableModel) tblUploadTable1.getModel();
        int numRows = tm.getRowCount();
        tm.getDataVector().removeAllElements();
        if (numRows > 0) {
            tm.getDataVector().removeAllElements();
            tm.fireTableRowsDeleted(0, numRows - 1);
        }
    }//GEN-LAST:event_btnUploadCancelActionPerformed

    private void btnBrowseIRODSUploadDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseIRODSUploadDestActionPerformed
        IRODSFinderDialog finderDialog = new IRODSFinderDialog(true, iDropCore);
        // get current size of applet
        int width = this.getWidth();
        int height = this.getHeight();
        if (width < 500 || height < 500) {
            finderDialog.setSize(width, height);
        } else {
            finderDialog.setSize(500, 500);
        }
        finderDialog.setLocationRelativeTo(iDropCore.findAppletParentFrame(this));
        finderDialog.setVisible(true);
        String targetPath = finderDialog.getSelectedAbsolutePath();
        if (targetPath != null) {
            //then do stuff
            log.info("upload drop target selected:{}", targetPath);
            txtIRODSUploadDest.setText(targetPath);
            btnUploadBeginImport.setEnabled(true);
        }
        finderDialog.dispose();
    }//GEN-LAST:event_btnBrowseIRODSUploadDestActionPerformed

    private void txtIRODSUploadDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIRODSUploadDestActionPerformed
        btnUploadBeginImport.setEnabled(txtIRODSUploadDest.getText().length() > 0);
    }//GEN-LAST:event_txtIRODSUploadDestActionPerformed

    private void btnUploadBeginImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadBeginImportActionPerformed

        PutTransferRunner currentTransferRunner = null;
        final List<UploadDataObj> sourceFiles = new ArrayList<UploadDataObj>();

        // make sure IRODS destination is legal
        final String targetPath = txtIRODSUploadDest.getText();
        log.info("upload destination is: {}", targetPath);

        try {
            IRODSFileService irodsFS = new IRODSFileService(iDropCore.getIrodsAccount(), iDropCore.getIrodsFileSystem());
            IRODSFile ifile = irodsFS.getIRODSFileForPath(targetPath);
            if (!ifile.isDirectory()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid IRODS destination for upload.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid IRODS destination for upload.");
            return;
        } finally {
            iDropCore.getIrodsFileSystem().closeAndEatExceptions();
        }

        // now go through and process selected import files from table
        if (!isTransferInProgress()) {

            // collect list of files and/or URLS in the table
            int rows = tblUploadTable1.getRowCount();
            this.filesInTable = rows; // reset to 0 in overall status callback when all files have been transferred
            for (int row = 0; row < rows; row++) {
                if ((Integer) tblUploadTable1.getValueAt(row, 4) == iDropLiteApplet.uploadURL) { // this is an URL
                    sourceFiles.add(new UploadDataObj((String) tblUploadTable1.getValueAt(row, 0), Boolean.TRUE));
                } else { // this is just a regular file or folder
                    sourceFiles.add(new UploadDataObj(new File((String) tblUploadTable1.getValueAt(row, 0))));
                }
            }

            // set Upload button test to Cancel
            try {
                currentTransferRunner = new PutTransferRunner(applet, targetPath, sourceFiles);
                final Thread transferThread = new Thread(currentTransferRunner);
                log.info("launching transfer thread");
                // close so that transfer thread can grab account
                irodsFileSystem.closeAndEatExceptions();
                transferThread.start();
                //transferThread.join();
            } catch (Exception e) {
                log.error("exception choosings iRODS file");
                throw new IdropRuntimeException("exception choosing irods file", e);
            } finally {
                iDropCore.getIrodsFileSystem().closeAndEatExceptions();
            }
        }

    }//GEN-LAST:event_btnUploadBeginImportActionPerformed

    private void btnBrowseDownloadTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseDownloadTargetActionPerformed
        collectDownloadTarget();
    }//GEN-LAST:event_btnBrowseDownloadTargetActionPerformed

    private void bntBeginDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntBeginDownloadActionPerformed
        GetTransferRunner currentTransferRunner = null;
        final List<File> sourceFiles = new ArrayList<File>();

        // make sure local destination is legal
        final String targetPath = txtDownloadTarget.getText();
        log.info("download destination is: {}", targetPath);

        try {
            if (!new File(targetPath).exists()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid local destination for download.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid local destination for download.");
            return;
        }

        // now go through and process selected import files from table
        if (!isTransferInProgress()) {

            IRODSFileService irodsFS = null;
            try {
                irodsFS = new IRODSFileService(iDropCore.getIrodsAccount(), iDropCore.getIrodsFileSystem());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot access iRODS file system for get.");
                log.error("cannot create irods file service");
                return;
            }

            // collect list of files in the table
            int rows = tblUploadTable1.getRowCount();
            for (int row = 0; row < rows; row++) {
                IRODSFile ifile = null;
                try {
                    ifile = irodsFS.getIRODSFileForPath((String) tblUploadTable1.getValueAt(row, 0));
                    sourceFiles.add((File) ifile);
                } catch (Exception ex) {
                    log.error("cannot access irods file for get: {}", (String) tblUploadTable1.getValueAt(row, 0));
                    ex.printStackTrace();
                }
            }
            try {
                this.filesInTable = rows; // reset to 0 in overall status callback when all files have been transferred
                // process a get
                //currentTransferRunner = new GetTransferRunner(applet, targetPath, sourceFiles, iDropCore.getTransferControlBlock());
                currentTransferRunner = new GetTransferRunner(applet, targetPath, sourceFiles);
                final Thread transferThread = new Thread(currentTransferRunner);
                log.info("launching transfer thread");
                // close so that transfer thread can grab account
                irodsFileSystem.closeAndEatExceptions();
                transferThread.start();
            } catch (Exception e) {
                log.error("exception choosings local file");
                throw new IdropRuntimeException("exception choosing locL file", e);
            } finally {
                iDropCore.getIrodsFileSystem().closeAndEatExceptions();
            }
        }
    }//GEN-LAST:event_bntBeginDownloadActionPerformed

    private void btnUploadUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadUrlActionPerformed
        UploadFromURLDialog dlgUploadFromURL = new UploadFromURLDialog(this, true);
        dlgUploadFromURL.setSize(495, 200);
        dlgUploadFromURL.setLocationRelativeTo(iDropCore.findAppletParentFrame(this));
        dlgUploadFromURL.setVisible(true);
    }//GEN-LAST:event_btnUploadUrlActionPerformed

    private void btnIdropWebModeTargetBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdropWebModeTargetBrowseActionPerformed
        collectDownloadTarget();
    }//GEN-LAST:event_btnIdropWebModeTargetBrowseActionPerformed

    private void btnIdropWebModeBeginDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdropWebModeBeginDownloadActionPerformed
        executeDownload();
    }//GEN-LAST:event_btnIdropWebModeBeginDownloadActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntBeginDownload;
    private javax.swing.JButton btnBrowseDownloadTarget;
    private javax.swing.JButton btnBrowseIRODSUploadDest;
    private javax.swing.JButton btnIdropWebModeBeginDownload;
    private javax.swing.JButton btnIdropWebModeTargetBrowse;
    private javax.swing.JButton btnIrodsTreeRefresh;
    private javax.swing.JButton btnLocalRefresh;
    private javax.swing.JToggleButton btnToggleLocalView;
    private javax.swing.JButton btnUploadBeginImport;
    private javax.swing.JButton btnUploadCancel;
    private javax.swing.JButton btnUploadLocalRefresh;
    private javax.swing.JButton btnUploadMove;
    private javax.swing.JButton btnUploadUrl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane5;
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
    private javax.swing.JProgressBar pbIdropWebModeDownloadProgress;
    private javax.swing.JPanel pnlDownloadButtons;
    private javax.swing.JPanel pnlDownloadModeTarget;
    private javax.swing.JPanel pnlDownloadProgressTable;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlIRODSUploadBrowse;
    private javax.swing.JPanel pnlIdropBottom;
    private javax.swing.JPanel pnlIdropWebMode;
    private javax.swing.JPanel pnlIdropWebModeBeginDownload;
    private javax.swing.JPanel pnlIdropWebModeDownloadDest;
    private javax.swing.JPanel pnlIdropWebModeDownloadTarget;
    private javax.swing.JPanel pnlIdropWebModeProgressBar;
    private javax.swing.JPanel pnlIdropWebModeText;
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
    private javax.swing.JPanel pnlSearchSizer;
    private javax.swing.JSplitPane pnlSplitPaneLocalRemote;
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
    private javax.swing.JPanel pnllSeems2BneededForCorrectResizing;
    private javax.swing.JProgressBar progressIntraFile;
    private javax.swing.JScrollPane scrIrodsTreeView;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JScrollPane scrollUploadLocalDrives;
    private javax.swing.JScrollPane scrollUploadLocalTree;
    private javax.swing.JTabbedPane tabIrodsViews;
    private javax.swing.JTable tblUploadTable1;
    private javax.swing.JPanel testCardPanel;
    private javax.swing.JProgressBar transferStatusProgressBar;
    private javax.swing.JTextField txtDownloadTarget;
    private javax.swing.JTextField txtIRODSUploadDest;
    private javax.swing.JTextField txtIdropWebModeDownloadTarget;
    // End of variables declaration//GEN-END:variables

    //TODO: add a dialog?
    @Override
    public CallbackResponse transferAsksWhetherToForceOperation(String irodsAbsolutePath, boolean isCollection) {
        return CallbackResponse.YES_FOR_ALL;
    }
}
