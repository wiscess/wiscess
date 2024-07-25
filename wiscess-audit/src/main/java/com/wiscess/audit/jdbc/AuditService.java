package com.wiscess.audit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wiscess.jpa.config.MultiDataSourceConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import com.wiscess.audit.dto.AuditLog;
import com.wiscess.jpa.jdbc.JdbcJpaSupport;
import com.wiscess.jpa.util.ISqlElement;
import com.wiscess.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnWebApplication
@AutoConfiguration(after={MultiDataSourceConfig.class})
public class AuditService extends JdbcJpaSupport{

	/**
	 * 当前数据库名称
	 */
	private String currentDbName;
	/**
	 * 当前数据库类型
	 */
	private String currentDbType;
	/**
	 * 审计数据库名称
	 */
	private String auditDbName;
	/**
	 * 应用名称，从session中读取
	 */
	private String applicationName;
	
	/**
	 * 无须记录的参数
	 */
	public static List<String> notRecordParameterName=Arrays.asList("_csrf".split(","));
	
	/**
	 * 黑名单列表
	 */
	private List<String> blackIpList=new ArrayList<>();

	/**
	 * 检测并创建审计数据库和表
	 * @return
	 */
	public boolean initDatabase(String auditDbName) {
		log.debug("check audit database");
		try {
			//获取当前应用的数据库名称
			Connection con=getJdbcTemplate().getDataSource().getConnection();
			//获取当前数据库的产品名称
			currentDbType = con.getMetaData().getDatabaseProductName().toLowerCase();
			log.debug("current database type is {}",currentDbType);
			if(currentDbType.equals("microsoft sql server")) {
				currentDbType="sqlserver";
			}
			//根据数据库的产品，获取当前数据库名称
			if(currentDbType.equals("oracle")) {
				currentDbName=con.getMetaData().getUserName();
				log.debug("current username is {}",currentDbName);
			}else {
				currentDbName=con.getCatalog();
				log.debug("current database name is {}",currentDbName);
			}
			
			Map<String, Object> params=new HashMap<>();
			
			//未指定数据库名称时，使用当前数据库名称
			if(auditDbName==null || auditDbName.equals("")) {
				auditDbName=currentDbName;
				params.put("dbName", auditDbName);
			}else {
				params.put("dbName", auditDbName);
				//数据库名称不为空，检查数据库是否存在
				List<String> result=findList(currentDbType+".checkDatabaseIsExist", params);
				if(result.size()==0) {
					//数据库不存在，创建数据库
					updateBatch(currentDbType+".autoCreateDatabase",params);
					log.debug("auto create database {} success.",auditDbName);
				}
			}
			//数据库创建成功
			//检查表是否存在
			if((Integer)queryForObject(currentDbType+".checkTableIsExist", params,Integer.class)==0) {
				//表不存在，创建表
				updateBatch(currentDbType+".autoCreateTable",params);
				log.debug("auto create table audit_log success.");
			}
			//检查表是否存在
			if((Integer)queryForObject(currentDbType+".checkBlacklistTableIsExist", params,Integer.class)==0) {
				//表不存在，创建表
				updateBatch(currentDbType+".autoCreateBlacklistTable",params);
				log.debug("auto create table blacklist success.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		this.auditDbName=auditDbName;
		return true;
	}
	/**
	 * 审计记录列表
	 */
	private static List<AuditLog> auditList=new ArrayList<>();
	/**
	 * 生成审计日志，记录请求的参数
	 * @param request
	 * @return
	 */
	public AuditLog createAuditLog(HttpServletRequest request,boolean simples) {
		//从request中读取需要的数据，生成审计记录信息
		HttpSession session=request.getSession();
		String url=request.getRequestURI();
		String queryString=request.getQueryString();
		if(StringUtils.isNotEmpty(queryString)) {
			url+="?"+queryString;
			if(url.length()>2000){
				url=url.substring(0,2000);
			}
		}
		AuditLog log=AuditLog.builder()
				.applicationName(applicationName)
				.userId(getAttribute(session,"userId","-1"))
				.userName(getCurrentUser(session, "未登录"))
				.method(request.getMethod())
				.url(url)
				.remoteAddr(getIpAddress(request))
				.sessionId(request.getSession().getId())
				.parameters(getParameter(request,simples))
				.createTime(new Timestamp(System.currentTimeMillis()))
				.build();
		return log;
	}
	/**
	 * 补充请求完成后的响应数据，并保存审计记录
	 * @param request
	 * @param response
	 * @return
	 */
	public void saveLog(AuditLog log,HttpServletRequest request, HttpServletResponse response) {
		//获取请求错误码
		int status=response.getStatus();
		//当前时间
		long currentTime = System.currentTimeMillis();
		//请求开始时间
		long time=log.getCreateTime().getTime();
		//设置请求时间差
		log.setTimeCousuming(currentTime-time);
		//设置返回时间
		log.setReturnTime(new Timestamp(currentTime));
		//设置返回错误码
		log.setStatusCode(status);
		//保存
 		synchronized (AuditService.class) {
			auditList.add(log);
		}
	}
  	
	/**
	 * 从session中获取审计需要的数据
	 * @param session
	 * @param attr
	 * @param defaultValue
	 * @return
	 */
	private String getAttribute(HttpSession session,String attr,String defaultValue) {
		return session.getAttribute(attr)==null?defaultValue:session.getAttribute(attr).toString();
	}
	private String getIpAddress(HttpServletRequest request){
		String ip = "";
		try {
			ip = request.getHeader("x-real-ip");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("x-original-forwarded-for");
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getRemoteAddr();
				}
			} else if (ip.length() > 15) {
				String[] ips = ip.split(",");
				for (int index = 0; index < ips.length; index++) {
					String strIp = (String) ips[index];
					if (!("unknown".equalsIgnoreCase(strIp))) {
						ip = strIp;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}
	/**
	 * 获取当前登录用户
	 * @param session
	 * @param defaultValue
	 * @return
	 */
	public String getCurrentUser(HttpSession session,String defaultValue) {
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
	/**
	 * 获取请求的参数
	 * @param request
	 * @param simples 
	 * @return
	 */
	private String getParameter(HttpServletRequest request, boolean simples) {
		StringBuffer result=new StringBuffer(0);
		Map<String, String[]> map=request.getParameterMap();
		//URL上带的参数
		final String queryString=request.getQueryString();
		if(isMultipart(request)) {
			//有上传文件内容
			try {
				for (Part part : request.getParts()) {
					String name=part.getName();
					String headerValue = part.getHeader(HttpHeaders.CONTENT_DISPOSITION);
					ContentDisposition disposition = ContentDisposition.parse(headerValue);
					String filename = disposition.getFilename();
					if (StringUtils.isNotEmpty(filename)) {
						result.append(name);
						result.append("=");
						result.append(filename);
						result.append(";");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		/**
		 * 取参数值
		 */
		map.forEach((k,vs)->{
			if(queryString==null || !(queryString.endsWith(k) || queryString.indexOf(k+"&")!=-1 || queryString.indexOf(k+"=")!=-1)) {
				//请求串为空、请求串中以参数名结尾（如/login?error)、或请求串包含参数名（如/action?param1&param2=value2&...)
				if(!notRecordParameterName.contains(k)) {
					//需要记录的参数
					String values=getValues(vs);
					if(StringUtils.isNotEmpty(values)) {
						//只记录有值的参数
						result.append(k);
						result.append("=");
						result.append(simples?"...":values);
						result.append(";");
					}
				}
 			}
		});
		return result.toString();
	}
	
	public boolean isMultipart(HttpServletRequest request) {
		return org.springframework.util.StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/");
	}
	private String getValues(String[] vs) {
		StringBuffer result=new StringBuffer();
		int cn=0;
		for(String v:vs) {
			if(StringUtils.isNotEmpty(v)) {
				if(cn>0) {
					result.append(",");
				}
				result.append(v);
				cn++;
 			}
		}
		if(cn>1) {
			result.insert(0, "[");
			result.append("]");
		}
		return result.toString();
	}
	/**
	 * 批量保存日志
	 */
	public void batchSave() {
		List<AuditLog> newList=new ArrayList<>();
		synchronized (AuditService.class) {
			newList.addAll(auditList);
			auditList=new ArrayList<>();
		}
		//执行保存
		Map<String, Object> params=new HashMap<>();
		params.put("dbName", auditDbName);
		ISqlElement sql = processSql(params, currentDbType+".insertAuditLog");
		getJdbcTemplate().batchUpdate(sql.getSql(), newList, 100, new ParameterizedPreparedStatementSetter<AuditLog>() {
			@Override
			public void setValues(PreparedStatement ps, AuditLog log) throws SQLException {
				int i=1;
				ps.setString(i++, log.getApplicationName());
				ps.setString(i++, log.getUserId());
				ps.setString(i++, log.getUserName());
				ps.setString(i++, log.getMethod());
				ps.setString(i++, log.getUrl());
				ps.setString(i++, log.getRemoteAddr());
				ps.setString(i++, log.getSessionId());
				ps.setString(i++, log.getParameters());
				ps.setTimestamp(i++, log.getCreateTime());
				ps.setInt(i++, log.getStatusCode());
				ps.setTimestamp(i++, log.getReturnTime());
				ps.setLong(i++, log.getTimeCousuming());
			}
		});
	}

	/**
	 * 查询审计记录
	 * @param map
	 * @param pageAble
	 * @return
	 */
	public Page<Map<String, Object>> findAuditLogPage(Map<String, Object> map, Pageable pageable) {
		try {
			map.put("dbName", auditDbName);
			map.put("applicationName", applicationName);
			return findPage(currentDbType+".findAuditLogPage",map, pageable);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return new PageImpl<Map<String, Object>>(Collections.<Map<String, Object>> emptyList());
	}
	/**
	 * 查询审计统计数据
	 * @param map
	 * @param pageAble
	 * @return
	 */
	public Page<Map<String, Object>> findAuditLogReportPage(Map<String, Object> map, Pageable pageable) {
		try {
			map.put("dbName", auditDbName);
			map.put("applicationName", applicationName);
			return findPage(currentDbType+".findAuditLogReportPage",map, pageable);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return new PageImpl<Map<String, Object>>(Collections.<Map<String, Object>> emptyList());
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		if(StringUtils.isEmpty(applicationName)) {
			this.applicationName = currentDbName;
		}else {
			this.applicationName = applicationName;
		}
	}
	
	/**
	 * 显示黑名单列表
	 * @return
	 */
	public List<String> getBlacklist() {
		synchronized (blackIpList) {
			return blackIpList;
		}
	}
	/**
	 * 刷新黑名单列表
	 */
	public void refreshBlackIpList(Map<String, Object> map) {
		//先查询黑名单列表
		try {
			map.put("dbName", auditDbName);
			map.put("applicationName", applicationName);
			//查询5分钟之内的黑名单
			List<String> list=findList(currentDbType+".queryBlackIpList.in5Minutes", map,String.class);
			synchronized (blackIpList) {
				blackIpList.clear();
				blackIpList.addAll(list);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 判断是否在黑名单中
	 * @param ip
	 * @return
	 */
	public boolean isBlackip(String ip) {
		synchronized (blackIpList) {
			return blackIpList.contains(ip);
		}
	}
	/**
	 * 加入黑名单
	 * @param ip
	 */
	public void addBlacklist(String ip) {
		synchronized (blackIpList) {
			if(!blackIpList.contains(ip)) {
				//不存在时，插入到数据库中，只记录第一次
				//执行保存
				Map<String, Object> params=new HashMap<>();
				params.put("dbName", auditDbName);
				params.put("ip", ip);
				update(currentDbType+".insertBlacklist", params);
				blackIpList.add(ip);
			}
		}
	}
}
