package com.wiscess.oauth.delegate;

import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;

/**
 * 异常处理类，可以设置自定义的异常信息
 * @author wh
 */
public interface OAuth2AuthenticationEntryPointDelegate {
	
	/**
	 * 自定义返回格式
	 * @param response
	 * @param r
	 */
	public void doDelegate(HttpServletResponse response,R r) ;
}
