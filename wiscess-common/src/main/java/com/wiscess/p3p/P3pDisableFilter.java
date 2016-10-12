package com.wiscess.p3p;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class P3pDisableFilter implements Filter{

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain ch) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.addHeader("P3P", "CP=CAO PSA OUR");
		ch.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		if (log.isDebugEnabled()) {
			log.debug("add header: P3P='CP=CAO PSA OUR'");
		}
	}
}
