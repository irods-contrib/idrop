package org.irods.jargon.idrop.desktop.systraygui.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;

/**
 * Access data about the configuration of Idrop.  This serves as a view to the loaded cache of properties that iDrop consults.  
 * The properties are originally 'bootstrapped' at load time and resolved from various sources to come up with the operative set.
 * This bootstrapping is done by the {@link IdropConfigurationService}.
 * <p/>
 * In normal operation, this config class is queried by iDrop to save database accesses.  When any configuration information is updated, this
 * is through the <code>IdropConfigurationService</code>, which will make necessary database updates, and then update this cache.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropConfig {

    private final Properties idropProperties;
    
    /**
     * Given a key, get the value in the cached properties (this is not going against the config database)
     * @param propKey <code>String</code> with the key of the property
     * @return  <code>String</code> with the resulting value, or <code>null</code> if not found
     */
    public String getPropertyForKey(final String propKey) {
        return idropProperties.getProperty(propKey);
    }
    
    /**
     * General method to set a property in the cached properties (this does not update the config database)
     * @param propKey <code>String</code> with the name of the property, cannot be null
     * @param propValue  <code>String</code> with the value of the property, can be null
     */
    public void setProperty(final String propKey, final String propValue) {
        if (propKey == null) {
            throw new IllegalArgumentException("null propKey");
        }
        idropProperties.put(propKey, propValue);
    }

    public Properties getIdropProperties() {
        return idropProperties;
    }

    /**
     * Initialize this wrapper around properties with the
     * <code>Properties</code> that represent the idrop configuration.
     * 
     * @param properties
     */
    public IdropConfig(final Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("null properties");
        }
        this.idropProperties = properties;
    }

    /**
     * Does iDrop need to display policy-aware features?
     * 
     * @return <code>boolean</code> that will be <code>true</code> if policy
     *         features are displayed.
     */
    public boolean isPolicyAware() {
        boolean policyAware = false;
        String policyAwareValue = idropProperties.getProperty(IdropConfigurationService.POLICY_AWARE_PROPERTY);

        if (policyAwareValue != null && policyAwareValue.equals("true")) {
            policyAware = true;
        }

        return policyAware;

    }

    /**
     * Does iDrop need to display advanced options? Otherwise, a simpler client
     * is presented
     * 
     * @return <code>boolean</code> that will be <code>true</code> if policy
     *         features are displayed.
     */
    public boolean isAdvancedView() {
        boolean advancedView = false;
        String propValue = idropProperties.getProperty(IdropPropertiesHelper.ADVANCED_VIEW_PROPERTY);

        if (propValue != null && propValue.equals("true")) {
            advancedView = true;
        }

        return advancedView;

    }

    public String getTransferDatabaseName() {
        String propValue = idropProperties.getProperty(IdropPropertiesHelper.TRANSFER_DATABASE_NAME);

        if (propValue != null) {
            return propValue;
        } else {
            return "transferDatabase";
        }
    }

    /**
     * Should iDrop display a preset login limited to a user's home directory?
     * 
     * @return
     */
    public boolean isLoginPreset() {
        boolean loginPreset = false;
        String loginPresetValue = idropProperties.getProperty(IdropPropertiesHelper.LOGIN_PRESET);

        if (loginPresetValue != null && loginPresetValue.equals("true")) {
            loginPreset = true;
        }

        return loginPreset;
    }

    /**
     * Should successful transfers be logged to the internal database?
     * 
     * @return
     */
    public boolean isLogSuccessfulTransfers() {
        boolean logSuccessful = false;
        String logSuccessfulTransfers = idropProperties.getProperty(IdropConfigurationService.TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES);

        if (logSuccessfulTransfers != null
                && logSuccessfulTransfers.equals("true")) {
            logSuccessful = true;
        }

        return logSuccessful;
    }
    
    /**
     * Should a checksum be created and verifed during get/put transfers?
     * @return 
     */
       public boolean isVerifyChecksum() {
        boolean verify = false;
        String verifyChecksumValue = idropProperties.getProperty(IdropConfigurationService.VERIFY_CHECKSUM_ON_TRANSFER);

        if (verifyChecksumValue != null
                && verifyChecksumValue.equals("true")) {
            verify = true;
        }

        return verify;
    }


    /**
     * Get the configured synch device name. If not set, this will return a
     * <code>null</code>
     * 
     * @return
     */
    public String getSynchDeviceName() {
        return idropProperties.getProperty(IdropConfigurationService.DEVICE_NAME);
    }

    public boolean isShowStartupWizard() {
        boolean showWizard = false;
        String showStartup = idropProperties.getProperty(IdropConfigurationService.SHOW_STARTUP);

        if (showStartup != null && showStartup.equals("true")) {
            showWizard = true;
        }

        return showWizard;

    }

    /**
     * Should I have a rolling log in the user dir? Will return null of no
     * logging desired, otherwise, will return a log level
     * 
     * @return
     */
    public String getLogLevelForRollingLog() {
        String propValue = idropProperties.getProperty(IdropPropertiesHelper.ROLLING_LOG_LEVEL);
        return propValue;

    }

    public void setUpLogging() {
        String rollingLogLevel = getLogLevelForRollingLog();

        if (rollingLogLevel == null) {
            return;
        }

        // log level is specified, set up a rolling logger

        String userHomeDirectory = System.getProperty("user.home");
        StringBuilder sb = new StringBuilder();
        sb.append(userHomeDirectory);
        sb.append("/.idrop/idrop.log");

        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        if (rollingLogLevel.equalsIgnoreCase("INFO")) {
            rootLogger.setLevel(Level.INFO);
        } else if (rollingLogLevel.equalsIgnoreCase("DEBUG")) {
            rootLogger.setLevel(Level.DEBUG);
        } else if (rollingLogLevel.equalsIgnoreCase("WARN")) {
            rootLogger.setLevel(Level.WARN);
        } else {
            rootLogger.setLevel(Level.ERROR);
        }

        PatternLayout layout = new PatternLayout(
                "%d %-4r [%t] %-5p %c %x - %m%n");

        try {
            RollingFileAppender rfa = new RollingFileAppender(layout,
                    sb.toString());
            rfa.setMaximumFileSize(1000000);
            rootLogger.addAppender(rfa);
        } catch (IOException e) {
            // e.printStackTrace();
        }

    }

    public boolean isShowGuiAtStartup() {
        boolean propBoolean = false;
        String propString = idropProperties.getProperty(IdropConfigurationService.SHOW_GUI);

        if (propString != null && propString.equals("true")) {
            propBoolean = true;
        }

        return propBoolean;
    }
}
