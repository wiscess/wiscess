/**
 * 
 */
package com.wiscess.security.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * @author audin
 *
 */
public class CaptchaAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, CaptchaAuthenticationDetails> {

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationDetailsSource#buildDetails(java.lang.Object)
	 */
	@Override
	public CaptchaAuthenticationDetails buildDetails(HttpServletRequest context) {
		return new CaptchaAuthenticationDetails(context);
	}

}
