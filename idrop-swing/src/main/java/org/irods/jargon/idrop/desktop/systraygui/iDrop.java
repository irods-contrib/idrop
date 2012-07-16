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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.*;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.FieldFormatHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.IconHelper;
import org.irods.jargon.idrop.desktop.systraygui.utils.LocalFileUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.LookAndFeelManager;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSFileSystemModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSRowModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSSearchTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.InfoPanelTransferHandler;
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
import org.irods.jargon.usertagging.IRODSTaggingService;
import org.irods.jargon.usertagging.IRODSTaggingServiceImpl;
import org.irods.jargon.usertagging.domain.IRODSTagGrouping;
import org.irods.jargon.usertagging.domain.IRODSTagValue;
import org.irods.jargon.usertagging.domain.TagQuerySearchResult;
import org.netbeans.swing.outline.Outline;
import org.slf4j.LoggerFactory;

/**
 * Main system tray and GUI. Create system tray menu, start timer process for queue.
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class iDrop extends javax.swing.JFrame implements ActionListener,
        ItemListener, TransferManagerCallbackListener {

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
    public DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
    private ChangePasswordDialog changePasswordDialog = null;
    public static JDialog newPreferencesDialog;
    public JCheckBox showGUICheckBox;
    public JButton preferencesDialogOKButton;
    private static SimpleDateFormat SDF = new SimpleDateFormat("MM-dd-yyyy");
    private static SimpleDateFormat STF = new SimpleDateFormat("hh:mm:ss");
    private boolean receivedStartupSignal = false;
    private ImageIcon pnlIdropProgressIcon = null;

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

    protected void buildIdropGuiComponents() throws IdropRuntimeException,
            HeadlessException {
        initComponents();
        this.pnlLocalTreeArea.setVisible(false);
        this.pnlIrodsInfo.setVisible(false);
        this.splitTargetCollections.setResizeWeight(0.8d);
        try {
            pnlIrodsInfo.setTransferHandler(new InfoPanelTransferHandler(this));
        } catch (IdropException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException(
                    "error setting up infoPanelTransferHandler", ex);
        }

        tableSearchResults.setModel(new IRODSSearchTableModel());
        MouseListener popupListener = new PopupListener();
        // add the listener specifically to the header
        tableSearchResults.addMouseListener(popupListener);
        tableSearchResults.getTableHeader().addMouseListener(popupListener);

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

        userNameLabel.setText("User: "
                + getiDropCore().getIrodsAccount().getUserName());

    }

    protected void showIdropGui() {

        if (fileTree == null) {
            buildIdropGuiComponents();
        }

        initializeLookAndFeelSelected();

        if (irodsTree == null) {
            buildTargetTree(false);
        }
        // setting look and feel will also trigger build of irods tree view
        //setLookAndFeel(iDropCore.getIdropConfig().getPropertyForKey(IdropConfigurationService.LOOK_AND_FEEL));
        setUpLocalFileSelectTree();
        togglePauseTransfer.setSelected(pausedItem.getState());
        RunningStatus status = iDropCore.getTransferManager().getRunningStatus();
        iDropCore.getIconManager().setRunningStatus(status);
        iDropCore.getIconManager().setErrorStatus(
                iDropCore.getTransferManager().getErrorStatus());
        if (status == RunningStatus.PROCESSING) {
            setUpTransferPanel(true);
        } else {
            setUpTransferPanel(false);
        }

        setUpAccountGutter();

        setVisibleComponentsAtStartup();

        setVisible(true);

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
        this.btnSetRootCustomTargetTree.setVisible(false);

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
     * Status callback per file, or intra-file, from the transfer manager
     *
     * @param ts
     */
    @Override
    public void statusCallback(final TransferStatus ts) {

        log.info("transfer status callback to iDROP:{}", ts);
        final iDrop idrop = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                if (ts.getTransferState() == TransferStatus.TransferState.FAILURE) {
                    // an error occurs, stop the transfer
                    log.error("error occurred in transfer: {}", ts);
                    if (ts.getTransferException() == null) {
                        idrop.showMessageFromOperation("An error occurred in the transfer, this transfer will be cancelled");
                    } else {
                        idrop.showIdropException(ts.getTransferException());
                    }

                } else if (ts.isIntraFileStatusReport()) {

                    log.debug("transferred so far:{}", ts.getBytesTransfered());
                    log.debug("total bytes:{}", ts.getTotalSize());
                    float rawPct = (float) ts.getBytesTransfered() / ts.getTotalSize();
                    int percentDone = (int) (rawPct * 100F);
                    log.info("pct done:{}", percentDone);

                    progressIntraFile.setValue(percentDone);
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {

                    // start of a file operation
                    progressIntraFile.setMinimum(0);
                    progressIntraFile.setMaximum(100);
                    progressIntraFile.setValue(0);
                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));

                } else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {

                    progressIntraFile.setValue(100);

                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));

                } else {

                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(ts.getTotalFilesTransferredSoFar());
                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
                }
            }
        });

    }

    /**
     * Be able to do things to the transfer panel
     *
     * @param isBegin
     */
    private void setUpTransferPanel(boolean isBegin) {
        if (isBegin) {
            pnlCurrentTransferStatus.setVisible(true);
        } else {
            pnlCurrentTransferStatus.setVisible(true);
        }
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

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
                    idropGui.setUpTransferPanel(true);
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
                    idropGui.setUpTransferPanel(false);
                }

                /*
                 * Handle appropriate tree notifications, so some filtering to prevent notifications
                 * when for a different host/zone
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
                 * Handle progress bar and messages. These are cleared on overall initiation
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION || ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    clearProgressBar();
                    // on initiation, clear and reset the status bar info
                    lblTransferType.setText(ts.getTransferType().name());
                    transferStatusProgressBar.setString(FieldFormatHelper.formatFileProgress(ts.getTotalFilesToTransfer(), ts.getTotalFilesTransferredSoFar(), 0));
                    progressIntraFile.setString(FieldFormatHelper.formatByteProgress(ts.getTotalSize(), ts.getBytesTransfered(), 0));
                    lblCurrentFile.setText(IDropUtils.abbreviateFileName(ts.getSourceFileAbsolutePath()));
                    transferStatusProgressBar.setMinimum(0);
                    transferStatusProgressBar.setMaximum(ts.getTotalFilesToTransfer());
                    transferStatusProgressBar.setValue(0);
                }

                /*
                 * Handle any text messages
                 */
                if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_INITIALIZATION) {
                    lblTransferMessage.setText("Synchronization Initializing");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_GENERATION) {
                    lblTransferMessage.setText("Synchronization looking for updates");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_DIFF_STEP) {
                    lblTransferMessage.setText("Synchronizing differences");
                } else if (ts.getTransferState() == TransferStatus.TransferState.SYNCH_COMPLETION) {
                    lblTransferMessage.setText("Synchronization complete");
                } else if (ts.getTransferEnclosingType() == TransferStatus.TransferType.SYNCH) {
                    lblTransferMessage.setText("Transfer to synchronize local and iRODS");
                } else if (ts.getTransferState() == TransferStatus.TransferState.OVERALL_INITIATION) {
                    // initiation not within a synch
                    lblTransferMessage.setText("Processing a " + ts.getTransferType().name() + " operation");
                }
            }
        });
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

                if (pnlIdropProgressIcon != null) {
                    progressIconImageLabel.setIcon(new ImageIcon(newIcon));

                }
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

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setTrayIcon(final TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
        trayIcon.setImageAutoSize(true);
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
     * A dialog to indicate that the queue should start processing
     */
    public int showTransferStartupConfirm() {

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(
                this,
                "There are transfers ready to process, should the transfer queue be started?  Click NO to pause the transfersf",
                "Begin Transfer Confirmation",
                JOptionPane.YES_NO_OPTION);

        return n;
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

    /**
     * Indicate that the GUI should reflect a paused state
     *
     */
    public void setTransferStatePaused() {
        if (pausedItem != null) {
            pausedItem.setState(true);
        }

        if (togglePauseTransfer != null) {
            this.togglePauseTransfer.setSelected(true);
        }
    }

    /**
     * Indicate that the gui should show an unpaused state.
     */
    public void setTransferStateUnpaused() {
        if (pausedItem != null) {
            pausedItem.setState(false);
        }

        if (togglePauseTransfer != null) {
            this.togglePauseTransfer.setSelected(false);
        }
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

    /**
     * Show or hide the iRODS info panel and manage the state of the show info menu and toggle so
     * that they remain in synch
     */
    private void handleInfoPanelShowOrHide() {
        final iDrop idropGuiReference = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                pnlIrodsInfo.setVisible(toggleIrodsDetails.isSelected());
                jCheckBoxMenuItemShowIrodsInfo.setSelected(toggleIrodsDetails.isSelected());
                // if info is being opened, initialize to the first selected
                // item, or the root of the iRODS tree if none
                // selected
                IRODSNode node;

                if (irodsTree.getSelectionModel().getLeadSelectionIndex() < 0) {
                    if (irodsTree.getRowCount() > 0) {
                        irodsTree.setRowSelectionInterval(0, 0);
                    }
                }

                if (pnlIrodsInfo.isVisible()) {
                    idropGuiReference.triggerInfoPanelUpdate();
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
         * build a list of the roots (e.g. drives on windows systems). If there is only one, use it
         * as the basis for the file model, otherwise, display an additional panel listing the other
         * roots, and build the tree for the first drive encountered.
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
                                Object selectedItem = listLocalDrives.getSelectedValue();
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

            listLocalDrives.setModel(listModel);

            scrollLocalDrives.setVisible(true);
        }
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
     * build the JTree that will depict the iRODS resource
     */
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

    public void initializeInfoPane(
            final CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntry)
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
                        CollectionAO collectionAO = getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().getCollectionAO(getIrodsAccount());
                        Collection collection = collectionAO.findByAbsolutePath(collectionAndDataObjectListingEntry.getPathOrName());
                        initializeInfoPanel(collection);
                    } else {
                        log.info("looking up data object to build info panel");
                        DataObjectAO dataObjectAO = getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory().getDataObjectAO(getIrodsAccount());
                        DataObject dataObject = dataObjectAO.findByAbsolutePath(collectionAndDataObjectListingEntry.getParentPath()
                                + "/"
                                + collectionAndDataObjectListingEntry.getPathOrName());
                        initializeInfoPanel(dataObject);
                    }

                } catch (Exception e) {
                    log.error(
                            "exception building info panel from collection and data object listing entry:{}",
                            collectionAndDataObjectListingEntry, e);
                    throw new IdropRuntimeException(e);
                } finally {
                    getiDropCore().getIrodsFileSystem().closeAndEatExceptions(
                            getIrodsAccount());
                    idropGui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                }
            }
        });
    }

    /**
     * Initialize the info panel with data from iRODS. In this case, the data is an iRODS data
     * object (file). Called from a runnable that will handle the irods collection and busy cursor.
     *
     * @param dataObject
     * <code>DataObject</code> iRODS domain object for a file.
     * @throws IdropException
     */
    public void initializeInfoPanel(final DataObject dataObject)
            throws IdropException {

        if (!toggleIrodsDetails.isSelected()) {
            log.info("info display not selected, don't bother");
            return;
        }

        if (dataObject == null) {
            throw new IdropException("Null dataObject");
        }

        this.lastCachedInfoItem = dataObject;
        final iDrop idropGui = this;


        lblFileOrCollectionName.setText(IDropUtils.abbreviateFileName(dataObject.getAbsolutePath()));
        lblFileOrCollectionName.setToolTipText(dataObject.getAbsolutePath());

        log.debug("getting available tags for data object");

        try {
            FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                    getiDropCore().getIrodsAccount());
            IRODSTagGrouping irodsTagGrouping = freeTaggingService.getTagsForDataObjectInFreeTagForm(dataObject.getCollectionName()
                    + "/"
                    + dataObject.getDataName());
            txtTags.setText(irodsTagGrouping.getSpaceDelimitedTagsForDomain());

            IRODSTaggingService irodsTaggingService = IRODSTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                    getIrodsAccount());

            IRODSTagValue tagValue = irodsTaggingService.getDescriptionOnDataObjectForLoggedInUser(dataObject.getAbsolutePath());

            if (tagValue == null) {
                txtComment.setText("");
            } else {
                txtComment.setText(tagValue.getTagData());
            }

            pnlInfoIcon.removeAll();
            pnlInfoIcon.add(IconHelper.getFileIcon());
            pnlInfoIcon.validate();
            lblInfoCreatedAtValue.setText(SDF.format(dataObject.getCreatedAt()));
            lblInfoCreatedAtTimeValue.setText(STF.format(dataObject.getCreatedAt()));
            lblInfoUpdatedAtValue.setText(SDF.format(dataObject.getUpdatedAt()));
            lblInfoUpdatedAtTimeValue.setText(STF.format(dataObject.getUpdatedAt()));
            lblInfoLengthValue.setText(FieldFormatHelper.formatFileLength(dataObject.getDataSize()));

            lblInfoChecksumValue.setText(dataObject.getChecksum());
            lblCollectionTypeLabel.setVisible(false);
            lblCollectionType.setVisible(false);
            adjustInfoPanelVisibilityOfDataObjectSpecificContent(true);

            lblDataPath.setText(IDropUtils.abbreviateFileName(dataObject.getDataPath()));
            lblDataPath.setToolTipText(dataObject.getDataPath());

            lblDataReplicationStatus.setText(dataObject.getReplicationStatus());
            lblDataVersion.setText(String.valueOf(dataObject.getDataVersion()));

            lblDataType.setText(dataObject.getDataTypeName());

            lblDataStatus.setText(dataObject.getDataStatus());

            lblOwnerName.setText(dataObject.getDataOwnerName());
            lblOwnerZone.setText(dataObject.getDataOwnerZone());

        } catch (JargonException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new IdropRuntimeException(ex);
        } finally {
            this.getiDropCore().closeIRODSConnectionForLoggedInAccount();
        }

    }

    /**
     * Initialize the info panel with data from iRODS. In this case, the data is an iRODS collection
     * (directory). Called from a runnable that will handle the irods collection and busy cursor.
     *
     * @param collection
     * @throws IdropException
     */
    public void initializeInfoPanel(final Collection collection)
            throws IdropException {
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

        lblFileOrCollectionName.setText(IDropUtils.abbreviateFileName(collection.getAbsolutePath()));
        lblFileOrCollectionName.setToolTipText(collection.getAbsolutePath());

        log.debug("getting available tags for data object");

        try {
            FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                    getIrodsAccount());
            IRODSTaggingService irodsTaggingService = IRODSTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                    getIrodsAccount());

            log.info("looking up description for collection");
            IRODSTagValue comments = irodsTaggingService.getDescriptionOnCollectionForLoggedInUser(collection.getCollectionName());

            if (comments == null) {
                txtComment.setText("");
            } else {
                txtComment.setText(comments.getTagData());
            }

            IRODSTagGrouping irodsTagGrouping = freeTaggingService.getTagsForCollectionInFreeTagForm(collection.getCollectionName());
            txtTags.setText(irodsTagGrouping.getSpaceDelimitedTagsForDomain());
            pnlInfoIcon.removeAll();
            pnlInfoIcon.add(IconHelper.getFolderIcon());
            pnlInfoIcon.validate();
            lblInfoCreatedAtValue.setText(SDF.format(collection.getCreatedAt()));
            lblInfoCreatedAtTimeValue.setText(STF.format(collection.getCreatedAt()));
            lblInfoUpdatedAtValue.setText(SDF.format(collection.getModifiedAt()));
            lblInfoUpdatedAtTimeValue.setText(STF.format(collection.getModifiedAt()));
            lblCollectionTypeLabel.setVisible(true);
            lblCollectionType.setVisible(true);
            lblCollectionType.setText(collection.getSpecColType().name());
            adjustInfoPanelVisibilityOfDataObjectSpecificContent(false);

        } catch (JargonException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new IdropRuntimeException(ex);
        } finally {
            this.getiDropCore().closeIRODSConnectionForLoggedInAccount();
        }
    }

    private void adjustInfoPanelVisibilityOfDataObjectSpecificContent(final boolean visible) {
        lblInfoLengthValue.setVisible(visible);
        lblInfoLength.setVisible(visible);
        lblInfoChecksum.setVisible(visible);
        lblInfoChecksumValue.setVisible(visible);
        lblOwnerNameLabel.setVisible(visible);
        lblOwnerName.setVisible(visible);
        lblOwnerZoneLabel.setVisible(visible);
        lblOwnerZone.setVisible(visible);
        lblDataPathLabel.setVisible(visible);
        lblDataPath.setVisible(visible);
        lblDataReplicationStatusLabel.setVisible(visible);
        lblDataReplicationStatus.setVisible(visible);
        lblDataVersionLabel.setVisible(visible);
        lblDataVersion.setVisible(visible);
        lblDataTypeLabel.setVisible(visible);
        lblDataType.setVisible(visible);
        lblDataStatusLabel.setVisible(visible);
        lblDataStatus.setVisible(visible);
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

    public JToggleButton getToggleIrodsDetails() {
        return toggleIrodsDetails;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     *
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        searchTablePopupMenu = new javax.swing.JPopupMenu();
        menuItemShowInHierarchy = new javax.swing.JMenuItem();
        buttonGroupLandF = new javax.swing.ButtonGroup();
        pnlInfoMetadata = new javax.swing.JPanel();
        lblMetadataInfo = new javax.swing.JLabel();
        pnlInfoSharing = new javax.swing.JPanel();
        lblInfoSharing = new javax.swing.JLabel();
        pnlInfoReplication = new javax.swing.JPanel();
        lblMetadataInfo1 = new javax.swing.JLabel();
        iDropToolbar = new javax.swing.JPanel();
        pnlToolbarSizer = new javax.swing.JPanel();
        pnlTopToolbarSearchArea = new javax.swing.JPanel();
        pnlSearchSizer = new javax.swing.JPanel();
        lblMainSearch = new javax.swing.JLabel();
        comboSearchType = new javax.swing.JComboBox();
        txtMainSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
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
        toolbarIrodsTree = new javax.swing.JToolBar();
        btnRefreshTargetTree = new javax.swing.JButton();
        btnGoHomeTargetTree = new javax.swing.JButton();
        btnGoRootTargetTree = new javax.swing.JButton();
        btnSetRootCustomTargetTree = new javax.swing.JButton();
        pnlIrodsTreeMaster = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();
        pnlTabSearch = new javax.swing.JPanel();
        pnlTabSearchTop = new javax.swing.JPanel();
        pnlTabSearchResults = new javax.swing.JPanel();
        scrollPaneSearchResults = new javax.swing.JScrollPane();
        tableSearchResults = new javax.swing.JTable();
        pnlIrodsInfo = new javax.swing.JPanel();
        tabInfo = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlInfoInner = new javax.swing.JPanel();
        pnlInfoIcon = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblSelectedFileInfo = new javax.swing.JLabel();
        lblFilePathLabel = new javax.swing.JLabel();
        lblFileOrCollectionName = new javax.swing.JLabel();
        lblComment = new javax.swing.JLabel();
        scrollComment = new javax.swing.JScrollPane();
        txtComment = new javax.swing.JTextArea();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        lblTags = new javax.swing.JLabel();
        txtTags = new javax.swing.JTextField();
        btnUpdateInfo = new javax.swing.JButton();
        pnlFileInfoDemographics = new javax.swing.JPanel();
        lblInfoCreatedAt = new javax.swing.JLabel();
        lblInfoCreatedAtValue = new javax.swing.JLabel();
        lblInfoCreatedAtTimeValue = new javax.swing.JLabel();
        lblInfoUpdatedAt = new javax.swing.JLabel();
        lblInfoUpdatedAtValue = new javax.swing.JLabel();
        lblInfoUpdatedAtTimeValue = new javax.swing.JLabel();
        lblInfoLength = new javax.swing.JLabel();
        lblInfoLengthValue = new javax.swing.JLabel();
        lblInfoChecksum = new javax.swing.JLabel();
        lblInfoChecksumValue = new javax.swing.JLabel();
        lblOwnerNameLabel = new javax.swing.JLabel();
        lblOwnerName = new javax.swing.JLabel();
        lblOwnerZoneLabel = new javax.swing.JLabel();
        lblOwnerZone = new javax.swing.JLabel();
        lblCollectionTypeLabel = new javax.swing.JLabel();
        lblCollectionType = new javax.swing.JLabel();
        lblDataPathLabel = new javax.swing.JLabel();
        lblDataPath = new javax.swing.JLabel();
        lblDataReplicationStatusLabel = new javax.swing.JLabel();
        lblDataReplicationStatus = new javax.swing.JLabel();
        lblDataVersionLabel = new javax.swing.JLabel();
        lblDataVersion = new javax.swing.JLabel();
        lblDataTypeLabel = new javax.swing.JLabel();
        lblDataType = new javax.swing.JLabel();
        lblDataStatusLabel = new javax.swing.JLabel();
        lblDataStatus = new javax.swing.JLabel();
        pnlIdropBottom = new javax.swing.JPanel();
        pnlBottomGutter = new javax.swing.JPanel();
        pnlHostInfo = new javax.swing.JPanel();
        lblUserNameLabel = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        lblZoneLabel = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        lblHostLabel = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        lblDefaultResource = new javax.swing.JLabel();
        comboDefaultResource = new javax.swing.JComboBox();
        pnlStatusIcon = new javax.swing.JPanel();
        btnManageGrids = new javax.swing.JButton();
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
        btnShowTransferManager = new javax.swing.JButton();
        togglePauseTransfer = new javax.swing.JToggleButton();
        progressIconImageLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuView = new javax.swing.JMenu();
        jCheckBoxMenuItemShowSourceTree = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowIrodsInfo = new javax.swing.JCheckBoxMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuLookAndFeel = new javax.swing.JMenu();
        jRadioButtonLookAndFeelDefault = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonLookAndFeelNimbus = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemMetal = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemMotif = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemGTK = new javax.swing.JRadioButtonMenuItem();
        jMenuItemConfig = new javax.swing.JMenuItem();

        menuItemShowInHierarchy.setText("Show in iRODS");
        menuItemShowInHierarchy.setToolTipText("Show this file or collection in the iRODS hierarchy");
        menuItemShowInHierarchy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemShowInHierarchyActionPerformed(evt);
            }
        });
        searchTablePopupMenu.add(menuItemShowInHierarchy);

        lblMetadataInfo.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblMetadataInfo.setForeground(java.awt.Color.blue);
        lblMetadataInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit-4.png"))); // NOI18N
        lblMetadataInfo.setText("iRODS AVU Metadata");
        pnlInfoMetadata.add(lblMetadataInfo);

        lblInfoSharing.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInfoSharing.setForeground(java.awt.Color.blue);
        lblInfoSharing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/share.png"))); // NOI18N
        lblInfoSharing.setText("Access Permissions and Tickets");
        pnlInfoSharing.add(lblInfoSharing);

        lblMetadataInfo1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblMetadataInfo1.setForeground(java.awt.Color.blue);
        lblMetadataInfo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit-copy-3.png"))); // NOI18N
        lblMetadataInfo1.setText("File Replication");
        pnlInfoReplication.add(lblMetadataInfo1);

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

        btnSearch.setMnemonic('s');
        btnSearch.setText("Search");
        btnSearch.setToolTipText("Search iRODS based on the current view selected");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnlSearchSizer.add(btnSearch);

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

        pnlIrodsArea.setMinimumSize(new java.awt.Dimension(500, 300));
        pnlIrodsArea.setPreferredSize(new java.awt.Dimension(600, 304));
        pnlIrodsArea.setLayout(new java.awt.BorderLayout());

        splitTargetCollections.setDividerLocation(400);
        splitTargetCollections.setMinimumSize(new java.awt.Dimension(0, 0));

        tabIrodsViews.setMinimumSize(new java.awt.Dimension(200, 129));
        tabIrodsViews.setPreferredSize(new java.awt.Dimension(350, 300));
        tabIrodsViews.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabIrodsViewsStateChanged(evt);
            }
        });

        pnlTabHierarchicalView.setLayout(new java.awt.BorderLayout());

        toolbarIrodsTree.setFloatable(false);
        toolbarIrodsTree.setRollover(true);

        btnRefreshTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recur.png"))); // NOI18N
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
        toolbarIrodsTree.add(btnRefreshTargetTree);

        btnGoHomeTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/go-home-4.png"))); // NOI18N
        btnGoHomeTargetTree.setMnemonic('h');
        btnGoHomeTargetTree.setText("Home");
        btnGoHomeTargetTree.setToolTipText("Go to the user home directory on the iRODS grid");
        btnGoHomeTargetTree.setFocusable(false);
        btnGoHomeTargetTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoHomeTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoHomeTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoHomeTargetTreeActionPerformed(evt);
            }
        });
        toolbarIrodsTree.add(btnGoHomeTargetTree);

        btnGoRootTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/go-parent-folder.png"))); // NOI18N
        btnGoRootTargetTree.setMnemonic('t');
        btnGoRootTargetTree.setText("Top");
        btnGoRootTargetTree.setToolTipText("Go to the top of the iRODS tree");
        btnGoRootTargetTree.setFocusable(false);
        btnGoRootTargetTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoRootTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoRootTargetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoRootTargetTreeActionPerformed(evt);
            }
        });
        toolbarIrodsTree.add(btnGoRootTargetTree);

        btnSetRootCustomTargetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/go-jump.png"))); // NOI18N
        btnSetRootCustomTargetTree.setMnemonic('c');
        btnSetRootCustomTargetTree.setText("Custom Root");
        btnSetRootCustomTargetTree.setToolTipText("Set the root of the tree to a custom path");
        btnSetRootCustomTargetTree.setFocusable(false);
        btnSetRootCustomTargetTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetRootCustomTargetTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarIrodsTree.add(btnSetRootCustomTargetTree);

        pnlIrodsTreeToolbar.add(toolbarIrodsTree);

        pnlTabHierarchicalView.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlIrodsTreeMaster.setLayout(new java.awt.BorderLayout());
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

        tabInfo.setToolTipText("View basic demographics for a file or collection");

        pnlInfoInner.setMinimumSize(null);
        pnlInfoInner.setPreferredSize(null);
        pnlInfoInner.setLayout(new java.awt.GridBagLayout());

        pnlInfoIcon.setMaximumSize(new java.awt.Dimension(50, 50));
        pnlInfoIcon.setLayout(new java.awt.GridLayout(1, 0));

        jLabel1.setBackground(new java.awt.Color(255, 0, 204));
        pnlInfoIcon.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        pnlInfoInner.add(pnlInfoIcon, gridBagConstraints);

        lblSelectedFileInfo.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblSelectedFileInfo.setForeground(java.awt.Color.blue);
        lblSelectedFileInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/help-contents.png"))); // NOI18N
        lblSelectedFileInfo.setText("Selected File Info");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        pnlInfoInner.add(lblSelectedFileInfo, gridBagConstraints);
        lblSelectedFileInfo.getAccessibleContext().setAccessibleDescription("Label indicating panel for selected file info");

        lblFilePathLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblFilePathLabel.setText("Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlInfoInner.add(lblFilePathLabel, gridBagConstraints);
        lblFilePathLabel.getAccessibleContext().setAccessibleName("Path Label");
        lblFilePathLabel.getAccessibleContext().setAccessibleDescription("Label for file Path");

        lblFileOrCollectionName.setText("file or collection name      ");
        lblFileOrCollectionName.setMaximumSize(new java.awt.Dimension(900, 100));
        lblFileOrCollectionName.setMinimumSize(new java.awt.Dimension(80, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 0);
        pnlInfoInner.add(lblFileOrCollectionName, gridBagConstraints);
        lblFileOrCollectionName.getAccessibleContext().setAccessibleName("Abbreviated file name ");
        lblFileOrCollectionName.getAccessibleContext().setAccessibleDescription("File name of selected file or collection (abbreviated if necessary with elipses)");

        lblComment.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblComment.setText("Comment:");
        lblComment.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlInfoInner.add(lblComment, gridBagConstraints);
        lblComment.getAccessibleContext().setAccessibleDescription("lable for comment area");

        scrollComment.setMinimumSize(null);

        txtComment.setColumns(30);
        txtComment.setRows(6);
        txtComment.setTabSize(5);
        txtComment.setToolTipText("Free form comment for a file or collection");
        txtComment.setWrapStyleWord(true);
        txtComment.setMaximumSize(null);
        txtComment.setMinimumSize(null);
        txtComment.setPreferredSize(null);
        scrollComment.setViewportView(txtComment);
        txtComment.getAccessibleContext().setAccessibleName("Comment");
        txtComment.getAccessibleContext().setAccessibleDescription("Comment for a file");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 0.2;
        pnlInfoInner.add(scrollComment, gridBagConstraints);
        scrollComment.getAccessibleContext().setAccessibleName("scroll box for file comment");
        scrollComment.getAccessibleContext().setAccessibleDescription("Scroll box for file comment");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        pnlInfoInner.add(filler4, gridBagConstraints);

        lblTags.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblTags.setText("Tags:");
        lblTags.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlInfoInner.add(lblTags, gridBagConstraints);
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInfoInner.add(txtTags, gridBagConstraints);

        btnUpdateInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dialog-accept.png"))); // NOI18N
        btnUpdateInfo.setMnemonic('u');
        btnUpdateInfo.setText("Update Comment and Tags");
        btnUpdateInfo.setToolTipText("Update information on the info panel such as tags and comment");
        btnUpdateInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateInfoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        pnlInfoInner.add(btnUpdateInfo, gridBagConstraints);

        pnlFileInfoDemographics.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlFileInfoDemographics.setLayout(new java.awt.GridBagLayout());

        lblInfoCreatedAt.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInfoCreatedAt.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoCreatedAt.setText("Created:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 2, 0);
        pnlFileInfoDemographics.add(lblInfoCreatedAt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 0);
        pnlFileInfoDemographics.add(lblInfoCreatedAtValue, gridBagConstraints);

        lblInfoCreatedAtTimeValue.setToolTipText("Time file was created");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 0);
        pnlFileInfoDemographics.add(lblInfoCreatedAtTimeValue, gridBagConstraints);
        lblInfoCreatedAtTimeValue.getAccessibleContext().setAccessibleName("Created at time");
        lblInfoCreatedAtTimeValue.getAccessibleContext().setAccessibleDescription("time fiel was created");

        lblInfoUpdatedAt.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInfoUpdatedAt.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoUpdatedAt.setText("Updated:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlFileInfoDemographics.add(lblInfoUpdatedAt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 2, 0);
        pnlFileInfoDemographics.add(lblInfoUpdatedAtValue, gridBagConstraints);

        lblInfoUpdatedAtTimeValue.setToolTipText("Time file was last updated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        pnlFileInfoDemographics.add(lblInfoUpdatedAtTimeValue, gridBagConstraints);
        lblInfoUpdatedAtTimeValue.getAccessibleContext().setAccessibleName("Updated at time");

        lblInfoLength.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInfoLength.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoLength.setText("Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlFileInfoDemographics.add(lblInfoLength, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        pnlFileInfoDemographics.add(lblInfoLengthValue, gridBagConstraints);

        lblInfoChecksum.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInfoChecksum.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInfoChecksum.setText("Checksum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 2, 0);
        pnlFileInfoDemographics.add(lblInfoChecksum, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 2, 0);
        pnlFileInfoDemographics.add(lblInfoChecksumValue, gridBagConstraints);

        lblOwnerNameLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblOwnerNameLabel.setText("Owner:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 15, 2, 0);
        pnlFileInfoDemographics.add(lblOwnerNameLabel, gridBagConstraints);
        lblOwnerNameLabel.getAccessibleContext().setAccessibleDescription("Label for file or collection owner name");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(20, 2, 2, 0);
        pnlFileInfoDemographics.add(lblOwnerName, gridBagConstraints);

        lblOwnerZoneLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblOwnerZoneLabel.setText("Owner Zone:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 26, 2, 0);
        pnlFileInfoDemographics.add(lblOwnerZoneLabel, gridBagConstraints);
        lblOwnerZoneLabel.getAccessibleContext().setAccessibleName("Owner Zone");
        lblOwnerZoneLabel.getAccessibleContext().setAccessibleDescription("label for owner zone");

        lblOwnerZone.setToolTipText("Zone of file owner");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 0);
        pnlFileInfoDemographics.add(lblOwnerZone, gridBagConstraints);
        lblOwnerZone.getAccessibleContext().setAccessibleName("Owner Zone");

        lblCollectionTypeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblCollectionTypeLabel.setText("Collection Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
        pnlFileInfoDemographics.add(lblCollectionTypeLabel, gridBagConstraints);
        lblCollectionTypeLabel.getAccessibleContext().setAccessibleName("Collection Type");
        lblCollectionTypeLabel.getAccessibleContext().setAccessibleDescription("Label for collection type");

        lblCollectionType.setToolTipText("Collection type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
        pnlFileInfoDemographics.add(lblCollectionType, gridBagConstraints);
        lblCollectionType.getAccessibleContext().setAccessibleName("Collection type");
        lblCollectionType.getAccessibleContext().setAccessibleDescription("Type of collection");

        lblDataPathLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDataPathLabel.setText("Data Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlFileInfoDemographics.add(lblDataPathLabel, gridBagConstraints);
        lblDataPathLabel.getAccessibleContext().setAccessibleDescription("Label for data path");

        lblDataPath.setToolTipText("Physical path of file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 10);
        pnlFileInfoDemographics.add(lblDataPath, gridBagConstraints);
        lblDataPath.getAccessibleContext().setAccessibleName("Data Path");
        lblDataPath.getAccessibleContext().setAccessibleDescription("Physical path of the data file");

        lblDataReplicationStatusLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDataReplicationStatusLabel.setText("Replication Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlFileInfoDemographics.add(lblDataReplicationStatusLabel, gridBagConstraints);
        lblDataReplicationStatusLabel.getAccessibleContext().setAccessibleName("Label for data replication status");
        lblDataReplicationStatusLabel.getAccessibleContext().setAccessibleDescription("Label for data replication status");

        lblDataReplicationStatus.setToolTipText("Data replication status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 0, 10);
        pnlFileInfoDemographics.add(lblDataReplicationStatus, gridBagConstraints);
        lblDataReplicationStatus.getAccessibleContext().setAccessibleName("Data Replication Status");
        lblDataReplicationStatus.getAccessibleContext().setAccessibleDescription("Status of replication of this data object");

        lblDataVersionLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDataVersionLabel.setText("Data Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 2, 0);
        pnlFileInfoDemographics.add(lblDataVersionLabel, gridBagConstraints);
        lblDataVersionLabel.getAccessibleContext().setAccessibleName("Data Version Label");
        lblDataVersionLabel.getAccessibleContext().setAccessibleDescription("Label for data version");

        lblDataVersion.setText("jLabel2");
        lblDataVersion.setToolTipText("Data version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 0, 10);
        pnlFileInfoDemographics.add(lblDataVersion, gridBagConstraints);
        lblDataVersion.getAccessibleContext().setAccessibleName("Data Version");
        lblDataVersion.getAccessibleContext().setAccessibleDescription("Version of data");

        lblDataTypeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDataTypeLabel.setText("Data Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlFileInfoDemographics.add(lblDataTypeLabel, gridBagConstraints);
        lblDataTypeLabel.getAccessibleContext().setAccessibleName("Data Type Label");
        lblDataTypeLabel.getAccessibleContext().setAccessibleDescription("Label for data type");

        lblDataType.setToolTipText("Data type for selected file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlFileInfoDemographics.add(lblDataType, gridBagConstraints);
        lblDataType.getAccessibleContext().setAccessibleName("Data Type");

        lblDataStatusLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDataStatusLabel.setText("Data Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlFileInfoDemographics.add(lblDataStatusLabel, gridBagConstraints);
        lblDataStatusLabel.getAccessibleContext().setAccessibleName("Data Status label");
        lblDataStatusLabel.getAccessibleContext().setAccessibleDescription("Label for data status");

        lblDataStatus.setToolTipText("Status of selected file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 10);
        pnlFileInfoDemographics.add(lblDataStatus, gridBagConstraints);
        lblDataStatus.getAccessibleContext().setAccessibleName("Data status");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 6, 2);
        pnlInfoInner.add(pnlFileInfoDemographics, gridBagConstraints);

        jScrollPane1.setViewportView(pnlInfoInner);

        tabInfo.addTab("Info", jScrollPane1);

        pnlIrodsInfo.add(tabInfo, java.awt.BorderLayout.CENTER);
        tabInfo.getAccessibleContext().setAccessibleName("Info");

        splitTargetCollections.setRightComponent(pnlIrodsInfo);

        pnlIrodsArea.add(splitTargetCollections, java.awt.BorderLayout.CENTER);

        jSplitPanelLocalRemote.setRightComponent(pnlIrodsArea);

        pnlIdropMain.add(jSplitPanelLocalRemote);

        getContentPane().add(pnlIdropMain, java.awt.BorderLayout.CENTER);

        pnlIdropBottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlIdropBottom.setToolTipText("Display area for status and messages");
        pnlIdropBottom.setLayout(new java.awt.BorderLayout());

        pnlBottomGutter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlBottomGutter.setMaximumSize(new java.awt.Dimension(2147483647, 10));
        pnlBottomGutter.setLayout(new java.awt.BorderLayout());

        lblUserNameLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblUserNameLabel.setText("User Name:");
        pnlHostInfo.add(lblUserNameLabel);

        userNameLabel.setText("usernamelabel");
        userNameLabel.setMinimumSize(null);
        userNameLabel.setPreferredSize(null);
        pnlHostInfo.add(userNameLabel);
        pnlHostInfo.add(filler1);

        lblZoneLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblZoneLabel.setText("Zone:");
        pnlHostInfo.add(lblZoneLabel);

        lblZone.setText("this is the zone");
        pnlHostInfo.add(lblZone);
        pnlHostInfo.add(filler2);

        lblHostLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblHostLabel.setText("Host:");
        pnlHostInfo.add(lblHostLabel);

        lblHost.setText("this is the host");
        pnlHostInfo.add(lblHost);
        pnlHostInfo.add(filler3);

        lblDefaultResource.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblDefaultResource.setText("Default Storage Resource:");
        pnlHostInfo.add(lblDefaultResource);

        comboDefaultResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDefaultResourceActionPerformed(evt);
            }
        });
        pnlHostInfo.add(comboDefaultResource);
        comboDefaultResource.getAccessibleContext().setAccessibleName("Default resource selection");
        comboDefaultResource.getAccessibleContext().setAccessibleDescription("Selection options for the default storage resource");

        pnlBottomGutter.add(pnlHostInfo, java.awt.BorderLayout.WEST);

        btnManageGrids.setIcon(new javax.swing.ImageIcon(getClass().getResource("/im-user.png"))); // NOI18N
        btnManageGrids.setMnemonic('S');
        btnManageGrids.setLabel("Switch Grid Account");
        btnManageGrids.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageGridsActionPerformed(evt);
            }
        });
        pnlStatusIcon.add(btnManageGrids);
        btnManageGrids.getAccessibleContext().setAccessibleDescription("Switch the grid or iRODS account information");

        pnlBottomGutter.add(pnlStatusIcon, java.awt.BorderLayout.EAST);

        pnlIdropBottom.add(pnlBottomGutter, java.awt.BorderLayout.SOUTH);
        pnlBottomGutter.getAccessibleContext().setAccessibleName("Current Grid Panel");
        pnlBottomGutter.getAccessibleContext().setAccessibleDescription("Panel Showing the Current iRODS Grid");

        pnlCurrentTransferStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlCurrentTransferStatus.setLayout(new java.awt.GridBagLayout());

        lblCurrentFile.setMaximumSize(new java.awt.Dimension(999, 999));
        lblCurrentFile.setMinimumSize(new java.awt.Dimension(30, 10));
        lblCurrentFile.setPreferredSize(new java.awt.Dimension(500, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 0);
        pnlCurrentTransferStatus.add(lblCurrentFile, gridBagConstraints);

        progressIntraFile.setBorder(null);
        progressIntraFile.setMinimumSize(new java.awt.Dimension(10, 60));
        progressIntraFile.setString("");
        progressIntraFile.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlCurrentTransferStatus.add(progressIntraFile, gridBagConstraints);
        progressIntraFile.getAccessibleContext().setAccessibleName("Progress bar for total bytes transferred");
        progressIntraFile.getAccessibleContext().setAccessibleDescription("Total progress for the current file (in bytes)");

        lblTransferFilesCounts.setText("Total Progress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentTransferStatus.add(lblTransferFilesCounts, gridBagConstraints);

        transferStatusProgressBar.setBorder(null);
        transferStatusProgressBar.setMinimumSize(new java.awt.Dimension(10, 60));
        transferStatusProgressBar.setString("");
        transferStatusProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8.0;
        pnlCurrentTransferStatus.add(transferStatusProgressBar, gridBagConstraints);
        transferStatusProgressBar.getAccessibleContext().setAccessibleName("Progress bar for the total number of files in this transfer so far");
        transferStatusProgressBar.getAccessibleContext().setAccessibleDescription("Progress of the transfer, showing what percentage of files have been transferred");

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

        lblTransferByteCounts.setText("Current File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlCurrentTransferStatus.add(lblTransferByteCounts, gridBagConstraints);

        pnlIdropBottom.add(pnlCurrentTransferStatus, java.awt.BorderLayout.CENTER);
        pnlCurrentTransferStatus.getAccessibleContext().setAccessibleName("Current Transfer Status Panel");
        pnlCurrentTransferStatus.getAccessibleContext().setAccessibleDescription("Panel Describing the stete of the current transfer");

        pnlTransferOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Upload Options"));

        idropProgressPanelToolbar.setFloatable(false);
        idropProgressPanelToolbar.setRollover(true);

        btnShowTransferManager.setIcon(new javax.swing.ImageIcon(getClass().getResource("/configure-5.png"))); // NOI18N
        btnShowTransferManager.setMnemonic('m');
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

        togglePauseTransfer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/media-playback-pause-7.png"))); // NOI18N
        togglePauseTransfer.setMnemonic('p');
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

        pnlTransferOptions.add(idropProgressPanelToolbar);
        pnlTransferOptions.add(progressIconImageLabel);

        pnlIdropBottom.add(pnlTransferOptions, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlIdropBottom, java.awt.BorderLayout.SOUTH);

        jMenuFile.setMnemonic('f');
        jMenuFile.setText("File");

        jMenuItemClose.setMnemonic('c');
        jMenuItemClose.setText("Close iDrop GUI");
        jMenuItemClose.setToolTipText("Close the iDrop GUI, leaving iDrop running ");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setToolTipText("Exit iDrop entirely");
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

        jMenuTools.setMnemonic('T');
        jMenuTools.setText("Tools");
        jMenuTools.setToolTipText("Tools and options for iDrop");

        jMenuLookAndFeel.setMnemonic('l');
        jMenuLookAndFeel.setText("Set look and feel");
        jMenuLookAndFeel.setToolTipText("Set the look and feel for the GUI");

        buttonGroupLandF.add(jRadioButtonLookAndFeelDefault);
        jRadioButtonLookAndFeelDefault.setMnemonic('d');
        jRadioButtonLookAndFeelDefault.setSelected(true);
        jRadioButtonLookAndFeelDefault.setText("Default");
        jRadioButtonLookAndFeelDefault.setToolTipText("Default system look an dfeel");
        jRadioButtonLookAndFeelDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonLookAndFeelDefaultActionPerformed(evt);
            }
        });
        jMenuLookAndFeel.add(jRadioButtonLookAndFeelDefault);

        buttonGroupLandF.add(jRadioButtonLookAndFeelNimbus);
        jRadioButtonLookAndFeelNimbus.setMnemonic('n');
        jRadioButtonLookAndFeelNimbus.setText("Nimbus");
        jRadioButtonLookAndFeelNimbus.setToolTipText("Nimbus look and feel");
        jRadioButtonLookAndFeelNimbus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonLookAndFeelNimbusActionPerformed(evt);
            }
        });
        jMenuLookAndFeel.add(jRadioButtonLookAndFeelNimbus);

        buttonGroupLandF.add(jRadioButtonMenuItemMetal);
        jRadioButtonMenuItemMetal.setMnemonic('m');
        jRadioButtonMenuItemMetal.setText("Metal");
        jRadioButtonMenuItemMetal.setToolTipText("Metal look and feel");
        jRadioButtonMenuItemMetal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemMetalActionPerformed(evt);
            }
        });
        jMenuLookAndFeel.add(jRadioButtonMenuItemMetal);

        buttonGroupLandF.add(jRadioButtonMenuItemMotif);
        jRadioButtonMenuItemMotif.setMnemonic('t');
        jRadioButtonMenuItemMotif.setText("Motif");
        jRadioButtonMenuItemMotif.setToolTipText("Motif look and feel");
        jRadioButtonMenuItemMotif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemMotifActionPerformed(evt);
            }
        });
        jMenuLookAndFeel.add(jRadioButtonMenuItemMotif);

        buttonGroupLandF.add(jRadioButtonMenuItemGTK);
        jRadioButtonMenuItemGTK.setMnemonic('g');
        jRadioButtonMenuItemGTK.setText("GTK");
        jRadioButtonMenuItemGTK.setToolTipText("GTK look and feel");
        jRadioButtonMenuItemGTK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGTKActionPerformed(evt);
            }
        });
        jMenuLookAndFeel.add(jRadioButtonMenuItemGTK);

        jMenuTools.add(jMenuLookAndFeel);

        jMenuItemConfig.setMnemonic('p');
        jMenuItemConfig.setText("Preferences");
        jMenuItemConfig.setToolTipText("Set preferences");
        jMenuItemConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConfigActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemConfig);

        jMenuBar1.add(jMenuTools);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

        private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
            this.setVisible(false);
        }//GEN-LAST:event_jMenuItemCloseActionPerformed

        private void jRadioButtonMenuItemGTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGTKActionPerformed
            if (jRadioButtonMenuItemGTK.isSelected()) {
                log.info("setting GTK l&f");
                setLookAndFeel("GTK");
            }
        }//GEN-LAST:event_jRadioButtonMenuItemGTKActionPerformed

        private void jRadioButtonLookAndFeelDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonLookAndFeelDefaultActionPerformed
            if (jRadioButtonLookAndFeelDefault.isSelected()) {
                log.info("setting System l&f");
                setLookAndFeel("System");
            }
        }//GEN-LAST:event_jRadioButtonLookAndFeelDefaultActionPerformed

        private void jRadioButtonLookAndFeelNimbusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonLookAndFeelNimbusActionPerformed
            if (jRadioButtonLookAndFeelNimbus.isSelected()) {
                log.info("setting Nimbus l&f");
                setLookAndFeel("Nimbus");
            }
        }//GEN-LAST:event_jRadioButtonLookAndFeelNimbusActionPerformed

        private void jRadioButtonMenuItemMetalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemMetalActionPerformed
            if (jRadioButtonMenuItemMetal.isSelected()) {
                log.info("setting Metal l&f");
                setLookAndFeel("Metal");
            }
        }//GEN-LAST:event_jRadioButtonMenuItemMetalActionPerformed

        private void jRadioButtonMenuItemMotifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemMotifActionPerformed
            if (jRadioButtonMenuItemMotif.isSelected()) {
                log.info("setting Motif l&f");
                setLookAndFeel("Motif");
            }
        }//GEN-LAST:event_jRadioButtonMenuItemMotifActionPerformed

        private void jMenuItemConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfigActionPerformed
            IDROPConfigurationPanel idropConfigurationPanel = new IDROPConfigurationPanel(this, true, iDropCore);
            idropConfigurationPanel.setLocationRelativeTo(null);
            idropConfigurationPanel.setVisible(true);
        }//GEN-LAST:event_jMenuItemConfigActionPerformed

    /**
     * Handle a change in default storage resource
     *
     * @param evt
     */
    private void comboDefaultResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDefaultResourceActionPerformed

        String newResource = (String) comboDefaultResource.getSelectedItem();
        this.getiDropCore().getIrodsAccount().setDefaultStorageResource(newResource);

    }//GEN-LAST:event_comboDefaultResourceActionPerformed

    /**
     * Set the iRODS tree to the user home directory
     *
     * @param evt
     */
    private void btnGoHomeTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoHomeTargetTreeActionPerformed
        // set the root path of the irods tree to root and refresh
        String homeRoot;
        if (this.getiDropCore().getIrodsAccount().isAnonymousAccount()) {
            log.info("setting home dir to public");
            homeRoot = MiscIRODSUtils.computePublicDirectory(this.getiDropCore().getIrodsAccount());
        } else {
            homeRoot = MiscIRODSUtils.computeHomeDirectoryForIRODSAccount(this.getiDropCore().getIrodsAccount());
        }

        this.getiDropCore().setBasePath(homeRoot);
        buildTargetTree(false);
    }//GEN-LAST:event_btnGoHomeTargetTreeActionPerformed

    /**
     * Set the iRODS tree to the root directory
     *
     * @param evt
     */
    private void btnGoRootTargetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoRootTargetTreeActionPerformed
        this.getiDropCore().setBasePath("/");
        buildTargetTree(false);
    }//GEN-LAST:event_btnGoRootTargetTreeActionPerformed

    /**
     * Signal to switch grids
     *
     * @param evt
     */
    private void btnManageGridsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageGridsActionPerformed
        displayAndProcessSignOn();

    }//GEN-LAST:event_btnManageGridsActionPerformed

    private void btnShowTransferManagerActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnShowTransferManagerActionPerformed

        showQueueManagerDialog();
    }// GEN-LAST:event_btnShowTransferManagerActionPerformed

    /**
     * Click of 'pause' toggle in iDrop client view
     *
     * @param evt
     */
    private void togglePauseTransferActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_togglePauseTransferActionPerformed

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
    }// GEN-LAST:event_togglePauseTransferActionPerformed
    public ActionListener showPreferencesDialogActionListener = new ActionListener() {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            newPreferencesDialog.setVisible(true);
        }
    };
    public ActionListener okButtonPreferencesDialogActionListener = new ActionListener() {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            /*
             * getiDropCore().getPreferences().putBoolean("showGUI", showGUICheckBox.isSelected() ?
             * true : false); FIXME: recast as database options
             *
             *
             */

            newPreferencesDialog.setVisible(false);
        }
    };

    private void formWindowClosed(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
        this.setVisible(false);
        this.formShown = false;
    }// GEN-LAST:event_formWindowClosed

    /**
     * refresh the iRODS file system tree view
     *
     * @param evt
     */
    private void btnRefreshTargetTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshTargetTreeActionPerformed
        buildTargetTree(false);
    }// GEN-LAST:event_btnRefreshTargetTreeActionPerformed

    private void toggleLocalFilesStateChanged(
            final javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_toggleLocalFilesStateChanged
    }// GEN-LAST:event_toggleLocalFilesStateChanged

    private void toggleLocalFilesActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleLocalFilesActionPerformed
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
    private void jCheckBoxMenuItemShowSourceTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItemShowSourceTreeActionPerformed
        toggleLocalFiles.setSelected(jCheckBoxMenuItemShowSourceTree.isSelected());
        toggleLocalFilesActionPerformed(evt);
    }// GEN-LAST:event_jCheckBoxMenuItemShowSourceTreeActionPerformed

    private void jMenuItemExitActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemExitActionPerformed
        shutdownWithConfirmation();
    }// GEN-LAST:event_jMenuItemExitActionPerformed

    /**
     * Handle the press of the refresh local drives button, refresh the local file tree.
     *
     * @param evt
     */
    private void btnRefreshLocalDrivesActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshLocalDrivesActionPerformed
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
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                            null, ex);
                    throw new IdropRuntimeException(
                            "exception expanding tree nodes", ex);
                }

            }
        });

    }// GEN-LAST:event_btnRefreshLocalDrivesActionPerformed

    private void toggleIrodsDetailsStateChanged(
            final javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_toggleIrodsDetailsStateChanged
        // unused right now
    }// GEN-LAST:event_toggleIrodsDetailsStateChanged

    /**
     * Show or hide the irods details panel
     *
     * @param evt
     */
    private void toggleIrodsDetailsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleIrodsDetailsActionPerformed
        jCheckBoxMenuItemShowIrodsInfo.setSelected(toggleIrodsDetails.isSelected());
        handleInfoPanelShowOrHide();
    }// GEN-LAST:event_toggleIrodsDetailsActionPerformed

    private void jCheckBoxMenuItemShowIrodsInfoActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItemShowIrodsInfoActionPerformed
        toggleIrodsDetails.setSelected(jCheckBoxMenuItemShowIrodsInfo.isSelected());
        handleInfoPanelShowOrHide();

    }// GEN-LAST:event_jCheckBoxMenuItemShowIrodsInfoActionPerformed

    /**
     * Focus lost on tags, update tags in the info box. Updates are done in the tagging service by
     * taking a delta between the current and desired tag set.
     *
     * @param evt
     */
    private void txtTagsFocusLost(final java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtTagsFocusLost
    }// GEN-LAST:event_txtTagsFocusLost

    /**
     * Set the account information in the gutter, including the available resources on the grid.
     * Note that this method should be called in the context of a
     * <code>Runnable</code>
     */
    private void setUpAccountGutter() {
        userNameLabel.setText(this.getIrodsAccount().getUserName());
        lblZone.setText(this.getIrodsAccount().getZone());
        lblHost.setText(this.getIrodsAccount().getHost());
        /*
         * Get a list of storage resources on this host
         */
        try {
            ResourceAO resourceAO = this.getiDropCore().getIRODSAccessObjectFactory().getResourceAO(this.getIrodsAccount());
            log.info("getting a list of all resources in the zone");
            List<String> resources = new ArrayList<String>();
            resources.add("");
            resources.addAll(resourceAO.listResourceAndResourceGroupNames());
            comboDefaultResource.setModel(new DefaultComboBoxModel(resources.toArray()));
            comboDefaultResource.setSelectedItem(this.getIrodsAccount().getDefaultStorageResource());
        } catch (JargonException ex) {
            log.error("error getting resource list", ex);
            throw new IdropRuntimeException("error getting resource list", ex);
        }
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
                idropGui.toggleIrodsDetails.setSelected(false);
                handleInfoPanelShowOrHide();
                getiDropCore().setBasePath(null);
                setUpAccountGutter();
            }
        });

    }

    /**
     * Tag view master panel has been shown, lazily load the user tag cloud if not loaded
     *
     * @param evt
     */
    private void pnlTagViewMasterComponentShown(
            final java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_pnlTagViewMasterComponentShown
        // dont think right event TODO: loose this event
    }// GEN-LAST:event_pnlTagViewMasterComponentShown

    private void tabIrodsViewsStateChanged(
            final javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_tabIrodsViewsStateChanged
    }// GEN-LAST:event_tabIrodsViewsStateChanged

    private void txtMainSearchKeyPressed(final java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtMainSearchKeyPressed

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

    private void txtTagsKeyPressed(final java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtTagsKeyPressed

        // FIXME: cull this

        // enter key triggers search
        if (evt.getKeyCode() != java.awt.event.KeyEvent.VK_ENTER) {
            return;
        }

    }// GEN-LAST:event_txtTagsKeyPressed

    /**
     * Display the data replication dialog for the collection or data object depicted in the info
     * panel
     *
     * @param evt
     */
    private void btnReplicationActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnReplicationActionPerformed
        if (lastCachedInfoItem == null) {
            return;
        }

        ReplicationDialog replicationDialog;
        if (lastCachedInfoItem instanceof DataObject) {
            DataObject cachedDataObject = (DataObject) lastCachedInfoItem;
            replicationDialog = new ReplicationDialog(this, true,
                    cachedDataObject.getCollectionName(),
                    cachedDataObject.getDataName());
        } else if (lastCachedInfoItem instanceof Collection) {
            Collection collection = (Collection) lastCachedInfoItem;
            replicationDialog = new ReplicationDialog(this, true,
                    collection.getCollectionName());
        } else {
            showIdropException(new IdropException(
                    "Unknown type of object displayed in info area, cannot create the replication dialog"));
            throw new IdropRuntimeException(
                    "unknown type of object displayed in info area");
        }

        replicationDialog.setLocation(
                (int) (this.getLocation().getX() + replicationDialog.getWidth() / 2), (int) (this.getLocation().getY() + replicationDialog.getHeight() / 2));
        replicationDialog.setVisible(true);
    }// GEN-LAST:event_btnReplicationActionPerformed

    /**
     * Display the metadata edit /view dialog for the item displayed in the info panel
     *
     * @param evt
     */
    private void btnViewMetadataActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnViewMetadataActionPerformed
        if (lastCachedInfoItem == null) {
            return;
        }

        MetadataViewDialog metadataViewDialog;
        if (lastCachedInfoItem instanceof DataObject) {
            DataObject cachedDataObject = (DataObject) lastCachedInfoItem;
            metadataViewDialog = new MetadataViewDialog(this,
                    getIrodsAccount(), cachedDataObject.getCollectionName(),
                    cachedDataObject.getDataName());
        } else if (lastCachedInfoItem instanceof Collection) {
            Collection collection = (Collection) lastCachedInfoItem;
            metadataViewDialog = new MetadataViewDialog(this,
                    getIrodsAccount(), collection.getCollectionName());
        } else {
            showIdropException(new IdropException(
                    "Unknown type of object displayed in info area, cannot create the replication dialog"));
            throw new IdropRuntimeException(
                    "unknown type of object displayed in info area");
        }

        metadataViewDialog.setLocation(
                (int) (this.getLocation().getX() + metadataViewDialog.getWidth() / 2),
                (int) (this.getLocation().getY() + metadataViewDialog.getHeight() / 2));
        metadataViewDialog.setVisible(true);
    }// GEN-LAST:event_btnViewMetadataActionPerformed

    private void btnUpdateInfoActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateInfoActionPerformed
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

                idropGui.setBusyCursor();
                FreeTaggingService freeTaggingService;
                IRODSTaggingService userTaggingService;

                try {
                    freeTaggingService = FreeTaggingServiceImpl.instance(
                            getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                            getIrodsAccount());

                    userTaggingService = IRODSTaggingServiceImpl.instance(getiDropCore().getIrodsFileSystem().getIRODSAccessObjectFactory(),
                            getIrodsAccount());

                    if (lastCachedInfoItem instanceof Collection) {
                        log.info("processing tags for collection");
                        Collection collection = (Collection) lastCachedItemToProcessTagsFor;
                        IRODSTagGrouping irodsTagGrouping = new IRODSTagGrouping(
                                MetaDataAndDomainData.MetadataDomain.COLLECTION, collection.getCollectionName(),
                                txtTags.getText(), getIrodsAccount().getUserName());
                        log.debug("new tag set is:{}", txtTags.getText());
                        freeTaggingService.updateTags(irodsTagGrouping);
                        IRODSTagValue descriptionValue = new IRODSTagValue(txtComment.getText().trim(), getIrodsAccount().getUserName());
                        log.info("checking update of description");
                        userTaggingService.checkAndUpdateDescriptionOnCollection(collection.getCollectionName(), descriptionValue);
                    } else if (lastCachedInfoItem instanceof DataObject) {
                        log.info("processing tags for data object");
                        DataObject dataObject = (DataObject) lastCachedItemToProcessTagsFor;
                        IRODSTagGrouping irodsTagGrouping = new IRODSTagGrouping(
                                MetadataDomain.DATA, dataObject.getCollectionName()
                                + "/"
                                + dataObject.getDataName(), txtTags.getText(), getIrodsAccount().getUserName());
                        log.debug("new tag set is:{}", txtTags.getText());
                        freeTaggingService.updateTags(irodsTagGrouping);
                        IRODSTagValue descriptionValue = new IRODSTagValue(txtComment.getText().trim(), getIrodsAccount().getUserName());
                        log.info("checking update of description");
                        userTaggingService.checkAndUpdateDescriptionOnDataObject(dataObject.getAbsolutePath(), descriptionValue);
                    } else {
                        log.error("unknown item type cached as being displayed in info area");
                        throw new IdropRuntimeException(
                                "unknown item type cached");
                    }

                    idropGui.showMessageFromOperation("update of info successful");

                } catch (JargonException ex) {
                    idropGui.showIdropException(ex);
                    Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE,
                            null, ex);
                    throw new IdropRuntimeException(ex);
                } finally {
                    try {
                        getiDropCore().getIrodsFileSystem().close(
                                getIrodsAccount());
                    } catch (JargonException ex) {
                        Logger.getLogger(iDrop.class.getName()).log(
                                Level.SEVERE, null, ex);
                        // logged and ignored
                    }
                    idropGui.setNormalCursor();
                }
            }
        });
    }// GEN-LAST:event_btnUpdateInfoActionPerformed

    /*
     * The search button has been pressed, do a search
     */
    private void btnSearchActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSearchActionPerformed
        processSearchRequest();
    }// GEN-LAST:event_btnSearchActionPerformed

    private void menuItemShowInHierarchyActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuItemShowInHierarchyActionPerformed

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
            selPath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree,
                    entry.getFormattedAbsolutePath());
        } catch (IdropException ex) {
            Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
            this.showMessageFromOperation("That collection or file is not visible in the context of the current tree");
            return;
        }

        if (selPath == null) {
            log.warn("no tree path found for collection entry:{}", entry);
            return;
        }

        // FIXME: reimplement
        // select the path in the tree and show the hierarchy tab
        // TreeUtils.expandAll(irodsTree, selPath, true);
        // irodsTree.scrollPathToVisible(selPath);
        tabIrodsViews.setSelectedComponent(pnlTabHierarchicalView);
    }// GEN-LAST:event_menuItemShowInHierarchyActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGoHomeTargetTree;
    private javax.swing.JButton btnGoRootTargetTree;
    private javax.swing.JButton btnManageGrids;
    private javax.swing.JButton btnRefreshLocalDrives;
    private javax.swing.JButton btnRefreshTargetTree;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSetRootCustomTargetTree;
    private javax.swing.JButton btnShowTransferManager;
    private javax.swing.JButton btnUpdateInfo;
    private javax.swing.ButtonGroup buttonGroupLandF;
    private javax.swing.JComboBox comboDefaultResource;
    private javax.swing.JComboBox comboSearchType;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JPanel iDropToolbar;
    private javax.swing.JToolBar idropProgressPanelToolbar;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowIrodsInfo;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowSourceTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemConfig;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenu jMenuLookAndFeel;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JRadioButtonMenuItem jRadioButtonLookAndFeelDefault;
    private javax.swing.JRadioButtonMenuItem jRadioButtonLookAndFeelNimbus;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGTK;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemMetal;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemMotif;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPanelLocalRemote;
    private javax.swing.JLabel lblCollectionType;
    private javax.swing.JLabel lblCollectionTypeLabel;
    private javax.swing.JLabel lblComment;
    private javax.swing.JLabel lblCurrentFile;
    private javax.swing.JLabel lblDataPath;
    private javax.swing.JLabel lblDataPathLabel;
    private javax.swing.JLabel lblDataReplicationStatus;
    private javax.swing.JLabel lblDataReplicationStatusLabel;
    private javax.swing.JLabel lblDataStatus;
    private javax.swing.JLabel lblDataStatusLabel;
    private javax.swing.JLabel lblDataType;
    private javax.swing.JLabel lblDataTypeLabel;
    private javax.swing.JLabel lblDataVersion;
    private javax.swing.JLabel lblDataVersionLabel;
    private javax.swing.JLabel lblDefaultResource;
    private javax.swing.JLabel lblFileOrCollectionName;
    private javax.swing.JLabel lblFilePathLabel;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHostLabel;
    private javax.swing.JLabel lblInfoChecksum;
    private javax.swing.JLabel lblInfoChecksumValue;
    private javax.swing.JLabel lblInfoCreatedAt;
    private javax.swing.JLabel lblInfoCreatedAtTimeValue;
    private javax.swing.JLabel lblInfoCreatedAtValue;
    private javax.swing.JLabel lblInfoLength;
    private javax.swing.JLabel lblInfoLengthValue;
    private javax.swing.JLabel lblInfoSharing;
    private javax.swing.JLabel lblInfoUpdatedAt;
    private javax.swing.JLabel lblInfoUpdatedAtTimeValue;
    private javax.swing.JLabel lblInfoUpdatedAtValue;
    private javax.swing.JLabel lblMainSearch;
    private javax.swing.JLabel lblMetadataInfo;
    private javax.swing.JLabel lblMetadataInfo1;
    private javax.swing.JLabel lblOwnerName;
    private javax.swing.JLabel lblOwnerNameLabel;
    private javax.swing.JLabel lblOwnerZone;
    private javax.swing.JLabel lblOwnerZoneLabel;
    private javax.swing.JLabel lblSelectedFileInfo;
    private javax.swing.JLabel lblTags;
    private javax.swing.JLabel lblTransferByteCounts;
    private javax.swing.JLabel lblTransferFilesCounts;
    private javax.swing.JLabel lblTransferMessage;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JLabel lblUserNameLabel;
    private javax.swing.JLabel lblZone;
    private javax.swing.JLabel lblZoneLabel;
    private javax.swing.JList listLocalDrives;
    private javax.swing.JMenuItem menuItemShowInHierarchy;
    private javax.swing.JPanel pnlBottomGutter;
    private javax.swing.JPanel pnlCurrentTransferStatus;
    private javax.swing.JPanel pnlDrivesFiller;
    private javax.swing.JPanel pnlFileInfoDemographics;
    private javax.swing.JPanel pnlHostInfo;
    private javax.swing.JPanel pnlIdropBottom;
    private javax.swing.JPanel pnlIdropMain;
    private javax.swing.JPanel pnlInfoIcon;
    private javax.swing.JPanel pnlInfoInner;
    private javax.swing.JPanel pnlInfoMetadata;
    private javax.swing.JPanel pnlInfoReplication;
    private javax.swing.JPanel pnlInfoSharing;
    private javax.swing.JPanel pnlIrodsArea;
    private javax.swing.JPanel pnlIrodsDetailsToggleSizer;
    private javax.swing.JPanel pnlIrodsInfo;
    private javax.swing.JPanel pnlIrodsTreeMaster;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JPanel pnlLocalRoots;
    private javax.swing.JPanel pnlLocalToggleSizer;
    private javax.swing.JPanel pnlLocalTreeArea;
    private javax.swing.JPanel pnlRefreshButton;
    private javax.swing.JPanel pnlSearchSizer;
    private javax.swing.JPanel pnlStatusIcon;
    private javax.swing.JPanel pnlTabHierarchicalView;
    private javax.swing.JPanel pnlTabSearch;
    private javax.swing.JPanel pnlTabSearchResults;
    private javax.swing.JPanel pnlTabSearchTop;
    private javax.swing.JPanel pnlToolbarSizer;
    private javax.swing.JPanel pnlTopToolbarSearchArea;
    private javax.swing.JPanel pnlTransferOptions;
    private javax.swing.JLabel progressIconImageLabel;
    private javax.swing.JProgressBar progressIntraFile;
    private javax.swing.JScrollPane scrollComment;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JScrollPane scrollLocalDrives;
    private javax.swing.JScrollPane scrollLocalFileTree;
    private javax.swing.JScrollPane scrollPaneSearchResults;
    protected javax.swing.JPopupMenu searchTablePopupMenu;
    private javax.swing.JSplitPane splitTargetCollections;
    private javax.swing.JTabbedPane tabInfo;
    private javax.swing.JTabbedPane tabIrodsViews;
    private javax.swing.JTable tableSearchResults;
    private javax.swing.JToggleButton toggleIrodsDetails;
    private javax.swing.JToggleButton toggleLocalFiles;
    private javax.swing.JToggleButton togglePauseTransfer;
    private javax.swing.JToolBar toolbarIrodsTree;
    private javax.swing.JProgressBar transferStatusProgressBar;
    private javax.swing.JTextArea txtComment;
    private javax.swing.JTextField txtMainSearch;
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
                    FreeTaggingService freeTaggingService = FreeTaggingServiceImpl.instance(iDropCore.getIRODSAccessObjectFactory(),
                            getIrodsAccount());
                    TagQuerySearchResult result = freeTaggingService.searchUsingFreeTagString(searchTerms);
                    IRODSSearchTableModel irodsSearchTableModel = new IRODSSearchTableModel(
                            result.getQueryResultEntries());
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

    private void initializeLookAndFeelSelected() {
        String lookAndFeelChoice = iDropCore.getIdropConfig().getPropertyForKey(IdropConfigurationService.LOOK_AND_FEEL);
        if (lookAndFeelChoice == null || lookAndFeelChoice.isEmpty()) {
            lookAndFeelChoice = "System";
        }
        if (lookAndFeelChoice.equals("Metal")) {

            this.jRadioButtonMenuItemMetal.setSelected(true);

        } else if (lookAndFeelChoice.equals("System")) {

            this.jRadioButtonLookAndFeelDefault.setSelected(true);
        } else if (lookAndFeelChoice.equals("Motif")) {

            this.jRadioButtonMenuItemMotif.setSelected(true);
        } else if (lookAndFeelChoice.equals("GTK")) {

            this.jRadioButtonMenuItemGTK.setSelected(true);
        } else if (lookAndFeelChoice.equals("Nimbus")) {
            this.jRadioButtonLookAndFeelNimbus.setSelected(true);

        } else {
            this.jRadioButtonLookAndFeelDefault.setSelected(true);
        }
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

            if (lookAndFeelChoice.equals("Metal")) {
                lookAndFeel = lookAndFeelChoice;
                this.jRadioButtonMenuItemMetal.setSelected(true);
                //  an alternative way to set the Metal L&F is to replace the 
                // previous line with:
                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";

            } else if (lookAndFeelChoice.equals("System")) {
                lookAndFeel = lookAndFeelChoice;
                this.jRadioButtonLookAndFeelDefault.setSelected(true);
            } else if (lookAndFeelChoice.equals("Motif")) {
                lookAndFeel = lookAndFeelChoice;
                this.jRadioButtonMenuItemMotif.setSelected(true);
            } else if (lookAndFeelChoice.equals("GTK")) {
                lookAndFeel = lookAndFeelChoice;
                this.jRadioButtonMenuItemGTK.setSelected(true);
            } else if (lookAndFeelChoice.equals("Nimbus")) {
                this.jRadioButtonLookAndFeelNimbus.setSelected(true);
                lookAndFeel = lookAndFeelChoice;
            } else {
                lookAndFeel = "System";

            }
            try {
                LookAndFeelManager laf = new LookAndFeelManager(iDropCore);
                laf.setLookAndFeel(lookAndFeel);
                shutdown();
            } catch (Exception e) {
                log.warn("unable to set look and feel to :{}", lookAndFeel);
            }
        }
    }

    @Override
    public CallbackResponse transferAsksWhetherToForceOperation(String irodsAbsolutePath, boolean isCollection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(final MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                int rowAtPoint = tableSearchResults.rowAtPoint(e.getPoint());
                tableSearchResults.getSelectionModel().setSelectionInterval(
                        rowAtPoint, rowAtPoint);
                searchTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public IDROPCore getiDropCore() {
        return iDropCore;
    }

    public void setiDropCore(final IDROPCore iDropCore) {
        this.iDropCore = iDropCore;
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
     * Call from a swing event queue runnable
     */
    private void clearProgressBar() {
        lblTransferType.setText("");
        lblCurrentFile.setText("");
        transferStatusProgressBar.setMinimum(0);
        transferStatusProgressBar.setMaximum(100);
        transferStatusProgressBar.setValue(0);
        transferStatusProgressBar.setString("");
        progressIntraFile.setString("");
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

        if (!getToggleIrodsDetails().isSelected()) {
            return;
        }

        if (irodsNode == null) {
            return;
        }

        if (irodsNode.isLeaf()) {
            log.info("selected node is a leaf, get a data object");
            buildDataObjectFromSelectedIRODSNodeAndGiveToInfoPanel(irodsNode);
        } else {
            log.info("selected node is a collection, get a collection object");
            buildCollectionFromSelectedIRODSNodeAndGiveToInfoPanel(irodsNode);
        }
    }

    /**
     * When a selected node in the iRODS tree is a data object, put the data object info in the info
     * panel
     *
     * @param irodsNode
     */
    private void buildDataObjectFromSelectedIRODSNodeAndGiveToInfoPanel(
            final IRODSNode irodsNode) throws IdropException {
        try {
            CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntry = (CollectionAndDataObjectListingEntry) irodsNode.getUserObject();
            log.info(
                    "will be getting a data object based on entry in IRODSNode:{}",
                    irodsNode);
            DataObjectAO dataObjectAO = getiDropCore().getIRODSAccessObjectFactory().getDataObjectAO(this.getiDropCore().getIrodsAccount());
            DataObject dataObject = dataObjectAO.findByCollectionNameAndDataName(
                    collectionAndDataObjectListingEntry.getParentPath(),
                    collectionAndDataObjectListingEntry.getPathOrName());
            initializeInfoPanel(dataObject);
        } catch (Exception e) {
            log.error("error building data object for: {}", irodsNode);
            throw new IdropException(e);
        }
    }

    /**
     * When a selected node in the iRODS tree is a collection, put the collection info into the info
     * panel
     *
     * @param irodsNode
     */
    private void buildCollectionFromSelectedIRODSNodeAndGiveToInfoPanel(
            final IRODSNode irodsNode) throws IdropException {
        try {
            CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntry = (CollectionAndDataObjectListingEntry) irodsNode.getUserObject();
            log.info(
                    "will be getting a collection based on entry in IRODSNode:{}",
                    irodsNode);
            CollectionAO collectionAO = getiDropCore().getIRODSAccessObjectFactory().getCollectionAO(getIrodsAccount());
            Collection collection = collectionAO.findByAbsolutePath(collectionAndDataObjectListingEntry.getPathOrName());
            initializeInfoPanel(collection);
        } catch (Exception e) {
            log.error("error building collection object for: {}", irodsNode);
            throw new IdropException(e);

        }
    }
}
