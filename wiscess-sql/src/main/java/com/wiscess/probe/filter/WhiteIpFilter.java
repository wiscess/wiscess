package com.wiscess.probe.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wiscess.probe.webapp.util.NestUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WhiteIpFilter extends OncePerRequestFilter implements OrderedFilter{

	 /**
	   * 白名单IP列表
	   */
	private static List<String> whiteIpList=new ArrayList<String>();

	private RequestMatcher requireMatcher = null;
	
	public static String ipUrl;
	
	private List<String> requestUrl=Arrays.asList(
			"/probe/**",
			"/sql/queryHistory.ajax",
			"/sql/queryHistoryItem.ajax",
			"/sql/recordset.ajax",
			"/sql/connection.ajax",
			"/sql/cachedRecordset.ajax"
			);
	@Override
	public int getOrder() {
		return 0;
	}
	
	public WhiteIpFilter() {
		List<RequestMatcher> matchers=new ArrayList<>();
		
		 requestUrl.forEach(url->{
			matchers.add(new AntPathRequestMatcher(url));
		});
		requireMatcher=new OrRequestMatcher(matchers);
	}
	@Value("${ipurl:http://42.96.168.102:8081/ip.properties}")
	public void setIpUrl(String url) {
		ipUrl=url;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			//只校验指定的请求
			if(requireMatcher.matches(request)) {
				String ip=request.getRemoteAddr();
				if(whiteIpList.size()==0) {
					//没有ip时，加载所有ip
					updateWhiteIpList();
				}
				if(!checkIp(ip)){
					//已加载完，不在范围内，页面显示ip
					response.getWriter().write(ip);
					log.error("not allow ip:"+ip);
					//重新加载所有ip
					updateWhiteIpList();
					return;
				}
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			filterChain.doFilter(request, response);
			log.error(e.getMessage());
		}
	}

	public synchronized void updateWhiteIpList(){
		synchronized (whiteIpList) {
			// 刷新ip
			whiteIpList=getWhiteIpList();
		}
	}
	/**
	 * 加载允许的ip
	 * @return
	 */
	private List<String> getWhiteIpList(){
		List<String> alllist = new ArrayList<String>();
		alllist.add("0:0:0:0:0:0:0:1");
		alllist.add("localhost");
		alllist.add("127.0.0.1");
		
		try{
			if(ipUrl!=null && !ipUrl.equals("")) {
				URL url = new URL(ipUrl);
				InputStream in=url.openStream();
				NestUtil.load(alllist, new InputStreamReader(in, "utf-8"));
				in.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return alllist;
	}
	/*
	 * 校验ip是否合法
	 * 通过返回true
	 */
	private boolean checkIp(String ip){
		if(whiteIpList.contains(ip)){
			//如果直接包含该ip，则合法
			return true;
		}
		//按正则表达式的方式进行比较
		for(String wip:whiteIpList){
			//是否为正则表达式
			if(wip.contains("*")){
				//把*替换成正则表达式
				String pattern = wip.replaceAll("\\*","\\\\d*");
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(ip);
				if(m.matches()){
					return true;
				}
			}
		}
		//匹配不成功
		return false;
	}
	
}
