package com.wiscess.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	@Bean(name="loginSuccessHandler")
	@ConditionalOnMissingBean
	public SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler(){
		log.debug("loginSuccessHandler init");
		SavedRequestAwareAuthenticationSuccessHandler handler= new LoginSuccessHandler();
		handler.setDefaultTargetUrl("/");
		handler.setAlwaysUseDefaultTargetUrl(true);
		return handler;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		if(authentication.isAuthenticated()){
			onLogonSuccess(request,response,authentication);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	protected void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		
	}

	public String getIpAddress(HttpServletRequest request){     
		String ip = request.getHeader("x-forwarded-for");     
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {                  
			ip = request.getHeader("Proxy-Client-IP");     
        }      

		if	 (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
			ip = request.getHeader("WL-Proxy-Client-IP");     
        }      

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
        	ip = request.getHeader("HTTP_CLIENT_IP");     
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	ip = request.getHeader("HTTP_X_FORWARDED_FOR");     
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
        	ip = request.getRemoteAddr();     
        }      
        return ip;     
    } 

}
