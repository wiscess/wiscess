/**
 * 
 */
package com.wiscess.security.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wiscess.utils.RSA_Encrypt;

/**
 * 增加了对用户名的加密认证
 * @author wh
 */
public class EncryptUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private boolean encryptUsername;
	private boolean encryptPassword;
	
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY="SPRING_SECURITY_LAST_USERNAME_KEY";

	public EncryptUsernamePasswordAuthenticationFilter(boolean encryptUsername, boolean encryptPassword) {
		this.encryptUsername=encryptUsername;
		this.encryptPassword=encryptPassword;
	}
	protected String obtainUsername(HttpServletRequest request) {
		String username = super.obtainUsername(request);
		//对用户名进行解密
		if(encryptUsername) {
			try {
				username=RSA_Encrypt.decrypt(username.toString(),true);
			} catch (Exception e) {
				e.printStackTrace();
				username="";
			}
		}		
		if (request.getSession() != null || getAllowSessionCreation()) {
			request.getSession()
					.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY,username);
		}
		return username;
	}
	protected String obtainPassword(HttpServletRequest request) {
		String password = super.obtainPassword(request);
		//对密码进行解密
		if(encryptPassword) {
			try {
				password=RSA_Encrypt.decrypt(password.toString(),true);
			} catch (Exception e) {
				e.printStackTrace();
				password="";
			}
		}		
		return password;
	}
}
