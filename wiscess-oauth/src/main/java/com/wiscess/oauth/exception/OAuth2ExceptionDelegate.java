package com.wiscess.oauth.exception;

import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;

/**
 * 异常处理类，可以设置自定义的异常信息
 * @author wh
 *
 */
public abstract class OAuth2ExceptionDelegate {
	
	public void dealReturnObject(HttpServletResponse response,R r) {
		
	}
}
