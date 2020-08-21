package com.wiscess.oauth.config.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscess.common.R;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@ConditionalOnMissingBean(AccessDeniedHandler.class)
@Configuration
public class OauthAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException e) throws IOException,
			ServletException {
		log.debug("AccessDenied:{}",request.getRequestURI());
		response.setContentType("application/json;charset=utf-8");
		//设置返回状态
		response.setStatus(403);
        R r=R.error("访问资源时权限不足。");
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

	public void onFailure(R r, AccessDeniedException e) throws IOException, ServletException {
		r.message("访问资源时权限不足。");
	}

}
