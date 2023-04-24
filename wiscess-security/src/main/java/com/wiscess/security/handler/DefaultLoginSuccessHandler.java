package com.wiscess.security.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DefaultLoginSuccessHandler extends AbstractLoginSuccessHandler {
	
	@Bean
	@Qualifier("loginSuccessHandler")
	@ConditionalOnMissingBean
	public AuthenticationSuccessHandler handler(){
		log.debug("DefaultLoginSuccessHandler loaded");
		return new DefaultLoginSuccessHandler();
	}

	@Override
	protected void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		
	}
}
