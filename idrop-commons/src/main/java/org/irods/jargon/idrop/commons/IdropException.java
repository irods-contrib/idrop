package org.irods.jargon.idrop.commons;

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
