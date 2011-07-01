/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.services;

import java.util.Properties;

import org.irods.jargon.idrop.exceptions.IdropException;

/**
 * 
 * @author mikeconway
 */
public interface IdropConfigurationService {
    String IDROP_PROPS_FILE_NAME = "idrop.properties";
    String FORCE_MODE = "force.mode";
    String LOGIN_PRESET = "login.preset";

    Properties bootstrapConfiguration() throws IdropException;

    /**
     * Save the database configuration information to a properties file
     * 
     * @throws IdropException
     */
    void saveConfigurationToPropertiesFile() throws IdropException;

}
