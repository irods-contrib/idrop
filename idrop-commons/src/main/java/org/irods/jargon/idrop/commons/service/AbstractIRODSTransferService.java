/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.commons.service;

import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.idrop.commons.IdropException;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public abstract class AbstractIRODSTransferService {
    
    /**
     * Copy a file from source to target
     * @param dataTransferOperations
     * @param sourceFile
     * @param targetAbsolutePath
     * @throws IdropException 
     */
        public abstract void copyFile(DataTransferOperations dataTransferOperations, IRODSFile sourceFile, String targetAbsolutePath) throws IdropException;

    
}
