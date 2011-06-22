package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropAlreadyRunningException;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.TransferEngineException;
import org.irods.jargon.transfer.TransferServiceFactoryImpl;
import org.irods.jargon.transfer.engine.ConfigurationService;
import org.slf4j.LoggerFactory;

/**
 * Manage configuration information.  This service will initialize and manage configuration information from iDrop
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropConfigurationServiceImpl implements IdropConfigurationService {

    private final String idropConfigRootDirectoryAbsolutePath;
    private final ConfigurationService configurationService;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IdropConfigurationServiceImpl.class);

    public IdropConfigurationServiceImpl(final String idropConfigRootDirectoryAbsolutePath) throws IdropAlreadyRunningException, IdropException {

        if (idropConfigRootDirectoryAbsolutePath == null) {
            throw new IllegalArgumentException("idropConfigRootDirectoryAbsolutePath is null");
        }

        log.info("getting config service via factory");
        try {
            TransferServiceFactoryImpl transferServiceFactory = new TransferServiceFactoryImpl();
            this.idropConfigRootDirectoryAbsolutePath = idropConfigRootDirectoryAbsolutePath;
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
        try {
            databaseProperties = configurationService.exportProperties();

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
        saveConfigurationToPropertiesFile();
        return databaseProperties;

    }

    /**
     * Save the database configuration information to a properties file
     * @throws IdropException 
     */
    @Override
    public void saveConfigurationToPropertiesFile() throws IdropException {
        log.info("saveConfigurationToPropertiesFile()");
        StringBuilder sb = new StringBuilder(idropConfigRootDirectoryAbsolutePath);
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
            throw new IdropException("exception storing final properties in file", ioe);
        }
    }

    /**
     * In cases where there are no database properties, attempt to import them from a file in the .idrop directory
     * @return
     * @throws IdropException 
     */
    private Properties importPropertiesFromPropertiesFile() throws IdropException {
        log.info("importPropertiesFromPropertiesFile()");
        StringBuilder sb = new StringBuilder(idropConfigRootDirectoryAbsolutePath);
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
}
