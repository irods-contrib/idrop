/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.services;

import java.util.Properties;
import org.irods.jargon.core.connection.IRODSAccount;

import org.irods.jargon.idrop.exceptions.IdropException;

/**
 * 
 * @author mikeconway
 */
public interface IdropConfigurationService {

    public final String IDROP_PROPS_FILE_NAME = "idrop.properties";
    public final String FORCE_MODE = "force.mode";
    public final String FORCE_NO_SYNCH = "force.no.synch";
    public final String LOGIN_PRESET = "login.preset";
    public final String SHOW_STARTUP = "show.startup";
    public final String SHOW_GUI = "idrop.show.gui";
    // account info for cache
    public final String ACCOUNT_CACHE_HOST = "irods.account.host";
    public final String ACCOUNT_CACHE_PORT = "irods.account.port";
    public final String ACCOUNT_CACHE_ZONE = "irods.account.zone";
    public final String ACCOUNT_CACHE_RESOURCE = "irods.account.resource";
    public final String ACCOUNT_CACHE_USER_NAME = "irods.account.user";
    public final String ACCOUNT_CACHE_ROOT_DIR = "irods.account.root.dir";

    Properties bootstrapConfiguration() throws IdropException;

    /**
     * Save the database configuration information to a properties file
     * 
     * @throws IdropException
     */
    void saveConfigurationToPropertiesFile() throws IdropException;

    /**
     * Update the configuration database, and update the cached configs, with the given information
     * @param key
     * @param value
     * @throws IdropException 
     */
    void updateConfig(final String key, final String value) throws IdropException;

    /**
     * Save a given iRODS account as a cached last login in the configuration database
     * @param irodsAccount {@link IRODSAccount} to be cached
     * @throws IdropException 
     */
    void saveLogin(final IRODSAccount irodsAccount) throws IdropException;
}
