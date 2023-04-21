package com.wiscess.security.web.controller;

import jakarta.servlet.http.HttpServletRequest;

import com.wiscess.security.exception.LoginFailNumException;
import com.wiscess.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wiscess.common.R;
import com.wiscess.security.WiscessSecurityProperties;
import com.wiscess.security.exception.BadCodeAuthenticationServiceException;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@ConditionalOnProperty(name = "security.defaultLoginController", havingValue = "true")
public class LoginController {

	@Autowired
	public WiscessSecurityProperties wiscessSecurityProperties;

	@Autowired
	public void init() {
		log.info("LoginController");
	}
	/**
	 * 登录入口页面，跳转到首页
	 */
	@RequestMapping(value="/login",method = {RequestMethod.GET,RequestMethod.POST})
	public String login(Model model,HttpServletRequest request){
		String queryString=request.getQueryString();
		log.debug(queryString);
		if(StringUtils.isNotEmpty(queryString)) {
			if(queryString.equals("error")) {
				model.addAttribute("errorMessage",loadException(request));
			}else if(queryString.equals("logout")) {
				model.addAttribute("paramLogout","您已退出");
			}
		}
		if(wiscessSecurityProperties.isSingleLoginPage()) {
			//独立的登录页
			model.addAttribute("encryptUsername",wiscessSecurityProperties.isEncryptUsername());
			model.addAttribute("encryptPassword",wiscessSecurityProperties.isEncryptPassword());
			model.addAttribute("isCaptcha",wiscessSecurityProperties.isCaptcha());
			model.addAttribute("isSingleLoginPage",wiscessSecurityProperties.isSingleLoginPage());
			return wiscessSecurityProperties.getDefaultLoginPage();
		}else {
			//内嵌在首页，页面上加载登录参数是否加密等信息
			return "redirect:/";
		}
	}
	
	/**
	 * 加载登录页的参数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loginProperties")
	public R loadProperties() {
		return R.ok()
				.data("encryptUsername",wiscessSecurityProperties.isEncryptUsername())
				.data("encryptPassword",wiscessSecurityProperties.isEncryptPassword())
				.data("isCaptcha",wiscessSecurityProperties.isCaptcha());
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
			}else if(exception instanceof LoginFailNumException) {
				message=exception.getMessage();
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
