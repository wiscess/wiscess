package com.wiscess.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
