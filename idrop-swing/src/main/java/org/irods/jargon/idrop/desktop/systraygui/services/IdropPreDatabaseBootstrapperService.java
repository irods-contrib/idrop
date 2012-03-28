/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.services;

import org.irods.jargon.idrop.exceptions.IdropException;

/**
 *
 * @author mikeconway
 */
public interface IdropPreDatabaseBootstrapperService {
 /**
     * Given the idrop home directory (the location of the .idrop config directory for a given user), 
     * see if an indicator exists that shows the current version of iDrop.  This will be null if not found.  
     * <p/>
     * This value can be used to determine any migration strategy for new versions of iDrop.
     * @param idropHomeDir <code>String</code> with the absolute path to the .idrop directory in the users home directory
     * @return <code>String</code> containing the existing version of iDrop deployed, or <code>null</code> if no prior version exists.
     * @throws IdropException 
     */
    String detectPriorVersion(final String idropHomeDir) throws IdropException;

    /**
     * Store a version number into the proper prior version file in the .idrop configuration directory.  This is used to 
     * compare the current version of the code with any existing configuration/data to trigger any necessary migrations
     * @param idropHomeDir <code>String</code> with the absolute path to the .idrop directory in the users home directory
     * @param desiredVersionString <code>String</code> containing the existing version of iDrop deployed
     * @throws IdropException 
     */
    void storePriorVersion(final String idropHomeDir, final String desiredVersionString) throws IdropException;

    /**
     * Given the prior version contained in any existing properties file (can be null), the current version (in the classpath
     * properties), and the target iDrop home directory, trigger any migration activities required to align the database and
     * configurations to the new version
     * @param idropHomeDirectory <code>String</code> with the required absolute path tot he idrop home directory (typically
     * this is {user.home}/.idrop
     * @param priorVersion <code>String</code>, which can be <code>null</code> that points to the previous version of 
     * @param thisVersion <code>String</code> representing the current version number.  These are constants kept in the {@link IdropConfigurationService}
     * @throws IdropException 
     */
    void triggerMigrations(final String idropHomeDirectory, final String priorVersion, final String thisVersion) throws IdropException;
}
