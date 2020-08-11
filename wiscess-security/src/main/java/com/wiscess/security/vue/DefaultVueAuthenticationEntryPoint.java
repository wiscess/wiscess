package com.wiscess.security.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultVueAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
		log.info(request.getRequestURI());
        R r = null;
        r = R.error("权限不足，请重新登录!");
        //设置返回状态
        response.setStatus(403);
        //设置定制化的消息或内容
		onFailure(r,e);
        //输出json内容
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = response.getWriter();
        String error=om.writeValueAsString(r);
        out.write(error);
        out.flush();
        out.close();
	}
	public void onFailure(R r, AuthenticationException e)
			throws IOException, ServletException {
		//可以设置定制化的内容
		log.info("权限不足，请重新登录!");
	}
}
