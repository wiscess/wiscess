package com.wiscess.security.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginFailNumException extends AuthenticationException {
	public LoginFailNumException(String msg) {
		super(msg);
	}
	private static final long serialVersionUID = 1L;
}
