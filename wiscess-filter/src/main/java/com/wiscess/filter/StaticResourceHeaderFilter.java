package com.wiscess.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wh
 */
@Slf4j
public class StaticResourceHeaderFilter extends HeaderWriterFilter implements OrderedFilter{
	/**
	 * 过滤器顺序
	 */
	private int STATIC_FILTER_ORDER = -9990;
	/**
	 * 默认静态资源文件
	 */
	private static String[] DEFAULT_IGNORES="/bower_components/**,/css/**,/js/**,/images/**,/webjars/**,/**/favicon.ico,/captcha.jpg".split(",");
	
	private Set<String> excludes = new HashSet<>();
	private RequestMatcher requireMatcher = null;
	
	public StaticResourceHeaderFilter(List<HeaderWriter> headerWriters, List<String> ignoreList){
		super(headerWriters);
		//1、增加默认的资源
		excludes.addAll(Arrays.asList(DEFAULT_IGNORES));
		//2、默认加载public目录下的目录
		try {
			Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:public/*");
			for(Resource r:resources) {
				excludes.add("/"+r.getFilename()+"/**");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//3、增加附加的资源
		if(!ignoreList.isEmpty())
			excludes.addAll(ignoreList);
		//
		excludes.forEach(url->{log.debug("static resource:{}",url);});
		
		List<RequestMatcher> matchers=new ArrayList<>();
		excludes.forEach(url->{
			matchers.add(new AntPathRequestMatcher(url));
		});
		requireMatcher=new OrRequestMatcher(matchers);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = request;
		HttpServletResponse resp = response;
		if (handleExcludeURL(req, resp)) {
			filterChain.doFilter(request, response);
			return;
		}
		super.doFilterInternal(request,response,filterChain);
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
		return STATIC_FILTER_ORDER;
	}

	

}
