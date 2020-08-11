package com.wiscess.security.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractVueAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException e) throws IOException,
			ServletException {
		response.setContentType("application/json;charset=utf-8");
		//设置返回状态
		response.setStatus(403);
        R r=R.error("权限不足，请重新登录!");
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
	/**
	 * 登录失败的处理
	 * @param r
	 * @param e
	 * @throws IOException
	 * @throws ServletException
	 */
	public abstract void onFailure(R r, AccessDeniedException e) throws IOException, ServletException;
}
