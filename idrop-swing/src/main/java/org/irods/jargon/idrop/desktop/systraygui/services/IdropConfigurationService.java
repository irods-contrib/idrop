package org.irods.jargon.idrop.desktop.systraygui.services;

import java.util.Properties;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.JargonProperties;
import org.irods.jargon.core.exception.JargonException;

import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.engine.synch.ConflictingSynchException;

/**
 * 
 * @author mikeconway
 */
public interface IdropConfigurationService  {

    public static final String IDROP_PROPS_FILE_NAME = "idrop.properties";
    public static final String FORCE_MODE = "force.mode";
    public static final String FORCE_NO_SYNCH = "force.no.synch";
    public static final String LOGIN_PRESET = "login.preset";
    public static final String SHOW_STARTUP = "show.startup";
    public static final String SHOW_GUI = "idrop.show.gui";
    public static final String DEVICE_NAME = "idrop.device.name";
    // account info for cache
    public static final String ACCOUNT_CACHE_HOST = "irods.account.host";
    public static final String ACCOUNT_CACHE_PORT = "irods.account.port";
    public static final String ACCOUNT_CACHE_ZONE = "irods.account.zone";
    public static final String ACCOUNT_CACHE_RESOURCE = "irods.account.resource";
    public static final String ACCOUNT_CACHE_USER_NAME = "irods.account.user";
    public static final String ACCOUNT_CACHE_ROOT_DIR = "irods.account.root.dir";
    public static final String POLICY_AWARE_PROPERTY = "policy.aware";
    public static final String LOOK_AND_FEEL = "idrop.lookandfeel";
    public static final String TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES = "transferengine.record.successful.files";
    public static final String  VERIFY_CHECKSUM_ON_TRANSFER = "transfer.computeandvalidate.checksum";
    public static final String  ALLOW_CONNECTION_REROUTING = "transfer.allow.redirects";
    public static final String  INTRA_FILE_STATUS_CALLBACKS = "transfer.intra.file.callbacks";
    public static final String  IRODS_CONNECTION_TIMEOUT = "socket.timeout";
    public static final String  IRODS_PARALLEL_CONNECTION_TIMEOUT = "parallel.socket.timeout";
    public static final String  IRODS_PARALLEL_CONNECTION_MAX_THREADS = "transfer.max.parallel.threads";
    public static final String  IRODS_PARALLEL_USE_PARALLEL = "transfer.use.parallel";
      public static final String  IRODS_PARALLEL_USE_NIO = "transfer.use.nio.for.parallel";
    public static final String  IRODS_PARALLEL_USE_POOL = "transfer.use.pool";
    public static final String IRODS_IO_INTERNAL_INPUT_STREAM_BUFFER_SIZE = "jargon.io.internal.input.stream.buffer.size";
    public static final String IRODS_IO_INTERNAL_OUTPUT_STREAM_BUFFER_SIZE = "jargon.io.internal.output.stream.buffer.size";
    public static final String IRODS_IO_INTERNAL_CACHE_BUFFER_SIZE = "jargon.io.internal.cache.buffer.size";
    public static final String IRODS_IO_SEND_INPUT_STREAM_BUFFER_SIZE = "jargon.io.send.input.stream.buffer.size";
    public static final String IRODS_IO_INPUT_TO_OUTPUT_COPY_BUFFER_SIZE = "jargon.io.input.to.output.copy.byte.buffer.size";
    public static final String IRODS_IO_LOCAL_INPUT_STREAM_BUFFER_SIZE = "jargon.io.local.input.stream.buffer.size";
    public static final String IRODS_IO_LOCAL_OUTPUT_STREAM_BUFFER_SIZE = "jargon.io.local.output.stream.buffer.size";
    public static final String IRODS_IO_PUT_BUFFER_SIZE = "jargon.put.buffer.size";
     public static final String IRODS_IO_GET_BUFFER_SIZE = "jargon.get.buffer.size";
    
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

    void removeConfigProperty(final String key) throws IdropException;

    /**
     * Create a new synchronization configuration, checking for conflicts and properly configuring both local and iRODS configuration
     * @param synchConfiguration {@link Synchronization}
     * @throws IdropException
     * @throws ConflictingSynchException 
     */
    void createNewSynchronization(final Synchronization synchConfiguration) throws IdropException, ConflictingSynchException;

    void updateSynchronization(final Synchronization synchConfiguration) throws IdropException, ConflictingSynchException;

    /**
     * Cause the transfer options using in the transfer engine to be updated
     * @throws JargonException
     */
    void updateTransferOptions() throws JargonException;

    void updateJargonPropertiesBasedOnIDROPConfig() throws JargonException;

    void pushIDROPConfigToJargonAndTransfer() throws IdropException;

    /**
     * Set the idrop configuration to values defined in the jargon properties file in the classpath.  These should contain
     * (hopefully) sensible defaults
     * @param jargonProperties
     * @throws JargonException 
     */
    void restoreIDROPConfigFromJargonProperties(final JargonProperties jargonProperties) throws JargonException;
}
