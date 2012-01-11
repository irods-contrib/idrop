/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.lite;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.table.DefaultTableModel;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.DataTransferOperationsImpl;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
//import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
//import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class iDropLiteCore {

    private IRODSAccount irodsAccount = null;

    private IRODSFileSystem irodsFileSystem = null;
    
    private DataTransferOperations dataTransferOps = null;
    
    private TransferControlBlock transferControlBlock = null;
    
    private DefaultTableModel tmUploadTable = null;

    public IRODSFileSystem getIrodsFileSystem() {
        return irodsFileSystem;
    }

    public void setIrodsFileSystem(IRODSFileSystem irodsFileSystem) {
        this.irodsFileSystem = irodsFileSystem;
    }

    private IdropConfig idropConfig = null;

 
//    private IconManager iconManager = null;

    private Timer queueTimer = new Timer();

    private Preferences preferences = Preferences.userRoot();

    public IRODSAccount getIrodsAccount() {
        return irodsAccount;
    }

    public void setIrodsAccount(IRODSAccount irodsAccount) {
        this.irodsAccount = irodsAccount;
    }

    public void setIrodsAccount(String sessionID) {
        // FIX ME: need to implement this
    }

    public IdropConfig getIdropConfig() {
        return idropConfig;
    }

    public void setIdropConfig(IdropConfig idropConfig) {
        this.idropConfig = idropConfig;
    }

    public DataTransferOperations getTransferManager() {
        return dataTransferOps;
    }

    public void setTransferManager(DataTransferOperations transferOps) {
        this.dataTransferOps = transferOps;
    }

//    public IconManager getIconManager() {
//        return iconManager;
//    }
//
//    public void setIconManager(IconManager iconManager) {
//        this.iconManager = iconManager;
//    }

//    public Timer getQueueTimer() {
//        return queueTimer;
//    }

//    public void setQueueTimer(Timer queueTimer) {
//        this.queueTimer = queueTimer;
//    }

//    @Deprecated
//    public Preferences getPreferences() {
//        return preferences;
//    }
//
//     @Deprecated
//    public void setPreferences(Preferences preferences) {
//        this.preferences = preferences;
//    }

    /**
     * Handy method that delegates the process of getting an <code>IRODSAccessObjectFactory</code>.
     * @return {@link IRODSAccessObjectFactory}
     */
    public IRODSAccessObjectFactory getIRODSAccessObjectFactory() {
        if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSAccessObjectFactory();
        } catch (JargonException ex) {
            Logger.getLogger(iDropLiteCore.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("exception getting IRODSAccessObjectFactory");
        }
    }

    /**
     * Method to close any iRODS connections in the current thread.   This delegates to the <code>IRODSFileSystem</code>.
     */
    public void closeAllIRODSConnections() {
          if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
       irodsFileSystem.closeAndEatExceptions();
    }

    /**
     * Method to close  iRODS connection denoted by the logged in <code>IRODSAccount</code>.
     */
    public void closeIRODSConnectionForLoggedInAccount() {
          if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
       irodsFileSystem.closeAndEatExceptions(irodsAccount);
    }

    /**
     * Method to close  iRODS connection denoted by the given <code>IRODSAccount</code> in the current thread.   This delegates to the <code>IRODSFileSystem</code>.
     */
    public void closeIRODSConnection(final IRODSAccount irodsAccount) {
          if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
       irodsFileSystem.closeAndEatExceptions(irodsAccount);
    }
    /**
     * Get the <code>IRODSFileFactory</code> for the given account
     * @return {@link IRODSFileFactory} associated with the account currently logged in
     */
    public IRODSFileFactory getIRODSFileFactory(final IRODSAccount irodsAccount) {
          if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
           if (irodsAccount == null) {
            throw new IdropRuntimeException("No IRODSAccount set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSFileFactory(irodsAccount);
        } catch (JargonException ex) {
            Logger.getLogger(iDropLiteCore.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("Exception getting iRODS file factory", ex);
        }

    }

     /**
     * Get the <code>IRODSFileFactory</code> for the current logged-in account.
     * @return {@link IRODSFileFactory} associated with the account currently logged in
     */
    public IRODSFileFactory getIRODSFileFactoryForLoggedInAccount() {
          if (irodsFileSystem == null) {
            throw new IdropRuntimeException("No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
        }
           if (irodsAccount == null) {
            throw new IdropRuntimeException("No IRODSAccount set, cannot obtain the IRODSAccessObjectFactory");
        }
        try {
            return irodsFileSystem.getIRODSFileFactory(irodsAccount);
        } catch (JargonException ex) {
            Logger.getLogger(iDropLiteCore.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropRuntimeException("Exception getting iRODS file factory", ex);
        }

    }
    
    public void setTransferControlBlock(TransferControlBlock tcb) {
    	this.transferControlBlock = tcb;
    }
    
    public TransferControlBlock getTransferControlBlock() {
    	return this.transferControlBlock;
    }
    
    protected void setUploadTableModel(DefaultTableModel tm) {
    	this.tmUploadTable = tm;
    }
    
    public DefaultTableModel getUploadTableModel() {
	   return this.tmUploadTable;
    }
}

