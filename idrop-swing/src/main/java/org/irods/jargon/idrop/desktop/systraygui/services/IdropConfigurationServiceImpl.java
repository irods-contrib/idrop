package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.TransferEngineException;
import org.irods.jargon.transfer.TransferServiceFactoryImpl;
import org.irods.jargon.transfer.engine.ConfigurationService;
import org.slf4j.LoggerFactory;

/**
 * Manage configuration information. This service will initialize and manage configuration information from iDrop
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropConfigurationServiceImpl implements IdropConfigurationService {

    private final File propertyFile;

    private final ConfigurationService configurationService;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IdropConfigurationServiceImpl.class);

    public IdropConfigurationServiceImpl(final File propertyFile) throws IdropAlreadyRunningException, IdropException {

        if (propertyFile == null) {
            throw new IllegalArgumentException("propertyFile is null");
        }

        log.info("getting config service via factory");
        try {
            TransferServiceFactoryImpl transferServiceFactory = new TransferServiceFactoryImpl();
            this.propertyFile = propertyFile;
            this.configurationService = transferServiceFactory.instanceConfigurationService();

        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            if (ex.getMessage().indexOf("Failed to start database") != -1) {
                throw new IdropAlreadyRunningException("iDrop is already running");
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
            configFileProperties.putAll(getProperties());
        } catch (Exception ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            if (ex.getMessage().indexOf("Could not open Hibernate Session") != -1) {
                throw new IdropAlreadyRunningException("iDrop is already running, or the iDrop database is in use");
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

        databaseProperties.putAll(configFileProperties);

        /*
         * This is something of a shim right now until config things settle down. For lifetime library, force into login
         * preset mode
         */

        String forceMode = databaseProperties.getProperty(FORCE_MODE);
        if (forceMode != null) {
            boolean isForce = Boolean.valueOf(forceMode);
            log.info("force mode is:{}", isForce);
            if (isForce) {
                log.warn("forcing into login preset mode");
                databaseProperties.setProperty(LOGIN_PRESET, "true");
            }
        }

        log.info("checking for force mode, which forces certain properties to be loaded from the idrop.properties file");

        importGivenPropertiesIntoDatabase(databaseProperties);
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
        try {
            Properties databaseProperties = configurationService.exportProperties();
            databaseProperties.store(new FileOutputStream(propertyFile), null);
        } catch (TransferEngineException ex) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new IdropException("exception exporting final properties", ex);
        } catch (IOException ioe) {
            Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ioe);
            throw new IdropException("exception storing final properties in file", ioe);
        }
    }

    /**
     * In cases where there are no database properties, attempt to import them from a file in the .idrop directory
     * 
     * @return
     * @throws IdropException
     */
    private Properties importPropertiesFromPropertiesFile() throws IdropException {
        log.info("importPropertiesFromPropertiesFile()");
        Properties properties = getProperties();
        importGivenPropertiesIntoDatabase(properties);
        return properties;
    }

    private Properties getProperties() throws IdropException {
        log.debug("getProperties()");
        Properties properties = new Properties();
        try {
            if (!this.propertyFile.exists()) {
                FileUtils.touch(this.propertyFile);
            }
            FileInputStream fis = new FileInputStream(this.propertyFile);
            properties.load(fis);
            fis.close();
        } catch (IOException ex) {
            log.warn("idrop properties not found");
        }
        return properties;
    }

    private void importGivenPropertiesIntoDatabase(Properties properties) throws IdropException {
        if (!properties.isEmpty()) {
            log.info("some properties were located, importing into the database");
            log.debug("props from file:{}", properties);
            try {
                configurationService.importProperties(properties);
            } catch (TransferEngineException ex) {
                Logger.getLogger(IdropConfigurationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropException("unable to import properties into database", ex);
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

    private Properties importPropertiesFromDefaultFile(Properties props) throws IdropException {
        log.info("importPropertiesFromDefaultFile()");
        IdropPropertiesHelper idropPropertiesHelper = new IdropPropertiesHelper();
        Properties properties = idropPropertiesHelper.loadIdropProperties();

        importGivenPropertiesIntoDatabase(properties);
        return properties;
    }

}
