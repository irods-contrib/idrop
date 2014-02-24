package org.irods.jargon.idrop.exceptions;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropException extends Exception {

	/**   
	 * 
	 */
	private static final long serialVersionUID = 5893379469123450760L;

	public IdropException(final Throwable cause) {
		super(cause);
	}

	public IdropException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public IdropException(final String message) {
		super(message);
	}

	public IdropException() {
	}
}
