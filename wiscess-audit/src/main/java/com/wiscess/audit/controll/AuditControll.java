package com.wiscess.audit.controll;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.audit.jdbc.AuditService;
import com.wiscess.common.utils.RequestUtils;
import com.wiscess.utils.StringUtils;

/**
 * 审计日志查看控制器
 * @author wh
 *
 */
@Controller
@RequestMapping("/audit")
public class AuditControll {
	
	private static final Integer DEFAULT_PAGESIZE = 10;
	
	@Autowired
	private AuditService auditService;
	/**
	 * 页面上的参数及数据类型，默认为String
	 */
	public static String[] pageParameters=new String[]{
			"username",
			"sessionId","ip",
			"statusCode",
			"method",
			"url",
			"accessStartDate","accessEndDate",
			"timeMin","timeMax"};
	/**
	 * 指定允许查看审计记录的用户
	 */
	@Value("${audit.view.username:admin}")
	private String auditUsername;
	
	/**
	 * 检查是否是允许的查看用户
	 * @param request
	 * @return
	 */
	private boolean isAuditUser(HttpServletRequest request) {
		HttpSession session=request.getSession();
		//只允许指定用户查看审计记录
		//判断是否存在指定的审计用户
 		//从session中读取指定参数
		String userName=auditService.getCurrentUser(session, "NOT_LOGIN_USER");
		return auditUsername.indexOf(userName)!=-1;
	}
	@RequestMapping(value="/list")
	public String list(HttpServletRequest request,Model model) {
		if(!isAuditUser(request)) {
			//获取不到userName，未登录或者未设置当前登录用户信息，跳到登录页
			//未发现
			return "redirect:/login";
		}
		//获取页面上的参数
		Map<String, Object> map=RequestUtils.parseParameters(request, pageParameters);
				
		//根据参数，查询账号数据
		Page<Map<String, Object>> page=auditService.findAuditLogPage(map, getPageAble(request));
		model.addAttribute("page",page);
		model.addAllAttributes(map);
		return "/audit/list";
	}
	@RequestMapping(value="/report")
	public String report(HttpServletRequest request,Model model) {
		if(!isAuditUser(request)) {
			//获取不到userName，未登录或者未设置当前登录用户信息，跳到登录页
			//未发现
			return "redirect:/login";
		}
		//获取页面上的参数
		Map<String, Object> map=RequestUtils.parseParameters(request, pageParameters);
				
		//根据参数，查询账号数据
		Page<Map<String, Object>> page=auditService.findAuditLogReportPage(map, getPageAble(request));
		model.addAttribute("page",page);
		model.addAllAttributes(map);
		return "/audit/report";
	}
	private Integer getIntParameter(HttpServletRequest request,String paraName){
		String paraValue=request.getParameter(paraName);
		try{
			return new Integer(paraValue);
		}catch(Exception e){
			return null;
		}
	}
	private Pageable getPageAble(HttpServletRequest request){
		Integer pageNo = getIntParameter(request,"pageNo");
		Integer pageSize = getIntParameter(request, "pageSize");
		if(pageNo==null){
			pageNo=0;
		}
		if(pageSize==null){
			pageSize=DEFAULT_PAGESIZE;
		}
		return PageRequest.of(pageNo, pageSize,getSort(request));
	}
	private Sort getSort(HttpServletRequest request){
		String orderBy=(String)RequestUtils.parseParameters(request, "orderBy", String.class);
		if(StringUtils.isNotEmpty(orderBy)){
			String[] o=orderBy.split(" ",2);
			Sort sort=Sort.by(o[0]);
			if(o.length>1){
				if(o[1].equalsIgnoreCase("desc")){
					sort=new Sort(Direction.DESC,o[0]);
				}
			}
			return sort;
		}
		return null;
	}
}
