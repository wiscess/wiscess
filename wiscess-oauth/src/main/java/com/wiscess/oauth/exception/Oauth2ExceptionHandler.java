package com.wiscess.oauth.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wiscess.common.R;

/**
 * 全局处理Oauth2抛出的异常
 * Created by wh on 2020/8/19.
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public R handleOauth2(OAuth2Exception e) {
    	return R.error().data(e.getMessage());
    }

}

