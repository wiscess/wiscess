package com.wiscess.security.sso;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * 统一认证登陆的入口页面
 * @author wh
 *
 */
@Slf4j
public class SSOAuthenticationEntryPoint implements AuthenticationEntryPoint,
	InitializingBean{

	public SSOAuthenticationEntryPoint(String authUrl) {
		this.authUrl=authUrl;
	}
	private String authUrl;

	public void afterPropertiesSet() throws Exception {
		Assert.hasText(authUrl, "authUrl must be specified");
	}
	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		log.debug("SSOAuthenticationEntryPoint commence");
		String thisUrl = UrlUtils.buildFullRequestUrl(request);
		log.debug("thisUrl="+thisUrl);
		thisUrl = URLEncoder.encode(thisUrl, "UTF-8");
		String key = request.getSession().getId();
		
		String redirectUrl = authUrl + "?auth_url=" + thisUrl + "&auth_key=" + key;
		log.debug("redirectUrl="+redirectUrl);
		response.sendRedirect(redirectUrl);

	}

}
