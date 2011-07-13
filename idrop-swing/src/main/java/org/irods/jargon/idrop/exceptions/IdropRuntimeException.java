package org.irods.jargon.idrop.exceptions;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropRuntimeException extends RuntimeException {

    public IdropRuntimeException(final Throwable cause) {
        super(cause);
    }

    public IdropRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IdropRuntimeException(final String message) {
        super(message);
    }

    public IdropRuntimeException() {
    }
}
