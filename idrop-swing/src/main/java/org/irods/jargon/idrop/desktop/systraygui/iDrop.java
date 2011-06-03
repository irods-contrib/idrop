/*
 * IDrop.java
 *
 * Created on May 20, 2010, 2:59:48 PM
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.idrop.desktop.systraygui.utils.IconHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.LocalFileUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSSearchTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.InfoPanelTransferHandler;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IrodsTreeListenerForBuildingInfoPanel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.LocalFileTree;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.engine.TransferManager.ErrorStatus;
import org.irods.jargon.transfer.engine.TransferManager.RunningStatus;
import org.irods.jargon.transfer.engine.TransferManagerCallbackListener;
import org.irods.jargon.usertagging.FreeTaggingService;
import org.irods.jargon.usertagging.FreeTaggingServiceImpl;
import org.irods.jargon.usertagging.domain.IRODSTagGrouping;
import org.irods.jargon.usertagging.domain.TagQuerySearchResult;
import org.irods.jargon.usertagging.domain.UserTagCloudView;
import org.slf4j.LoggerFactory;

import cookxml.cookswing.CookSwing;

/**
 * Main system tray and GUI. Create system tray menu, start timer process for queue.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class iDrop extends javax.swing.JFrame implements ActionListener, ItemListener, TransferManagerCallbackListener {

    private static final long serialVersionUID = 1L;
    private LocalFileSystemModel localFileModel = null;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDrop.class);
    private boolean formShown = false;
    private LocalFileTree fileTree = null;
    private IRODSTree irodsTree = null;
    private QueueManagerDialog queueManagerDialog = null;
    private IDROPCore iDropCore = new IDROPCore();
    private CheckboxMenuItem pausedItem = null;
    private TrayIcon trayIcon = null;
    private Object lastCachedInfoItem = null;
    public DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
    private ChangePasswordDialog changePasswordDialog = null;
    private SynchSetupDialog synchSetupDialog = null;
    public static JDialog newPreferencesDialog;
    public JCheckBox showGUICheckBox;
    public JButton preferencesDialogOKButton;
    private static SimpleDateFormat SDF = new SimpleDateFormat("MM-dd-yyyy");

    public iDrop(final IDROPCore idropCore) {

        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            throw new IdropRuntimeException(e);
        }

        this.iDropCore = idropCore;
    }

    /** Creates new form IDrop */
    public iDrop() {
    }

    protected void buildIdropGuiComponents() throws IdropRuntimeException, HeadlessException {
        initComponents();
        this.pnlLocalTreeArea.setVisible(false);
        this.pnlIrodsInfo.setVisible(false);
        this.splitTargetCollections.setResizeWeight(0.8d);
        try {
            pnlIrodsInfo.setTransferHandler(new InfoPanelTransferHandler(this));
        } catch (IdropException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("error setting up infoPanelTransferHandler", ex);
        }

        tableSearchResults.setModel(new IRODSSearchTableModel());
        MouseListener popupListener = new PopupListener();
        // add the listener specifically to the header
        tableSearchResults.addMouseListener(popupListener);
        tableSearchResults.getTableHeader().addMouseListener(popupListener);


        Toolkit t = getToolkit();
        int width = t.getScreenSize().width;
        int height = t.getScreenSize().height;

        // FIXME: don't build prefs panel here

        int showX = (width / 2) - (this.getWidth() / 2);
        int showY = (height / 2) - (this.getHeight() / 2);
        this.setLocation(showX, showY);
        CookSwing cookSwing = new CookSwing(this);
        newPreferencesDialog = (JDialog) cookSwing.render("org/irods/jargon/idrop/preferencesDialog.xml");
        boolean showGUI = getiDropCore().getPreferences().getBoolean("showGUI", true);
        showGUICheckBox.setSelected(showGUI);

        if (!getiDropCore().getIdropConfig().isAdvancedView()) {
            toolBarInfo.setVisible(false);
        }

        if (getiDropCore().getIrodsAccount() == null) {
            log.warn("no account, exiting");
            System.exit(0);
        }

        userNameLabel.setText("User: "
                + getiDropCore().getIrodsAccount().getUserName());


    }

    protected void showIdropGui() {
        buildIdropGuiComponents();
        setUpLocalFileSelectTree();
        buildTargetTree();
        setVisible(true);

    }

    protected void signalIdropCoreReadyAndSplashComplete() {
        createAndShowSystemTray();

        // FIXME: set up panel and options
        iDropCore.getIconManager().setRunningStatus(iDropCore.getTransferManager().getRunningStatus());
        boolean showGUI = getiDropCore().getPreferences().getBoolean("showGUI", true);
        if (showGUI) {
            showIdropGui();
        } else {
            MessageManager.showMessage(this,
                    "iDrop has started.\nCheck your system tray to access the iDrop user interface.",
                    "iDrop has started");
        }
    }

    @Override
    public synchronized void transferManagerErrorStatusUpdate(ErrorStatus es) {
        iDropCore.getIconManager().setErrorStatus(es);
    }

    @Override
    public synchronized void transferManagerRunningStatusUpdate(RunningStatus rs) {
        iDropCore.getIconManager().setRunningStatus(rs);
        if (rs == RunningStatus.PAUSED) {
            this.setTransferStatePaused();
        } else {
             this.setTransferStateUnpaused();
        }
    }

    @Override
    public void transferStatusCallback(TransferStatus ts) {
        // this.queuedTransfersLabel.setText("Queued Transfers: " + ts.getTotalFilesTransferredSoFar() + "/"
        //       + ts.getTotalFilesToTransfer());
        this.transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
        this.transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
        log.info("transfer status callback to iDROP:{}", ts);
    }

    /**
     * Display an error message dialog that indicates an exception has occcurred
     * 
     * @param idropException
     */
    public void showIdropException(Exception idropException) {
        JOptionPane.showMessageDialog(this, idropException.getMessage(), "iDROP Exception", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(thisIdropGui, messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Update the system tray icon based on the current status.
     * 
     * @param iconFile
     */
    public void updateIcon(final String iconFile) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                /* listener events may occur at startup before the GUI is fully prepared, ignore these */
                if (trayIcon == null) {
                    return;
                }
                Image newIcon = createImage(iconFile, "icon");

                trayIcon.setImage(newIcon);
            }
        });
    }

    /**
     * Builds the system tray menu and installs the iDrop icon in the system tray. The iDrop GUI is displayed when the
     * iDrop menu item is selected from the system tray
     */
    protected void createAndShowSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();

        final SystemTray tray = SystemTray.getSystemTray();

        if (trayIcon == null) {
            trayIcon = new TrayIcon(createImage("images/dialog-ok-2.png", "tray icon"));
        }
        trayIcon.setImageAutoSize(true);

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem iDropItem = new MenuItem("iDrop");
        MenuItem preferencesItem = new MenuItem("Preferences");
        MenuItem changePasswordItem = new MenuItem("Change Password");
        MenuItem synchItem = new MenuItem("Synch");

        iDropItem.addActionListener(this);

        MenuItem currentItem = new MenuItem("Show Current and Past Activity");

        MenuItem logoutItem = new MenuItem("Logout");

        pausedItem = new CheckboxMenuItem("Pause");

        MenuItem exitItem = new MenuItem("Exit");

        exitItem.addActionListener(this);
        currentItem.addActionListener(this);
        preferencesItem.addActionListener(this);
        synchItem.addActionListener(this);
        changePasswordItem.addActionListener(this);

        /*
         * See if I am in a paused state
         */

        if (this.getiDropCore().getTransferManager().getRunningStatus() == RunningStatus.PAUSED) {
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
        popup.add(synchItem);
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

    /** Returns an ImageIcon, or null if the path was invalid. FIXME: move to static util */
    protected static Image createImage(String path, String description) {
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
     * @return <code>IRODSAccount</code> with the current iRODS connection information.
     */
    public IRODSAccount getIrodsAccount() {
        synchronized (this) {
            return this.iDropCore.getIrodsAccount();
        }
    }

    /**
     * Set the current connection information.
     * 
     * @return <code>IRODSAccount</code> with the current iRODS connection information.
     */
    public void setIrodsAccount(IRODSAccount irodsAccount) {
        synchronized (this) {
            this.iDropCore.setIrodsAccount(irodsAccount);
        }
    }

    /**
     * Handler for iDrop system tray menu options.
     * 
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        Toolkit toolkit = getToolkit();

        if (e.getActionCommand().equals("Exit")) {
            getiDropCore().getQueueTimer().cancel();
            System.exit(0);
        } else if (e.getActionCommand().equals("Logout")) {
            this.setIrodsAccount(null);
            this.signalChangeInAccountSoCachedDataCanBeCleared();
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);

            if (getIrodsAccount() == null) {
                log.warn("no account, exiting");
                System.exit(0);
            } else {
                this.setVisible(false);
            }

        } else if (e.getActionCommand().equals("About")) {
            AboutDialog aboutDialog = new AboutDialog(this, true);
            int x = (toolkit.getScreenSize().width - aboutDialog.getWidth()) / 2;
            int y = (toolkit.getScreenSize().height - aboutDialog.getHeight()) / 2;
            aboutDialog.setLocation(x, y);
            aboutDialog.setVisible(true);
        } else if (e.getActionCommand().equals("Preferences")) {
            showGUICheckBox.setSelected(getiDropCore().getPreferences().getBoolean("showGUI", true));
            newPreferencesDialog.setVisible(true);
        } else if (e.getActionCommand().equals("Synch")) {
            synchSetupDialog = new SynchSetupDialog(this, getiDropCore(), getiDropCore().getIrodsFileSystem());
            synchSetupDialog.setVisible(true);
        } else if (e.getActionCommand().equals("Change Password")) {

            if (changePasswordDialog == null) {
                changePasswordDialog = new ChangePasswordDialog(this, true);
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
                // refresh the tree when setting visible again, the account may have changed.

                buildTargetTree();
                this.setVisible(true);
            }

            this.toFront();
        }

    }

    private boolean showQueueManagerDialog() {
        try {
            if (queueManagerDialog == null) {
                queueManagerDialog = new QueueManagerDialog(this, iDropCore.getTransferManager(),
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

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
        trayIcon.setImageAutoSize(true);
    }

    /**
     * A transfer confirm dialog
     * 
     * @param sourcePath
     *            <code>String</code> with the source path of the transfer
     * @param targetPath
     *            <code>String</code> with the target of the transfer
     * @return <code>int</code> with the dialog user response.
     */
    public int showTransferConfirm(final String sourcePath, final String targetPath) {

        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to transfer from ");
        sb.append(sourcePath);
        sb.append(" to ");
        sb.append(targetPath);

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(), "Transfer Confirmaiton", JOptionPane.YES_NO_OPTION);

        return n;
    }

    /**
     * A dialog to indicate that the queue should start processing
     */
    public int showTransferStartupConfirm() {

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(
                this,
                "There are transfers ready to process, should the transfer queue be started?  Click NO to pause the transfersf",
                "Begin Transfer Confirmation", JOptionPane.YES_NO_OPTION);

        return n;
    }

    /**
     * Returns the current iRODS remote tree view component.
     * 
     * @return <code>JTree</code> visual representation of the remote iRODS resource
     */
    public javax.swing.JTree getTreeStagingResource() {
        return irodsTree;
    }

    /**
     * Indicate that the GUI should reflect a paused state
     * 
     */
    public void setTransferStatePaused() {
        pausedItem.setState(true);
        this.togglePauseTransfer.setSelected(true);
    }

    /**
     * Indicate that the gui should show an unpaused state.
     */
    public void setTransferStateUnpaused() {
        pausedItem.setState(false);
        this.togglePauseTransfer.setSelected(false);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

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
                Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Show or hide the iRODS info panel and manage the state of the show info menu and toggle so that they remain in
     * synch
     */
    private void handleInfoPanelShowOrHide() {
        final iDrop idropGuiReference = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                pnlIrodsInfo.setVisible(toggleIrodsDetails.isSelected());
                jCheckBoxMenuItemShowIrodsInfo.setSelected(toggleIrodsDetails.isSelected());
                // if info is being opened, initialize to the first selected item, or the root of the iRODS tree if none
                // selected
                IRODSNode node;
                if (!(irodsTree.getLastSelectedPathComponent() instanceof IRODSNode)) {
                    log.info("last selected is not a Node, using root node");
                    node = (IRODSNode) irodsTree.getModel().getRoot();
                } else {
                    log.info("initializing with last selected node");
                    node = (IRODSNode) irodsTree.getLastSelectedPathComponent();
                }
                try {
                    IrodsTreeListenerForBuildingInfoPanel treeBuilder = new IrodsTreeListenerForBuildingInfoPanel(
                            idropGuiReference);
                    treeBuilder.identifyNodeTypeAndInitializeInfoPanel(node);
                } catch (Exception ex) {
                    Logger.getLogger(IrodsTreeListenerForBuildingInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException("exception processing valueChanged() event for IRODSNode selection");
                }
                if (pnlIrodsInfo.isVisible()) {
                    splitTargetCollections.setDividerLocation(0.5d);
                }
            }
        });
    }

    /**
     * Set up a JTree that depicts the local file system
     */
    private void setUpLocalFileSelectTree() {

        /*
         * build a list of the roots (e.g. drives on windows systems). If there is only one, use it as the basis for the
         * file model, otherwise, display an additional panel listing the other roots, and build the tree for the first
         * drive encountered.
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
                fileTree.setDragEnabled(true);
                fileTree.setDropMode(javax.swing.DropMode.ON);
                fileTree.setTransferHandler(new TransferHandler("selectionModel"));
                fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                listLocalDrives.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        log.debug("new local file system model");
                        log.debug("selection event:{}", e);
                        Object selectedItem = listLocalDrives.getSelectedValue();
                        initializeLocalFileTreeModelWhenDriveIsSelected(selectedItem);

                    }
                });
                scrollLocalFileTree.setViewportView(fileTree);
                pnlLocalTreeArea.add(scrollLocalFileTree, java.awt.BorderLayout.CENTER);
            }
        });

    }

    private void initializeLocalFileTreeModelWhenDriveIsSelected(final Object selectedDrive) {
        if (selectedDrive == null) {
            log.debug("selected drive is null, use the first one");
            listLocalDrives.setSelectedIndex(0);

            localFileModel = new LocalFileSystemModel(new LocalFileNode(new File(
                    (String) listLocalDrives.getSelectedValue())));

            fileTree.setModel(localFileModel);
        } else {
            log.debug("selected drive is not null, create new root based on selection", selectedDrive);
            listLocalDrives.setSelectedValue(selectedDrive, true);
            localFileModel = new LocalFileSystemModel(new LocalFileNode(new File((String) selectedDrive)));
            fileTree.setModel(localFileModel);

        }

        scrollLocalDrives.setVisible(true);
    }

    private void initializeLocalFileTreeModel(final Object selectedDrive) {
        List<String> roots = LocalFileUtils.listFileRootsForSystem();

        if (roots.isEmpty()) {
            IdropException ie = new IdropException("unable to find any roots on the local file system");
            log.error("error building roots on local file system", ie);
            showIdropException(ie);
            return;
        } else if (roots.size() == 1) {
            scrollLocalDrives.setVisible(false);
            localFileModel = new LocalFileSystemModel(new LocalFileNode(new File(roots.get(0))));

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
     * build the JTree that will depict the iRODS resource
     */
    public void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final iDrop gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                log.debug("refreshing series panel");
                Enumeration<TreePath> currentPaths = null;
                TreePath rootPath = null;

                if (getTreeStagingResource() != null) {
                    rootPath = getTreeStagingResource().getPathForRow(0);
                    currentPaths = getTreeStagingResource().getExpandedDescendants(rootPath);
                    log.debug("selected tree node, paths are:{}", currentPaths);
                }

                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();

                if (iDropCore.getIdropConfig().isLoginPreset()) {
                    log.info("using policy preset home directory");
                    StringBuilder sb = new StringBuilder();
                    sb.append("/");
                    sb.append(getIrodsAccount().getZone());
                    sb.append("/");
                    sb.append("home");
                    root.setParentPath(sb.toString());
                    root.setPathOrName(getIrodsAccount().getHomeDirectory());
                } else {
                    log.info("using root path, no login preset");
                    root.setPathOrName("/");
                }

                log.info("building new iRODS tree");
                try {
                    irodsTree = new IRODSTree(gui);
                    IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(), getiDropCore().getIrodsFileSystem(), irodsTree);
                    irodsTree.setModel(new IRODSFileSystemModel(rootNode, getIrodsAccount()));
                    irodsTree.setRefreshingTree(true);
                    irodsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    IrodsTreeListenerForBuildingInfoPanel treeListener = new IrodsTreeListenerForBuildingInfoPanel(gui);
                    irodsTree.addTreeExpansionListener(treeListener);
                    irodsTree.addTreeSelectionListener(treeListener);
                    // preset to display root tree node
                    irodsTree.setSelectionRow(0);
                } catch (Exception ex) {
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                }

                scrollIrodsTree.setViewportView(getTreeStagingResource());

                TreePath currentPath;

                if (currentPaths != null) {
                    while (currentPaths.hasMoreElements()) {
                        currentPath = (TreePath) currentPaths.nextElement();
                        log.debug("expanding tree path:{}", currentPath);
                        irodsTree.expandPath(currentPath);
                    }
                }
                irodsTree.setRefreshingTree(false);
                getiDropCore().getIrodsFileSystem().closeAndEatExceptions(iDropCore.getIrodsAccount());
            }
        });
    }

    public void initializeInfoPane(final CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntry)
            throws IdropException {
        if (!toggleIrodsDetails.isSelected()) {
            log.info("info display not selected, don't bother");
            return;
        }

        if (collectionAndDataObjectListingEntry == null) {
            throw new IdropException("null collectionAndDataObjectListingEntry");
        }

        final iDrop idropGui = this;

        // need to get the collection or data object info from iRODS

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    if (collectionAndDataObjectListingEntry.getObjectType() == CollectionAndDataObjectListingEntry.ObjectType.COLLECTION) {
                        log.info("looking up collection to build info panel");
                        CollectionAO collectionAO = getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().getCollectionAO(
                                getIrodsAccount());
                        Collection collection = collectionAO.findByAbsolutePath(collectionAndDataObjectListingEntry.getPathOrName());
                        initializeInfoPanel(collection);
                    } else {
                        log.info("looking up data object to build info panel");
                        DataObjectAO dataObjectAO = getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().getDataObjectAO(
                                getIrodsAccount());
                        DataObject dataObject = dataObjectAO.findByAbsolutePath(collectionAndDataObjectListingEntry.getParentPath() + "/" + collectionAndDataObjectListingEntry.getPathOrName());
                        initializeInfoPanel(dataObject);
                    }

                } catch (Exception e) {
                    log.error("exception building info panel from collection and data object listing entry:{}",
                            collectionAndDataObjectListingEntry, e);
                    throw new IdropRuntimeException(e);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(getIrodsAccount());
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                }
            }
        });
    }

    /**
     * Initialize the info panel with data from iRODS. In this case, the data is an iRODS data object (file)
     * 
     * @param dataObject
     *            <code>DataObject</code> iRODS domain object for a file.
     * @throws IdropException
     */
    public void initializeInfoPanel(final DataObject dataObject) throws IdropException {

        if (!toggleIrodsDetails.isSelected()) {
            log.info("info display not selected, don't bother");
            return;
        }

        if (dataObject == null) {
            throw new IdropException("Null dataObject");
        }

        this.lastCachedInfoItem = dataObject;
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                lblFileOrCollectionName.setText(dataObject.getDataName());
                txtParentPath.setText(dataObject.getCollectionName());
                txtComment.setText(dataObject.getComments());

                log.debug("getting available tags for data object");

                try {
                    FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(
                            getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(), getiDropCore().getIrodsAccount());
                    IRODSTagGrouping irodsTagGrouping = freeTaggingService.getTagsForDataObjectInFreeTagForm(dataObject.getCollectionName() + "/" + dataObject.getDataName());
                    txtTags.setText(irodsTagGrouping.getSpaceDelimitedTagsForDomain());
                    pnlInfoIcon.removeAll();
                    pnlInfoIcon.add(IconHelper.getFileIcon());
                    pnlInfoIcon.validate();
                    lblInfoCreatedAtValue.setText(df.format(dataObject.getCreatedAt()));
                    lblInfoUpdatedAtValue.setText(df.format(dataObject.getUpdatedAt()));
                    lblInfoLengthValue.setText(String.valueOf(dataObject.getDataSize()));
                    lblInfoLengthValue.setVisible(true);
                    lblInfoLength.setVisible(true);
                } catch (JargonException ex) {
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(getIrodsAccount());
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

    }

    /**
     * Initialize the info panel with data from iRODS. In this case, the data is an iRODS collection (directory).
     * 
     * @param collection
     * @throws IdropException
     */
    public void initializeInfoPanel(final Collection collection) throws IdropException {
        if (collection == null) {
            throw new IdropException("Null collection");
        }

        log.info("initialize info panel with collection:{}", collection);

        if (!toggleIrodsDetails.isSelected()) {
            log.info("info display not selected, don't bother");
            return;
        }

        this.lastCachedInfoItem = collection;
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                lblFileOrCollectionName.setText(collection.getCollectionLastPathComponent());
                txtParentPath.setText(collection.getCollectionParentName());
                txtComment.setText(collection.getComments());

                log.debug("getting available tags for data object");

                try {
                    FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(
                            getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(), getIrodsAccount());
                    IRODSTagGrouping irodsTagGrouping = freeTaggingService.getTagsForCollectionInFreeTagForm(collection.getCollectionName());
                    txtTags.setText(irodsTagGrouping.getSpaceDelimitedTagsForDomain());
                    pnlInfoIcon.removeAll();
                    pnlInfoIcon.add(IconHelper.getFolderIcon());
                    pnlInfoIcon.validate();
                    lblInfoCreatedAtValue.setText(df.format(collection.getCreatedAt()));
                    lblInfoUpdatedAtValue.setText(df.format(collection.getModifiedAt()));
                    lblInfoLengthValue.setVisible(false);
                    lblInfoLength.setVisible(false);
                } catch (JargonException ex) {
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(getIrodsAccount());
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    /**
     * Get the JTree component that represents the iRODS file system in the iDrop gui.
     * 
     * @return <code>IRODSTree</code> that is the JTree component for the iRODS file system view.
     */
    public IRODSTree getIrodsTree() {
        return irodsTree;
    }

    public JToggleButton getToggleIrodsDetails() {
        return toggleIrodsDetails;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     * 
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        searchTablePopupMenu = new javax.swing.JPopupMenu();
        menuItemShowInHierarchy = new javax.swing.JMenuItem();
        iDropToolbar = new javax.swing.JPanel();
        pnlToolbarSizer = new javax.swing.JPanel();
        pnlTopToolbarSearchArea = new javax.swing.JPanel();
        pnlSearchSizer = new javax.swing.JPanel();
        lblMainSearch = new javax.swing.JLabel();
        comboSearchType = new javax.swing.JComboBox();
        txtMainSearch = new javax.swing.JTextField();
        btnearch = new javax.swing.JButton();
        pnlLocalToggleSizer = new javax.swing.JPanel();
        toggleLocalFiles = new javax.swing.JToggleButton();
        pnlIrodsDetailsToggleSizer = new javax.swing.JPanel();
        toggleIrodsDetails = new javax.swing.JToggleButton();
        pnlIdropMain = new javax.swing.JPanel();
        jSplitPanelLocalRemote = new javax.swing.JSplitPane();
        pnlLocalTreeArea = new javax.swing.JPanel();
        pnlLocalRoots = new javax.swing.JPanel();
        scrollLocalDrives = new javax.swing.JScrollPane();
        listLocalDrives = new javax.swing.JList();
        pnlRefreshButton = new javax.swing.JPanel();
        btnRefreshLocalDrives = new javax.swing.JButton();
        pnlDrivesFiller = new javax.swing.JPanel();
        scrollLocalFileTree = new javax.swing.JScrollPane();
        pnlIrodsArea = new javax.swing.JPanel();
        splitTargetCollections = new javax.swing.JSplitPane();
        tabIrodsViews = new javax.swing.JTabbedPane();
        pnlTabHierarchicalView = new javax.swing.JPanel();
        pnlIrodsTreeToolbar = new javax.swing.JPanel();
        btnRefreshTargetTree = new javax.swing.JButton();
        pnlIrodsTreeMaster = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();
        pnlTargetTree = new javax.swing.JPanel();
        pnlTabSearch = new javax.swing.JPanel();
        pnlTabSearchTop = new javax.swing.JPanel();
        pnlTabSearchResults = new javax.swing.JPanel();
        scrollPaneSearchResults = new javax.swing.JScrollPane();
        tableSearchResults = new javax.swing.JTable();
        pnlIrodsInfo = new javax.swing.JPanel();
        pnlIrodsInfoInner = new javax.swing.JPanel();
        pnlFileIconSizer = new javax.swing.JPanel();
        pnlInfoIcon = new javax.swing.JPanel();
        pnlFileNameAndIcon = new javax.swing.JPanel();
        lblFileOrCollectionName = new javax.swing.JLabel();
        pnlInfoCollectionParent = new javax.swing.JPanel();
        lblFileParent = new javax.swing.JLabel();
        scrollParentPath = new javax.swing.JScrollPane();
        txtParentPath = new javax.swing.JTextArea();
        pnlInfoComment = new javax.swing.JPanel();
        lblComment = new javax.swing.JLabel();
        pnlInfoCommentScrollSizer = new javax.swing.JPanel();
        scrollComment = new javax.swing.JScrollPane();
        txtComment = new javax.swing.JTextArea();
        pnlInfoTags = new javax.swing.JPanel();
        lblTags = new javax.swing.JLabel();
        txtTags = new javax.swing.JTextField();
        pnlInfoButton = new javax.swing.JPanel();
        btnUpdateInfo = new javax.swing.JButton();
        pnlInfoDetails = new javax.swing.JPanel();
        lblInfoCreatedAt = new javax.swing.JLabel();
        lblInfoCreatedAtValue = new javax.swing.JLabel();
        lblInfoUpdatedAt = new javax.swing.JLabel();
        lblInfoUpdatedAtValue = new javax.swing.JLabel();
        lblInfoLength = new javax.swing.JLabel();
        lblInfoLengthValue = new javax.swing.JLabel();
        pnlToolbarInfo = new javax.swing.JPanel();
        toolBarInfo = new javax.swing.JToolBar();
        btnViewMetadata = new javax.swing.JButton();
        btnReplication = new javax.swing.JButton();
        separator1 = new javax.swing.JToolBar.Separator();
        btnMoveToTrash = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        pnlIdropBottom = new javax.swing.JPanel();
        userNameLabel = new javax.swing.JLabel();
        transferStatusProgressBar = new javax.swing.JProgressBar();
        transferQueueToolbarPanel = new javax.swing.JPanel();
        idropProgressPanelToolbar = new javax.swing.JToolBar();
        btnShowTransferManager = new javax.swing.JButton();
        togglePauseTransfer = new javax.swing.JToggleButton();
        pnlIdropProgressIcon = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuView = new javax.swing.JMenu();
        jCheckBoxMenuItemShowSourceTree = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowIrodsInfo = new javax.swing.JCheckBoxMenuItem();

        menuItemShowInHierarchy.setText("Show in iRODS");
        menuItemShowInHierarchy.setToolTipText("Show this file or collection in the iRODS hierarchy");
        menuItemShowInHierarchy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemShowInHierarchyActionPerformed(evt);
            }
        });
        searchTablePopupMenu.add(menuItemShowInHierarchy);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("iDrop - iRODS Cloud Browser");
        setMinimumSize(new java.awt.Dimension(600, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        iDropToolbar.setMinimumSize(new java.awt.Dimension(800, 400));
        iDropToolbar.setPreferredSize(new java.awt.Dimension(1077, 40));
        iDropToolbar.setLayout(new java.awt.BorderLayout());

        pnlToolbarSizer.setLayout(new java.awt.BorderLayout());

        pnlTopToolbarSearchArea.setMinimumSize(new java.awt.Dimension(45, 50));
        pnlTopToolbarSearchArea.setLayout(new java.awt.BorderLayout());

        pnlSearchSizer.setMinimumSize(new java.awt.Dimension(74, 30));
        pnlSearchSizer.setPreferredSize(new java.awt.Dimension(254, 50));
        pnlSearchSizer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        lblMainSearch.setText("Search:");
        lblMainSearch.setMaximumSize(null);
        lblMainSearch.setMinimumSize(null);
        lblMainSearch.setPreferredSize(new java.awt.Dimension(45, 40));
        pnlSearchSizer.add(lblMainSearch);

        comboSearchType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "by name", "by tag", "by name and tag" }));
        comboSearchType.setToolTipText("Select the type of search to be carried out using the supplied search string");
        pnlSearchSizer.add(comboSearchType);

        txtMainSearch.setColumns(20);
        txtMainSearch.setToolTipText("Search for files or tags");
        txtMainSearch.setMinimumSize(null);
        txtMainSearch.setPreferredSize(new java.awt.Dimension(100, 30));
        txtMainSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMainSearchKeyPressed(evt);
            }
        });
        pnlSearchSizer.add(txtMainSearch);

        btnearch.setMnemonic('s');
        btnearch.setText("Search");
        btnearch.setToolTipText("Search iRODS based on the current view selected");
        btnearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnearchActionPerformed(evt);
            }
        });
        pnlSearchSizer.add(btnearch);

        pnlTopToolbarSearchArea.add(pnlSearchSizer, java.awt.BorderLayout.SOUTH);

        pnlToolbarSizer.add(pnlTopToolbarSearchArea, java.awt.BorderLayout.CENTER);

        pnlLocalToggleSizer.setLayout(new java.awt.BorderLayout());

        toggleLocalFiles.setText("<<< Local Files");
        toggleLocalFiles.setToolTipText("Browse the local file system.");
        toggleLocalFiles.setMaximumSize(new java.awt.Dimension(144, 10));
        toggleLocalFiles.setMinimumSize(new java.awt.Dimension(144, 10));
        toggleLocalFiles.setPreferredSize(new java.awt.Dimension(144, 30));
        toggleLocalFiles.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                toggleLocalFilesStateChanged(evt);
            }
        });
        toggleLocalFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleLocalFilesActionPerformed(evt);
            }
        });
        pnlLocalToggleSizer.add(toggleLocalFiles, java.awt.BorderLayout.NORTH);
        toggleLocalFiles.getAccessibleContext().setAccessibleName("<<< Local Files ");

        pnlToolbarSizer.add(pnlLocalToggleSizer, java.awt.BorderLayout.WEST);

        pnlIrodsDetailsToggleSizer.setLayout(new java.awt.BorderLayout());

        toggleIrodsDetails.setToolTipText("Browse the local file system.");
        toggleIrodsDetails.setLabel("iRODS Info >>>>");
        toggleIrodsDetails.setMaximumSize(new java.awt.Dimension(144, 10));
        toggleIrodsDetails.setMinimumSize(new java.awt.Dimension(144, 10));
        toggleIrodsDetails.setPreferredSize(new java.awt.Dimension(144, 30));
        toggleIrodsDetails.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                toggleIrodsDetailsStateChanged(evt);
            }
        });
        toggleIrodsDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleIrodsDetailsActionPerformed(evt);
            }
        });
        pnlIrodsDetailsToggleSizer.add(toggleIrodsDetails, java.awt.BorderLayout.NORTH);
        toggleIrodsDetails.getAccessibleContext().setAccessibleName("");

        pnlToolbarSizer.add(pnlIrodsDetailsToggleSizer, java.awt.BorderLayout.EAST);

        iDropToolbar.add(pnlToolbarSizer, java.awt.BorderLayout.NORTH);

        getContentPane().add(iDropToolbar, java.awt.BorderLayout.NORTH);

        pnlIdropMain.setPreferredSize(new java.awt.Dimension(500, 300));
        pnlIdropMain.setLayout(new javax.swing.BoxLayout(pnlIdropMain, javax.swing.BoxLayout.PAGE_AXIS));

        jSplitPanelLocalRemote.setBorder(null);
        jSplitPanelLocalRemote.setDividerLocation(250);
        jSplitPanelLocalRemote.setDividerSize(30);
        jSplitPanelLocalRemote.setMaximumSize(null);
        jSplitPanelLocalRemote.setPreferredSize(new java.awt.Dimension(0, 0));

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

        pnlRefreshButton.setMaximumSize(new java.awt.Dimension(1000, 30));
        pnlRefreshButton.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlRefreshButton.setPreferredSize(new java.awt.Dimension(101, 30));

        btnRefreshLocalDrives.setLabel("Refresh");
        btnRefreshLocalDrives.setMaximumSize(new java.awt.Dimension(200, 50));
        btnRefreshLocalDrives.setMinimumSize(new java.awt.Dimension(0, 0));
        btnRefreshLocalDrives.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshLocalDrivesActionPerformed(evt);
            }
        });
        pnlRefreshButton.add(btnRefreshLocalDrives);

        pnlLocalRoots.add(pnlRefreshButton, java.awt.BorderLayout.NORTH);
        pnlLocalRoots.add(pnlDrivesFiller, java.awt.BorderLayout.SOUTH);

        pnlLocalTreeArea.add(pnlLocalRoots, java.awt.BorderLayout.NORTH);

        scrollLocalFileTree.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        scrollLocalFileTree.setBorder(null);
        scrollLocalFileTree.setToolTipText("scroll panel tooltip");
        scrollLocalFileTree.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollLocalFileTree.setMaximumSize(null);
        scrollLocalFileTree.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollLocalFileTree.setPreferredSize(new java.awt.Dimension(500, 500));
        pnlLocalTreeArea.add(scrollLocalFileTree, java.awt.BorderLayout.CENTER);

        jSplitPanelLocalRemote.setLeftComponent(pnlLocalTreeArea);

        pnlIrodsArea.setPreferredSize(new java.awt.Dimension(600, 304));
        pnlIrodsArea.setLayout(new java.awt.BorderLayout());

        splitTargetCollections.setDividerLocation(400);
        splitTargetCollections.setMinimumSize(new java.awt.Dimension(0, 0));

        tabIrodsViews.setPreferredSize(new java.awt.Dimension(350, 300));
        tabIrodsViews.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabIrodsViewsStateChanged(evt);
            }
        });

        pnlTabHierarchicalView.setLayout(new java.awt.BorderLayout());

        btnRefreshTargetTree.setMnemonic('r');
        btnRefreshTargetTree.setText("Refresh");
        btnRefreshTargetTree.setToolTipText("Refresh the view of the iRODS server");
        btnRefreshTargetTree.setFocusable(false);
        btnRefreshTargetTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshTargetTreeActionPerformed(evt);
            }
        });
        pnlIrodsTreeToolbar.add(btnRefreshTargetTree);

        pnlTabHierarchicalView.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlIrodsTreeMaster.setLayout(new java.awt.BorderLayout());

        scrollIrodsTree.setMinimumSize(null);
        scrollIrodsTree.setPreferredSize(null);

        pnlTargetTree.setLayout(new java.awt.BorderLayout());
        scrollIrodsTree.setViewportView(pnlTargetTree);

        pnlIrodsTreeMaster.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        pnlTabHierarchicalView.add(pnlIrodsTreeMaster, java.awt.BorderLayout.CENTER);

        tabIrodsViews.addTab("iRODS Tree View", pnlTabHierarchicalView);

        pnlTabSearch.setToolTipText("Search for files and collections in iRODS and display search results");
        pnlTabSearch.setLayout(new java.awt.BorderLayout());
        pnlTabSearch.add(pnlTabSearchTop, java.awt.BorderLayout.NORTH);

        pnlTabSearchResults.setLayout(new java.awt.GridLayout(1, 0));

        tableSearchResults.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollPaneSearchResults.setViewportView(tableSearchResults);

        pnlTabSearchResults.add(scrollPaneSearchResults);

        pnlTabSearch.add(pnlTabSearchResults, java.awt.BorderLayout.CENTER);

        tabIrodsViews.addTab("Search", null, pnlTabSearch, "Search for files and collections in iRODS and display search results");

        splitTargetCollections.setLeftComponent(tabIrodsViews);

        pnlIrodsInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlIrodsInfo.setLayout(new java.awt.BorderLayout());

        pnlIrodsInfoInner.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlIrodsInfoInner.setToolTipText("Information on selected iRODS file or collection");
        pnlIrodsInfoInner.setLayout(new java.awt.GridBagLayout());

        pnlFileIconSizer.setMinimumSize(new java.awt.Dimension(80, 40));
        pnlFileIconSizer.setLayout(new java.awt.BorderLayout());

        pnlInfoIcon.setMaximumSize(new java.awt.Dimension(50, 50));
        pnlInfoIcon.setLayout(new java.awt.GridLayout(1, 0));
        pnlFileIconSizer.add(pnlInfoIcon, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlFileIconSizer, gridBagConstraints);

        pnlFileNameAndIcon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlFileNameAndIcon.setMinimumSize(new java.awt.Dimension(100, 100));
        pnlFileNameAndIcon.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        lblFileOrCollectionName.setMinimumSize(new java.awt.Dimension(80, 16));
        pnlFileNameAndIcon.add(lblFileOrCollectionName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlFileNameAndIcon, gridBagConstraints);

        pnlInfoCollectionParent.setLayout(new java.awt.BorderLayout());

        lblFileParent.setText("Parent path of file:");
        pnlInfoCollectionParent.add(lblFileParent, java.awt.BorderLayout.NORTH);
        lblFileParent.getAccessibleContext().setAccessibleDescription("The path of the parent of the file or collection");

        scrollParentPath.setMinimumSize(new java.awt.Dimension(100, 100));

        txtParentPath.setEditable(false);
        txtParentPath.setMaximumSize(null);
        txtParentPath.setMinimumSize(new java.awt.Dimension(600, 100));
        scrollParentPath.setViewportView(txtParentPath);

        pnlInfoCollectionParent.add(scrollParentPath, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlInfoCollectionParent, gridBagConstraints);

        pnlInfoComment.setLayout(new java.awt.BorderLayout());

        lblComment.setText("Comment:");
        lblComment.setToolTipText("");
        pnlInfoComment.add(lblComment, java.awt.BorderLayout.NORTH);
        lblComment.getAccessibleContext().setAccessibleDescription("lable for comment area");

        pnlInfoCommentScrollSizer.setPreferredSize(new java.awt.Dimension(388, 84));
        pnlInfoCommentScrollSizer.setLayout(new java.awt.BorderLayout());

        scrollComment.setMinimumSize(null);
        scrollComment.setPreferredSize(new java.awt.Dimension(388, 84));

        txtComment.setMaximumSize(null);
        txtComment.setMinimumSize(null);
        txtComment.setPreferredSize(null);
        scrollComment.setViewportView(txtComment);

        pnlInfoCommentScrollSizer.add(scrollComment, java.awt.BorderLayout.WEST);

        pnlInfoComment.add(pnlInfoCommentScrollSizer, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlIrodsInfoInner.add(pnlInfoComment, gridBagConstraints);

        pnlInfoTags.setLayout(new java.awt.BorderLayout());

        lblTags.setText("Tags:");
        lblTags.setToolTipText("");
        pnlInfoTags.add(lblTags, java.awt.BorderLayout.NORTH);
        lblTags.getAccessibleContext().setAccessibleName("Tags");
        lblTags.getAccessibleContext().setAccessibleDescription("Label for free tagging area");

        txtTags.setColumns(30);
        txtTags.setToolTipText("Name of file or collection.  This field allows editing to rename");
        txtTags.setMinimumSize(null);
        txtTags.setPreferredSize(null);
        txtTags.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTagsFocusLost(evt);
            }
        });
        txtTags.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTagsKeyPressed(evt);
            }
        });
        pnlInfoTags.add(txtTags, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlInfoTags, gridBagConstraints);

        pnlInfoButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnUpdateInfo.setMnemonic('I');
        btnUpdateInfo.setText("Update Info");
        btnUpdateInfo.setToolTipText("Update information on the info panel such as tags and comment");
        btnUpdateInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateInfoActionPerformed(evt);
            }
        });
        pnlInfoButton.add(btnUpdateInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        pnlIrodsInfoInner.add(pnlInfoButton, gridBagConstraints);

        pnlInfoDetails.setLayout(new java.awt.GridBagLayout());

        lblInfoCreatedAt.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoCreatedAt.setText("Created:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlInfoDetails.add(lblInfoCreatedAt, gridBagConstraints);

        lblInfoCreatedAtValue.setText("XXXXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInfoDetails.add(lblInfoCreatedAtValue, gridBagConstraints);

        lblInfoUpdatedAt.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoUpdatedAt.setText("Updated:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlInfoDetails.add(lblInfoUpdatedAt, gridBagConstraints);

        lblInfoUpdatedAtValue.setText("XXXXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInfoDetails.add(lblInfoUpdatedAtValue, gridBagConstraints);

        lblInfoLength.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoLength.setText("Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlInfoDetails.add(lblInfoLength, gridBagConstraints);

        lblInfoLengthValue.setText("XXXXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInfoDetails.add(lblInfoLengthValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlInfoDetails, gridBagConstraints);

        pnlToolbarInfo.setMinimumSize(new java.awt.Dimension(300, 200));
        pnlToolbarInfo.setLayout(new java.awt.BorderLayout());

        toolBarInfo.setRollover(true);

        btnViewMetadata.setText("Metadata");
        btnViewMetadata.setFocusable(false);
        btnViewMetadata.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnViewMetadata.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewMetadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewMetadataActionPerformed(evt);
            }
        });
        toolBarInfo.add(btnViewMetadata);

        btnReplication.setText("Replication");
        btnReplication.setFocusable(false);
        btnReplication.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplicationActionPerformed(evt);
            }
        });
        toolBarInfo.add(btnReplication);
        toolBarInfo.add(separator1);

        btnMoveToTrash.setText("Move to Trash");
        btnMoveToTrash.setFocusable(false);
        btnMoveToTrash.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveToTrash.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarInfo.add(btnMoveToTrash);

        separator2.setMinimumSize(new java.awt.Dimension(50, 1));
        toolBarInfo.add(separator2);

        pnlToolbarInfo.add(toolBarInfo, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.8;
        pnlIrodsInfoInner.add(pnlToolbarInfo, gridBagConstraints);

        pnlIrodsInfo.add(pnlIrodsInfoInner, java.awt.BorderLayout.CENTER);
        pnlIrodsInfoInner.getAccessibleContext().setAccessibleName("info panel");

        splitTargetCollections.setRightComponent(pnlIrodsInfo);

        pnlIrodsArea.add(splitTargetCollections, java.awt.BorderLayout.CENTER);

        jSplitPanelLocalRemote.setRightComponent(pnlIrodsArea);

        pnlIdropMain.add(jSplitPanelLocalRemote);

        getContentPane().add(pnlIdropMain, java.awt.BorderLayout.CENTER);

        pnlIdropBottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlIdropBottom.setToolTipText("Display area for status and messages");
        pnlIdropBottom.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0050;
        pnlIdropBottom.add(userNameLabel, gridBagConstraints);

        transferStatusProgressBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        transferStatusProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8.0;
        pnlIdropBottom.add(transferStatusProgressBar, gridBagConstraints);

        transferQueueToolbarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        idropProgressPanelToolbar.setRollover(true);

        btnShowTransferManager.setText("Manage");
        btnShowTransferManager.setToolTipText("Show a panel to manage transfers");
        btnShowTransferManager.setFocusable(false);
        btnShowTransferManager.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowTransferManager.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnShowTransferManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowTransferManagerActionPerformed(evt);
            }
        });
        idropProgressPanelToolbar.add(btnShowTransferManager);

        togglePauseTransfer.setText("Pause");
        togglePauseTransfer.setToolTipText("Pause the current transfer");
        togglePauseTransfer.setFocusable(false);
        togglePauseTransfer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togglePauseTransfer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togglePauseTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePauseTransferActionPerformed(evt);
            }
        });
        idropProgressPanelToolbar.add(togglePauseTransfer);

        transferQueueToolbarPanel.add(idropProgressPanelToolbar);
        transferQueueToolbarPanel.add(pnlIdropProgressIcon);

        pnlIdropBottom.add(transferQueueToolbarPanel, new java.awt.GridBagConstraints());

        getContentPane().add(pnlIdropBottom, java.awt.BorderLayout.SOUTH);

        jMenuFile.setMnemonic('f');
        jMenuFile.setText("File");

        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setToolTipText("Close the iDROP console window");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setMnemonic('E');
        jMenuEdit.setText("Edit");
        jMenuBar1.add(jMenuEdit);

        jMenuView.setMnemonic('V');
        jMenuView.setText("View");

        jCheckBoxMenuItemShowSourceTree.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK));
        jCheckBoxMenuItemShowSourceTree.setMnemonic('L');
        jCheckBoxMenuItemShowSourceTree.setText("Show Local");
        jCheckBoxMenuItemShowSourceTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowSourceTreeActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemShowSourceTree);

        jCheckBoxMenuItemShowIrodsInfo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK));
        jCheckBoxMenuItemShowIrodsInfo.setMnemonic('I');
        jCheckBoxMenuItemShowIrodsInfo.setText("Show iRODS Info");
        jCheckBoxMenuItemShowIrodsInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowIrodsInfoActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemShowIrodsInfo);

        jMenuBar1.add(jMenuView);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnShowTransferManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowTransferManagerActionPerformed

        showQueueManagerDialog();
    }//GEN-LAST:event_btnShowTransferManagerActionPerformed

    /**
     * Click of 'pause' toggle in iDrop client view
     * @param evt 
     */
    private void togglePauseTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togglePauseTransferActionPerformed
        
          try {
                if (togglePauseTransfer.isSelected()) {
                    log.info("pausing....");
                    iDropCore.getTransferManager().pause();
                } else {
                    log.info("resuming queue");
                    iDropCore.getTransferManager().resume();
                }
            } catch (Exception ex) {
                Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_togglePauseTransferActionPerformed
    public ActionListener showPreferencesDialogActionListener = new ActionListener() {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            newPreferencesDialog.setVisible(true);
        }
    };
    public ActionListener okButtonPreferencesDialogActionListener = new ActionListener() {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            getiDropCore().getPreferences().putBoolean("showGUI", showGUICheckBox.isSelected() ? true : false);
            newPreferencesDialog.setVisible(false);
        }
    };

    private void formWindowClosed(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
        this.setVisible(false);
        this.formShown = false;
    }// GEN-LAST:event_formWindowClosed

    private void btnRefreshTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshTargetTreeActionPerformed
        buildTargetTree();
    }// GEN-LAST:event_btnRefreshTargetTreeActionPerformed

    private void toggleLocalFilesStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_toggleLocalFilesStateChanged
    }// GEN-LAST:event_toggleLocalFilesStateChanged

    private void toggleLocalFilesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleLocalFilesActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                pnlLocalTreeArea.setVisible(toggleLocalFiles.isSelected());
                jCheckBoxMenuItemShowSourceTree.setSelected(toggleLocalFiles.isSelected());
                if (pnlLocalTreeArea.isVisible()) {
                    jSplitPanelLocalRemote.setDividerLocation(0.3d);
                }
            }
        });
    }// GEN-LAST:event_toggleLocalFilesActionPerformed

    /**
     * Display/hide a panel that depicts the local file system.
     * 
     * @param evt
     */
    private void jCheckBoxMenuItemShowSourceTreeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItemShowSourceTreeActionPerformed
        toggleLocalFiles.setSelected(jCheckBoxMenuItemShowSourceTree.isSelected());
        toggleLocalFilesActionPerformed(evt);
    }// GEN-LAST:event_jCheckBoxMenuItemShowSourceTreeActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemExitActionPerformed
        this.setVisible(false);
    }// GEN-LAST:event_jMenuItemExitActionPerformed

    /**
     * Handle the press of the refresh local drives button, refresh the local file tree.
     * 
     * @param evt
     */
    private void btnRefreshLocalDrivesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshLocalDrivesActionPerformed
        // keep track of currently selected paths
        log.debug("refreshing local files tree");

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
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException("exception expanding tree nodes", ex);
                }

            }
        });

    }// GEN-LAST:event_btnRefreshLocalDrivesActionPerformed

    private void toggleIrodsDetailsStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_toggleIrodsDetailsStateChanged
        // unused right now
    }// GEN-LAST:event_toggleIrodsDetailsStateChanged

    /**
     * Show or hide the irods details panel
     * 
     * @param evt
     */
    private void toggleIrodsDetailsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleIrodsDetailsActionPerformed
        jCheckBoxMenuItemShowIrodsInfo.setSelected(toggleIrodsDetails.isSelected());
        handleInfoPanelShowOrHide();
    }// GEN-LAST:event_toggleIrodsDetailsActionPerformed

    private void jCheckBoxMenuItemShowIrodsInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItemShowIrodsInfoActionPerformed
        toggleIrodsDetails.setSelected(jCheckBoxMenuItemShowIrodsInfo.isSelected());
        handleInfoPanelShowOrHide();

    }// GEN-LAST:event_jCheckBoxMenuItemShowIrodsInfoActionPerformed

    /**
     * Focus lost on tags, update tags in the info box. Updates are done in the tagging service by taking a delta
     * between the current and desired tag set.
     * 
     * @param evt
     */
    private void txtTagsFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtTagsFocusLost
    }// GEN-LAST:event_txtTagsFocusLost

    /**
     * Method to clear any cached values when an account changes. Some data is cached and lazily loaded
     */
    public void signalChangeInAccountSoCachedDataCanBeCleared() {
        log.info("clearing any cached data associated with the account");

        irodsTree = null;
        lastCachedInfoItem = null;
    }

    /**
     * Tag view master panel has been shown, lazily load the user tag cloud if not loaded
     * 
     * @param evt
     */
    private void pnlTagViewMasterComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_pnlTagViewMasterComponentShown
        // dont think right event TODO: loose this event
    }// GEN-LAST:event_pnlTagViewMasterComponentShown

    private void tabIrodsViewsStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_tabIrodsViewsStateChanged
    }// GEN-LAST:event_tabIrodsViewsStateChanged

    private void txtMainSearchKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtMainSearchKeyPressed

        // enter key triggers search
        if (evt.getKeyCode() != java.awt.event.KeyEvent.VK_ENTER) {
            return;
        }

        processSearchRequest();

    }// GEN-LAST:event_txtMainSearchKeyPressed

    private void processSearchRequest() {
        log.info("do a search based on the search type combo");
        if (comboSearchType.getSelectedIndex() == 0) {
            log.info("searching files and collections");
            searchFilesAndShowSearchResultsTab(txtMainSearch.getText());
        } else if (comboSearchType.getSelectedIndex() == 1) {
            log.info("searching by tag value");
            searchTagsAndShowSearchResultsTag(txtMainSearch.getText());
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    private void txtTagsKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtTagsKeyPressed

        // FIXME: cull this

        // enter key triggers search
        if (evt.getKeyCode() != java.awt.event.KeyEvent.VK_ENTER) {
            return;
        }

    }// GEN-LAST:event_txtTagsKeyPressed

    /**
     * Display the data replication dialog for the collection or data object depicted in the info panel
     * 
     * @param evt
     */
    private void btnReplicationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnReplicationActionPerformed
        if (lastCachedInfoItem == null) {
            return;
        }

        ReplicationDialog replicationDialog;
        if (lastCachedInfoItem instanceof DataObject) {
            DataObject cachedDataObject = (DataObject) lastCachedInfoItem;
            replicationDialog = new ReplicationDialog(this, true, cachedDataObject.getCollectionName(),
                    cachedDataObject.getDataName());
        } else if (lastCachedInfoItem instanceof Collection) {
            Collection collection = (Collection) lastCachedInfoItem;
            replicationDialog = new ReplicationDialog(this, true, collection.getCollectionName());
        } else {
            showIdropException(new IdropException(
                    "Unknown type of object displayed in info area, cannot create the replication dialog"));
            throw new IdropRuntimeException("unknown type of object displayed in info area");
        }

        replicationDialog.setLocation((int) (this.getLocation().getX() + replicationDialog.getWidth() / 2), (int) (this.getLocation().getY() + replicationDialog.getHeight() / 2));
        replicationDialog.setVisible(true);
    }// GEN-LAST:event_btnReplicationActionPerformed

    /**
     * Display the metadata edit /view dialog for the item displayed in the info panel
     * 
     * @param evt
     */
    private void btnViewMetadataActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnViewMetadataActionPerformed
        if (lastCachedInfoItem == null) {
            return;
        }

        MetadataViewDialog metadataViewDialog;
        if (lastCachedInfoItem instanceof DataObject) {
            DataObject cachedDataObject = (DataObject) lastCachedInfoItem;
            metadataViewDialog = new MetadataViewDialog(this, getIrodsAccount(), cachedDataObject.getCollectionName(),
                    cachedDataObject.getDataName());
        } else if (lastCachedInfoItem instanceof Collection) {
            Collection collection = (Collection) lastCachedInfoItem;
            metadataViewDialog = new MetadataViewDialog(this, getIrodsAccount(), collection.getCollectionName());
        } else {
            showIdropException(new IdropException(
                    "Unknown type of object displayed in info area, cannot create the replication dialog"));
            throw new IdropRuntimeException("unknown type of object displayed in info area");
        }

        metadataViewDialog.setLocation((int) (this.getLocation().getX() + metadataViewDialog.getWidth() / 2),
                (int) (this.getLocation().getY() + metadataViewDialog.getHeight() / 2));
        metadataViewDialog.setVisible(true);
    }// GEN-LAST:event_btnViewMetadataActionPerformed

    private void btnUpdateInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateInfoActionPerformed
        // if I have cached an item, see if it is a file or collection

        if (this.lastCachedInfoItem == null) {
            log.warn("unknown data item, tags will not be processed");
            return;
        }

        // initialize a variable with the last item visible to the runnable
        final Object lastCachedItemToProcessTagsFor = this.lastCachedInfoItem;
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                FreeTaggingService freeTaggingService;

                try {
                    freeTaggingService = FreeTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                            getIrodsAccount());

                    if (lastCachedInfoItem instanceof Collection) {
                        log.info("processing tags for collection");
                        Collection collection = (Collection) lastCachedItemToProcessTagsFor;
                        IRODSTagGrouping irodsTagGrouping = new IRODSTagGrouping(MetadataDomain.COLLECTION, collection.getCollectionName(), txtTags.getText(), getIrodsAccount().getUserName());
                        log.debug("new tag set is:{}", txtTags.getText());
                        freeTaggingService.updateTags(irodsTagGrouping);
                    } else if (lastCachedInfoItem instanceof DataObject) {
                        log.info("processing tags for data object");
                        DataObject dataObject = (DataObject) lastCachedItemToProcessTagsFor;
                        IRODSTagGrouping irodsTagGrouping = new IRODSTagGrouping(MetadataDomain.DATA, dataObject.getCollectionName() + "/" + dataObject.getDataName(), txtTags.getText(),
                                getIrodsAccount().getUserName());
                        log.debug("new tag set is:{}", txtTags.getText());
                        freeTaggingService.updateTags(irodsTagGrouping);
                    } else {
                        log.error("unknown item type cached as being displayed in info area");
                        throw new IdropRuntimeException("unknown item type cached");
                    }

                    idropGui.showMessageFromOperation("update of info successful");

                } catch (JargonException ex) {
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    try {
                        getiDropCore().getIrodsFileSystem().close(getIrodsAccount());
                    } catch (JargonException ex) {
                        Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                        // logged and ignored
                    }
                }
            }
        });
    }// GEN-LAST:event_btnUpdateInfoActionPerformed

    /*
     * The search button has been pressed, do a search
     */
    private void btnearchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnearchActionPerformed
        processSearchRequest();
    }// GEN-LAST:event_btnearchActionPerformed

    private void menuItemShowInHierarchyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuItemShowInHierarchyActionPerformed

        int selRow = tableSearchResults.getSelectedRow();
        log.debug("selected:{}", selRow);
        IRODSSearchTableModel irodsSearchTableModel = (IRODSSearchTableModel) tableSearchResults.getModel();
        CollectionAndDataObjectListingEntry entry = irodsSearchTableModel.getEntries().get(
                tableSearchResults.convertRowIndexToModel(selRow));
        if (entry == null) {
            log.warn("no row found");
        }

        TreePath selPath;
        try {
            selPath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, entry.getFormattedAbsolutePath());
        } catch (IdropException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            this.showMessageFromOperation("That collection or file is not visible in the context of the current tree");
            return;
        }

        if (selPath == null) {
            log.warn("no tree path found for collection entry:{}", entry);
            return;
        }

        // select the path in the tree and show the hierarchy tab
        TreeUtils.expandAll(irodsTree, selPath, true);
        irodsTree.scrollPathToVisible(selPath);
        tabIrodsViews.setSelectedComponent(pnlTabHierarchicalView);
    }// GEN-LAST:event_menuItemShowInHierarchyActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMoveToTrash;
    private javax.swing.JButton btnRefreshLocalDrives;
    private javax.swing.JButton btnRefreshTargetTree;
    private javax.swing.JButton btnReplication;
    private javax.swing.JButton btnShowTransferManager;
    private javax.swing.JButton btnUpdateInfo;
    private javax.swing.JButton btnViewMetadata;
    private javax.swing.JButton btnearch;
    private javax.swing.JComboBox comboSearchType;
    private javax.swing.JPanel iDropToolbar;
    private javax.swing.JToolBar idropProgressPanelToolbar;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowIrodsInfo;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowSourceTree;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JSplitPane jSplitPanelLocalRemote;
    private javax.swing.JLabel lblComment;
    private javax.swing.JLabel lblFileOrCollectionName;
    private javax.swing.JLabel lblFileParent;
    private javax.swing.JLabel lblInfoCreatedAt;
    private javax.swing.JLabel lblInfoCreatedAtValue;
    private javax.swing.JLabel lblInfoLength;
    private javax.swing.JLabel lblInfoLengthValue;
    private javax.swing.JLabel lblInfoUpdatedAt;
    private javax.swing.JLabel lblInfoUpdatedAtValue;
    private javax.swing.JLabel lblMainSearch;
    private javax.swing.JLabel lblTags;
    private javax.swing.JList listLocalDrives;
    private javax.swing.JMenuItem menuItemShowInHierarchy;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlFileIconSizer;
    private javax.swing.JPanel pnlFileNameAndIcon;
    private javax.swing.JPanel pnlIdropBottom;
    private javax.swing.JPanel pnlIdropMain;
    private javax.swing.JPanel pnlIdropProgressIcon;
    private javax.swing.JPanel pnlInfoButton;
    private javax.swing.JPanel pnlInfoCollectionParent;
    private javax.swing.JPanel pnlInfoComment;
    private javax.swing.JPanel pnlInfoCommentScrollSizer;
    private javax.swing.JPanel pnlInfoDetails;
    private javax.swing.JPanel pnlInfoIcon;
    private javax.swing.JPanel pnlInfoTags;
    private javax.swing.JPanel pnlIrodsArea;
    private javax.swing.JPanel pnlIrodsDetailsToggleSizer;
    private javax.swing.JPanel pnlIrodsInfo;
    private javax.swing.JPanel pnlIrodsInfoInner;
    private javax.swing.JPanel pnlIrodsTreeMaster;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JPanel pnlLocalRoots;
    private javax.swing.JPanel pnlLocalToggleSizer;
    private javax.swing.JPanel pnlLocalTreeArea;
    private javax.swing.JPanel pnlRefreshButton;
    private javax.swing.JPanel pnlSearchSizer;
    private javax.swing.JPanel pnlTabHierarchicalView;
    private javax.swing.JPanel pnlTabSearch;
    private javax.swing.JPanel pnlTabSearchResults;
    private javax.swing.JPanel pnlTabSearchTop;
    private javax.swing.JPanel pnlTargetTree;
    private javax.swing.JPanel pnlToolbarInfo;
    private javax.swing.JPanel pnlToolbarSizer;
    private javax.swing.JPanel pnlTopToolbarSearchArea;
    private javax.swing.JScrollPane scrollComment;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JScrollPane scrollPaneSearchResults;
    private javax.swing.JScrollPane scrollParentPath;
    protected javax.swing.JPopupMenu searchTablePopupMenu;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JSplitPane splitTargetCollections;
    private javax.swing.JTabbedPane tabIrodsViews;
    private javax.swing.JTable tableSearchResults;
    private javax.swing.JToggleButton toggleIrodsDetails;
    private javax.swing.JToggleButton toggleLocalFiles;
    private javax.swing.JToggleButton togglePauseTransfer;
    private javax.swing.JToolBar toolBarInfo;
    private javax.swing.JPanel transferQueueToolbarPanel;
    private javax.swing.JProgressBar transferStatusProgressBar;
    private javax.swing.JTextArea txtComment;
    private javax.swing.JTextField txtMainSearch;
    private javax.swing.JTextArea txtParentPath;
    private javax.swing.JTextField txtTags;
    private javax.swing.JLabel userNameLabel;
    // End of variables declaration//GEN-END:variables

    public Object getLastCachedInfoItem() {
        return lastCachedInfoItem;
    }

    private void searchFilesAndShowSearchResultsTab(final String searchText) {
        if (searchText.isEmpty()) {
            this.showMessageFromOperation("please enter text to search on");
            return;
        }

        final String searchTerms = searchText.trim();
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = iDropCore.getIRODSAccessObjectFactory().getCollectionAndDataObjectListAndSearchAO(
                            iDropCore.getIrodsAccount());
                    IRODSSearchTableModel irodsSearchTableModel = new IRODSSearchTableModel(
                            collectionAndDataObjectListAndSearchAO.searchCollectionsAndDataObjectsBasedOnName(searchTerms));
                    tableSearchResults.setModel(irodsSearchTableModel);
                    tabIrodsViews.setSelectedComponent(pnlTabSearch);
                } catch (Exception e) {
                    idropGui.showIdropException(e);
                    return;
                } finally {
                    iDropCore.closeAllIRODSConnections();
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    private void searchTagsAndShowSearchResultsTag(final String searchText) {
        if (searchText.isEmpty()) {
            this.showMessageFromOperation("please enter a tag to search on");
            return;
        }

        final String searchTerms = searchText.trim();
        final iDrop idropGui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(
                            iDropCore.getIRODSAccessObjectFactory(), getIrodsAccount());
                    TagQuerySearchResult result = freeTaggingService.searchUsingFreeTagString(searchTerms);
                    IRODSSearchTableModel irodsSearchTableModel = new IRODSSearchTableModel(result.getQueryResultEntries());
                    tableSearchResults.setModel(irodsSearchTableModel);
                    tabIrodsViews.setSelectedComponent(pnlTabSearch);
                } catch (Exception e) {
                    idropGui.showIdropException(e);
                    return;
                } finally {
                    iDropCore.closeIRODSConnectionForLoggedInAccount();
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int rowAtPoint = tableSearchResults.rowAtPoint(e.getPoint());
                tableSearchResults.getSelectionModel().setSelectionInterval(rowAtPoint, rowAtPoint);
                searchTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public IDROPCore getiDropCore() {
        return iDropCore;
    }

    public void setiDropCore(IDROPCore iDropCore) {
        this.iDropCore = iDropCore;
    }
}
