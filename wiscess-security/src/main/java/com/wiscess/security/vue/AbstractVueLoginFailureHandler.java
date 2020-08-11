package com.wiscess.security.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscess.security.exception.BadCodeAuthenticationServiceException;

public abstract class AbstractVueLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest req,
            HttpServletResponse resp,
            AuthenticationException e) throws IOException, ServletException {
		resp.setContentType("application/json;charset=utf-8");
        R r = null;
        if (e instanceof BadCredentialsException ||
                e instanceof UsernameNotFoundException) {
            r = R.error("账户名或者密码输入错误!");
        } else if (e instanceof LockedException) {
            r = R.error("账户被锁定，请联系管理员!");
        } else if (e instanceof CredentialsExpiredException) {
            r = R.error("密码过期，请联系管理员!");
        } else if (e instanceof AccountExpiredException) {
            r = R.error("账户过期，请联系管理员!");
        } else if (e instanceof DisabledException) {
            r = R.error("账户被禁用，请联系管理员!");
        } else if (e instanceof BadCodeAuthenticationServiceException) {
            r = R.error("验证码输入错误!");
        } else if (e instanceof SessionAuthenticationException) {
            r = R.error("您的用户名已经在其他机器上登录，请稍候再试。");
        } else {
            r = R.error("登录失败!");
        }
        //设置返回状态
        resp.setStatus(401);
        //设置定制化的消息或内容
		onFailure(r,e);
        //输出json内容
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = resp.getWriter();
        String error=om.writeValueAsString(r);
        out.write(error);
        out.flush();
        out.close();
	}
	/**
	 * 登录失败的处理
	 * @param r
	 * @param exception
	 * @throws IOException
	 * @throws ServletException
	 */
	public abstract void onFailure(R r, AuthenticationException exception) throws IOException, ServletException;
}
