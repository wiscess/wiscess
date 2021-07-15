package com.wiscess.audit.autoconfig.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 审计参数配置
 * @author wh
 */
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
	
	/**
	 * 是否启用审计功能
	 */
	private boolean enable=true;
	
	/**
	 * 审计数据库的名称，如果为空，则默认使用当前应用的主数据源，如果不为空，则应填写数据库的前缀
	 * 如：mysql数据库，可以写audit;sqlserver数据库，可以写audit;
	 */
	private String databaseName;
	
	/**
	 * 审计哪些method，默认所有
	 */
	private String auditMethod="*";
	
	/**
	 * 排除审计的资源
	 */
	private List<String> excludes=new ArrayList<>();
	
	/**
	 * 忽略参数的请求
	 * since 2.0
	 */
	private List<String> simples=new ArrayList<>();
	
	/**
	 * 审计记录的级别
	 * 0：最低级，只记录基本的访问请求，不含排除资源和method，忽略参数不做记录；
	 * 1：包含排除资源的访问，不含method，忽略参数不做记录；
	 * 2：记录所有资源请求和method，忽略参数不做记录；
	 * 3：记录所有资源请求和method，记录完整的参数；
	 * since 2.0
	 */
	private Integer auditLevel=0;
	
	/**
	 * 当前应用名称，默认值为当前数据库名称
	 */
	private String application;
	
	/**
	 * 是否启用黑名单过滤功能
	 */
	private boolean blackIp = true;
	
	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getAuditMethod() {
		return auditMethod;
	}

	public void setAuditMethod(String auditMethod) {
		this.auditMethod = auditMethod;
	}

	public Integer getAuditLevel() {
		return auditLevel;
	}

	public void setAuditLevel(Integer auditLevel) {
		this.auditLevel = auditLevel;
	}

	public List<String> getSimples() {
		return simples;
	}

	public void setSimples(List<String> simples) {
		this.simples = simples;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public boolean isBlackIp() {
		return enable && blackIp;
	}

	public void setBlackIp(boolean blackIp) {
		this.blackIp = blackIp;
	}
}
