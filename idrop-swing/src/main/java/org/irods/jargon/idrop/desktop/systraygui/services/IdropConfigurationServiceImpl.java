package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.irods.jargon.core.connection.IRODSAccount;

import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.TransferEngineException;
import org.irods.jargon.transfer.TransferServiceFactoryImpl;
import org.irods.jargon.transfer.dao.domain.ConfigurationProperty;
import org.irods.jargon.transfer.dao.domain.SynchConfiguration;
import org.irods.jargon.transfer.dao.domain.Synchronization;
import org.irods.jargon.transfer.engine.ConfigurationService;
import org.irods.jargon.transfer.engine.synch.ConflictingSynchException;
import org.irods.jargon.transfer.engine.synch.SynchException;
import org.irods.jargon.transfer.engine.synch.SynchManagerService;
import org.openide.util.Exceptions;
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
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IdropConfigurationServiceImpl.class);

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
            TransferServiceFactoryImpl transferServiceFactory = new TransferServiceFactoryImpl();
            this.configurationService = transferServiceFactory.instanceConfigurationService();

        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            if (ex.getMessage().indexOf("Failed to start database") != -1) {
                throw new IdropAlreadyRunningException(
                        "iDrop is already running");
            } else {
                throw new IdropException(ex);
            }
        }
    }

    @Override
    public Properties bootstrapConfiguration() throws IdropException {
        log.info("bootstrapConfiguratiion()\nlooking for properties in database");
        Properties databaseProperties;
        Properties configFileProperties;
        try {
                databaseProperties = configurationService.exportProperties();
            configFileProperties = this.importPropertiesFromDefaultFile();

        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
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
            databaseProperties = importPropertiesFromDefaultFile();
        }

        log.info("now storing derived properties in idrop configuration");
        log.info("checking for force mode, which forces certain properties to be loaded from the idrop.properties file");
        /*
         * This is something of a shim right now until config things settle
         * down. For lifetime library, force into login preset mode
         */

        String forceMode = configFileProperties.getProperty(FORCE_MODE);
        if (forceMode != null) {
            boolean isForce = Boolean.valueOf(forceMode);
            log.info("force mode is:{}", isForce);
            if (isForce) {
                log.warn("forcing into login preset mode");
                databaseProperties.setProperty(LOGIN_PRESET, "true");
            }
        }

        /*
         * If the distro has the idrop.properties to force.no.synch=true, then
         * override the force option in effect
         */
        forceMode = configFileProperties.getProperty(FORCE_NO_SYNCH);
        if (forceMode != null) {
            boolean isForce = Boolean.valueOf(forceMode);
            log.info("force no synch mode  mode is:{}", isForce);
            if (isForce) {
                log.warn("forcing into no synch mode");
                databaseProperties.setProperty(SHOW_STARTUP, "false");
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
            Properties databaseProperties = configurationService.exportProperties();
            databaseProperties.store(new FileOutputStream(sb.toString()), null);
        } catch (TransferEngineException ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropException("exception exporting final properties", ex);
        } catch (IOException ioe) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ioe);
            throw new IdropException(
                    "exception storing final properties in file", ioe);
        }
    }

    /**
     * In cases where there are no database properties, attempt to import them
     * from a file in the .idrop directory
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
            } catch (TransferEngineException ex) {
                Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropException(
                        "unable to import properties into database", ex);
            }
        }
    }

    private Properties importPropertiesFromDefaultFile() throws IdropException {
        log.info("importPropertiesFromDefaultFile()");
        IdropPropertiesHelper idropPropertiesHelper = new IdropPropertiesHelper();
        Properties properties = idropPropertiesHelper.loadIdropProperties();
        importGivenPropertiesIntoDatabase(properties);
        return properties;
    }
    
    @Override
    public void saveLogin(final IRODSAccount irodsAccount) throws IdropException {
        log.info("save login");
        if (irodsAccount == null) {
            throw new IllegalArgumentException("null irodsAccount");
        }
        log.info("saving irodsAccount:{}", irodsAccount);
        
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_HOST, irodsAccount.getHost());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_PORT, String.valueOf(irodsAccount.getPort()));
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_RESOURCE, irodsAccount.getDefaultStorageResource());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_ROOT_DIR, irodsAccount.getHomeDirectory());
        updateConfig(IdropConfigurationService.ACCOUNT_CACHE_ZONE, irodsAccount.getZone());
         updateConfig(IdropConfigurationService.ACCOUNT_CACHE_USER_NAME, irodsAccount.getUserName());
        log.info("config updated");
    }

    @Override
    public void updateConfig(final String key, final String value) throws IdropException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null or empty key");
        }
        try {
            ConfigurationProperty configurationProperty = configurationService.findConfigurationServiceByKey(key);

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

            configurationService.updateConfigurationProperty(configurationProperty);
            log.info("database updated...updating property cache");
            idropCore.getIdropConfig().setProperty(key, value);
            log.info("property cache updated");

        } catch (TransferEngineException ex) {
            log.error("exception updating config", ex);
        }
    }
    
    @Override
    public void removeConfigProperty(final String key) throws IdropException {
        log.info("removeConfig()");
          if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("null or empty key");
        }
          log.info("key to remove:{}", key);
        try {
            ConfigurationProperty configurationProperty = configurationService.findConfigurationServiceByKey(key);
            if (configurationProperty == null) {
                log.info("no prop with key, ignore");
                return;
            }
            configurationService.deleteConfigurationProperty(configurationProperty);
            log.info("configuration property is deleted");
            idropCore.getIdropConfig().getIdropProperties().remove(key);
            log.info("property removed");
        } catch (TransferEngineException ex) {
            log.error("exception removing config property");
        }
    }
 
    @Override
    public void createNewSynchronization(final Synchronization synchConfiguration) throws IdropException, ConflictingSynchException {
        log.info("saveSynchronization()");
        if (synchConfiguration == null) {
            throw new IllegalArgumentException("null synchConfiguration");
        }
        log.info("synchConfiguration:{}", synchConfiguration);
        SynchManagerService synchManagerService = idropCore.getTransferManager().getTransferServiceFactory().instanceSynchManagerService();
        try {
            synchManagerService.createNewSynchConfiguration(synchConfiguration);
        } catch (ConflictingSynchException cse) {
            log.error("synch configuration is conflicting:{}", synchConfiguration, cse);
            throw cse;
        } catch (SynchException ex) {
            log.error("error creating synch", ex);
            throw new IdropException("error creating synch", ex);
        }
        
        log.info("synch saved");

    }   
}
