package com.wiscess.filter;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wiscess.filter.autoconfig.properties.XssFilterProperties;
import com.wiscess.filter.xss.XssHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wh
 */
public class XssFilter extends OncePerRequestFilter implements OrderedFilter{
	/**
	 * 过滤器顺序
	 */
	private int XSS_FILTER_ORDER = -9990;
	/**
	 * 是否过滤富文本内容
	 */
	private static boolean IS_INCLUDE_RICH_TEXT = false;
	/**
	 * 默认静态资源文件
	 */
	private static String[] DEFAULT_IGNORES="/css/**,/js/**,/images/**,/webjars/**,/**/favicon.ico,/captcha.jpg".split(",");
	
	private List<String> excludes = new ArrayList<>();
	private RequestMatcher requireMatcher = null;
	
	public XssFilter(XssFilterProperties properties){
		IS_INCLUDE_RICH_TEXT = properties.getIsIncludeRichText();
		//增加默认的资源
		excludes.addAll(Arrays.asList(DEFAULT_IGNORES));
		//增加附加的资源
		excludes.addAll(properties.getExcludes());
		//
		List<RequestMatcher> matchers=new ArrayList<>();
		excludes.forEach(url->{
			matchers.add(new AntPathRequestMatcher(url));
		});
		requireMatcher=new OrRequestMatcher(matchers);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (handleExcludeURL(req, resp)) {
			filterChain.doFilter(request, response);
			return;
		}

		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request, IS_INCLUDE_RICH_TEXT);
		filterChain.doFilter(xssRequest, response);
	}
	
	/**
	 * 判断是否为排除的地址
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
		if(requireMatcher==null) {
			return false;
		}
		if(requireMatcher.matches(request)) {
			return true;
		}
		return false;
	}
	@Override
	public int getOrder() {
		return XSS_FILTER_ORDER;
	}

}
