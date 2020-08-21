package com.wiscess.oauth.filter;

import java.io.IOException;
import java.util.ArrayList;
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

import com.wiscess.oauth.config.OauthProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 白名单路径访问时需要移除JWT请求头
 * Created by wh on 2020/8/21
 */
@Slf4j
public class OAuth2WhiteListFilter extends OncePerRequestFilter implements OrderedFilter{

	/**
	 * 过滤器顺序
	 */
	private int FILTER_ORDER = -9900;

	private RequestMatcher requireMatcher = null;
    
    public OAuth2WhiteListFilter(OauthProperties oauthProperties) {
		List<RequestMatcher> matchers=new ArrayList<>();
        //白名单路径移除JWT请求头
        List<String> ignoreUrls = oauthProperties.getResources().getIgnored();
        if(ignoreUrls!=null) {
	        ignoreUrls.forEach(url->{
				matchers.add(new AntPathRequestMatcher(url));
				log.debug("white url list: {}",url);
			});
	    	requireMatcher=new OrRequestMatcher(matchers);
        }
    	log.info("OAuth白名单配置完成。");
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = request;
		HttpServletResponse resp = response;
		
		if (isWhiteList(req, resp)) {
			//认证白名单地址，需要加一层处理，在获取header和access_token时，直接返回空。
			Oauth2HttpServletRequestWrapper oauthRequest = new Oauth2HttpServletRequestWrapper(request);
			
			filterChain.doFilter(oauthRequest, response);
			return;
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 判断是否为白名单
	 * @param req
	 * @param resp
	 * @return
	 */
	private boolean isWhiteList(HttpServletRequest req, HttpServletResponse resp) {
		if(requireMatcher==null) {
			return false;
		}
		if(requireMatcher.matches(req)) {
			return true;
		}
		return false;
	}

	@Override
	public int getOrder() {
		return FILTER_ORDER;
	}
}
