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
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.FieldFormatHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.LookAndFeelManager;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSRowModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSSearchTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.InfoPanelTransferHandler;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.engine.TransferManager;
import org.irods.jargon.transfer.engine.TransferManager.ErrorStatus;
import org.irods.jargon.transfer.engine.TransferManager.RunningStatus;
import org.irods.jargon.transfer.engine.TransferManagerCallbackListener;
import org.netbeans.swing.outline.Outline;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lisa Stillwell
 */
public class iDrop extends javax.swing.JFrame implements ActionListener,
        ItemListener, TransferManagerCallbackListener {
    
    private IDROPCore iDropCore = new IDROPCore();
    private IRODSTree irodsTree = null;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDrop.class);
    private boolean receivedStartupSignal = false;
    private TrayIcon trayIcon = null;
    private Object lastCachedInfoItem = null;
    private CheckboxMenuItem pausedItem = null;
    private ChangePasswordDialog changePasswordDialog = null;
    private QueueManagerDialog queueManagerDialog = null;
    private boolean formShown = false;
    
    public iDrop(final IDROPCore idropCore) {

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        this.iDropCore = idropCore;

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

//        togglePauseTransfer.setSelected(pausedItem.getState());
//        TransferManager.RunningStatus status = iDropCore.getTransferManager().getRunningStatus();
//        iDropCore.getIconManager().setRunningStatus(status);
//        iDropCore.getIconManager().setErrorStatus(
//                iDropCore.getTransferManager().getErrorStatus());
//        if (status == TransferManager.RunningStatus.PROCESSING) {
//            setUpTransferPanel(true);
//        } else {
//            setUpTransferPanel(false);
//        }
//
//        setUpAccountGutter();

        setVisibleComponentsAtStartup();

        setVisible(true);

    }
    
    protected void buildIdropGuiComponents() throws IdropRuntimeException,
            HeadlessException {
        initComponents();
//        this.pnlLocalTreeArea.setVisible(false);
//        this.pnlIrodsInfo.setVisible(false);
//        this.splitTargetCollections.setResizeWeight(0.8d);
//        try {
//            pnlIrodsInfo.setTransferHandler(new InfoPanelTransferHandler(this));
//        } catch (IdropException ex) {
//            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
//            throw new IdropRuntimeException(
//                    "error setting up infoPanelTransferHandler", ex);
//        }

//        tableSearchResults.setModel(new IRODSSearchTableModel());
//        MouseListener popupListener = new PopupListener();
//        // add the listener specifically to the header
//        tableSearchResults.addMouseListener(popupListener);
//        tableSearchResults.getTableHeader().addMouseListener(popupListener);

        Toolkit t = getToolkit();
        int width = t.getScreenSize().width;
        int height = t.getScreenSize().height;

        int showX = (width / 2) - (this.getWidth() / 2);
        int showY = (height / 2) - (this.getHeight() / 2);
        this.setLocation(showX, showY);

        if (getiDropCore().getIrodsAccount() == null) {
            log.warn("no account, exiting");
            System.exit(0);
        }

//        userNameLabel.setText("User: "
//                + getiDropCore().getIrodsAccount().getUserName());

    }

    private void displayAndProcessSignOn() {
        final iDrop thisPanel = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                IRODSAccount savedAccount = iDropCore.getIrodsAccount();
                iDropCore.setIrodsAccount(null);
                LoginDialog loginDialog = new LoginDialog(null, iDropCore);
                loginDialog.setLocationRelativeTo(null);
                loginDialog.setVisible(true);

                if (iDropCore.getIrodsAccount() == null) {
                    log.warn("no account, reverting");
                    iDropCore.setIrodsAccount(savedAccount);
                } else {
                    thisPanel.reinitializeForChangedIRODSAccount();
                }
            }
        });
    }

    /**
     * Startup exit to set visibility of components in iDrop GUI at startup. Here is where the
     * initial visible status of components can be specified.
     */
    private void setVisibleComponentsAtStartup() {
//        this.btnSetRootCustomTargetTree.setVisible(false);

    }

    protected void signalIdropCoreReadyAndSplashComplete() {
        if (receivedStartupSignal) {
            log.info("already received startup signal");
        } else {
            createAndShowSystemTray();
        }

        receivedStartupSignal = true;

        iDropCore.getIconManager().setRunningStatus(
                iDropCore.getTransferManager().getRunningStatus());
        iDropCore.getIconManager().setErrorStatus(
                iDropCore.getTransferManager().getErrorStatus());

    }
    
        private void initializeLookAndFeelSelected() {
        String lookAndFeelChoice = iDropCore.getIdropConfig().getPropertyForKey(IdropConfigurationService.LOOK_AND_FEEL);
        if (lookAndFeelChoice == null || lookAndFeelChoice.isEmpty()) {
            lookAndFeelChoice = "System";
        }
//        if (lookAndFeelChoice.equals("Metal")) {
//
//            this.jRadioButtonMenuItemMetal.setSelected(true);
//
//        } else if (lookAndFeelChoice.equals("System")) {
//
//            this.jRadioButtonLookAndFeelDefault.setSelected(true);
//        } else if (lookAndFeelChoice.equals("Motif")) {
//
//            this.jRadioButtonMenuItemMotif.setSelected(true);
//        } else if (lookAndFeelChoice.equals("GTK")) {
//
//            this.jRadioButtonMenuItemGTK.setSelected(true);
//        } else if (lookAndFeelChoice.equals("Nimbus")) {
//            this.jRadioButtonLookAndFeelNimbus.setSelected(true);
//
//        } else {
//            this.jRadioButtonLookAndFeelDefault.setSelected(true);
//        }
    }

    private void setLookAndFeel(String lookAndFeelChoice) {

        int result = JOptionPane.showConfirmDialog(this,
                "Changing the look and feel requires a restart, would you like to change the look and feel?",
                "iDrop - Confirm change look and feel",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        String lookAndFeel = "";
        if (lookAndFeelChoice == null) {
            lookAndFeelChoice = "System";
        }

        if (lookAndFeelChoice != null) {

//            if (lookAndFeelChoice.equals("Metal")) {
//                lookAndFeel = lookAndFeelChoice;
//                this.jRadioButtonMenuItemMetal.setSelected(true);
//                //  an alternative way to set the Metal L&F is to replace the 
//                // previous line with:
//                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
//
//            } else if (lookAndFeelChoice.equals("System")) {
//                lookAndFeel = lookAndFeelChoice;
//                this.jRadioButtonLookAndFeelDefault.setSelected(true);
//            } else if (lookAndFeelChoice.equals("Motif")) {
//                lookAndFeel = lookAndFeelChoice;
//                this.jRadioButtonMenuItemMotif.setSelected(true);
//            } else if (lookAndFeelChoice.equals("GTK")) {
//                lookAndFeel = lookAndFeelChoice;
//                this.jRadioButtonMenuItemGTK.setSelected(true);
//            } else if (lookAndFeelChoice.equals("Nimbus")) {
//                this.jRadioButtonLookAndFeelNimbus.setSelected(true);
//                lookAndFeel = lookAndFeelChoice;
//            } else {
//                lookAndFeel = "System";
//
//            }
//            try {
//                LookAndFeelManager laf = new LookAndFeelManager(iDropCore);
//                laf.setLookAndFeel(lookAndFeel);
//                shutdown();
//            } catch (Exception e) {
//                log.warn("unable to set look and feel to :{}", lookAndFeel);
//            }
        }
    }
    
    public void buildTargetTree(final boolean reset) {
        log.info("building tree to look at staging resource");
        final iDrop gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                IRODSOutlineModel mdl = null;
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
                    throw new IdropRuntimeException(ex);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(
                            iDropCore.getIrodsAccount());
                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            /**
             * A tree has not been previosly loaded, establish the root (strict ACLs? Login preset?)
             */
            private void loadNewTree() throws JargonException, IdropException {
                IRODSOutlineModel mdl;
                TreePath[] currentPaths = null;
                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();
                String basePath = getBasePath();
                log.info("base path set to:{}", basePath);
                if (basePath.equals("/")) {
                    root.setPathOrName(basePath);
                    root.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
                } else {
                    IRODSFile baseFile = iDropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(basePath);
                    root.setParentPath(baseFile.getParent());
                    root.setPathOrName(baseFile.getAbsolutePath());
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
                scrollIrodsTree.setViewportView(irodsTree);
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
                scrollIrodsTree.getViewport().removeAll();
                irodsTree = null;
                loadNewTree();
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
     * Method to clear any cached values when an account changes. Some data is cached and lazily
     * loaded. Rebuilds gui state for new grid.
     */
    public void reinitializeForChangedIRODSAccount() {
        log.info("clearing any cached data associated with the account");
        final iDrop idropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                lastCachedInfoItem = null;
                idropGui.buildTargetTree(true);
//                idropGui.toggleIrodsDetails.setSelected(false);
//                handleInfoPanelShowOrHide();
                getiDropCore().setBasePath(null);
                setUpAccountGutter();
            }
        });

    }
    
    /**
     * Builds the system tray menu and installs the iDrop icon in the system tray. The iDrop GUI is
     * displayed when the iDrop menu item is selected from the system tray
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
        MenuItem changePasswordItem = new MenuItem("Change Password");

        iDropItem.addActionListener(this);

        MenuItem currentItem = new MenuItem("Show Current and Past Activity");

        MenuItem logoutItem = new MenuItem("Logout");

        pausedItem = new CheckboxMenuItem("Pause");

        MenuItem exitItem = new MenuItem("Exit");

        exitItem.addActionListener(this);
        currentItem.addActionListener(this);
        preferencesItem.addActionListener(this);
        changePasswordItem.addActionListener(this);

        /*
         * See if I am in a paused state
         */

        if (this.getiDropCore().getTransferManager().getRunningStatus() == TransferManager.RunningStatus.PAUSED) {
            this.setTransferStatePaused();
        }

        logoutItem.addActionListener(this);
        pausedItem.addItemListener(this);
        aboutItem.addActionListener(this);

        // Add components to pop-up menu
        popup.add(aboutItem);
        popup.add(iDropItem);
        popup.add(preferencesItem);
        popup.add(changePasswordItem);
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
     * Returns an ImageIcon, or null if the path was invalid. FIXME: move to static util
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
     * @return
     * <code>IRODSAccount</code> with the current iRODS connection information.
     */
    public IRODSAccount getIrodsAccount() {
        synchronized (this) {
            return this.iDropCore.getIrodsAccount();
        }
    }
    
        /**
     * Set the current connection information.
     *
     * @return
     * <code>IRODSAccount</code> with the current iRODS connection information.
     */
    public void setIrodsAccount(final IRODSAccount irodsAccount) {
        synchronized (this) {
            this.iDropCore.setIrodsAccount(irodsAccount);
        }
    }
    
    /**
     * Returns the current iRODS remote tree view component.
     *
     * @return
     * <code>JTree</code> visual representation of the remote iRODS resource
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
//        return fileTree;
        return null;
    }

    public void setFileTree(final LocalFileTree fileTree) {
//        this.fileTree = fileTree;
    }
    
    
    /**
     * Set the account information in the gutter, including the available resources on the grid.
     * Note that this method should be called in the context of a
     * <code>Runnable</code>
     */
    private void setUpAccountGutter() {
//        userNameLabel.setText(this.getIrodsAccount().getUserName());
//        lblZone.setText(this.getIrodsAccount().getZone());
//        lblHost.setText(this.getIrodsAccount().getHost());
//        /*
//         * Get a list of storage resources on this host
//         */
//        try {
//            ResourceAO resourceAO = this.getiDropCore().getIRODSAccessObjectFactory().getResourceAO(this.getIrodsAccount());
//            log.info("getting a list of all resources in the zone");
//            List<String> resources = new ArrayList<String>();
//            resources.add("");
//            resources.addAll(resourceAO.listResourceAndResourceGroupNames());
//            comboDefaultResource.setModel(new DefaultComboBoxModel(resources.toArray()));
//            comboDefaultResource.setSelectedItem(this.getIrodsAccount().getDefaultStorageResource());
//        } catch (JargonException ex) {
//            log.error("error getting resource list", ex);
//            throw new IdropRuntimeException("error getting resource list", ex);
//        }
    }
    
    /**
     * Establish base path (checking if strict acl's are in place.
     *
     * @return
     * <code>String</code> with the base path for the tree
     * @throws JargonException
     */
    private synchronized String getBasePath() throws JargonException {
        String myBase = this.getiDropCore().getBasePath();

        // if no base defined, see if there is a prese
        if (myBase == null) {

            if (this.getiDropCore().getIrodsAccount().isAnonymousAccount()) {
                log.info("user is anonymous, default to view the public directory");
                myBase = MiscIRODSUtils.computePublicDirectory(this.getiDropCore().getIrodsAccount());

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
                    // look up the strict acl setting for the server, if strict acl, home the person in their user directory
                    EnvironmentalInfoAO environmentalInfoAO = this.getiDropCore().getIRODSAccessObjectFactory().getEnvironmentalInfoAO(getiDropCore().getIrodsAccount());
                    boolean isStrict = environmentalInfoAO.isStrictACLs();
                    log.info("is strict?:{}", isStrict);

                    if (isStrict) {
                        myBase = MiscIRODSUtils.computeHomeDirectoryForIRODSAccount(iDropCore.getIrodsAccount());
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
     * Get the JTree component that represents the iRODS file system in the iDrop gui.
     *
     * @return
     * <code>IRODSTree</code> that is the JTree component for the iRODS file system view.
     */
    public IRODSTree getIrodsTree() {
        return irodsTree;
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
        } else if (e.getActionCommand().equals("Logout")) {
            log.info("logging out to log in to a new grid");

            displayAndProcessSignOn();

        } else if (e.getActionCommand().equals("About")) {
            AboutDialog aboutDialog = new AboutDialog(this, true);
            int x = (toolkit.getScreenSize().width - aboutDialog.getWidth()) / 2;
            int y = (toolkit.getScreenSize().height - aboutDialog.getHeight()) / 2;
            aboutDialog.setLocation(x, y);
            aboutDialog.setVisible(true);
        } else if (e.getActionCommand().equals("Preferences")) {
            IDROPConfigurationPanel idropConfigurationPanel = new IDROPConfigurationPanel(this, true, iDropCore);
            idropConfigurationPanel.setLocationRelativeTo(null);
            idropConfigurationPanel.setVisible(true);
        } else if (e.getActionCommand().equals("Change Password")) {

            if (changePasswordDialog == null) {
                changePasswordDialog = new ChangePasswordDialog(this, null, true);
                int x = (toolkit.getScreenSize().width - changePasswordDialog.getWidth()) / 2;
                int y = (toolkit.getScreenSize().height - changePasswordDialog.getHeight()) / 2;
                changePasswordDialog.setLocation(x, y);
            }
            changePasswordDialog.setVisible(true);

        } else if (e.getActionCommand().equals("Show Current and Past Activity")) {

            log.info("showing recent items in queue");
            showQueueManagerDialog();

        } else {

            if (!this.formShown) {

                showIdropGui();

            } else {
                // refresh the tree when setting visible again, the account may
                // have changed.

                buildTargetTree(false);
                this.setVisible(true);
            }

            this.toFront();
        }

    }
    
    private boolean showQueueManagerDialog() {
        try {
            if (queueManagerDialog == null) {
                queueManagerDialog = new QueueManagerDialog(this,
                        iDropCore.getTransferManager(),
                        QueueManagerDialog.ViewType.RECENT);
            } else {
                queueManagerDialog.refreshTableView(QueueManagerDialog.ViewType.RECENT);
            }
        } catch (IdropException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            this.showIdropException(ex);
            return true;
        }
        queueManagerDialog.setModal(false);
        queueManagerDialog.setVisible(true);
        queueManagerDialog.toFront();
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

//        if (togglePauseTransfer != null) {
//            this.togglePauseTransfer.setSelected(true);
//        }
    }

    /**
     * Indicate that the gui should show an unpaused state.
     */
    public void setTransferStateUnpaused() {
        if (pausedItem != null) {
            pausedItem.setState(false);
        }

//        if (togglePauseTransfer != null) {
//            this.togglePauseTransfer.setSelected(false);
//        }
    }
    
    
    @Override
    public void itemStateChanged(final ItemEvent e) {

        if (e.getItem().equals("Pause")) {

            try {
                if (pausedItem.getState() == true) {
                    log.info("pausing....");
                    iDropCore.getTransferManager().pause();
                } else {
                    log.info("resuming queue");
                    iDropCore.getTransferManager().resume();
                }
            } catch (Exception ex) {
                Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null,
                        ex);
            }

        }
    }
    
    
    private void shutdownWithConfirmation() {
        int result = JOptionPane.showConfirmDialog(this,
                "Shut down iDrop?",
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
            iDropCore.getIdropConfigurationService().saveConfigurationToPropertiesFile();
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
                 * listener events may occur at startup before the GUI is fully prepared, ignore
                 * these
                 */
                if (trayIcon == null) {
                    return;
                }

                Image newIcon = createImage(iconFile, "icon");

                trayIcon.setImage(newIcon);

//                if (pnlIdropProgressIcon != null) {
//                    progressIconImageLabel.setIcon(new ImageIcon(newIcon));
//
//                }
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
                idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) getIrodsTree().getModel();

                ListSelectionModel selectionModel = getIrodsTree().getSelectionModel();
                int idx = selectionModel.getLeadSelectionIndex();

                // use first selection for info
                IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(
                        idx, 0);
                log.info("selected node to initialize info panel:{}", selectedNode);
                try {
                    identifyNodeTypeAndInitializeInfoPanel(selectedNode);
                } catch (IdropException ex) {
                    log.error("error initializing info panel for selected iRODS node", ex);
                    throw new IdropRuntimeException(
                            "error initializing info panel for selected irods node");
                } finally {
                    iDropCore.closeIRODSConnectionForLoggedInAccount();
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

    }
    
    /**
     * Look at the kind of irods node and handle appropriately
     *
     * @param irodsNode
     * @throws IdropException
     */
    public void identifyNodeTypeAndInitializeInfoPanel(final IRODSNode irodsNode)
            throws IdropException {

//        if (!getToggleIrodsDetails().isSelected()) {
//            return;
//        }
//
//        if (irodsNode == null) {
//            return;
//        }
//
//        if (irodsNode.isLeaf()) {
//            log.info("selected node is a leaf, get a data object");
//            buildDataObjectFromSelectedIRODSNodeAndGiveToInfoPanel(irodsNode);
//        } else {
//            log.info("selected node is a collection, get a collection object");
//            buildCollectionFromSelectedIRODSNodeAndGiveToInfoPanel(irodsNode);
//        }
    }
    
    
    /**
     * A transfer confirm dialog
     *
     * @param sourcePath
     * <code>String</code> with the source path of the transfer
     * @param targetPath
     * <code>String</code> with the target of the transfer
     * @return
     * <code>int</code> with the dialog user response.
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
    public void statusCallback(final TransferStatus ts) {

        log.info("transfer status callback to iDROP:{}", ts);
        final iDrop idrop = this;

//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (ts.getTransferState() == TransferStatus.TransferState.FAILURE) {
//                    // an error occurs, stop the transfer
//                    log.error("error occurred in transfer: {}", ts);
//                    if (ts.getTransferException() == null) {
//                        idrop.showMessageFromOperation("An error occurred in the transfer, this transfer will be cancelled");
//                    } else {
//                        idrop.showIdropException(ts.getTransferException());
//                    }
//
//                } else if (ts.isIntraFileStatusReport()) {
//
//                    log.debug("transferred so far:{}", ts.getBytesTransfered());
//                    log.debug("total bytes:{}", ts.getTotalSize());
//                    float rawPct = (float) ts.getBytesTransfered() / ts.getTotalSize();
//                    int percentDone = (int) (rawPct * 100F);
//                    log.info("pct done:{}", percentDone);
//
//                    progressIntraFile.setValue(percentDone);
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//
//                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {
//
//                    // start of a file operation
//                    progressIntraFile.setMinimum(0);
//                    progressIntraFile.setMaximum(100);
//                    progressIntraFile.setValue(0);
//                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
//                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//
//                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {
//
//                    progressIntraFile.setValue(100);
//
//                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
//                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
//                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//
//                } else {
//
//                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
//                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
//                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
//                }
//            }
//        });

    }

    /**
     * Be able to do things to the transfer panel
     *
     * @param isBegin
     */
    private void setUpTransferPanel(boolean isBegin) {
//        if (isBegin) {
//            pnlCurrentTransferStatus.setVisible(true);
//        } else {
//            pnlCurrentTransferStatus.setVisible(true);
//        }
    }

    /**
     * Implementation of transfer manager callback. The overall status callback represents the start
     * and completion of a transfer operation
     *
     * @param ts
     */
    @Override
    public void overallStatusCallback(final TransferStatus ts) {

        final IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree.getModel();
        final iDrop idropGui = this;

//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
//                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//                    idropGui.setUpTransferPanel(true);
//                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
//                    idropGui.setUpTransferPanel(false);
//                }
//
//                /*
//                 * Handle appropriate tree notifications, so some filtering to prevent notifications
//                 * when for a different host/zone
//                 */
//                if (ts.getTransferType() == TransferStatus.TransferType.SYNCH || ts.getTransferType() == TransferStatus.TransferType.REPLICATE) {
//                    log.info("no need to notify tree for synch or replicate");
//                } else if (ts.getTransferType() == TransferStatus.TransferType.GET
//                        && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
//                    try {
//                        ((LocalFileSystemModel) idropGui.getFileTree().getModel()).notifyCompletionOfOperation(idropGui.getFileTree(), ts);
//
//                    } catch (IdropException ex) {
//                        log.error("error on tree notify after operation", ex);
//                        throw new IdropRuntimeException("error processing overall status callback", ex);
//                    }
//                } else if (ts.getTransferType() == TransferStatus.TransferType.COPY || ts.getTransferType() == TransferStatus.TransferType.PUT) {
//                    if (ts.getTransferZone().equals(
//                            iDropCore.getIrodsAccount().getZone()) && ts.getTransferHost().equals(iDropCore.getIrodsAccount().getHost())) {
//                        try {
//                            // should leave PUT, and COPY
//                            irodsTreeModel.notifyCompletionOfOperation(irodsTree, ts);
//                        } catch (IdropException ex) {
//                            log.error("error on tree notify after operation", ex);
//                            throw new IdropRuntimeException("error processing overall status callback", ex);
//                        }
//                    }
//                }
//
//                /*
//                 * Handle progress bar and messages. These are cleared on overall initiation
//                 */
//                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
//                    clearProgressBar();
//                    // on initiation, clear and reset the status bar info
//                    lblTransferType.setText(ts.getTransferType().name());
//                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
//                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
//                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
//                    transferStatusProgressBar.setMinimum(0);
//                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
//                    transferStatusProgressBar.setValue(0);
//                }
//
//                /*
//                 * Handle any text messages
//                 */
//                if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
//                    lblTransferMessage.setText("Synchronization Initializing");
//                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_GENERATION) {
//                    lblTransferMessage.setText("Synchronization looking for updates");
//                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_STEP) {
//                    lblTransferMessage.setText("Synchronizing differences");
//                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
//                    lblTransferMessage.setText("Synchronization complete");
//                } else if (ts.getTransferEnclosingType() == TransferStatus.TransferType.SYNCH) {
//                    lblTransferMessage.setText("Transfer to synchronize local and iRODS");
//                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION) {
//                    // initiation not within a synch
//                    lblTransferMessage.setText("Processing a " + ts.getTransferType().name() + " operation");
//                }
//            }
//        });
    }
    
    
    @Override
    public synchronized void transferManagerErrorStatusUpdate(
            final ErrorStatus es) {
        iDropCore.getIconManager().setErrorStatus(es);
    }

    @Override
    public synchronized void transferManagerRunningStatusUpdate(
            final RunningStatus rs) {
        iDropCore.getIconManager().setRunningStatus(rs);
        if (rs == RunningStatus.PAUSED) {
            this.setTransferStatePaused();
        } else {
            this.setTransferStateUnpaused();
        }
    }

    /**
     * Creates new form iDrop2
     */
    /*
    public iDrop2() {
        initComponents();
    }
    */

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        pnlMain = new javax.swing.JPanel();
        pnlMainToolbar = new javax.swing.JPanel();
        pnlMainToolbarIcons = new javax.swing.JPanel();
        btnMainToolbarSettings = new javax.swing.JButton();
        btnMainToolbarSync = new javax.swing.JButton();
        btnMainToolbarDownload = new javax.swing.JButton();
        btnMainToolbarUpload = new javax.swing.JButton();
        btnMainToolbarRefresh = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnMainToolbarAddEditMetaData = new javax.swing.JButton();
        btnMainToolbarCopy = new javax.swing.JButton();
        btnMainToolbarDelete = new javax.swing.JButton();
        pnlMainToolbarSearch = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        txtMainToolbarSearchTerms = new javax.swing.JTextField();
        btnMainToolbarSearchFiles = new javax.swing.JButton();
        pnlMainIrodsTree = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();
        pnlMainTransferStatus = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlMainToolbar.setLayout(new java.awt.BorderLayout());

        pnlMainToolbarIcons.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlMainToolbarIcons.setLayout(new javax.swing.BoxLayout(pnlMainToolbarIcons, javax.swing.BoxLayout.LINE_AXIS));

        btnMainToolbarSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_settings.png"))); // NOI18N
        btnMainToolbarSettings.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarSettings.text")); // NOI18N
        btnMainToolbarSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 28));
        btnMainToolbarSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMainToolbarSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainToolbarSettingsActionPerformed(evt);
            }
        });
        pnlMainToolbarIcons.add(btnMainToolbarSettings);

        btnMainToolbarSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_synch.png"))); // NOI18N
        btnMainToolbarSync.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarSync.text")); // NOI18N
        btnMainToolbarSync.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 28));
        btnMainToolbarSync.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarSync.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarSync);

        btnMainToolbarDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_download.png"))); // NOI18N
        btnMainToolbarDownload.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarDownload.text")); // NOI18N
        btnMainToolbarDownload.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 28));
        btnMainToolbarDownload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarDownload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarDownload);

        btnMainToolbarUpload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_upload.png"))); // NOI18N
        btnMainToolbarUpload.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarUpload.text")); // NOI18N
        btnMainToolbarUpload.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 28));
        btnMainToolbarUpload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarUpload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarUpload);

        btnMainToolbarRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_refresh.png"))); // NOI18N
        btnMainToolbarRefresh.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarRefresh.text")); // NOI18N
        btnMainToolbarRefresh.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 30));
        btnMainToolbarRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarRefresh);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMaximumSize(new java.awt.Dimension(34, 60));
        jSeparator1.setMinimumSize(new java.awt.Dimension(34, 60));
        jSeparator1.setPreferredSize(new java.awt.Dimension(34, 60));
        pnlMainToolbarIcons.add(jSeparator1);

        btnMainToolbarAddEditMetaData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_editmetadata.png"))); // NOI18N
        btnMainToolbarAddEditMetaData.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarAddEditMetaData.text")); // NOI18N
        btnMainToolbarAddEditMetaData.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 24));
        btnMainToolbarAddEditMetaData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarAddEditMetaData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarAddEditMetaData);

        btnMainToolbarCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_replicate.png"))); // NOI18N
        btnMainToolbarCopy.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 24));
        btnMainToolbarCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarCopy.setLabel(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarCopy.label")); // NOI18N
        btnMainToolbarCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarCopy);

        btnMainToolbarDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon_delete.png"))); // NOI18N
        btnMainToolbarDelete.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarDelete.text_1")); // NOI18N
        btnMainToolbarDelete.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnMainToolbarDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMainToolbarDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlMainToolbarIcons.add(btnMainToolbarDelete);

        pnlMainToolbar.add(pnlMainToolbarIcons, java.awt.BorderLayout.NORTH);

        pnlMainToolbarSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10));
        pnlMainToolbarSearch.setPreferredSize(new java.awt.Dimension(822, 45));
        pnlMainToolbarSearch.setLayout(new javax.swing.BoxLayout(pnlMainToolbarSearch, javax.swing.BoxLayout.LINE_AXIS));

        jComboBox1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Path to Current Directory" }));
        jComboBox1.setPreferredSize(new java.awt.Dimension(400, 45));
        pnlMainToolbarSearch.add(jComboBox1);

        txtMainToolbarSearchTerms.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtMainToolbarSearchTerms.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.txtMainToolbarSearchTerms.text")); // NOI18N
        txtMainToolbarSearchTerms.setPreferredSize(new java.awt.Dimension(130, 45));
        pnlMainToolbarSearch.add(txtMainToolbarSearchTerms);

        btnMainToolbarSearchFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search_files.png"))); // NOI18N
        btnMainToolbarSearchFiles.setText(org.openide.util.NbBundle.getMessage(iDrop.class, "iDrop.btnMainToolbarSearchFiles.text")); // NOI18N
        btnMainToolbarSearchFiles.setPreferredSize(new java.awt.Dimension(118, 40));
        pnlMainToolbarSearch.add(btnMainToolbarSearchFiles);

        pnlMainToolbar.add(pnlMainToolbarSearch, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlMainToolbar, java.awt.BorderLayout.NORTH);

        pnlMainIrodsTree.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMainIrodsTree.setPreferredSize(new java.awt.Dimension(822, 400));
        pnlMainIrodsTree.setLayout(new java.awt.BorderLayout());
        pnlMainIrodsTree.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlMainIrodsTree, java.awt.BorderLayout.CENTER);

        pnlMainTransferStatus.setPreferredSize(new java.awt.Dimension(822, 0));

        org.jdesktop.layout.GroupLayout pnlMainTransferStatusLayout = new org.jdesktop.layout.GroupLayout(pnlMainTransferStatus);
        pnlMainTransferStatus.setLayout(pnlMainTransferStatusLayout);
        pnlMainTransferStatusLayout.setHorizontalGroup(
            pnlMainTransferStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 863, Short.MAX_VALUE)
        );
        pnlMainTransferStatusLayout.setVerticalGroup(
            pnlMainTransferStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        pnlMain.add(pnlMainTransferStatus, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMainToolbarSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMainToolbarSettingsActionPerformed
        IDROPConfigurationPanel idropConfigurationPanel = new IDROPConfigurationPanel(this, true, iDropCore);
            idropConfigurationPanel.setLocationRelativeTo(null);
            idropConfigurationPanel.setVisible(true);
    }//GEN-LAST:event_btnMainToolbarSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(iDrop.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(iDrop.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(iDrop.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(iDrop.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new iDrop().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMainToolbarAddEditMetaData;
    private javax.swing.JButton btnMainToolbarCopy;
    private javax.swing.JButton btnMainToolbarDelete;
    private javax.swing.JButton btnMainToolbarDownload;
    private javax.swing.JButton btnMainToolbarRefresh;
    private javax.swing.JButton btnMainToolbarSearchFiles;
    private javax.swing.JButton btnMainToolbarSettings;
    private javax.swing.JButton btnMainToolbarSync;
    private javax.swing.JButton btnMainToolbarUpload;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlMainIrodsTree;
    private javax.swing.JPanel pnlMainToolbar;
    private javax.swing.JPanel pnlMainToolbarIcons;
    private javax.swing.JPanel pnlMainToolbarSearch;
    private javax.swing.JPanel pnlMainTransferStatus;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JTextField txtMainToolbarSearchTerms;
    // End of variables declaration//GEN-END:variables



    @Override
    public CallbackResponse transferAsksWhetherToForceOperation(String irodsAbsolutePath, boolean isCollection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
