package com.wiscess.oauth.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
public class DefaultOAuth2AuthenticationEntryPoint  implements AuthenticationEntryPoint {

	@Autowired(required = false)
	private OAuth2ExceptionDelegate oauth2ExceptionDelegate;
	
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
    	log.info("认证失败：{} {} ",request.getRequestURI(),authException.getMessage()); 
    	response.setContentType("application/json;charset=utf-8");
        
		//设置返回状态
        response.setStatus(HttpServletResponse.SC_OK);
        //设置返回对象
        R r=R.error();
        
        Throwable cause = authException.getCause();
        if(cause==null && authException instanceof InsufficientAuthenticationException) {
        	if(authException.getMessage().contains("Full authentication is required to access this resource")) {
        		//当认证时client_id缺失，或者访问资源时无access_token时，提示没有访问的权限
        		r.data(authException.getMessage());
        		r.message("没有访问的权限");
        		r.code(TokenCode.MISS_TOKEN.value());
        	}
        }else if(authException instanceof BadCredentialsException ) {
        	//client_id错误、client_secret缺失或错误
        	r.data(authException.getMessage());
    		r.message("客户端认证失败");
    		r.code(TokenCode.AUTH_FAILURE.value());
        }
        //访问资源时，无效的token
        else if(cause instanceof InvalidTokenException) {
        	//error="invalid_token", error_description="Invalid access token: "
        	if(authException.getMessage().contains("Invalid access token")) {
        		r.data(authException.getMessage());
        		r.message("无效的Token");
        		r.code(TokenCode.INVALID_TOKEN.value());
        	}
        }else if(cause instanceof OAuth2AccessDeniedException) {
        	if(authException.getMessage().contains("Invalid token does not contain resource id")) {
        		r.data(authException.getMessage());
        		r.message("无权访问资源");
        		r.code(TokenCode.MISS_TOKEN.value());
        	}
        }
        //设置定制化的消息或内容
        if(oauth2ExceptionDelegate!=null) {
        	oauth2ExceptionDelegate.dealReturnObject(response,r);
        }
        //输出json内容
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), r);
            response.flushBuffer();
        } catch (Exception e) {
            throw new ServletException();
        }
    }
    
}