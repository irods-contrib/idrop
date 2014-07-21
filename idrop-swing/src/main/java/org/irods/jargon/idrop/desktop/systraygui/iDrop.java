/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import org.irods.jargon.conveyor.core.ConveyorCallbackListener;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.ConveyorExecutorService.RunningStatus;
import org.irods.jargon.conveyor.core.QueueStatus;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.FieldFormatHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.LocalFileUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.MessageUtil;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSRowModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.GridAccount;
import org.netbeans.swing.outline.Outline;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lisa Stillwell
 */
public class iDrop extends javax.swing.JFrame implements ActionListener,
        ItemListener, ConveyorCallbackListener {

    /**
     *
     */
    private static final long serialVersionUID = 3333150007739240884L;
    private IDROPCore iDropCore = new IDROPCore();
    private IRODSTree irodsTree = null;
    private LocalFileTree fileTree = null;
    private LocalFileSystemModel localFileModel = null;
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(iDrop.class);
    private boolean receivedStartupSignal = false;
    private TrayIcon trayIcon = null;
    private Object lastCachedInfoItem = null;
    private CheckboxMenuItem pausedItem = null;
    private boolean formShown = false;
    private TransferAccountingManagerDialog transferManagerDialog = null;

    public iDrop(final IDROPCore idropCore) {

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        iDropCore = idropCore;

    }

    /**
     * Creates new form IDrop
     */
    public iDrop() {
    }

    protected void showIdropGui() {

        if (scrollIrodsTree == null) {
            buildIdropGuiComponents();
        }

        initializeLookAndFeelSelected();

        if (irodsTree == null) {
            buildTargetTree(false);
        }

        setUpLocalFileSelectTree();
        // splitPanelTrees.setDividerLocation(0.0d);
        togglePauseTransfer.setSelected(pausedItem.getState());
        QueueStatus status = iDropCore.getConveyorService()
                .getConveyorExecutorService().getQueueStatus();
        log.info("initial queue status:{}", status);

        if (status.getRunningStatus() == RunningStatus.BUSY) {
            setUpTransferPanel(true);
        } else {
            setUpTransferPanel(false);
        }

        setUpAccountGutter();
        setVisibleComponentsAtStartup();
        enableToolbarButtons(false);
        getiDropCore().getConveyorService().registerCallbackListener(this);
        setVisible(true);

    }

    protected void buildIdropGuiComponents() throws IdropRuntimeException,
            HeadlessException {
        initComponents();

        splitPanelTrees.setResizeWeight(0.8d);

        Toolkit t = getToolkit();
        int width = t.getScreenSize().width;
        int height = t.getScreenSize().height;

        int showX = (width / 2) - (getWidth() / 2);
        int showY = (height / 2) - (getHeight() / 2);
        this.setLocation(showX, showY);

        if (getiDropCore().irodsAccount() == null) {
            log.warn("no account, exiting");
            System.exit(0);
        }

    }

    private void displayAndProcessSignOn() {
        final iDrop thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                IRODSAccount savedAccount = iDropCore.irodsAccount();
                iDropCore.setIrodsAccount(null);
                showGridManagerDialog(savedAccount);
                // can't seem to get a wait cursor to work here
                thisPanel.setCursor(Cursor
                        .getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (iDropCore.irodsAccount() == null) {
                    log.warn("no account, reverting");
                    iDropCore.setIrodsAccount(savedAccount);
                } else {
                    thisPanel.reinitializeForChangedIRODSAccount();
                }
                thisPanel.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * Startup exit to set visibility of components in iDrop GUI at startup.
     * Here is where the initial visible status of components can be specified.
     */
    private void setVisibleComponentsAtStartup() {
    }

    protected void signalIdropCoreReadyAndSplashComplete() {
        if (receivedStartupSignal) {
            log.info("already received startup signal");
        } else {
            createAndShowSystemTray();
            showIdropGui();
        }

        receivedStartupSignal = true;

    }

    private void initializeLookAndFeelSelected() {
        String lookAndFeelChoice = iDropCore.getIdropConfig()
                .getPropertyForKey(IdropConfigurationService.LOOK_AND_FEEL);
        if (lookAndFeelChoice == null || lookAndFeelChoice.isEmpty()) {
            lookAndFeelChoice = "System";
        }

    }

    public void buildTargetTree(final boolean reset) {
        log.info("building tree to look at staging resource");
        final iDrop gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                log.info("building new iRODS tree");
                try {
                    if (getTreeStagingResource() != null) {
                        if (reset) {
                            loadNewTree();
                        } else {
                            reloadExistingTree();
                        }
                    } else {
                        loadNewTree();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                            null, ex);
                    MessageManager.showError(gui, ex.getMessage());
                    throw new IdropRuntimeException(ex);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(
                            iDropCore.irodsAccount());
                    gui.setCursor(Cursor
                            .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            /**
             * A tree has not been previously loaded, establish the root (strict
             * ACLs? Login preset?)
             */
            private void loadNewTree() throws JargonException, IdropException {
                IRODSOutlineModel mdl;
                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();
                String basePath = getBasePath();
                log.info("base path set to:{}", basePath);
                if (basePath.equals("/")) {
                    root.setPathOrName(basePath);
                    root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                } else {
                    IRODSFile baseFile = iDropCore
                            .getIRODSFileFactoryForLoggedInAccount()
                            .instanceIRODSFile(basePath);
                    root.setParentPath(baseFile.getParent());
                    root.setPathOrName(baseFile.getAbsolutePath());
                    root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                    getiDropCore().setBasePath(baseFile.getAbsolutePath());

                }

                try {
                    irodsTree = new IRODSTree(gui);
                    IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(),
                            getiDropCore().getIrodsFileSystem(), irodsTree);
                    irodsTree.setRefreshingTree(true);
                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(
                            rootNode, getIrodsAccount());
                    mdl = new IRODSOutlineModel(gui, irodsFileSystemModel,
                            new IRODSRowModel(), true, "File System");
                    irodsTree.setModel(mdl);
                    scrollIrodsTree.setViewportView(irodsTree);
                } catch (IdropException ie) {
                    log.error(
                            "exception loading new tree...attempting correction by setting to root",
                            ie);
                    if (gui.getiDropCore().getBasePath().equals("/")) {
                        MessageManager.showError(irodsTree,
                                "Invalid path, cannot set a root of the tree");
                        log.error(
                                "unable to reset tree, base path already at root",
                                ie);
                        throw new IdropException(
                                "Unable to set base path of tree", ie);
                    } else {
                        MessageManager
                                .showError(irodsTree,
                                "Invalid path, attempting to set tree root back to root");
                        gui.getiDropCore().setBasePath("/");
                        loadNewTree();
                    }
                }
            }

           
            /**
             * A tree already exists so use the current information to reload
             */
            private void reloadExistingTree() throws IdropException,
                    JargonException {

                Object root = irodsTree.getOutlineModel().getRoot();

                if (root == null) {
                    log.error("cannot load node, is null");
                    throw new IdropException(
                            "Cannot load a tree with a root at the given path, use the Tree option to select a valid root");
                }

                IRODSNode currentRoot = (IRODSNode) root;

                log.debug("current tree root:{}", currentRoot);
                TreePath rootPath = TreeUtils.getPath(currentRoot);
                TreePath[] currentPaths = irodsTree.getOutlineModel()
                        .getTreePathSupport().getExpandedDescendants(rootPath);
                log.info("expanded paths:{}", currentPaths);
                irodsTree.getSelectionModel().getMinSelectionIndex();
                irodsTree.getSelectionModel().getMaxSelectionIndex();
                scrollIrodsTree.getViewport().removeAll();
                loadNewTree();
                irodsTree.getSelectionModel().setSelectionInterval(0, 0);
                if (currentPaths != null) {
                    IRODSNode irodsNode = null;
                    CollectionAndDataObjectListingEntry expandedEntry = null;
                    log.info("looking to re-expand paths...");
                    for (TreePath treePath : currentPaths) {
                        irodsNode = (IRODSNode) treePath.getLastPathComponent();
                        expandedEntry = (CollectionAndDataObjectListingEntry) irodsNode
                                .getUserObject();
                        irodsNode = (IRODSNode) TreeUtils
                                .buildTreePathForIrodsAbsolutePath(
                                irodsTree,
                                expandedEntry
                                .getFormattedAbsolutePath())
                                .getLastPathComponent();
                        irodsNode.getChildCount();
                        TreePath pathInNew = TreeUtils.getPath(irodsNode);
                        irodsTree.collapsePath(pathInNew);
                        irodsTree.expandPath(pathInNew);
                        java.awt.Rectangle rect = irodsTree
                                .getPathBounds(treePath);
                        if (rect != null) {
                            irodsTree.scrollRectToVisible(rect);

                        }
                    }
                }
            }
        });
    }

    /**
     * Method to clear any cached values when an account changes. Some data is
     * cached and lazily loaded. Rebuilds gui state for new grid.
     */
    public void reinitializeForChangedIRODSAccount() {
        log.info("clearing any cached data associated with the account");
        final iDrop idropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                lastCachedInfoItem = null;
                getiDropCore().setBasePath(null);
                idropGui.buildTargetTree(true);
                setUpAccountGutter();
            }
        });

    }

    /**
     * Builds the system tray menu and installs the iDrop icon in the system
     * tray. The iDrop GUI is displayed when the iDrop menu item is selected
     * from the system tray
     */
    protected void createAndShowSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        if (trayIcon != null) {
            log.info("system tray already shown");
            return;
        }

        final PopupMenu popup = new PopupMenu();

        final SystemTray tray = SystemTray.getSystemTray();

        if (trayIcon == null) {
            trayIcon = new TrayIcon(createImage("images/dialog-ok-2.png",
                    "tray icon"));
        }
        trayIcon.setImageAutoSize(true);

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem iDropItem = new MenuItem("iDrop");
        MenuItem preferencesItem = new MenuItem("Preferences");
        MenuItem changePassPhraseItem = new MenuItem("Change Pass Phrase");

        iDropItem.addActionListener(this);

        MenuItem currentItem = new MenuItem("Show Current and Past Activity");

        MenuItem logoutItem = new MenuItem("Grid Accounts");

        pausedItem = new CheckboxMenuItem("Pause");

        MenuItem exitItem = new MenuItem("Exit");

        exitItem.addActionListener(this);
        currentItem.addActionListener(this);
        preferencesItem.addActionListener(this);
        changePassPhraseItem.addActionListener(this);

        /*
         * See if I am in a paused state
         */

        // FIXME: conveyor
		/*
         * if (this.getiDropCore().getTransferManager().getRunningStatus() ==
         * TransferManager.RunningStatus.PAUSED) {
         * this.setTransferStatePaused(); }
         */

        logoutItem.addActionListener(this);
        pausedItem.addItemListener(this);
        aboutItem.addActionListener(this);

        // Add components to pop-up menu
        popup.add(aboutItem);
        popup.add(iDropItem);
        popup.add(preferencesItem);
        popup.add(changePassPhraseItem);
        popup.addSeparator();
        popup.add(currentItem);
        popup.addSeparator();
        popup.add(pausedItem);
        popup.addSeparator();
        popup.add(logoutItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. FIXME: move to
     * static util
     */
    protected static Image createImage(final String path,
            final String description) {
        URL imageURL = iDrop.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    /**
     * Get the current iRODS login account.
     *
     * @return <code>IRODSAccount</code> with the current iRODS connection
     * information.
     */
    public IRODSAccount getIrodsAccount() {
        synchronized (this) {
            return iDropCore.irodsAccount();
        }
    }

    /**
     * Set the current connection information.
     *
     * @return <code>IRODSAccount</code> with the current iRODS connection
     * information.
     */
    public void setIrodsAccount(final IRODSAccount irodsAccount) {
        synchronized (this) {
            iDropCore.setIrodsAccount(irodsAccount);
        }
    }

    /**
     * Returns the current iRODS remote tree view component.
     *
     * @return <code>JTree</code> visual representation of the remote iRODS
     * resource
     */
    public Outline getTreeStagingResource() {
        return irodsTree;
    }

    public IDROPCore getiDropCore() {
        return iDropCore;
    }

    public void setBusyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setNormalCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public LocalFileTree getFileTree() {
        return fileTree;
    }

    public void setFileTree(final LocalFileTree fileTree) {
        this.fileTree = fileTree;
    }

    /**
     * Set the account information in the gutter, including the available
     * resources on the grid. Note that this method should be called in the
     * context of a
     * <code>Runnable</code>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setUpAccountGutter() {
        // userNameLabel.setText(this.irodsAccount().getUserName());
        // lblZone.setText(this.irodsAccount().getZone());
        // lblHost.setText(this.irodsAccount().getHost());
        // /*
        // * Get a list of storage resources on this host
        // */
        try {
            ResourceAO resourceAO = getiDropCore()
                    .getIRODSAccessObjectFactory().getResourceAO(
                    getIrodsAccount());
            log.info("getting a list of all resources in the zone");
            List<String> resources = new ArrayList<String>();
            resources.add("");
            resources.addAll(resourceAO.listResourceAndResourceGroupNames());
            lblHost.setText(getiDropCore().irodsAccount().getHost());
            lblZone.setText(getiDropCore().irodsAccount().getZone());
            lblUserName.setText(getiDropCore().irodsAccount().getUserName());
            cbIrodsResource.setModel(new DefaultComboBoxModel(resources
                    .toArray()));
            cbIrodsResource.setSelectedItem(getIrodsAccount()
                    .getDefaultStorageResource());

        } catch (JargonException ex) {
            log.error("error getting resource list", ex);
            throw new IdropRuntimeException("error getting resource list", ex);
        }
    }

    /**
     * Establish base path (checking if strict acl's are in place.
     *
     * @return <code>String</code> with the base path for the tree
     * @throws JargonException
     */
    private synchronized String getBasePath() throws JargonException {
        String myBase = getiDropCore().getBasePath();

        if (myBase == null) {
            log.info("checking to see if the grid account has a preset path");

            try {
                GridAccount gridAccount = this.getiDropCore().getConveyorService().getGridAccountService().findGridAccountByIRODSAccount(this.getIrodsAccount());
                myBase = gridAccount.getDefaultPath();
                log.info("base from grid account preset:{}", myBase);
            } catch (ConveyorExecutionException ex) {
                log.error("error obtaining grid account for this iRODS account", ex);
                throw new IdropRuntimeException("cannot obtain grid account for given iRODS account", ex);
            }


        }

        // if no base defined, see if there is a prese
        if (myBase == null || myBase.isEmpty()) {

            if (getiDropCore().irodsAccount().isAnonymousAccount()) {
                log.info("user is anonymous, default to view the public directory");
                myBase = MiscIRODSUtils.computePublicDirectory(getiDropCore()
                        .irodsAccount());

            } else {

                if (iDropCore.getIdropConfig().isLoginPreset()) {
                    log.info("using policy preset home directory");
                    StringBuilder sb = new StringBuilder();
                    sb.append("/");
                    sb.append(getIrodsAccount().getZone());
                    sb.append("/");
                    sb.append("home");
                    myBase = sb.toString();
                } else {

                    // look up the strict acl setting for the server, if strict
                    // acl, home the person in their user directory
                    EnvironmentalInfoAO environmentalInfoAO = getiDropCore()
                            .getIRODSAccessObjectFactory()
                            .getEnvironmentalInfoAO(
                            getiDropCore().irodsAccount());

                    // overhead for [#1362] apparent start-up errors idrop
                    // checking for strict acls

                    boolean isStrict = false;

                    try {
                        isStrict = environmentalInfoAO.isStrictACLs();
                    } catch (JargonException je) {
                        log.error("error checking is strict, warn and set to false");
                        MessageUtil
                                .showWarning(
                                this,
                                "Error checking if strict ACLS, assuming not strict",
                                "");
                    }

                    log.info("is strict?:{}", isStrict);

                    if (isStrict) {
                        myBase = MiscIRODSUtils
                                .computeHomeDirectoryForIRODSAccount(iDropCore
                                .irodsAccount());
                    } else {
                        myBase = "/";
                    }

                }
            }
        }
        getiDropCore().setBasePath(myBase);
        return myBase;

    }

    /**
     * Get the JTree component that represents the iRODS file system in the
     * iDrop gui.
     *
     * @return <code>IRODSTree</code> that is the JTree component for the iRODS
     * file system view.
     */
    public IRODSTree getIrodsTree() {
        return irodsTree;
    }

    /**
     * Set up a JTree that depicts the local file system
     */
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
        final iDrop gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                initializeLocalFileTreeModel(null);
                fileTree = new LocalFileTree(localFileModel, gui);
                listLocalDrives.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {
                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        log.debug("new local file system model");
                        log.debug("selection event:{}", e);
                        Object selectedItem = listLocalDrives
                                .getSelectedValue();
                        initializeLocalFileTreeModelWhenDriveIsSelected(selectedItem);

                    }
                });
                scrollLocalFileTree.setViewportView(fileTree);
                pnlLocalTreeArea.add(scrollLocalFileTree,
                        java.awt.BorderLayout.CENTER);
            }
        });

    }

    private void initializeLocalFileTreeModelWhenDriveIsSelected(
            final Object selectedDrive) {
        if (selectedDrive == null) {
            log.debug("selected drive is null, use the first one");
            listLocalDrives.setSelectedIndex(0);

            localFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File((String) listLocalDrives.getSelectedValue())));

            fileTree.setModel(localFileModel);
        } else {
            log.debug(
                    "selected drive is not null, create new root based on selection",
                    selectedDrive);
            listLocalDrives.setSelectedValue(selectedDrive, true);
            localFileModel = new LocalFileSystemModel(new LocalFileNode(
                    new File(((String) selectedDrive))));
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

            listLocalDrives.setModel(listModel);

            scrollLocalDrives.setVisible(true);
        }
    }

    /**
     * Display an error message dialog that indicates an exception has occcurred
     *
     * @param idropException
     */
    public void showIdropException(final Exception idropException) {
        JOptionPane.showMessageDialog(this, idropException.getMessage(),
                "iDROP Exception", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Utility method to display a dialog with a message.
     *
     * @param messageFromOperation
     */
    public void showMessageFromOperation(final String messageFromOperation) {

        final iDrop thisIdropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(thisIdropGui,
                        messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Handler for iDrop system tray menu options.
     *
     * @param e
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        Toolkit toolkit = getToolkit();

        if (e.getActionCommand().equals("Exit")) {
            shutdownWithConfirmation();
        } else if (e.getActionCommand().equals("Grid Accounts")) {
            log.info("logging out to log in to a new grid");

            displayAndProcessSignOn();

        } else if (e.getActionCommand().equals("About")) {
            AboutDialog aboutDialog = new AboutDialog(this, true);
            int x = (toolkit.getScreenSize().width - aboutDialog.getWidth()) / 2;
            int y = (toolkit.getScreenSize().height - aboutDialog.getHeight()) / 2;
            aboutDialog.setLocation(x, y);
            aboutDialog.setVisible(true);
        } else if (e.getActionCommand().equals("Preferences")) {
            IDROPConfigurationPanel idropConfigurationPanel = new IDROPConfigurationPanel(
                    this, true, iDropCore);
            idropConfigurationPanel.setLocationRelativeTo(null);
            idropConfigurationPanel.setVisible(true);

        } else if (e.getActionCommand().equals("Change Pass Phrase")) {
            ChangePassPhraseDialog changePassPhraseDialog = new ChangePassPhraseDialog(
                    this, true, iDropCore);
            int x = (toolkit.getScreenSize().width - changePassPhraseDialog
                    .getWidth()) / 2;
            int y = (toolkit.getScreenSize().height - changePassPhraseDialog
                    .getHeight()) / 2;
            changePassPhraseDialog.setLocation(x, y);
            changePassPhraseDialog.setVisible(true);

        } else if (e.getActionCommand()
                .equals("Show Current and Past Activity")) {

            log.info("showing recent items in queue");
            showQueueManagerDialog();

        } else {

            if (!formShown) {

                showIdropGui();

            } else {
                // refresh the tree when setting visible again, the account may
                // have changed.

                buildTargetTree(false);
                setVisible(true);
            }

            toFront();
        }

    }

    private boolean showQueueManagerDialog() {

        try {
            if (transferManagerDialog == null) {
                transferManagerDialog = new TransferAccountingManagerDialog(
                        this);
                Toolkit tk = getToolkit();
                int x = (tk.getScreenSize().width - transferManagerDialog
                        .getWidth()) / 2;
                int y = (tk.getScreenSize().height - transferManagerDialog
                        .getHeight()) / 2;
                transferManagerDialog.setLocation(x, y);
                transferManagerDialog.setModal(false);
            } else {
                transferManagerDialog.refreshTableView();
            }
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            showIdropException(ex);
            return true;
        }

        transferManagerDialog.setVisible(true);
        // transferManagerDialog.toFront();

        return false;
    }

    /**
     * Indicate that the GUI should reflect a paused state
     *
     */
    public void setTransferStatePaused() {
        if (pausedItem != null) {
            pausedItem.setState(true);
        }

    }

    /**
     * Indicate that the gui should show an unpaused state.
     */
    public void setTransferStateUnpaused() {
        if (pausedItem != null) {
            pausedItem.setState(false);
        }

    }

    @Override
    public void itemStateChanged(final ItemEvent e) {

        if (e.getItem().equals("Pause")) {
            // FIXME: conveyor
			/*
             * try { if (pausedItem.getState() == true) {
             * log.info("pausing...."); iDropCore.getTransferManager().pause();
             * } else { log.info("resuming queue");
             * iDropCore.getTransferManager().resume(); } } catch (Exception ex)
             * { Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null,
             * ex); }
             */
        }
    }

    private void shutdownWithConfirmation() {
        int result = JOptionPane
                .showConfirmDialog(this, "Shut down iDrop?",
                "Do you want to shut down iDrop?",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            log.info("shut down queue timer");
            iDropCore.getQueueTimer().cancel();
            log.info("saving current configuration to idrop.properties");
            iDropCore.getIdropConfigurationService()
                    .saveConfigurationToPropertiesFile();
            log.info("properties saved");
        } catch (IdropException ex) {
            log.error("iDrop exception on shutdown will be ignored", ex);
        }
        System.exit(0);
    }

    /**
     * Update the system tray icon based on the current status.
     *
     * @param iconFile
     */
    public void updateIcon(final String iconFile) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                /*
                 * listener events may occur at startup before the GUI is fully
                 * prepared, ignore these
                 */
                if (trayIcon == null) {
                    return;
                }

                Image newIcon = createImage(iconFile, "icon");

                trayIcon.setImage(newIcon);
                ImageIcon icon = new ImageIcon();
                icon.setImage(newIcon);
                lblStatusIcon.setIcon(icon);

            }
        });
    }

    public Object getLastCachedInfoItem() {
        return lastCachedInfoItem;
    }

    public void triggerInfoPanelUpdate() throws IdropRuntimeException {

        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                idropGui.setCursor(Cursor
                        .getPredefinedCursor(Cursor.WAIT_CURSOR));

                IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) getIrodsTree()
                        .getModel();

                ListSelectionModel selectionModel = getIrodsTree()
                        .getSelectionModel();
                int idx = selectionModel.getLeadSelectionIndex();

                // use first selection for info
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                        .getValueAt(idx, 0);
                log.info("selected node to initialize info panel:{}",
                        selectedNode);

                iDropCore.closeIRODSConnectionForLoggedInAccount();
                idropGui.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        });

    }

    // Update state of toolbar buttons when iRODS tree nodes are selected
    public void triggerToolbarUpdate() throws IdropRuntimeException {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) getIrodsTree()
                        .getModel();

                // first check for selected item in iRODS tree
                ListSelectionModel selectionModel = getIrodsTree()
                        .getSelectionModel();
                int idx = selectionModel.getLeadSelectionIndex();
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                        .getValueAt(idx, 0);


                if (selectedNode == null) {
                    return;
                }

                selectedNode.getFullPath();

                selectedNode.getUserObject();

                enableToolbarButtons(idx >= 0);

            }
        });
    }

    private void enableToolbarButtons(final boolean state) {
        btnMainToolbarInfo.setEnabled(state);
        btnMainToolbarCopy.setEnabled(state);
        btnMainToolbarDelete.setEnabled(state);

    }

    /**
     * A transfer confirm dialog
     *
     * @param sourcePath <code>String</code> with the source path of the
     * transfer
     * @param targetPath <code>String</code> with the target of the transfer
     * @return <code>int</code> with the dialog user response.
     */
    public int showTransferConfirm(final String sourcePath,
            final String targetPath) {

        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to transfer from ");
        sb.append(sourcePath);
        sb.append(" to ");
        sb.append(targetPath);

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(),
                "Transfer Confirmaiton", JOptionPane.YES_NO_OPTION);

        return n;
    }

    /**
     * Status callback per file, or intra-file, from the transfer manager
     *
     * @param ts
     */
    @Override
    public FileStatusCallbackResponse statusCallback(final TransferStatus ts) {

        log.info("transfer status callback to iDROP:{}", ts);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (ts.getTransferState() == TransferStatus.TransferState.FAILURE) {
                    // an error occurs, stop the transfer
                    log.error("error occurred in transfer: {}", ts);

                } else if (ts.isIntraFileStatusReport()) {

                    log.debug("transferred so far:{}", ts.getBytesTransfered());
                    log.debug("total bytes:{}", ts.getTotalSize());
                    float rawPct = (float) ts.getBytesTransfered()
                            / ts.getTotalSize();
                    int percentDone = (int) (rawPct * 100F);
                    log.info("pct done:{}", percentDone);

                    progressIntraFile.setValue(percentDone);

                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {

                    // start of a file operation
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum(100);
                    progressIntraFile.setValue(0);

                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts
                            .getSourceFileAbsolutePath()));
                    transferStatusProgressBar.setString(FieldFormatHelper
                            .formatFileProgress(ts.getTotalFilesToTransfer(),
                            ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {

                    progressIntraFile.setValue(100);

                    transferStatusProgressBar.setMaximum(ts
                            .getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts
                            .getTotalFilesTransferredSoFar());
                    transferStatusProgressBar.setString(FieldFormatHelper
                            .formatFileProgress(ts.getTotalFilesToTransfer(),
                            ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));

                } else {

                    transferStatusProgressBar.setMaximum(ts
                            .getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts
                            .getTotalFilesTransferredSoFar());
                    transferStatusProgressBar.setString(FieldFormatHelper
                            .formatFileProgress(ts.getTotalFilesToTransfer(),
                            ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));
                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts
                            .getSourceFileAbsolutePath()));
                }
            }
        });

        return FileStatusCallbackResponse.CONTINUE;

    }

    /**
     * Be able to do things to the transfer panel
     *
     * @param isBegin
     */
    private void setUpTransferPanel(final boolean isBegin) {
        if (isBegin) {
            pnlCurrentTransferStatus.setVisible(true);
        } else {
            pnlCurrentTransferStatus.setVisible(true);
        }
    }

    /**
     * Implementation of transfer manager callback. The overall status callback
     * represents the start and completion of a transfer operation
     *
     * @param ts
     */
    @Override
    public void overallStatusCallback(final TransferStatus ts) {

        final IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree
                .getModel();
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION
                        || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    transferStatusProgressBar.setString(FieldFormatHelper
                            .formatFileProgress(ts.getTotalFilesToTransfer(),
                            ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));
                    idropGui.setUpTransferPanel(true);
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION
                        || ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
                    idropGui.setUpTransferPanel(false);
                }

                /*
                 * Handle appropriate tree notifications, so some filtering to
                 * prevent notifications when for a different host/zone
                 */
                if (ts.getTransferType() == TransferStatus.TransferType.SYNCH
                        || ts.getTransferType() == TransferStatus.TransferType.REPLICATE) {
                    log.info("no need to notify tree for synch or replicate");
                } else if (ts.getTransferType() == TransferStatus.TransferType.GET
                        && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                    try {

                        LocalFileSystemModel localFileSystemModel = (LocalFileSystemModel) idropGui.getFileTree()
                                .getModel();


                        if (localFileSystemModel != null) {
                            localFileSystemModel.notifyCompletionOfOperation(
                                    idropGui.getFileTree(), ts);
                        }

                    } catch (IdropException ex) {
                        log.error("error on tree notify after operation", ex);
                        throw new IdropRuntimeException(
                                "error processing overall status callback", ex);
                    }
                } else if (ts.getTransferType() == TransferStatus.TransferType.COPY
                        || ts.getTransferType() == TransferStatus.TransferType.PUT) {
                    if (ts.getTransferZone().equals(
                            iDropCore.irodsAccount().getZone())
                            && ts.getTransferHost().equals(
                            iDropCore.irodsAccount().getHost())) {
                        try {
                            // should leave PUT, and COPY
                            irodsTreeModel.notifyCompletionOfOperation(
                                    irodsTree, ts);
                        } catch (IdropException ex) {
                            log.error("error on tree notify after operation",
                                    ex);
                            throw new IdropRuntimeException(
                                    "error processing overall status callback",
                                    ex);
                        }
                    }
                }

                /*
                 * Handle progress bar and messages. These are cleared on
                 * overall initiation
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION
                        || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    clearProgressBar();
                    // on initiation, clear and reset the status bar info
                    lblTransferType.setText(ts.getTransferType().name());
                    transferStatusProgressBar.setString(FieldFormatHelper
                            .formatFileProgress(ts.getTotalFilesToTransfer(),
                            ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper
                            .formatByteProgress(ts.getTotalSize(),
                            ts.getBytesTransfered(), 0));
                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts
                            .getSourceFileAbsolutePath()));
                    transferStatusProgressBar.setMinimum(0);
                    transferStatusProgressBar.setMaximum(ts
                            .getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(0);
                }

                /*
                 * Handle any text messages
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    lblTransferMessage.setText("Synchronization Initializing");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_GENERATION) {
                    lblTransferMessage
                            .setText("Synchronization looking for updates");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_RESOLVE_STEP) {
                    lblTransferMessage.setText("Synchronizing differences");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
                    lblTransferMessage.setText("Synchronization complete");
                } else if (ts.getTransferEnclosingType() == TransferStatus.TransferType.SYNCH) {
                    lblTransferMessage
                            .setText("Transfer to synchronize local and iRODS");
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION) {
                    // initiation not within a synch
                    lblTransferMessage.setText("Processing a "
                            + ts.getTransferType().name() + " operation");
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                    // initiation not within a synch
                    lblTransferMessage.setText("Completed a "
                            + ts.getTransferType().name() + " operation");
                }

            }
        });
    }

    /**
     * Call from a swing event queue runnable
     */
    private synchronized void clearProgressBar() {
        lblTransferType.setText("");
        lblCurrentFile.setText("");
        transferStatusProgressBar.setMinimum(0);
        transferStatusProgressBar.setMaximum(100);
        transferStatusProgressBar.setValue(0);
        transferStatusProgressBar.setString("");
        progressIntraFile.setString("");
        progressIntraFile.setMinimum(0);
        progressIntraFile.setMaximum(0);
        progressIntraFile.setValue(0);
        progressIntraFile.setString("");
    }

    /**
     * Callback from conveyor asking how to handle an overwrite situation
     *
     * @param irodsAbsolutePath
     * @param isCollection
     * @return
     */
    @Override
    public CallbackResponse transferAsksWhetherToForceOperation(
            final String irodsAbsolutePath, final boolean isCollection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Callback from conveyor framework with current running and error status of
     * the transfer queue
     *
     * @param qs {@link QueueStatus}
     */
    @Override
    public void setQueueStatus(final QueueStatus qs) {
        iDropCore.getIconManager().setRunningStatus(qs.getRunningStatus());
        iDropCore.getIconManager().setErrorStatus(qs.getErrorStatus());
        if (qs.getRunningStatus() == RunningStatus.PAUSED) {
            setTransferStatePaused();
        } else {
            setTransferStateUnpaused();
        }
    }

    /**
     * Callback from conveyor framework indicating that the conveyor transfer
     * manager cannot operate and manage the state of transfers, this is really
     * a fatal condition.
     *
     * @param excptn
     */
    @Override
    public void signalUnhandledConveyorException(final Exception excptn) {
        log.error("exception is occurring in conveyor framework", excptn);
        MessageManager.showError(this, excptn.getMessage(),
                MessageManager.TITLE_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnMainToolbarSync = new javax.swing.JButton();
        pnlMainToolbarIcons = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnMainToolbarTree = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarDownload = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarUpload = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarRefresh = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnNewFolder = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarCopy = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarDelete = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarInfo = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(25, 0), new java.awt.Dimension(2, 32767));
        jSeparator2 = new javax.swing.JToolBar.Separator();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarTools = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarSettings = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarQueueMgr = new javax.swing.JButton();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnMainToolbarGrids = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(2, 32767));
        btnSynch = new javax.swing.JButton();
        pnlBreadCrumbNav = new javax.swing.JPanel();
        pnlMainIrodsTree = new javax.swing.JPanel();
        splitPanelTrees = new javax.swing.JSplitPane();
        scrollIrodsTree = new javax.swing.JScrollPane();
        pnlLocalTreeArea = new javax.swing.JPanel();
        pnlLocalRoots = new javax.swing.JPanel();
        scrollLocalDrives = new javax.swing.JScrollPane();
        listLocalDrives = new javax.swing.JList();
        pnlDrivesFiller = new javax.swing.JPanel();
        scrollLocalFileTree = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pnlMainTransferStatus = new javax.swing.JPanel();
        pnlIdropBottom = new javax.swing.JPanel();
        pnlCurrentTransferStatus = new javax.swing.JPanel();
        lblCurrentFile = new javax.swing.JLabel();
        progressIntraFile = new javax.swing.JProgressBar();
        lblTransferFilesCounts = new javax.swing.JLabel();
        transferStatusProgressBar = new javax.swing.JProgressBar();
        lblTransferType = new javax.swing.JLabel();
        lblTransferMessage = new javax.swing.JLabel();
        lblTransferByteCounts = new javax.swing.JLabel();
        pnlTransferOptions = new javax.swing.JPanel();
        idropProgressPanelToolbar = new javax.swing.JToolBar();
        togglePauseTransfer = new javax.swing.JToggleButton();
        progressIconImageLabel = new javax.swing.JLabel();
        pnlIdropGutter = new javax.swing.JPanel();
        lblHostLabel = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        lblZoneLabel = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        lblUserNameLabel = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblResource = new javax.swing.JLabel();
        cbIrodsResource = new javax.swing.JComboBox();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        pnlStatus = new javax.swing.JPanel();
        lblStatusIcon = new javax.swing.JLabel();

        btnMainToolbarSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_457_transfer.png"))); // NOI18N
        btnMainToolbarSync.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarSync.text")); // NOI18N
        btnMainToolbarSync.setBorder(null);
        btnMainToolbarSync.setFocusable(false);
        btnMainToolbarSync.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarSync.setMargin(null);
        btnMainToolbarSync.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarSyncActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(622, 158));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlMainToolbarIcons.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlMainToolbarIcons.setFocusTraversalPolicyProvider(true);
        pnlMainToolbarIcons.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnMainToolbarTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_119_table.png"))); // NOI18N
        btnMainToolbarTree.setMnemonic('t');
        btnMainToolbarTree.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarTree.text")); // NOI18N
        btnMainToolbarTree.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarTree.toolTipText")); // NOI18N
        btnMainToolbarTree.setBorder(null);
        btnMainToolbarTree.setFocusable(false);
        btnMainToolbarTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarTree.setMargin(new java.awt.Insets(0, 0, 0, 4));
        btnMainToolbarTree.setMinimumSize(new java.awt.Dimension(30, 0));
        btnMainToolbarTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarTreeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarTree);
        jToolBar1.add(filler1);

        btnMainToolbarDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_181_download_alt.png"))); // NOI18N
        btnMainToolbarDownload.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarDownload.text")); // NOI18N
        btnMainToolbarDownload.setBorder(null);
        btnMainToolbarDownload.setFocusable(false);
        btnMainToolbarDownload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarDownload.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnMainToolbarDownload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarDownloadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarDownload);
        jToolBar1.add(filler2);

        btnMainToolbarUpload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_415_disk_open.png"))); // NOI18N
        btnMainToolbarUpload.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarUpload.text")); // NOI18N
        btnMainToolbarUpload.setBorder(null);
        btnMainToolbarUpload.setFocusable(false);
        btnMainToolbarUpload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarUpload.setMargin(null);
        btnMainToolbarUpload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarUploadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarUpload);
        jToolBar1.add(filler3);

        btnMainToolbarRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_081_refresh.png"))); // NOI18N
        btnMainToolbarRefresh.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarRefresh.text")); // NOI18N
        btnMainToolbarRefresh.setBorder(null);
        btnMainToolbarRefresh.setFocusable(false);
        btnMainToolbarRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarRefresh.setMargin(null);
        btnMainToolbarRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarRefresh);
        jToolBar1.add(filler4);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler15);

        btnNewFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_145_folder_plus.png"))); // NOI18N
        btnNewFolder.setMnemonic('n');
        btnNewFolder.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnNewFolder.text")); // NOI18N
        btnNewFolder.setActionCommand(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnNewFolder.actionCommand")); // NOI18N
        btnNewFolder.setBorder(null);
        btnNewFolder.setFocusable(false);
        btnNewFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewFolder.setMargin(null);
        btnNewFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewFolderActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNewFolder);
        jToolBar1.add(filler5);

        btnMainToolbarCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_318_more_items.png"))); // NOI18N
        btnMainToolbarCopy.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarCopy.text")); // NOI18N
        btnMainToolbarCopy.setActionCommand(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarCopy.actionCommand")); // NOI18N
        btnMainToolbarCopy.setBorder(null);
        btnMainToolbarCopy.setFocusable(false);
        btnMainToolbarCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarCopy.setMargin(null);
        btnMainToolbarCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarCopy);
        jToolBar1.add(filler6);

        btnMainToolbarDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnMainToolbarDelete.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarDelete.text_1")); // NOI18N
        btnMainToolbarDelete.setBorder(null);
        btnMainToolbarDelete.setFocusable(false);
        btnMainToolbarDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarDelete.setMargin(null);
        btnMainToolbarDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarDelete);
        jToolBar1.add(filler7);

        btnMainToolbarInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_195_circle_info.png"))); // NOI18N
        btnMainToolbarInfo.setBorderPainted(false);
        btnMainToolbarInfo.setFocusable(false);
        btnMainToolbarInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarInfo.setLabel(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarInfo.label")); // NOI18N
        btnMainToolbarInfo.setMargin(null);
        btnMainToolbarInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarInfoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarInfo);
        jToolBar1.add(filler8);
        jToolBar1.add(jSeparator2);
        jToolBar1.add(filler9);

        btnMainToolbarTools.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_280_settings.png"))); // NOI18N
        btnMainToolbarTools.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarTools.text")); // NOI18N
        btnMainToolbarTools.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarTools.toolTipText")); // NOI18N
        btnMainToolbarTools.setBorder(null);
        btnMainToolbarTools.setFocusable(false);
        btnMainToolbarTools.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarTools.setMargin(null);
        btnMainToolbarTools.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarTools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarToolsActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarTools);
        jToolBar1.add(filler10);

        btnMainToolbarSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_139_adjust_alt.png"))); // NOI18N
        btnMainToolbarSettings.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarSettings.text")); // NOI18N
        btnMainToolbarSettings.setBorder(null);
        btnMainToolbarSettings.setFocusable(false);
        btnMainToolbarSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarSettings.setMargin(null);
        btnMainToolbarSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarSettingsActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarSettings);
        jToolBar1.add(filler11);
        jToolBar1.add(filler14);

        btnMainToolbarQueueMgr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_136_cogwheel.png"))); // NOI18N
        btnMainToolbarQueueMgr.setMnemonic('Q');
        btnMainToolbarQueueMgr.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarQueueMgr.text")); // NOI18N
        btnMainToolbarQueueMgr.setBorder(null);
        btnMainToolbarQueueMgr.setFocusable(false);
        btnMainToolbarQueueMgr.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarQueueMgr.setMargin(null);
        btnMainToolbarQueueMgr.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarQueueMgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarQueueMgrActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarQueueMgr);
        jToolBar1.add(filler12);

        btnMainToolbarGrids.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_003_user.png"))); // NOI18N
        btnMainToolbarGrids.setMnemonic('G');
        btnMainToolbarGrids.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarGrids.text")); // NOI18N
        btnMainToolbarGrids.setBorder(null);
        btnMainToolbarGrids.setFocusable(false);
        btnMainToolbarGrids.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarGrids.setMargin(null);
        btnMainToolbarGrids.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarGrids.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarGridsActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMainToolbarGrids);
        jToolBar1.add(filler13);

        btnSynch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_428_git_compare.png"))); // NOI18N
        btnSynch.setMnemonic('s');
        btnSynch.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnSynch.text")); // NOI18N
        btnSynch.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnSynch.toolTipText")); // NOI18N
        btnSynch.setBorderPainted(false);
        btnSynch.setFocusable(false);
        btnSynch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSynch.setPreferredSize(new java.awt.Dimension(29, 40));
        btnSynch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSynch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSynchActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSynch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlMainToolbarIcons.add(jToolBar1, gridBagConstraints);

        pnlBreadCrumbNav.setMaximumSize(new java.awt.Dimension(20, 20));
        pnlBreadCrumbNav.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlMainToolbarIcons.add(pnlBreadCrumbNav, gridBagConstraints);

        getContentPane().add(pnlMainToolbarIcons, java.awt.BorderLayout.NORTH);

        pnlMainIrodsTree.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMainIrodsTree.setPreferredSize(new java.awt.Dimension(834, 395));
        pnlMainIrodsTree.setLayout(new java.awt.BorderLayout());

        splitPanelTrees.setBorder(null);
        splitPanelTrees.setDividerLocation(150);
        splitPanelTrees.setAutoscrolls(true);
        splitPanelTrees.setLastDividerLocation(200);
        splitPanelTrees.setPreferredSize(new java.awt.Dimension(834, 360));

        scrollIrodsTree.setBorder(null);
        splitPanelTrees.setRightComponent(scrollIrodsTree);

        pnlLocalTreeArea.setBackground(new java.awt.Color(153, 255, 102));
        pnlLocalTreeArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlLocalTreeArea.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalTreeArea.setOpaque(false);
        pnlLocalTreeArea.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlLocalTreeArea.setLayout(new java.awt.BorderLayout());

        pnlLocalRoots.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLocalRoots.setLayout(new java.awt.BorderLayout());

        scrollLocalDrives.setMaximumSize(null);
        scrollLocalDrives.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollLocalDrives.setPreferredSize(new java.awt.Dimension(300, 100));

        listLocalDrives.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listLocalDrives.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listLocalDrives.setMaximumSize(null);
        listLocalDrives.setPreferredSize(new java.awt.Dimension(150, 200));
        listLocalDrives.setVisibleRowCount(4);
        scrollLocalDrives.setViewportView(listLocalDrives);

        pnlLocalRoots.add(scrollLocalDrives, java.awt.BorderLayout.CENTER);
        pnlLocalRoots.add(pnlDrivesFiller, java.awt.BorderLayout.SOUTH);

        pnlLocalTreeArea.add(pnlLocalRoots, java.awt.BorderLayout.NORTH);

        scrollLocalFileTree.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        scrollLocalFileTree.setBorder(null);
        scrollLocalFileTree.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.scrollLocalFileTree.toolTipText")); // NOI18N
        scrollLocalFileTree.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollLocalFileTree.setMaximumSize(null);
        scrollLocalFileTree.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollLocalFileTree.setPreferredSize(new java.awt.Dimension(500, 500));
        pnlLocalTreeArea.add(scrollLocalFileTree, java.awt.BorderLayout.CENTER);

        splitPanelTrees.setLeftComponent(pnlLocalTreeArea);

        pnlMainIrodsTree.add(splitPanelTrees, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel3.setPreferredSize(new java.awt.Dimension(400, 35));
        jPanel1.add(jPanel3, java.awt.BorderLayout.EAST);

        pnlMainIrodsTree.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMainIrodsTree, java.awt.BorderLayout.CENTER);

        pnlMainTransferStatus.setPreferredSize(new java.awt.Dimension(835, 120));
        pnlMainTransferStatus.setLayout(new java.awt.BorderLayout());

        pnlIdropBottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlIdropBottom.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.pnlIdropBottom.toolTipText")); // NOI18N
        pnlIdropBottom.setMinimumSize(new java.awt.Dimension(166, 66));
        pnlIdropBottom.setLayout(new java.awt.BorderLayout());

        pnlCurrentTransferStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        pnlCurrentTransferStatus.setMinimumSize(new java.awt.Dimension(100, 132));
        pnlCurrentTransferStatus.setLayout(new java.awt.GridBagLayout());

        lblCurrentFile.setMaximumSize(new java.awt.Dimension(999, 999));
        lblCurrentFile.setMinimumSize(new java.awt.Dimension(30, 10));
        lblCurrentFile.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 0);
        pnlCurrentTransferStatus.add(lblCurrentFile, gridBagConstraints);

        progressIntraFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        progressIntraFile.setMinimumSize(new java.awt.Dimension(10, 60));
        progressIntraFile.setString(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.progressIntraFile.string")); // NOI18N
        progressIntraFile.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentTransferStatus.add(progressIntraFile, gridBagConstraints);

        lblTransferFilesCounts.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblTransferFilesCounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentTransferStatus.add(lblTransferFilesCounts, gridBagConstraints);

        transferStatusProgressBar.setMinimumSize(new java.awt.Dimension(10, 60));
        transferStatusProgressBar.setString(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.transferStatusProgressBar.string")); // NOI18N
        transferStatusProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8.0;
        pnlCurrentTransferStatus.add(transferStatusProgressBar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlCurrentTransferStatus.add(lblTransferType, gridBagConstraints);

        lblTransferMessage.setForeground(java.awt.Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentTransferStatus.add(lblTransferMessage, gridBagConstraints);

        lblTransferByteCounts.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblTransferByteCounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentTransferStatus.add(lblTransferByteCounts, gridBagConstraints);

        pnlIdropBottom.add(pnlCurrentTransferStatus, java.awt.BorderLayout.CENTER);

        idropProgressPanelToolbar.setFloatable(false);
        idropProgressPanelToolbar.setRollover(true);

        togglePauseTransfer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_174_pause.png"))); // NOI18N
        togglePauseTransfer.setMnemonic('p');
        togglePauseTransfer.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.togglePauseTransfer.text")); // NOI18N
        togglePauseTransfer.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.togglePauseTransfer.toolTipText")); // NOI18N
        togglePauseTransfer.setFocusable(false);
        togglePauseTransfer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togglePauseTransfer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togglePauseTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePauseTransferActionPerformed(evt);
            }
        });
        idropProgressPanelToolbar.add(togglePauseTransfer);

        pnlTransferOptions.add(idropProgressPanelToolbar);
        pnlTransferOptions.add(progressIconImageLabel);

        pnlIdropBottom.add(pnlTransferOptions, java.awt.BorderLayout.EAST);

        pnlMainTransferStatus.add(pnlIdropBottom, java.awt.BorderLayout.NORTH);

        pnlIdropGutter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlIdropGutter.setMinimumSize(new java.awt.Dimension(591, 45));
        pnlIdropGutter.setPreferredSize(new java.awt.Dimension(591, 45));
        pnlIdropGutter.setLayout(new java.awt.GridBagLayout());

        lblHostLabel.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblHostLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlIdropGutter.add(lblHostLabel, gridBagConstraints);

        lblHost.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblHost.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlIdropGutter.add(lblHost, gridBagConstraints);

        lblZoneLabel.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblZoneLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlIdropGutter.add(lblZoneLabel, gridBagConstraints);

        lblZone.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblZone.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlIdropGutter.add(lblZone, gridBagConstraints);

        lblUserNameLabel.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblUserNameLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlIdropGutter.add(lblUserNameLabel, gridBagConstraints);

        lblUserName.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblUserName.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlIdropGutter.add(lblUserName, gridBagConstraints);

        lblResource.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblResource.text_1")); // NOI18N
        lblResource.setToolTipText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblResource.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlIdropGutter.add(lblResource, gridBagConstraints);

        cbIrodsResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIrodsResourceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlIdropGutter.add(cbIrodsResource, gridBagConstraints);
        pnlIdropGutter.add(filler16, new java.awt.GridBagConstraints());

        pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        lblStatusIcon.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.lblStatusIcon.text_1")); // NOI18N
        pnlStatus.add(lblStatusIcon);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlIdropGutter.add(pnlStatus, gridBagConstraints);

        pnlMainTransferStatus.add(pnlIdropGutter, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMainTransferStatus, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSynchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynchActionPerformed

        SynchronizationDialog synchDialog = new SynchronizationDialog(this, true, this.getiDropCore());
        synchDialog.setVisible(true);

    }//GEN-LAST:event_btnSynchActionPerformed

    /**
     * Show a modal dialog with extended tools
     *
     * @param evt
     */
    private void btnMainToolbarToolsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarToolsActionPerformed
        ToolsDialog toolsDialog = new ToolsDialog(this, true);
        toolsDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        toolsDialog.setVisible(true);
    }// GEN-LAST:event_btnMainToolbarToolsActionPerformed

    private void btnMainToolbarTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarTreeActionPerformed
        NavPopupDialog navDialog = new NavPopupDialog(this, true);
        navDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        navDialog.setVisible(true);
    }// GEN-LAST:event_btnMainToolbarTreeActionPerformed

    private void btnMainToolbarSettingsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarSettingsActionPerformed
        IDROPConfigurationPanel idropConfigurationPanel = new IDROPConfigurationPanel(
                this, true, iDropCore);
        idropConfigurationPanel.setLocationRelativeTo(null);
        idropConfigurationPanel.setVisible(true);
    }

    /**
     * TODO: update resource in grid account
     */
    private void cbIrodsResourceActionPerformed(
            final java.awt.event.ActionEvent evt) {

        String newResource = (String) cbIrodsResource.getSelectedItem();
        getiDropCore().irodsAccount().setDefaultStorageResource(newResource);
        try {
            getiDropCore().getConveyorService().getGridAccountService().rememberDefaultStorageResource(newResource, getiDropCore().irodsAccount());
        } catch (ConveyorExecutionException ex) {
            log.error("unable to remember new resource", ex);
            MessageManager.showError(this, ex.getMessage());
        }
    }

    private void btnMainToolbarDownloadActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarDownloadActionPerformed

        DownloadDialog downloadDialog = new DownloadDialog(this, true,
                getIrodsTree(), getFileTree());

        downloadDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        downloadDialog.setVisible(true);

    }// GEN-LAST:event_btnMainToolbarDownloadActionPerformed

    private void togglePauseTransferActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_togglePauseTransferActionPerformed

        try {
            if (togglePauseTransfer.isSelected()) {
                log.info("pausing....");

                iDropCore.getConveyorService().getConveyorExecutorService()
                        .requestPause();
            } else {
                log.info("resuming queue");
                iDropCore.getConveyorService().getConveyorExecutorService()
                        .requestResumeFromPause();
            }
        } catch (Exception ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// GEN-LAST:event_togglePauseTransferActionPerformed

    private void btnMainToolbarRefreshActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarRefreshActionPerformed
        buildTargetTree(false);
         reloadExistingLocalTree();
    }// GEN-LAST:event_btnMainToolbarRefreshActionPerformed

    private void btnMainToolbarDeleteActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarDeleteActionPerformed

        log.info("deleting a node");
        int[] rows = irodsTree.getSelectedRows();
        log.debug("selected rows for delete:{}", rows);

        DeleteIRODSDialog deleteDialog;

        if (rows.length == 1) {

            IRODSNode toDelete = (IRODSNode) irodsTree.getValueAt(rows[0], 0);

            log.info("deleting a single node: {}", toDelete);
            deleteDialog = new DeleteIRODSDialog(this, true, irodsTree,
                    toDelete);
        } else {
            List<IRODSNode> nodesToDelete = new ArrayList<IRODSNode>();
            for (int row : rows) {
                nodesToDelete.add((IRODSNode) irodsTree.getValueAt(row, 0));

            }

            deleteDialog = new DeleteIRODSDialog(this, true, irodsTree,
                    nodesToDelete);

        }

        deleteDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        deleteDialog.setVisible(true);
    }// GEN-LAST:event_btnMainToolbarDeleteActionPerformed

    private void btnMainToolbarUploadActionPerformed(
            final java.awt.event.ActionEvent evt) {

        UploadDialog uploadDialog = new UploadDialog(this, true,
                getIrodsTree(), getFileTree());

        uploadDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        uploadDialog.setVisible(true);

    }

    private void btnMainToolbarInfoActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarInfoActionPerformed
        IRODSInfoDialog irodsInfoDialog = new IRODSInfoDialog(this, true,
                getIrodsTree());

        irodsInfoDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        irodsInfoDialog.setVisible(true);
    }// GEN-LAST:event_btnMainToolbarInfoActionPerformed

    private void btnMainToolbarCopyActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarCopyActionPerformed
        CopyMoveDialog copyMoveDialog = new CopyMoveDialog(this, true,
                getIrodsTree());

        copyMoveDialog.setLocation(
                (int) (this.getLocation().getX() + getWidth() / 2), (int) (this
                .getLocation().getY() + getHeight() / 2));
        copyMoveDialog.setVisible(true);

    }// GEN-LAST:event_btnMainToolbarCopyActionPerformed

    private void btnMainToolbarSyncActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarSyncActionPerformed

        log.info("synch now button pressed");

        int result = JOptionPane.showConfirmDialog(this,
                "Do you want to synchronize now?", "Synchronize",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            /*
             * try { SynchManagerService synchConfigurationService =
             * iDropCore.getTransferManager
             * ().getTransferServiceFactory().instanceSynchManagerService();
             * List<Synchronization> syncs =
             * synchConfigurationService.listAllSynchronizations();
             * log.info("number of synchronizations to process: {}",
             * syncs.size()); for (Synchronization sync: syncs) { if
             * (synchConfigurationService.isSynchRunning(sync)) {
             * MessageManager.showMessage(this,
             * "Cannot schedule the synchronization, a synch is currently running"
             * , MessageManager.TITLE_MESSAGE); return; }
             * iDropCore.getTransferManager().enqueueASynch(sync,
             * sync.buildIRODSAccountFromSynchronizationData()); } } catch
             * (Exception ex) { log.error("error starting synch", ex);
             * MessageManager.showError(this, ex.getMessage(),
             * MessageManager.TITLE_MESSAGE); throw new
             * IdropRuntimeException(ex); }
             */
        }

    }// GEN-LAST:event_btnMainToolbarSyncActionPerformed

    private void formWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        setVisible(false);
        formShown = false;
    }// GEN-LAST:event_formWindowClosing

    private void btnMainToolbarQueueMgrActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarQueueMgrActionPerformed
        showQueueManagerDialog();
    }// GEN-LAST:event_btnMainToolbarQueueMgrActionPerformed

    private void btnMainToolbarGridsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMainToolbarGridsActionPerformed
        showGridManagerDialog(getIrodsAccount());

    }// GEN-LAST:event_btnMainToolbarGridsActionPerformed

    private void btnNewFolderActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnNewFolderActionPerformed
        // show a dialog asking for the new directory name...

        IRODSFileService irodsFS = null;

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree
                .getModel();
        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idx = selectionModel.getLeadSelectionIndex();

        // make sure there is a selected node
        if (idx >= 0) {
            try {
                irodsFS = new IRODSFileService(
                        getiDropCore().irodsAccount(), getiDropCore()
                        .getIrodsFileSystem());
                IRODSFile ifile = null;
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                        .getValueAt(idx, 0);
                ifile = irodsFS.getIRODSFileForPath(selectedNode.getFullPath());

                // rule out "/" and choose parent if file is not a directory
                String path = ifile.getAbsolutePath();
                if (ifile.isFile()) {
                    path = ifile.getParent();
                    selectedNode = (IRODSNode) selectedNode.getParent();
                }

                NewIRODSDirectoryDialog newDirectoryDialog = new NewIRODSDirectoryDialog(
                        this, true, path, getIrodsTree(), selectedNode);
                newDirectoryDialog.setLocation(
                        (int) (this.getLocation().getX() + getWidth() / 2),
                        (int) (this.getLocation().getY() + getHeight() / 2));
                newDirectoryDialog.setVisible(true);
            } catch (Exception e) {
                log.error("exception creating new folder", e);
                MessageManager.showError(this, e.getMessage());
            }

        } else {
            MessageManager.showWarning(this,
                    "Please select a parent folder in the tree");
        }
    }// GEN-LAST:event_btnNewFolderActionPerformed
    
     /**
             * A tree already exists so use the current information to reload
             */
            private void reloadExistingLocalTree()  {

                if (fileTree == null) {
                    log.warn("null file tree - ignored when refreshing");
                    return;
                }

                final TreePath rootPath = fileTree.getPathForRow(0);
                final Enumeration<TreePath> currentPaths = fileTree.getExpandedDescendants(rootPath);
                log.debug("expanded local tree node, paths are:{}", currentPaths);

                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // keep track of the currently selected drive
                            Object selectedDrive = listLocalDrives.getSelectedValue();
                            initializeLocalFileTreeModel(selectedDrive);
                            fileTree.setModel(localFileModel);

                            // re-expand the tree paths that are currently expanded
                            final Enumeration<TreePath> pathsToExpand = currentPaths;
                            fileTree.expandTreeNodesBasedOnListOfPreviouslyExpandedNodes(pathsToExpand);
                        } catch (IdropException ex) {
                            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                                    null, ex);
                            throw new IdropRuntimeException(
                                    "exception expanding tree nodes", ex);
                        }

                    }
                });

            }

    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMainToolbarCopy;
    private javax.swing.JButton btnMainToolbarDelete;
    private javax.swing.JButton btnMainToolbarDownload;
    private javax.swing.JButton btnMainToolbarGrids;
    private javax.swing.JButton btnMainToolbarInfo;
    private javax.swing.JButton btnMainToolbarQueueMgr;
    private javax.swing.JButton btnMainToolbarRefresh;
    private javax.swing.JButton btnMainToolbarSettings;
    private javax.swing.JButton btnMainToolbarSync;
    private javax.swing.JButton btnMainToolbarTools;
    private javax.swing.JButton btnMainToolbarTree;
    private javax.swing.JButton btnMainToolbarUpload;
    private javax.swing.JButton btnNewFolder;
    private javax.swing.JButton btnSynch;
    private javax.swing.JComboBox cbIrodsResource;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JToolBar idropProgressPanelToolbar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblCurrentFile;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHostLabel;
    private javax.swing.JLabel lblResource;
    private javax.swing.JLabel lblStatusIcon;
    private javax.swing.JLabel lblTransferByteCounts;
    private javax.swing.JLabel lblTransferFilesCounts;
    private javax.swing.JLabel lblTransferMessage;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblUserNameLabel;
    private javax.swing.JLabel lblZone;
    private javax.swing.JLabel lblZoneLabel;
    private javax.swing.JList listLocalDrives;
    private javax.swing.JPanel pnlBreadCrumbNav;
    private javax.swing.JPanel pnlCurrentTransferStatus;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlIdropBottom;
    private javax.swing.JPanel pnlIdropGutter;
    private javax.swing.JPanel pnlLocalRoots;
    private javax.swing.JPanel pnlLocalTreeArea;
    private javax.swing.JPanel pnlMainIrodsTree;
    private javax.swing.JPanel pnlMainToolbarIcons;
    private javax.swing.JPanel pnlMainTransferStatus;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTransferOptions;
    private javax.swing.JLabel progressIconImageLabel;
    private javax.swing.JProgressBar progressIntraFile;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JSplitPane splitPanelTrees;
    private javax.swing.JToggleButton togglePauseTransfer;
    private javax.swing.JProgressBar transferStatusProgressBar;
    // End of variables declaration//GEN-END:variables

    public void closeTransferManagerDialog() {
        transferManagerDialog = null;
    }

    private void showGridManagerDialog(final IRODSAccount savedAccount)
            throws HeadlessException {

        final GridMemoryDialog gridMemoryDialog = new GridMemoryDialog(null,
                true, iDropCore, savedAccount);
        Toolkit tk = getToolkit();
        int x = (tk.getScreenSize().width - gridMemoryDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - gridMemoryDialog.getHeight()) / 2;
        gridMemoryDialog.setLocation(x, y);
        gridMemoryDialog.toFront();
        gridMemoryDialog.setVisible(true);
        reinitializeForChangedIRODSAccount();
    }
}
