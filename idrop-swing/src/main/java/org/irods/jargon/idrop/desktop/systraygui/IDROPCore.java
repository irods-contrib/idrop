package org.irods.jargon.idrop.desktop.systraygui;

import java.text.DateFormat;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.engine.TransferManager;
import org.slf4j.LoggerFactory;

public class IDROPCore {

    private IRODSAccount irodsAccount = null;
    private IRODSFileSystem irodsFileSystem = null;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IDROPCore.class);

    public IRODSFileSystem getIrodsFileSystem() {
        return irodsFileSystem;
    }

    public void setIrodsFileSystem(final IRODSFileSystem irodsFileSystem) {
        this.irodsFileSystem = irodsFileSystem;
    }
    private IdropConfig idropConfig = null;
    private TransferManager transferManager = null;
    private IconManager iconManager = null;
    private Timer queueTimer = new Timer();
    private IdropConfigurationService idropConfigurationService = null;
     private final DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public IdropConfigurationService getIdropConfigurationService() {
        return idropConfigurationService;
    }

    public void setIdropConfigurationService(IdropConfigurationService idropConfigurationService) {
        this.idropConfigurationService = idropConfigurationService;
    }

    public IDROPCore() {
        super();
    }

    public IRODSAccount getIrodsAccount() {
        return irodsAccount;
    }

    public void setIrodsAccount(final IRODSAccount irodsAccount) {
        this.irodsAccount = irodsAccount;
    }

    public IdropConfig getIdropConfig() {
        return idropConfig;
    }

    public void setIdropConfig(final IdropConfig idropConfig) {
        this.idropConfig = idropConfig;
    }

    public TransferManager getTransferManager() {
        return transferManager;
    }

    public void setTransferManager(final TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public void setIconManager(final IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public Timer getQueueTimer() {
        return queueTimer;
    }

    public void setQueueTimer(final Timer queueTimer) {
        this.queueTimer = queueTimer;
    }

    /**
     * Handy method that delegates the process of getting an
     * <code>IRODSAccessObjectFactory</code>.
     * 
     * @return {@link IRODSAccessObjectFactory}
     */
    public IRODSAccessObjectFactory getIRODSAccessObjectFactory() {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSAccessObjectFactory();
        } catch (JargonException ex) {
            Logger.getLogger(IDROPCore.class.getName()).log(Level.SEVERE, null,
                    ex);
            throw new IdropRuntimeException(
                    "exception getting IRODSAccessObjectFactory");
        }
    }

    /**
     * Method to close any iRODS connections in the current thread. This
     * delegates to the <code>IRODSFileSystem</code>.
     */
    public void closeAllIRODSConnections() {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        irodsFileSystem.closeAndEatExceptions();
    }

    /**
     * Method to close iRODS connection denoted by the logged in
     * <code>IRODSAccount</code>.
     */
    public void closeIRODSConnectionForLoggedInAccount() {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        irodsFileSystem.closeAndEatExceptions(irodsAccount);
    }

    /**
     * Method to close iRODS connection denoted by the given
     * <code>IRODSAccount</code> in the current thread. This delegates to the
     * <code>IRODSFileSystem</code>.
     */
    public void closeIRODSConnection(final IRODSAccount irodsAccount) {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        irodsFileSystem.closeAndEatExceptions(irodsAccount);
    }

    /**
     * Get the <code>IRODSFileFactory</code> for the given account
     * 
     * @return {@link IRODSFileFactory} associated with the account currently
     *         logged in
     */
    public IRODSFileFactory getIRODSFileFactory(final IRODSAccount irodsAccount) {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        if (irodsAccount == null) {
            throw new IdropRuntimeException(
                    "No IRODSAccount set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSFileFactory(irodsAccount);
        } catch (JargonException ex) {
            Logger.getLogger(IDROPCore.class.getName()).log(Level.SEVERE, null,
                    ex);
            throw new IdropRuntimeException(
                    "Exception getting iRODS file factory", ex);
        }

    }

    /**
     * Get the <code>IRODSFileFactory</code> for the current logged-in account.
     * 
     * @return {@link IRODSFileFactory} associated with the account currently
     *         logged in
     */
    public IRODSFileFactory getIRODSFileFactoryForLoggedInAccount() {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException(
                    "No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        if (irodsAccount == null) {
            throw new IdropRuntimeException(
                    "No IRODSAccount set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSFileFactory(irodsAccount);
        } catch (JargonException ex) {
            Logger.getLogger(IDROPCore.class.getName()).log(Level.SEVERE, null,
                    ex);
            throw new IdropRuntimeException(
                    "Exception getting iRODS file factory", ex);
        }

    }

    /**
     * Based on the configuration, get the default directory used in iDrop based on the current login
     * @return 
     */
    public IRODSFile getDefaultDirectory() {
        String root = null;
        if (getIdropConfig().isLoginPreset()) {
            log.info("using policy preset home directory");
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(getIrodsAccount().getZone());
            sb.append("/");
            sb.append("home");
            root = sb.toString();
        } else {
            log.info("using root path, no login preset");
            root = "/";
        }
        IRODSFile newFile;
        try {
            newFile = getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(root);
            return newFile;
        } catch (Exception ex) {
            log.error("error creating file", ex);
            throw new IdropRuntimeException("error creating file", ex);
        } finally {
            this.closeIRODSConnectionForLoggedInAccount();
        }

    }
}
