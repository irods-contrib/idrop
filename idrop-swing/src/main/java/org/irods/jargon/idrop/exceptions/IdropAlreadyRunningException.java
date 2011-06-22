package org.irods.jargon.idrop.exceptions;

/**
 * Exception caused by iDrop already running.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropAlreadyRunningException extends IdropException {

    public IdropAlreadyRunningException() {
    }

    public IdropAlreadyRunningException(String message) {
        super(message);
    }

    public IdropAlreadyRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdropAlreadyRunningException(Throwable cause) {
        super(cause);
    }

}
