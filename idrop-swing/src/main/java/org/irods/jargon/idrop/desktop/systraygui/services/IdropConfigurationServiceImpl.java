package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.irods.jargon.conveyor.basic.BasicConveyorBootstrapperImpl;
import org.irods.jargon.conveyor.basic.ConveyorBootstrapConfiguration;
import org.irods.jargon.conveyor.core.ConfigurationService;
import org.irods.jargon.conveyor.core.ConveyorBootstrapper;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.ConveyorService;
import org.irods.jargon.core.connection.ClientServerNegotiationPolicy;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.JargonProperties;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import static org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService.IDROP_PROPS_FILE_NAME;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.ConfigurationProperty;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.slf4j.LoggerFactory;

/**
 * Manage configuration information. This service will initialize and manage
 * configuration information from iDrop
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropConfigurationServiceImpl implements IdropConfigurationService {

    private final String idropConfigRootDirectoryAbsolutePath;
    private final ConfigurationService configurationService;
    private final IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(IdropConfigurationServiceImpl.class);

    public IdropConfigurationServiceImpl(
            final String idropConfigRootDirectoryAbsolutePath,
            final IDROPCore idropCore) throws IdropAlreadyRunningException,
            IdropException {

        if (idropConfigRootDirectoryAbsolutePath == null) {
            throw new IllegalArgumentException(
                    "idropConfigRootDirectoryAbsolutePath is null");
        }

        if (idropCore == null) {
            throw new IllegalArgumentException("idropCore is null");
        }

        this.idropConfigRootDirectoryAbsolutePath = idropConfigRootDirectoryAbsolutePath;
        this.idropCore = idropCore;

        log.info("getting config service via factory");
        try {

            ConveyorBootstrapConfiguration conveyorBootstrapConfiguration = new ConveyorBootstrapConfiguration();
            ConveyorBootstrapper conveyorBootstrapper = new BasicConveyorBootstrapperImpl(
                    conveyorBootstrapConfiguration);
            ConveyorService conveyorService = conveyorBootstrapper
                    .bootstrap(idropCore.getIRODSAccessObjectFactory());
            configurationService = conveyorService.getConfigurationService();
            idropCore.setConveyorService(conveyorService);

        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName())
                    .log(Level.SEVERE, null, ex);

            if (ex.getMessage().indexOf("Failed to start database") != -1) {
                throw new IdropAlreadyRunningException(
                        "iDrop is already running");
            } else {
                throw new IdropException(ex);
            }
        }
    }

    @Override
    public Properties bootstrapConfigurationAndMergePropertiesFromLocalAndClasspath()
            throws IdropException {
        log.info("bootstrapConfiguratiion()\nlooking for properties in database");
        Properties databaseProperties;
        Properties configFileProperties;
        try {
            databaseProperties = configurationService.exportProperties();
            configFileProperties = importPropertiesFromDefaultFile(false);

        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName())
                    .log(Level.SEVERE, null, ex);
            if (ex.getMessage().indexOf("Could not open Hibernate Session") != -1) {
                throw new IdropAlreadyRunningException(
                        "iDrop is already running, or the iDrop database is in use");
            } else {
                throw new IdropException(ex);
            }

        }
        log.debug("properties from database:{}", databaseProperties);

        if (databaseProperties.isEmpty()) {
            log.info("no properties found in database, attempt to import from idrop.properties in .idrop home dir");
            databaseProperties = importPropertiesFromPropertiesFile();
        }

        if (databaseProperties.isEmpty()) {
            log.info("no properties found in properties file in home directory, attempt to import default idrop.properties from classpath properties");
            databaseProperties = importPropertiesFromDefaultFile(true);
        }

        Properties overrideTearOffProperties = new Properties();


        if (configFileProperties.getProperty("login.preset") != null) {
            overrideTearOffProperties.setProperty("login.preset", configFileProperties.getProperty("login.preset"));
        }

        if (configFileProperties.getProperty("login.preset.host") != null) {
            overrideTearOffProperties.setProperty("login.preset.host", configFileProperties.getProperty("login.preset.host"));
        }

        if (configFileProperties.getProperty("login.preset.port") != null) {
            overrideTearOffProperties.setProperty("login.preset.port", configFileProperties.getProperty("login.preset.port"));
        }

        if (configFileProperties.getProperty("login.preset.zone") != null) {
            overrideTearOffProperties.setProperty("login.preset.zone", configFileProperties.getProperty("login.preset.zone"));
        }

        if (configFileProperties.getProperty("login.preset.resource") != null) {
            overrideTearOffProperties.setProperty("login.preset.resource", configFileProperties.getProperty("login.preset.resource"));
        }

        if (configFileProperties.getProperty("login.preset.authscheme") != null) {
            overrideTearOffProperties.setProperty("login.preset.authscheme", configFileProperties.getProperty("login.preset.authscheme"));
        }

        if (configFileProperties.getProperty("tear.off.mode") != null) {
            overrideTearOffProperties.setProperty("tear.off.mode", configFileProperties.getProperty("tear.off.mode"));
        }

        importGivenPropertiesIntoDatabase(overrideTearOffProperties);

        log.info("now storing derived properties in idrop configuration");


        /*
         * Bring over anything in the configuration file that is not stored n
         * the database properties file
         */

        Set<Object> configPropKeys = configFileProperties.keySet();

        for (Object configPropKey : configPropKeys) {
            if (databaseProperties.get(configPropKey) == null) {
                log.info(
                        "propogating config file prop to database, as not currently set:{}",
                        configPropKey);
                databaseProperties.put(configPropKey,
                        configFileProperties.get(configPropKey));
            }
        }

        saveConfigurationToPropertiesFile();
        return databaseProperties;

    }

    /**
     * Save the database configuration information to a properties file
     *
     * @throws IdropException
     */
    @Override
    public void saveConfigurationToPropertiesFile() throws IdropException {
        log.info("saveConfigurationToPropertiesFile()");
        StringBuilder sb = new StringBuilder(
                idropConfigRootDirectoryAbsolutePath);
        sb.append("/");
        sb.append(IDROP_PROPS_FILE_NAME);

        try {
            Properties databaseProperties = configurationService
                    .exportProperties();
            databaseProperties.store(new FileOutputStream(sb.toString()), null);
        } catch (ConveyorExecutionException ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName())
                    .log(Level.SEVERE, null, ex);
            throw new IdropException("exception exporting final properties", ex);
        } catch (IOException ioe) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName())
                    .log(Level.SEVERE, null, ioe);
            throw new IdropException(
                    "exception storing final properties in file", ioe);
        }
    }

    /**
     * In cases where there are no database properties, attempt to import them
     * from a file in the .idrop directory In cases where there are no database
     * properties, attempt to import them from a file in the .idrop directory
     *
     * @return
     * @throws IdropException
     */
    private Properties importPropertiesFromPropertiesFile()
            throws IdropException {
        log.info("importPropertiesFromPropertiesFile()");
        StringBuilder sb = new StringBuilder(
                idropConfigRootDirectoryAbsolutePath);
        sb.append("/");
        sb.append(IDROP_PROPS_FILE_NAME);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(sb.toString()));
            importGivenPropertiesIntoDatabase(properties);
        } catch (IOException ex) {
            log.warn("idrop properties not found");
        }

        return properties;

    }

    private void importGivenPropertiesIntoDatabase(final Properties properties)
            throws IdropException {
        if (!properties.isEmpty()) {
            log.info("some properties were located, importing into the database");
            log.debug("props from file:{}", properties);
            try {
                configurationService.importProperties(properties);
            } catch (ConveyorExecutionException ex) {
                Logger.getLogger(IdropConfigurationServiceImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                throw new IdropException(
                        "unable to import properties into database", ex);
            }
        }
    }

    private Properties importPropertiesFromDefaultFile(
            final boolean exportToDatabase) throws IdropException {
        log.info("importPropertiesFromDefaultFile()");
        IdropPropertiesHelper idropPropertiesHelper = new IdropPropertiesHelper();
        Properties properties = idropPropertiesHelper.loadIdropProperties();

        if (exportToDatabase) {
            importGivenPropertiesIntoDatabase(properties);
        }

        return properties;
    }

    @Override
    public void saveLogin(final IRODSAccount irodsAccount)
            throws IdropException {
        log.info("save login");
        if (irodsAccount == null) {
            throw new IllegalArgumentException("null irodsAccount");
        }
        log.info("saving irodsAccount:{}", irodsAccount);

        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_HOST,
                irodsAccount.getHost());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_PORT,
                String.valueOf(irodsAccount.getPort()));
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_RESOURCE,
                irodsAccount.getDefaultStorageResource());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_ROOT_DIR,
                irodsAccount.getHomeDirectory());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_ZONE,
                irodsAccount.getZone());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_USER_NAME,
                irodsAccount.getUserName());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_LOGIN_MODE,
                irodsAccount.getAuthenticationScheme().name());
        log.info("config updated");
    }

    @Override
    public void updateConfig(final String key, final String value)
            throws IdropException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null or empty key");
        }

        synchronized (this) {
            try {
                ConfigurationProperty configurationProperty = configurationService
                        .findConfigurationPropertyByKey(key);

                if (configurationProperty == null) {
                    log.info("not found, this is new configuration");
                    configurationProperty = new ConfigurationProperty();
                    configurationProperty.setPropertyKey(key);
                    configurationProperty.setPropertyValue(value);
                    configurationProperty.setCreatedAt(new Date());
                } else {
                    log.info("found config property:{}", configurationProperty);
                    configurationProperty.setPropertyValue(value);
                    configurationProperty.setUpdatedAt(new Date());
                }

                configurationService
                        .updateConfigurationProperty(configurationProperty);
                log.info("database updated...updating property cache");
                idropCore.getIdropConfig().setProperty(key, value);
                log.info("property cache updated");

            } catch (Exception ex) {
                log.error("exception removing config property");
                throw new IdropException("exception updating config", ex);
            }
        }
    }

    /**
     * Cause the transfer options using in the transfer engine to be updated
     *
     * @throws JargonException
     */
    @Override
    public synchronized void updateTransferOptions() throws JargonException {
        /*
         * The transfer manager may not have been built the first time this is
         * invoked
         */
        // FIXME: conveyor
		/*
         * if (idropCore.getTransferManager() != null) {
         * idropCore.getTransferManager
         * ().getTransferEngineConfigurationProperties
         * ().setLogSuccessfulTransfers
         * (idropCore.getIdropConfig().isLogSuccessfulTransfers()); }
         */
    }

    @Override
    public void restoreIDROPConfigFromJargonProperties(
            final JargonProperties jargonProperties) throws JargonException {
        if (jargonProperties == null) {
            throw new IllegalArgumentException("null jargonProperties");
        }

        synchronized (this) {
            IdropConfig idropConfig = idropCore.getIdropConfig();
            idropConfig.setProperty(
                    IdropConfigurationService.IRODS_CONNECTION_TIMEOUT,
                    String.valueOf(jargonProperties.getIRODSSocketTimeout()));
            idropConfig.setProperty(
                    IdropConfigurationService.IRODS_IO_GET_BUFFER_SIZE,
                    String.valueOf(jargonProperties.getGetBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_INPUT_TO_OUTPUT_COPY_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getInputToOutputCopyBufferByteSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_INTERNAL_CACHE_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getInternalCacheBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_INTERNAL_INPUT_STREAM_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getInternalInputStreamBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_INTERNAL_OUTPUT_STREAM_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getInternalOutputStreamBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_LOCAL_INPUT_STREAM_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getLocalFileInputStreamBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_LOCAL_OUTPUT_STREAM_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getLocalFileOutputStreamBufferSize()));
            idropConfig.setProperty(
                    IdropConfigurationService.IRODS_IO_PUT_BUFFER_SIZE,
                    String.valueOf(jargonProperties.getPutBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_IO_SEND_INPUT_STREAM_BUFFER_SIZE,
                    String.valueOf(jargonProperties
                    .getSendInputStreamBufferSize()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_PARALLEL_CONNECTION_MAX_THREADS,
                    String.valueOf(jargonProperties
                    .getMaxParallelThreads()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_PARALLEL_CONNECTION_TIMEOUT,
                    String.valueOf(jargonProperties
                    .getIRODSParallelTransferSocketTimeout()));
          
            idropConfig.setProperty(
                    IdropConfigurationService.IRODS_PARALLEL_USE_PARALLEL,
                    String.valueOf(jargonProperties.isUseParallelTransfer()));
            idropConfig
                    .setProperty(
                    IdropConfigurationService.IRODS_PARALLEL_USE_POOL,
                    String.valueOf(jargonProperties
                    .isUseTransferThreadsPool()));
            idropConfig.setProperty(
                    IdropConfigurationService.IRODS_CONNECTION_RESTART,
                    String.valueOf(jargonProperties.isReconnect()));
        }
    }

    @Override
    public void updateJargonPropertiesBasedOnIDROPConfig()
            throws JargonException {
        JargonProperties props = idropCore.getIrodsFileSystem()
                .getIrodsSession().getJargonProperties();
        SettableJargonProperties newProps = new SettableJargonProperties(props);
        synchronized (this) {
            newProps.setComputeAndVerifyChecksumAfterTransfer(idropCore
                    .getIdropConfig().isVerifyChecksum());
            newProps.setIntraFileStatusCallbacks(idropCore.getIdropConfig()
                    .isIntraFileStatusCallbacks());
            newProps.setTransferThreadPoolMaxSimultaneousTransfers(1);
            newProps.setUseTransferThreadsPool(idropCore.getIdropConfig()
                    .isParallelUsePool());
            newProps.setIrodsSocketTimeout(idropCore.getIdropConfig()
                    .getIrodsConnectionTimeout());
            newProps.setIrodsParallelSocketTimeout(idropCore.getIdropConfig()
                    .getIrodsParallelConnectionTimeout());
            newProps.setAllowPutGetResourceRedirects(idropCore.getIdropConfig()
                    .isAllowConnectionRerouting());
            newProps.setMaxParallelThreads(idropCore.getIdropConfig()
                    .getIrodsParallelTransferMaxThreads());
            newProps.setGetBufferSize(idropCore.getIdropConfig()
                    .getGetBufferSize());
            newProps.setInputToOutputCopyBufferByteSize(idropCore
                    .getIdropConfig().getInputToOutputCopyBufferByteSize());
            newProps.setInternalCacheBufferSize(idropCore.getIdropConfig()
                    .getInternalCacheBufferSize());
            newProps.setInternalInputStreamBufferSize(idropCore
                    .getIdropConfig().getInternalInputStreamBufferSize());
            newProps.setInternalOutputStreamBufferSize(idropCore
                    .getIdropConfig().getInternalOutputStreamBufferSize());
            newProps.setLocalFileInputStreamBufferSize(idropCore
                    .getIdropConfig().getLocalFileInputStreamBufferSize());
            newProps.setLocalFileOutputStreamBufferSize(idropCore
                    .getIdropConfig().getLocalFileOutputStreamBufferSize());
            newProps.setPutBufferSize(idropCore.getIdropConfig()
                    .getPutBufferSize());
            newProps.setSendInputStreamBufferSize(idropCore.getIdropConfig()
                    .getSendInputStreamBufferSize());
            newProps.setUseParallelTransfer(idropCore.getIdropConfig()
                    .isUseParallelTransfers());
            newProps.setNegotiationPolicy(ClientServerNegotiationPolicy.SslNegotiationPolicy.CS_NEG_REFUSE);  // FIXME: shim - mcc
           

            /*
             * Default to handling of strict acls (assume public dir and user
             * home paths)
             */

            newProps.setDefaultToPublicIfNothingUnderRootWhenListing(true);
            // newProps.setReconnect(idropCore.getIdropConfig().isConnectionRestart());

            idropCore.getIrodsFileSystem().getIrodsSession()
                    .setJargonProperties(newProps);
        }
    }

    @Override
    public void removeConfigProperty(final String key) throws IdropException {
        log.info("removeConfig()");
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null or empty key");
        }
        log.info("key to remove:{}", key);
        synchronized (this) {
            try {
                ConfigurationProperty configurationProperty = configurationService
                        .findConfigurationPropertyByKey(key);
                if (configurationProperty == null) {
                    log.info("no prop with key, ignore");
                    return;
                }
                configurationService
                        .deleteConfigurationProperty(configurationProperty);
                log.info("configuration property is deleted");
                idropCore.getIdropConfig().getIdropProperties().remove(key);
                log.info("property removed");
                updateTransferOptions();
            } catch (Exception ex) {
                log.error("exception removing config property");
                throw new IdropRuntimeException("exception updating config", ex);
            }
        }
    }

   @Override
	public synchronized void pushIDROPConfigToJargonAndTransfer()
			throws IdropException {
		try {
			updateTransferOptions();
			updateJargonPropertiesBasedOnIDROPConfig();
		} catch (Exception ex) {
			log.error("exception removing config property");
			throw new IdropException("exception updating config", ex);
		}
	}

}
