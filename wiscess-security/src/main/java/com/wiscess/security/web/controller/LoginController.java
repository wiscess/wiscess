package com.wiscess.security.web.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.security.exception.BadCodeAuthenticationServiceException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
@Api(value = "LoginController",description =  "Security登录接口")
public class LoginController {

    @ApiOperation(value = "登录接口，username和password需用Rsa加密")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "username",value = "用户名",paramType = "query"),
        @ApiImplicitParam(name = "password",value = "密码",paramType = "query"),
        @ApiImplicitParam(name = "code",value = "验证码",paramType = "query")
    })
	@RequestMapping(value="/login",method = {RequestMethod.GET,RequestMethod.POST})
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
