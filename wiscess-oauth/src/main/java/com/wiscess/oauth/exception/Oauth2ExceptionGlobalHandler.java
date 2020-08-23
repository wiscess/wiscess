package com.wiscess.oauth.exception;

import javax.annotation.Priority;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wiscess.common.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局处理Oauth2抛出的异常
 * Created by wh on 2020/8/19.
 */
@Slf4j
@ControllerAdvice
@Priority(-999)
public class Oauth2ExceptionGlobalHandler {

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public R handleOauth2(OAuth2Exception e) {
    	/**
    	 * 处理Oauth2Exception的异常
    	 */
    	log.debug(e.getMessage());
    	//检查Scope的错误
    	if(e instanceof InvalidScopeException) {
    		//登录时，Scope错误时，提示登录失败
        	return R.error("登录失败").data(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    	}
    	//检查GrantType的错误
    	else if(e instanceof InvalidRequestException) {
    		//登录时，GrantType缺失时，提示登录失败
    		//error="invalid_request", error_description="Missing grant type"
        	return R.error("登录失败").data(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    	}else if(e instanceof UnsupportedGrantTypeException) {
    		//登录时，grantType错误时，提示登录失败
    		//error="unsupported_grant_type", error_description="Unsupported grant type"
        	return R.error("登录失败").data(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    	}
    	//用户密码错误
    	else if(e instanceof InvalidGrantException) {
    		//刷新Token时,refresh_token不对时
    		//error="invalid_grant", error_description="Invalid refresh token: null"
    		if(e.getMessage().contains("Invalid refresh token")) {
            	return R.error("刷新token失败").data(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    		}
    		//用户名密码错误
        	return R.error(e.getMessage()).data(e.getMessage());
    	}else if(e instanceof ClientAuthenticationException) {
    		//客户端认证异常
        	return R.error(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    	}
    	return R.error("登录认证失败").data(e.getMessage()).code(TokenCode.AUTH_FAILURE.value());
    }

}

