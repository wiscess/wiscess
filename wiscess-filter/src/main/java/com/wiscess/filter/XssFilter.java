package com.wiscess.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wiscess.filter.autoconfig.properties.XssFilterProperties;
import com.wiscess.filter.xss.XssHttpServletRequestWrapper;
import com.wiscess.utils.StringUtils;

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
	private List<String> allowHosts = new ArrayList<>();
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
		//判断header中的host是否合法
		allowHosts.addAll(properties.getAllowHosts());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = request;
		HttpServletResponse resp = response;
		
		if(allowHosts.size()>0) {
			//判断header中的host是否合法
			String host=request.getHeader("host");
			if(StringUtils.isNotEmpty(host) && !allowHosts.contains(host)) {
				return;
			}
		}
		
		if (handleExcludeURL(req, resp)) {
			//不需要过滤的地址
			filterChain.doFilter(request, response);
			return;
		}

		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request, IS_INCLUDE_RICH_TEXT);
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
