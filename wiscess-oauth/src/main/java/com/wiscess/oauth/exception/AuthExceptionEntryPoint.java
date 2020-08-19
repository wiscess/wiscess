package com.wiscess.oauth.exception;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscess.common.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 访问资源时，无效Token的异常类
 * @author wh
 *
 */
@Slf4j
@ConditionalOnMissingBean(AuthenticationEntryPoint.class)
@Configuration
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws  ServletException {
    	log.debug("认证失败：{} {} ",request.getRequestURI(),authException.getMessage());
        response.setContentType("application/json;charset=utf-8");
		//设置返回状态
        response.setStatus(HttpServletResponse.SC_OK);
        Throwable cause = authException.getCause();
        
        R r=R.error("认证失败，请重新登录!");
        if(cause==null && authException instanceof InsufficientAuthenticationException) {
        	if(authException.getMessage().contains("Full authentication is required to access this resource")) {
        		r.data(authException.getMessage());
        		r.message("Token缺失");
        		r.code(50008);
        	}
        }else
        if(cause instanceof InvalidTokenException) {
        	if(authException.getMessage().contains("Invalid access token")) {
        		r.data(authException.getMessage());
        		r.message("无效的Token");
        		r.code(50008);
        	}
        }else if(cause instanceof OAuth2AccessDeniedException) {
        	if(authException.getMessage().contains("Invalid token does not contain resource id")) {
        		r.data(authException.getMessage());
        		r.message("无权访问资源");
        		r.code(50008);
        	}
        }
        //设置定制化的消息或内容
        dealReturnObject(response,r);
        //输出json内容
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), r);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
    
    public void dealReturnObject(HttpServletResponse response,R r) {
    	
    }
}