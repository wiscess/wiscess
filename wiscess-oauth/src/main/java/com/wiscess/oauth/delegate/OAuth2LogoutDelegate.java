package com.wiscess.oauth.delegate;

import javax.servlet.http.HttpServletRequest;

/**
 * OAuth2退出处理逻辑
 * @author wh
 *
 */
public interface OAuth2LogoutDelegate {

	/**
	 * 业务系统中执行退出方法，清除用户信息等。
	 */
	public void doLogout(HttpServletRequest request,String username);
}
