/**
 * 
 */
package com.wiscess.security.web;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author audin
 *
 */
public class CaptchaDaoAuthenticationProvider extends DaoAuthenticationProvider {
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken token)
			throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, token);

		Object obj = token.getDetails();
		if (!(obj instanceof CaptchaAuthenticationDetails)) {
			throw new InsufficientAuthenticationException(
					"Captcha details not found.");
		}

		CaptchaAuthenticationDetails captchaDetails = (CaptchaAuthenticationDetails) obj;
		String expected = captchaDetails.getCaptcha();
		if (expected != null) {
			String actual = captchaDetails.getAnswer();
			if (!expected.equals(actual)) {
				throw new BadCredentialsException("Captcha does not match.");
			}
		} else {
			throw new BadCredentialsException("Captcha does not match.");
		}
	}
}
