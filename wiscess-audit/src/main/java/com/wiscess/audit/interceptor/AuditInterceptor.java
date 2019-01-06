package com.wiscess.audit.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wiscess.audit.autoconfig.properties.AuditProperties;
import com.wiscess.audit.jdbc.AuditService;

/**
 * 审计功能拦截器
 * @author wh
 *
 */
@Deprecated
public class AuditInterceptor implements HandlerInterceptor{
//	private AuditService auditService;
	
	public AuditInterceptor(AuditProperties properties, AuditService auditService) {
//		this.auditService=auditService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
//		AuditLog log=auditService.createAuditLog(request);
//		log.setApplicationName("interceptor");
//		request.setAttribute("AUDIT_LOG",log);
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
//		AuditLog log=(AuditLog)request.getAttribute("AUDIT_LOG");
//		auditService.saveLog(log, request, response);
	}
}
