package com.wiscess.security.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractVueLoginSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest req,
            HttpServletResponse resp,
            Authentication auth) throws IOException {
		resp.setContentType("application/json;charset=utf-8");
		R r = null;
		
		if(auth.isAuthenticated()){
			r=R.ok("登录成功!");
	        //设置定制化的消息或内容
			onSuccess(req,r,auth);
		}else {
			r=R.error("登录失败");
		}
        //输出json内容
		ObjectMapper om = new ObjectMapper();
		PrintWriter out = resp.getWriter();
		out.write(om.writeValueAsString(r));
		out.flush();
		out.close();
	}
	/**
	 * 登录成功的处理
	 * @param req
	 * @param r
	 * @param auth
	 */
	public abstract void onSuccess(HttpServletRequest req,R r,Authentication auth);
}
