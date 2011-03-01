package org.irods.jargon.idrop.desktop.systraygui;

import java.util.Timer;
import java.util.prefs.Preferences;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.transfer.engine.TransferManager;

public class IDROPCore {

    private IRODSAccount irodsAccount = null;

    private IdropConfig idropConfig = null;

    private TransferManager transferManager = null;

    private IconManager iconManager = null;

    private Timer queueTimer = new Timer();

    private Preferences preferences = Preferences.userRoot();

    public IDROPCore() {
        super();
    }

    public IRODSAccount getIrodsAccount() {
        return irodsAccount;
    }

    public void setIrodsAccount(IRODSAccount irodsAccount) {
        this.irodsAccount = irodsAccount;
    }

    public IdropConfig getIdropConfig() {
        return idropConfig;
    }

    public void setIdropConfig(IdropConfig idropConfig) {
        this.idropConfig = idropConfig;
    }

    public TransferManager getTransferManager() {
        return transferManager;
    }

    public void setTransferManager(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public Timer getQueueTimer() {
        return queueTimer;
    }

    public void setQueueTimer(Timer queueTimer) {
        this.queueTimer = queueTimer;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

}
