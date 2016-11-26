/**
 * 
 */
package com.wiscess.exporter.exception;

/**
 * @author austin
 *
 */
@SuppressWarnings("serial")
public class ManagerException extends RuntimeException {

	/**
	 * 
	 */
	public ManagerException() {
	}

	/**
	 * @param message
	 */
	public ManagerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
