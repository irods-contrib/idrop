package org.irods.jargon.idrop.exceptions;

/**
 * Exception caused by iDrop already running.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropAlreadyRunningException extends IdropException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2435686638718255181L;

	public IdropAlreadyRunningException() {
	}

	public IdropAlreadyRunningException(final String message) {
		super(message);
	}

	public IdropAlreadyRunningException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public IdropAlreadyRunningException(final Throwable cause) {
		super(cause);
	}
}
