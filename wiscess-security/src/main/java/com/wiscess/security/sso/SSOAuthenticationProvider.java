package com.wiscess.security.sso;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.wiscess.security.sso.encrypt.EncryptHandler;
import com.wiscess.security.sso.encrypt.MD5EncryptHandler;
import com.wiscess.security.sso.encrypt.NoneEncryptHandler;
import com.wiscess.security.sso.encrypt.RSAEncryptHandler;

@Slf4j
public class SSOAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailsService;
	private String encryptType="MD5";
	private EncryptHandler encryptHandler=new MD5EncryptHandler();
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Assert.isInstanceOf(SSOAuthenticationToken.class, authentication, "Only SSOAuthenticationToken is supported");
		
		SSOAuthenticationToken auth = (SSOAuthenticationToken) authentication;
		log.debug("id:"+auth.getClientKey());
		if (auth.getClientKey() != null && auth.getRemoteKey() != null
				&& encryptHandler.encrypt(auth.getClientKey() + auth.getLoginName(),auth.getRemoteKey())) {
			UserDetails user = null;
			try {
				log.debug("username:"+auth.getCredentials());
				user = getUserDetailsService().loadUserByUsername(auth.getLoginName());
			} catch (UsernameNotFoundException e) {
				throw new BadCredentialsException("No such user.", e);
			}
			
//			Assert.notNull(user, "Can't get userdetails - a violation of the interface contract");
			
			return new SSOAuthenticationToken(auth.getLoginName(), auth.getTestName(), auth.getRemoteKey(), auth.getClientKey(), user.getAuthorities());
		} else {
			throw new BadCredentialsException("Authentication failed.");
		}
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return SSOAuthenticationToken.class.isAssignableFrom(authentication);
	}
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	public String getEncryptType() {
		return encryptType;
	}
	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
		if(EncryptHandler.NONE.equalsIgnoreCase(this.encryptType)){
			this.encryptHandler=new NoneEncryptHandler();
		}else if(EncryptHandler.MD5.equalsIgnoreCase(this.encryptType)){
			this.encryptHandler=new MD5EncryptHandler();
		}else if(EncryptHandler.RSA.equalsIgnoreCase(this.encryptType)){
			this.encryptHandler=new RSAEncryptHandler();
		}
	}
	public EncryptHandler getEncryptHandler() {
		return encryptHandler;
	}
	public void setEncryptHandler(EncryptHandler encryptHandler) {
		this.encryptType=encryptHandler.encryptType();
		this.encryptHandler = encryptHandler;
	}

}
