package com.wiscess.oauth.delegate;

import javax.servlet.http.HttpServletRequest;

/**
 * OAuth2登录处理逻辑
 * @author wh
 *
 */
public interface OAuth2LoginDelegate {

	/**
	 * 业务系统中执行登录方法，缓存用户信息等。
	 */
	public void doLogin(HttpServletRequest request,String username);
}
