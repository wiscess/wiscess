package com.wiscess.security;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "security.sso")
public class SsoProperties {

	private String authUrl;
	
	private String failureUrl="/login?error";
	
	private String authTestUrl;
	
	private String logoutUrl="/logout";
	
	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getFailureUrl() {
		return failureUrl;
	}

	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	public String getAuthTestUrl() {
		return authTestUrl;
	}

	public void setAuthTestUrl(String authTestUrl) {
		this.authTestUrl = authTestUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
}
