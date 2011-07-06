package org.irods.jargon.idrop.desktop.systraygui;

import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_DEVICE_NAME;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_PREFERENCES;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_SPLASH;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_SHOW_UI;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREFERENCE_KEY_FIRST_TIME_RUN;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREF_LOGIN_HOST;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREF_LOGIN_PORT;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREF_LOGIN_RESOURCE;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREF_LOGIN_USERNAME;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.PREF_LOGIN_ZONE;
import static org.irods.jargon.idrop.desktop.systraygui.Constants.STARTUP_SEQUENCE_PAUSE_INTERVAL;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.lang.StringUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.idrop.desktop.systraygui.components.RemoteFSChooserListCellRenderer;
import org.irods.jargon.idrop.desktop.systraygui.components.RemoteFileChooserDialogLookInComboBoxRender;
import org.irods.jargon.idrop.desktop.systraygui.listeners.LoginCancelActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.LoginDialogEnterKeyListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.LoginDialogWindowListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.LoginOKActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupEditSynchronizationDialogCancelActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupEditSynchronizationDialogLocalPathBrowseActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupEditSynchronizationDialogRemotePathBrowseActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupEditSynchronizationDialogSaveActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupPreferencesDialogCancelActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupPreferencesDialogDefaultLocalDirectoryActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupPreferencesDialogSaveActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogCancelActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogDetailsViewActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogFileTypeActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogHomeFolderActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogListMouseListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogListViewActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogLookInActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogNewFolderActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogOpenActionListener;
import org.irods.jargon.idrop.desktop.systraygui.listeners.StartupRemoteFileChooserDialogUpFolderActionListener;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationServiceImpl;
import org.irods.jargon.idrop.desktop.systraygui.services.QueueSchedulerTimerTask;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.TransferDAOManager;
import org.irods.jargon.transfer.dao.SynchronizationDAO;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.engine.TransferManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cookxml.cookswing.CookSwing;

/**
 * 
 * @author jdr0887
 */
public class IDROPSplashWindow implements Runnable {

    private final Logger log = LoggerFactory.getLogger(IDROPSplashWindow.class);

    private IDROPCore idropCore;

    private IDROPDesktop desktop;

    public JWindow window;

    public JProgressBar startupProgressBar;

    public JCheckBox preferencesDialogShowHiddenFilesCheckBox, preferencesDialogShowUICheckBox,
            preferencesDialogShowPreferencesCheckBox, preferencesDialogShowSplashScreenCheckBox;

    public JList remoteFileChooserDialogList;

    public JTextField loginDialogHostTextField, loginDialogZoneTextField, loginDialogResourceTextField,
            loginDialogPortTextField, loginDialogUsernameTextField, preferencesDialogDeviceNameTextField,
            preferencesDialogDefaultLocalDirectoryTextField;

    public JPasswordField loginDialogPasswordPasswordField;

    public JTextField editSynchronizationDialogLocalPathTextField, editSynchronizationDialogNameTextField,
            editSynchronizationDialogRemotePathTextField, remoteFileChooserDialogFileNameTextField,
            editSynchronizationDialogDeviceNameTextField;

    public JDialog preferencesDialog, loginDialog, editSynchronizationDialog, remoteFileChooserDialog;

    public DefaultListModel remoteFileChooserDialogListModel;

    public JButton editSynchronizationDialogSaveButton, editSynchronizationDialogCancelButton,
            editSynchronizationDialogRemotePathBrowseButton, editSynchronizationDialogLocalPathBrowseButton,
            remoteFileChooserDialogNewFolderButton, remoteFileChooserDialogUpFolderButton,
            remoteFileChooserDialogHomeFolderButton, remoteFileChooserDialogListViewButton,
            remoteFileChooserDialogDetailsViewButton, remoteFileChooserDialogOpenButton,
            remoteFileChooserDialogCancelButton, loginDialogOKButton, preferencesDialogDefaultLocalDirectoryButton;

    public JFileChooser editSynchronizationDialogLocalPathFileChooser, editSynchronizationDialogRemotePathFileChooser,
            preferencesDialogDefaultLocalDirectoryFileChooser;

    public JComboBox remoteFileChooserDialogFileTypeComboBox, remoteFileChooserDialogLookInComboBox;

    public IDROPSplashWindow() {
        super();
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method stores all initialization commands for the window.
     */
    private void init() throws Exception {

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

        log.info("creating idropCore...");
        idropCore = new IDROPCore();
        desktop = new IDROPDesktop(idropCore);

        Preferences prefs = idropCore.getPreferences();

        CookSwing cookSwing = new CookSwing(this);
        this.window = (JWindow) cookSwing.render("org/irods/jargon/idrop/splashWindow.xml");

        boolean showSplash = prefs.getBoolean(PREFERENCE_KEY_SHOW_SPLASH, true);
        preferencesDialogShowSplashScreenCheckBox.setSelected(showSplash ? true : false);

        boolean showUI = prefs.getBoolean(PREFERENCE_KEY_SHOW_UI, true);
        preferencesDialogShowUICheckBox.setSelected(showUI ? true : false);

        if (showUI) {

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int width = this.window.getWidth();
            int height = this.window.getHeight();

            int xLocation = (screenSize.width / 2) - (width / 2);
            int yLocation = (screenSize.height / 2) - (height / 2);

            this.window.setLocation(xLocation, yLocation);
            this.window.pack();
            this.window.setVisible(true);

            Icon newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
            remoteFileChooserDialogNewFolderButton.setIcon(newFolderIcon);

            Icon upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
            remoteFileChooserDialogUpFolderButton.setIcon(upFolderIcon);

            Icon homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
            remoteFileChooserDialogHomeFolderButton.setIcon(homeFolderIcon);

            Icon listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
            remoteFileChooserDialogListViewButton.setIcon(listViewIcon);

            Icon detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
            remoteFileChooserDialogDetailsViewButton.setIcon(detailsViewIcon);

            remoteFileChooserDialogList.setCellRenderer(new RemoteFSChooserListCellRenderer());
            remoteFileChooserDialogLookInComboBox.setRenderer(new RemoteFileChooserDialogLookInComboBoxRender());

        }
    }

    public final ActionListener editSynchronizationDialogLocalPathBrowseActionListener = new StartupEditSynchronizationDialogLocalPathBrowseActionListener(
            this);

    public final ActionListener editSynchronizationDialogRemotePathBrowseActionListener = new StartupEditSynchronizationDialogRemotePathBrowseActionListener(
            this);

    public final ActionListener editSynchronizationDialogSaveActionListener = new StartupEditSynchronizationDialogSaveActionListener(
            this);

    public final ActionListener editSynchronizationDialogCancelActionListener = new StartupEditSynchronizationDialogCancelActionListener(
            this);

    public final ActionListener remoteFileChooserDialogUpFolderActionListener = new StartupRemoteFileChooserDialogUpFolderActionListener(
            this);

    public final ActionListener remoteFileChooserDialogHomeFolderActionListener = new StartupRemoteFileChooserDialogHomeFolderActionListener(
            this);

    public final ActionListener remoteFileChooserDialogNewFolderActionListener = new StartupRemoteFileChooserDialogNewFolderActionListener(
            this);

    public final ActionListener remoteFileChooserDialogListViewActionListener = new StartupRemoteFileChooserDialogListViewActionListener(
            this);

    public final ActionListener remoteFileChooserDialogDetailsViewActionListener = new StartupRemoteFileChooserDialogDetailsViewActionListener(
            this);

    public final ActionListener remoteFileChooserDialogLookInActionListener = new StartupRemoteFileChooserDialogLookInActionListener(
            this);

    public final ActionListener remoteFileChooserDialogFileTypeActionListener = new StartupRemoteFileChooserDialogFileTypeActionListener(
            this);

    public final ActionListener remoteFileChooserDialogOpenActionListener = new StartupRemoteFileChooserDialogOpenActionListener(
            this);

    public final ActionListener remoteFileChooserDialogCancelActionListener = new StartupRemoteFileChooserDialogCancelActionListener(
            this);

    public final ActionListener loginDialogOKActionListener = new LoginOKActionListener(this);

    public final ActionListener loginDialogCancelActionListener = new LoginCancelActionListener(this);

    public final KeyListener loginDialogEnterKeyListener = new LoginDialogEnterKeyListener(this);

    public final MouseListener remoteFileChooserDialogListMouseListener = new StartupRemoteFileChooserDialogListMouseListener(
            this);

    public final ActionListener preferencesDialogSaveActionListener = new StartupPreferencesDialogSaveActionListener(
            this);

    public final ActionListener preferencesDialogCancelActionListener = new StartupPreferencesDialogCancelActionListener(
            this);

    public final ActionListener preferencesDialogDefaultLocalDirectoryActionListener = new StartupPreferencesDialogDefaultLocalDirectoryActionListener(
            this);

    public final WindowListener loginDialogWindowListener = new LoginDialogWindowListener();

    class Task extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            int count = 0;

            setStatus("Initializing...", ++count);

            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "iDrop Client for iRODS");

            try {
                idropCore.setIrodsFileSystem(IRODSFileSystem.instance());
            } catch (JargonException ex) {
                java.util.logging.Logger.getLogger(IDROPSplashWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

            log.info("determine config root directory");
            String userHomeDirectory = System.getProperty("user.home");
            StringBuilder sb = new StringBuilder();
            sb.append(userHomeDirectory);
            sb.append("/.idrop");
            String derivedConfigHomeDirectory = sb.toString();
            log.info("set config home directory as: {}", derivedConfigHomeDirectory);

            /*
             * Here is where I first try and start the database to get the configuration. A database error indicates
             * that iDrop is already running
             */

            setStatus("Checking preferences...", ++count);

            Properties derivedProperties = null;
            try {
                IdropConfigurationService idropConfigurationService = new IdropConfigurationServiceImpl(
                        derivedConfigHomeDirectory);
                derivedProperties = idropConfigurationService.bootstrapConfiguration();
            } catch (IdropAlreadyRunningException are) {
                log.error("idrop is already running, shutting down");
                JOptionPane.showMessageDialog((Component) null, "iDrop is already running, cannot start",
                        "iDrop Error", JOptionPane.OK_OPTION);
                System.exit(1);
            } catch (IdropException ex) {
                java.util.logging.Logger.getLogger(IDROPSplashWindow.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropRuntimeException(ex);
            }

            log.info("config properties derived...");
            idropCore.setIdropConfig(new IdropConfig(derivedProperties));
            idropCore.getIdropConfig().setUpLogging();

            log.info("logging in in splash background thread");
            setStatus("Logging in...", ++count);


            String host = idropCore.getPreferences().get(PREF_LOGIN_HOST, null);
            if (StringUtils.isNotEmpty(host)) {
                loginDialogHostTextField.setText(host);
            }

            String zone = idropCore.getPreferences().get(PREF_LOGIN_ZONE, null);
            if (StringUtils.isNotEmpty(zone)) {
                loginDialogZoneTextField.setText(zone);
            }

            String resource = idropCore.getPreferences().get(PREF_LOGIN_RESOURCE, null);
            if (StringUtils.isNotEmpty(resource)) {
                loginDialogResourceTextField.setText(resource);
            }

            String username = idropCore.getPreferences().get(PREF_LOGIN_USERNAME, null);
            if (StringUtils.isNotEmpty(username)) {
                loginDialogUsernameTextField.setText(username);
            }

            String port = idropCore.getPreferences().get(PREF_LOGIN_PORT, null);
            if (StringUtils.isNotEmpty(port)) {
                loginDialogPortTextField.setText(port);
            }

            Toolkit tk = Toolkit.getDefaultToolkit();
            int x = (tk.getScreenSize().width - loginDialog.getWidth()) / 2;
            int y = (tk.getScreenSize().height - loginDialog.getHeight()) / 2;
            loginDialog.setLocation(x, y);
            window.toBack();
            loginDialog.toFront();
            loginDialog.setVisible(true);

            log.info("logged in, now checking for first run...");

            setStatus("Initial Synchronization setup...", ++count);
            
            TransferDAOManager transferMgr = TransferDAOManager.getInstance();
            SynchronizationDAO synchDAO = transferMgr.getTransferDAOBean().getSynchronizationDAO();
            Synchronization synch = synchDAO.findByName("Backup");

            Preferences prefs = idropCore.getPreferences();
            String deviceName = prefs.get(PREFERENCE_KEY_DEVICE_NAME, "");
            if (StringUtils.isNotEmpty(deviceName)) {
                preferencesDialogDeviceNameTextField.setText(deviceName);
            }

            boolean firstTimeRun = prefs.getBoolean(PREFERENCE_KEY_FIRST_TIME_RUN, true);

            if (firstTimeRun && synch == null) {
                log.info("first time running idrop, starting configuration wizard");
                editSynchronizationDialogDeviceNameTextField.setText(deviceName);
                if (StringUtils.isNotEmpty(deviceName)) {
                    editSynchronizationDialogDeviceNameTextField.setText(deviceName);
                }
                editSynchronizationDialogNameTextField.setText("Backup");
                window.toBack();
                editSynchronizationDialog.setVisible(true);
                editSynchronizationDialog.toFront();
                remoteFileChooserDialog.setLocationRelativeTo(editSynchronizationDialog);
            }
             
            setStatus("Building transfer engine...", ++count);

            log.info("building transfer manager...");

            try {
                idropCore.setTransferManager(new TransferManagerImpl(idropCore.getIrodsFileSystem(), desktop, idropCore
                        .getIdropConfig().isLogSuccessfulTransfers()));
            } catch (JargonException ex) {
                java.util.logging.Logger.getLogger(IDROPSplashWindow.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropRuntimeException("error creating transferManager", ex);
            }

            try {
                List<LocalIRODSTransfer> currentQueue = idropCore.getTransferManager().getCurrentQueue();

                if (!currentQueue.isEmpty()) {
                    int result = JOptionPane.showConfirmDialog((Component) null,
                            "Transfers are waiting to process, restart transfer?", "iDrop Transfers in Progress",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.CANCEL_OPTION) {
                        idropCore.getTransferManager().pause();
                    }
                }
            } catch (JargonException ex) {
                java.util.logging.Logger.getLogger(IDROPSplashWindow.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropRuntimeException("error evaluating current queue", ex);
            }

            setStatus("Starting work queue...", ++count);
            try {
                QueueSchedulerTimerTask queueSchedulerTimerTask = new QueueSchedulerTimerTask(
                        idropCore.getTransferManager(), window);
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(queueSchedulerTimerTask, 10000, 120000);
                idropCore.setQueueTimer(timer);
            } catch (IdropException ex) {
                java.util.logging.Logger.getLogger(IDROPSplashWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

            window.dispose();
            return null;
        }

        @Override
        protected void done() {
            idropCore.setIconManager(new IconManager(desktop));

            desktop.createAndShowSystemTray();

            Preferences prefs = idropCore.getPreferences();

            // set some preferences
            boolean showUI = prefs.getBoolean(PREFERENCE_KEY_SHOW_UI, true);
            log.debug("showIU: {}", showUI);
            if (showUI) {
                desktop.showIdropGui();
                desktop.preferencesDialogShowUICheckBox.setSelected(showUI);
                desktop.mainFrame.setVisible(true);
            } else {
                MessageManager.showMessage(desktop.mainFrame,
                        "iDrop has started.\nCheck your system tray to access the iDrop user interface.",
                        "iDrop has started");
            }
            idropCore.getIconManager().setRunningStatus(idropCore.getTransferManager().getRunningStatus());
            idropCore.getIconManager().setErrorStatus(idropCore.getTransferManager().getErrorStatus());
            desktop.togglePauseTransfer.setSelected(desktop.pausedItem.getState());

            prefs.putBoolean(PREFERENCE_KEY_FIRST_TIME_RUN, false);

            super.done();

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        Task t = new Task();
        try {
            t.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param msg
     * @param value
     */
    public void setStatus(final String msg, final int value) {
        startupProgressBar.setString(msg);
        startupProgressBar.setValue(value);
        try {
            Thread.sleep(STARTUP_SEQUENCE_PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            throw new IdropRuntimeException(e);
        }
    }

    /**
     * @return the desktop
     */
    public IDROPDesktop getDesktop() {
        return desktop;
    }

    /**
     * @param desktop
     *            the desktop to set
     */
    public void setDesktop(IDROPDesktop desktop) {
        this.desktop = desktop;
    }

}
