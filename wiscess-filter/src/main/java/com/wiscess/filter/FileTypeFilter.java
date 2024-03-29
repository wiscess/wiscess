package com.wiscess.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 文件类型过滤器
 * @author wh
 *
 */
public class FileTypeFilter extends OncePerRequestFilter{
	private String errorPage="/error";
	private RequestMatcher requireMatcher = null;
	
	public FileTypeFilter(){
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String errorPage=getErrorPage();
		if (this.requireMatcher==null || this.requireMatcher.matches(request)) {
			//如果没有配置，则允许访问
			//如果匹配上，则允许访问
			filterChain.doFilter(request, response);
			return;
		}
//		不允许访问
		response.sendRedirect(request.getContextPath() + errorPage);	
	}

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}
	
	public RequestMatcher getRequireMatcher() {
		return requireMatcher;
	}
	public void setRequireMatcher(RequestMatcher requireMatcher) {
		this.requireMatcher = requireMatcher;
	}
}