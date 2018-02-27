package com.wiscess.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * HTTP METHOD过滤器
 * @author wh
 *
 */
public class HttpMethodFilter extends OncePerRequestFilter{
	private String errorPage="/error";
	private Pattern allowedMethods=Pattern.compile("^(HEAD|TRACE|OPTIONS|PUT|DELETE|PATCH)$");
	
	public HttpMethodFilter(){
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String errorPage=getErrorPage();
		if(!allowedMethods.matcher(request.getMethod()).matches()){
			//request方法，如果是GET或POST
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
	
}