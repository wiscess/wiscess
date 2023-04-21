/**
 * 
 */
package com.wiscess.security.web;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * @author wh
 */
public class CaptchaAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, CaptchaAuthenticationDetails> {

	public CaptchaAuthenticationDetails buildDetails(HttpServletRequest context) {
		return new CaptchaAuthenticationDetails(context);
	}

}
