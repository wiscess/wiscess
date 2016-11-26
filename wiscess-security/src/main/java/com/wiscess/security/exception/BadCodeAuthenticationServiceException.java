package com.wiscess.security.exception;

import org.springframework.security.core.AuthenticationException;

public class BadCodeAuthenticationServiceException extends
		AuthenticationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BadCodeAuthenticationServiceException(String msg) {
		super(msg);
	}


}
