package com.wiscess.security.handler;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public abstract class AbstractLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		if(authentication.isAuthenticated()){
			onLogonSuccess(request,response,authentication);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	protected abstract void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication);
}
