/**
 * 
 */
package com.wiscess.security.web;

import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 验证码对象
 * @author wh
 */
public class CaptchaAuthenticationDetails implements Serializable {

	private static final long serialVersionUID = -740464724350486529L;

	private final String answer;
	private final String captcha;

	/**
	 * @param request
	 */
	public CaptchaAuthenticationDetails(HttpServletRequest request) {
		this.answer = request.getParameter("captcha");
		this.captcha = (String) request.getSession(true).getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
	}

	public String getAnswer() {
		return answer;
	}

	public String getCaptcha() {
		return captcha;
	}

}
