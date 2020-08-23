package com.wiscess.oauth.token;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

public class CustomAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator{
	
	private static final String CLIENT_ID = "client_id";

	private static final String SCOPE = "scope";

	private static final String USERNAME = "username";
	
	/**
	 * 自定义生成方式，
	 */
	public String extractKey(OAuth2Authentication authentication) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		OAuth2Request authorizationRequest = authentication.getOAuth2Request();
		if (!authentication.isClientOnly()) {
			values.put(USERNAME, authentication.getName());
		}
		values.put(CLIENT_ID, authorizationRequest.getClientId());
		if (authorizationRequest.getScope() != null) {
			values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<String>(authorizationRequest.getScope())));
		}
		//添加唯一uuid
		values.put("uuid", UUID.randomUUID().toString());
		//authentication.getUserAuthentication().getDetails()
		return generateKey(values);
	}
}
