package com.wiscess.security.exception;

import org.springframework.security.core.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {
	public BadCredentialsException(String msg) {
		super(msg);
	}
	private static final long serialVersionUID = 1L;
}
