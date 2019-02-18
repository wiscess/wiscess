package com.wiscess.security.sso;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 统一认证的Token
 * @author wh
 *
 */
public class SSOAuthenticationToken extends AbstractAuthenticationToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7749894770011369333L;
	private final String loginName;
	private final String remoteKey;
	private final String testName;
	private final String clientKey;

	public SSOAuthenticationToken(final String loginName, final String testName, final String remoteKey, final String clientKey) {
		super(null);
		this.loginName = loginName;
		this.testName = testName;
		this.remoteKey = remoteKey;
		this.clientKey = clientKey;
		
		setAuthenticated(false);
	}
	
	public SSOAuthenticationToken(final String loginName, final String testName, final String remoteKey, final String clientKey, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.loginName = loginName;
		this.testName = testName;
		this.remoteKey = remoteKey;
		this.clientKey = clientKey;
		
		setAuthenticated(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.acegisecurity.Authentication#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return loginName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.acegisecurity.Authentication#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return loginName;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getTestName() {
		return testName;
	}

	public String getRemoteKey() {
		return remoteKey;
	}

	public String getClientKey() {
		return clientKey;
	}
}
