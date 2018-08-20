package com.wiscess.security.sso;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

@Slf4j
public class SSOAuthenticationProvider extends DaoAuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Assert.isInstanceOf(SSOAuthenticationToken.class, authentication, "Only SSOAuthenticationToken is supported");
		
		SSOAuthenticationToken auth = (SSOAuthenticationToken) authentication;
		log.debug("id:"+auth.getClientKey());
		if (auth.getClientKey() != null && auth.getRemoteKey() != null
				&& getPasswordEncoder().matches(auth.getClientKey() + auth.getLoginName(),auth.getRemoteKey())) {
			UserDetails user = null;
			try {
				log.debug("username:"+auth.getCredentials());
				user = getUserDetailsService().loadUserByUsername(auth.getLoginName());
			} catch (UsernameNotFoundException e) {
				throw new BadCredentialsException("No such user.", e);
			}
			
			return new SSOAuthenticationToken(auth.getLoginName(), auth.getTestName(), auth.getRemoteKey(), auth.getClientKey(), user.getAuthorities());
		} else {
			throw new BadCredentialsException("Authentication failed.");
		}
	}
	
	public boolean supports(Class<?> authentication) {
		return SSOAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
