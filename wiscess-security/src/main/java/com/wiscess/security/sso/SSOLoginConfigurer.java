package com.wiscess.security.sso;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 统一认证登录的配置
 * @author wh
 *
 * @param <H>
 */
public class SSOLoginConfigurer<H extends HttpSecurityBuilder<H>>
	extends AbstractAuthenticationFilterConfigurer<H, SSOLoginConfigurer<H>, SSOAuthenticationFilter> {
	
	public SSOLoginConfigurer() {
		super(new SSOAuthenticationFilter(), null);
	}

	protected SSOLoginConfigurer(
			SSOAuthenticationFilter authenticationFilter,
			String defaultLoginProcessingUrl) {
		super(authenticationFilter, defaultLoginProcessingUrl);
	}

	@Override
	protected RequestMatcher createLoginProcessingUrlMatcher(
			String loginProcessingUrl) {
		return new AntPathRequestMatcher(loginProcessingUrl, "POST");
	}

}
