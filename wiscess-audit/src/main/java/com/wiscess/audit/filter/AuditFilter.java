package com.wiscess.audit.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wiscess.audit.autoconfig.properties.AuditProperties;
import com.wiscess.audit.dto.AuditLog;
import com.wiscess.audit.jdbc.AuditService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wh
 */
@Slf4j
public class AuditFilter extends OncePerRequestFilter implements OrderedFilter{
	/**
	 * 过滤器顺序
	 */
	private int AUDIT_FILTER_ORDER = -9999;
	
	private AuditService auditService;
	
	/**
	 * 是否启用
	 */
	private boolean enabled;
	/**
	 * 默认静态资源文件
	 */
	private static String[] DEFAULT_IGNORES="/css/**,/js/**,/bower_components/**,/less/**,/images/**,/img/**,/webjars/**,/**/favicon.ico,/captcha.jpg,/accessdenied,/error,/deny,/audit/list,/audit/report".split(",");
	/**
	 * 排除的资源
	 */
	private List<String> excludes = new ArrayList<>();
	/**
	 * 排除资源的匹配器
	 */
	private RequestMatcher excludeMatcher = null;

	/**
	 * 不记录参数值的资源
	 */
	private List<String> simples= new ArrayList<>();
	private RequestMatcher ignoreMatcher = null;
	
	/**
	 * 是否启用黑名单功能
	 */
	private boolean blackIpEnabled = true;
	private List<String> blackUrls = new ArrayList<>();
	private RequestMatcher blacklistMatcher = null;
	/**
	 * 审计的method
	 */
	private Pattern auditMethods;//=Pattern.compile("^(GET|POST|HEAD|TRACE|OPTIONS|PUT|DELETE|PATCH)$");
	
	/**
	 * 审计等级
	 */
	private Integer auditLevel;
	
	public AuditFilter(AuditProperties properties,AuditService auditService){
		log.debug("audit filter configuration.");
		this.auditService = auditService;
		//增加默认的资源
		excludes.addAll(Arrays.asList(DEFAULT_IGNORES));

		//增加附加的资源
		excludes.addAll(properties.getExcludes());
		//
		List<RequestMatcher> matchers=new ArrayList<>();
		excludes.forEach(url->{
			matchers.add(new AntPathRequestMatcher(url));
		});
		excludeMatcher=new OrRequestMatcher(matchers);
		
		//忽略参数的资源
		simples.addAll(properties.getSimples());
		List<RequestMatcher> ignoreMatchers=new ArrayList<>();
		simples.forEach(url->{
			ignoreMatchers.add(new AntPathRequestMatcher(url));
		});
		if(!ignoreMatchers.isEmpty())
			ignoreMatcher=new OrRequestMatcher(ignoreMatchers);
		//黑名单url的资源
		blackUrls.addAll(properties.getBlackUrls());
		List<RequestMatcher> blacklistMatchers=new ArrayList<>();
		blackUrls.forEach(url->{
			blacklistMatchers.add(new AntPathRequestMatcher(url));
		});
		if(!blacklistMatchers.isEmpty())
			blacklistMatcher=new OrRequestMatcher(blacklistMatchers);
		
		//审计哪些方法
		String auditMethod=properties.getAuditMethod();
		if(auditMethod==null || auditMethod.equals("*")) {
			//未设置时审计所有的方法
			auditMethod="GET|POST|HEAD|TRACE|OPTIONS|PUT|DELETE|PATCH";
		}
		auditMethods=Pattern.compile("^("+auditMethod+")$");
		
		if(properties.isEnable()) {
	    	//初始化数据库信息,根据数据库类型和数据库名，查询审计表是否存在，如果不存在则创建审计表，创建成功则返回true，失败返回false，不启用审计
			enabled=auditService.initDatabase(properties.getDatabaseName());
	    }else {
	    	enabled=false;
	    }
		//应用名
		auditService.setApplicationName(properties.getApplication());
		auditLevel=properties.getAuditLevel();
		blackIpEnabled=properties.isBlackIp();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		AuditLog auditLog=null;
		try {
			String ip = AuditService.getIpAddress(request);
			if(isMatches(blacklistMatcher,request)) {
				//该请求认定为攻击访问，直接加入黑名单
				auditService.addBlacklist(ip);
				//输出访问被拒绝的页面
//				response.sendError(444, "error");
//				return;
			}
			//初始化审计日志数据
			if(enabled) {
				//启用审计功能
				if(auditLevel==0 && !isMatches(excludeMatcher,request) && auditMethods.matcher(request.getMethod()).matches()) {
					//最低级，只记录基本的访问请求，不含排除资源和method，忽略参数不做记录
					auditLog=auditService.createAuditLog(request,isMatches(ignoreMatcher,request));
				}else if(auditLevel==1 && auditMethods.matcher(request.getMethod()).matches()) {
					//包含排除资源的访问，不含method，忽略参数不做记录
					auditLog=auditService.createAuditLog(request,isMatches(ignoreMatcher,request));
				}else if(auditLevel==2) {
					//记录所有资源请求和method，忽略参数不做记录；
					auditLog=auditService.createAuditLog(request,isMatches(ignoreMatcher,request));
				}else if(auditLevel==3) {
					//记录所有资源请求和method，记录完整的参数；
					auditLog=auditService.createAuditLog(request,false);
				}
			}
			//判断是否在黑名单中
			if(blackIpEnabled && auditService.isBlackip(ip)) {
				//输出访问被拒绝的页面
				response.sendError(444, "error");
				return;
			}
			//执行下一个过滤器
			filterChain.doFilter(request, response);
		}finally {
			if(auditLog!=null) {
				auditService.saveLog(auditLog, request, response);
			}
		}
	}
	
	/**
	 * 判断是否匹配
	 * @param matcher
	 * @param request
	 * @return
	 */
	private boolean isMatches(RequestMatcher matcher,HttpServletRequest request) {
		if(matcher==null)
			return false;
		return matcher.matches(request);
	}
	@Override
	public int getOrder() {
		return AUDIT_FILTER_ORDER;
	}
	
	/**
	 * 输出拒绝页面
	 * @param request
	 * @param response
	 */
	public void writeAccessDenied(HttpServletRequest request, HttpServletResponse response) {
		if (response.isCommitted()) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		if (response.getContentType() == null) {
			response.setContentType("text/html");
		}
		builder.append("<html><body><h1>访问被拒绝</h1>")
				.append("<p>您的IP在短时间内多次访问服务器，访问被拒绝。</p>")
				.append("<div>您的IP："+request.getRemoteAddr()+"已被加入黑名单，如有异议，请联系系统管理员。")
				.append("</div>");
		builder.append("</body></html>");
		try {
			response.setCharacterEncoding("utf-8");
			response.getWriter().append(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
