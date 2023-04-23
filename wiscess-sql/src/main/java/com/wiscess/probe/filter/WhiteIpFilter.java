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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.wiscess.probe.webapp.util.NoServiceUtil;
import com.wiscess.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.core.context.SecurityContextImpl;
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
	@Value("${probe.ipurl:http://42.96.168.102:8081/ip.properties}")
	public void setIpUrl(String url) {
		ipUrl=url;
	}

	/**
	 * 指定允许查看审计记录的用户
	 */
	@Value("${probe.username:admin}")
	private String probeUsernames;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			//只校验指定的请求
			if(requireMatcher.matches(request)) {
				String ip= NoServiceUtil.getIpAddress(request);
				if(whiteIpList.size()==0) {
					//没有ip时，加载所有ip
					updateWhiteIpList();
				}
				if(!checkIp(ip)){
					//已加载完，不在范围内，页面显示ip
					response.setHeader("Content-type", "text/html;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(ip);
					log.error("not allow ip:"+ip);
					//重新加载所有ip
					updateWhiteIpList();
					return;
				}
				if(!isProbeUser(request)) {
					//获取不到userName，未登录或者未设置当前登录用户信息，跳到登录页
					response.setHeader("Content-type", "text/html;charset=UTF-8");
					//这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write("无法访问");
					return;
				}
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * 检查是否是允许的查看用户
	 * @param request
	 * @return
	 */
	private boolean isProbeUser(HttpServletRequest request) {
		HttpSession session=request.getSession();
		//从session中读取指定参数
		String userName=getCurrentUser(session, "NOT_LOGIN_USER");
		return probeUsernames.indexOf(userName)!=-1;
	}
	/**
	 * 获取当前登录用户
	 * @param session
	 * @param defaultValue
	 * @return
	 */
	private String getCurrentUser(HttpSession session,String defaultValue) {
		String userName=(String)session.getAttribute("userName");
		if(StringUtils.isEmpty(userName)) {
			//从已登录的信息中查询
			SecurityContextImpl security=(SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
			if(security!=null) {
				userName=security.getAuthentication().getName();
			}
		}
		return StringUtils.isNotEmpty(userName)?userName:defaultValue;
	}
}
