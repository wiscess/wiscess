package com.wiscess.security.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.security.exception.BadCodeAuthenticationServiceException;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {

	@RequestMapping(value="/login")
	public String login(Model model,HttpServletRequest request){
		log.debug("LoginAction(model) - login");
		//读取最后一次的用户名
		//读取异常信息
		model.addAttribute("errorMessage",loadException(request));
		return "login";
	}
	
	/**
	 * 读取异常信息
	 * @param request
	 * @return
	 */
	protected String loadException(HttpServletRequest request) {
		String message=null;
		Exception exception=(Exception)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if(exception!=null){
			if(exception instanceof BadCredentialsException){
				message="用户名或密码错误";
			}else if(exception instanceof BadCodeAuthenticationServiceException){
				message="验证码错误";
			}else if(exception instanceof SessionAuthenticationException) {
				log.error(exception.getMessage());
				message="该用户名登录人数已经达到最大限制数，无法登录";
			}else{
				message="系统异常";
			}
			return message;
		}
		return null;
	}
}
