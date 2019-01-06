/**
 * 
 */
package com.wiscess.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wiscess.filter.xss.XssHttpServletRequestWrapper;
import com.wiscess.utils.RSA_Encrypt;

import lombok.extern.slf4j.Slf4j;

/**
 * 增加了对用户名的加密认证
 * @author wh
 */
@Slf4j
public class EncryptUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private boolean encryptUsername;
	private boolean encryptPassword;
	
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY="SPRING_SECURITY_LAST_USERNAME_KEY";

	public EncryptUsernamePasswordAuthenticationFilter(boolean encryptUsername, boolean encryptPassword) {
		this.encryptUsername=encryptUsername;
		this.encryptPassword=encryptPassword;
	}
	
	private HttpServletRequest getOrgRequest(HttpServletRequest request) {
		try {
			HttpServletRequest orgRequest=(HttpServletRequest)((HttpServletRequestWrapper) request).getRequest();
			while(!(orgRequest instanceof XssHttpServletRequestWrapper)) {
				orgRequest=(HttpServletRequest)((HttpServletRequestWrapper) orgRequest).getRequest();
			}
			return ((XssHttpServletRequestWrapper)orgRequest).getOrgRequest();
		}catch (Exception e) {
		}
		return request;
	}
	protected String obtainUsername(HttpServletRequest request) {
		String username = super.obtainUsername(getOrgRequest(request));
		log.debug("username:{}",username);
		//对用户名进行解密
		if(encryptUsername) {
			try {
				username=RSA_Encrypt.decrypt(username.toString(),true);
			} catch (Exception e) {
				e.printStackTrace();
				username="";
			}
		}
		log.debug("username:{}",username);		
		if (request.getSession() != null || getAllowSessionCreation()) {
			request.getSession()
					.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY,username);
		}
		return username;
	}
	protected String obtainPassword(HttpServletRequest request) {
		String password = super.obtainPassword(getOrgRequest(request));
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
