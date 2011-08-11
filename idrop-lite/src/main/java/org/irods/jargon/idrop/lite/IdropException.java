/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.lite;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropException extends Exception {

    public IdropException(Throwable cause) {
        super(cause);
    }

    public IdropException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdropException(String message) {
        super(message);
    }

    public IdropException() {
    }


}
