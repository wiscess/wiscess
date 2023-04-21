package com.wiscess.security.sso;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Slf4j
public class SSOLogoutJsSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		try {
			String jscallback = request.getParameter("jscallback");
			String siteid = request.getParameter("siteid");
			PrintWriter writer = response.getWriter();
			StringBuffer sb = new StringBuffer();
			sb.append(jscallback).append("(true, \"").append(siteid).append("\");");
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (RuntimeException e) {
			log.error("SSOLogoutJsSuccessHandler(HttpServletRequest, HttpServletResponse)", e);
		}
	}

}
