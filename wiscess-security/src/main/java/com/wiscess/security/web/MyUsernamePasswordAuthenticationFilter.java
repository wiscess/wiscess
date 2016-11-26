package com.wiscess.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wiscess.security.exception.BadCodeAuthenticationServiceException;


public class MyUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	
	/**
	 * 是否校验验证
	 */
	private boolean needCheckCode = true;
	
	public static final String SESSION_CODE="_session_image_code"; 

	public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY="SPRING_SECURITY_LAST_USERNAME_KEY";

	private String codeParameter = SPRING_SECURITY_FORM_CODE_KEY;
	
	protected MyUsernamePasswordAuthenticationFilter() {
		 super();
	 }

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String code = obtainCode(request);
		//保存用户
		obtainUsername(request);
		//校验验证码是否正确
		if(needCheckCode && !checkCode(code,(String)request.getSession().getAttribute(SESSION_CODE))){
			throw new BadCodeAuthenticationServiceException("验证码错误");
		}
		return super.attemptAuthentication(request, response);
	}

	protected String obtainCode(HttpServletRequest request) {
		return request.getParameter(codeParameter);
	}
	protected String obtainUsername(HttpServletRequest request) {
		String username = super.obtainUsername(request);
//		try {
//			username = new String(super.obtainUsername(request).getBytes("gbk"), "utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		if (request.getSession() != null || getAllowSessionCreation()) {
			request.getSession()
					.setAttribute(
							MyUsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY,
							username);
		}

		return username;
	}
	/**
	 * 校验验证码是否正确
	 * @param code
	 * @param sessionCode
	 * @return
	 */
	private boolean checkCode(String code,String sessionCode){
		return code!=null && sessionCode!=null && code.equals(sessionCode);
	}

	public String getCodeParameter() {
		return codeParameter;
	}

	public void setCodeParameter(String codeParameter) {
		this.codeParameter = codeParameter;
	}
}
