/**
 * 
 */
package com.wiscess.security.web;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wiscess.filter.xss.XssHttpServletRequestWrapper;
import com.wiscess.utils.RSA_Encrypt;

/**
 * 增加了对用户名的加密认证
 * @author wh
 */
public class EncryptUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter  implements OrderedFilter{
	/**
	 * 过滤器顺序
	 */
	private int FILTER_ORDER = -1000;	
	
	private boolean encryptUsername;
	private boolean encryptPassword;
	
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY="SPRING_SECURITY_LAST_USERNAME_KEY";

	public EncryptUsernamePasswordAuthenticationFilter(boolean encryptUsername, boolean encryptPassword) {
		this.encryptUsername=encryptUsername;
		this.encryptPassword=encryptPassword;
	}
	@Override
	public int getOrder() {
		return FILTER_ORDER;
	}
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);
			return;
		}
		//登录请求
		EncryptRequestWrapper loginRequest = new EncryptRequestWrapper(request);
		chain.doFilter(loginRequest, response);
	}
	
	private class EncryptRequestWrapper extends HttpServletRequestWrapper {
		public EncryptRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		/**
		 * * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
		 * * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
		 * * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
		 */
		@Override
		public String getParameter(String name) {
			if((encryptUsername && name.equals(getUsernameParameter()))
				||(encryptPassword && name.equals(getPasswordParameter()))){
				//解压
				//log.debug("decrypt {}",name);
				String value=getOrgRequest().getParameter(name);
				try {
					value=RSA_Encrypt.decrypt(value.toString(),true);
				} catch (Exception e) {
					e.printStackTrace();
					value="";
				}
				//log.debug("value={}",value);
				return value;
			}
			return super.getParameter(name);
		}

		/** * 获取最原始的request * * @return */
		public HttpServletRequest getOrgRequest() {
			try {
				HttpServletRequest orgRequest=(HttpServletRequest)this.getRequest();
				return XssHttpServletRequestWrapper.getOrgRequest(orgRequest);
			}catch (Exception e) {
			}
			return (HttpServletRequest)this.getRequest();
		}
	}
}
