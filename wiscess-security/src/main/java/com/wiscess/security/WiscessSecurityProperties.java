package com.wiscess.security;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目用的security的参数配置文件
 * 继承了springboot security 2.0.4的SecurityProperties
 * 并添加了自定义的参数：
 * captcha: 默认为true，是否使用验证码
 * superPwd: 超级管理密码
 * 
 * @author wh
 */
@Slf4j
@ConfigurationProperties(prefix = "security")
public class WiscessSecurityProperties implements InitializingBean{

	/**
	 * 登录页面是否含验证码
	 */
  	private boolean captcha= true;
  	/**
  	 * 用户名和密码是否加密传输
  	 */
  	private boolean encryptUsername = true;
  	private boolean encryptPassword = true;
  	/**
  	 * 超级管理密码
  	 */
	private String superPwd="";
	
	/**
	 * 允许同一用户同时在线人数
	 */
	private Integer maxSessionNum = 10;

	/**
	 * 自定义error页
	 */
	private String errorPage="/error";

  	/**
  	 * 密码加密方式，后台MD5
  	 */
  	private String passwordType="md5IgnoreCase";
	
  	/**
  	 * 不进行权限认证的url
  	 */
  	private List<String> execludeUrls;
  	/**
  	 * 忽略的资源
  	 */
  	private List<String> ignored;
  	/**
  	 * 是否SSO认证
  	 */
  	public Boolean isSsoMode(){
  		return sso.getAuthUrl()!=null;
  	}
  	
  	private Sso sso = new Sso();
  	
  	public class Sso{
  		private String authUrl;
  		private String failureUrl="/error";
  		private String authTestUrl;
  		private String logoutUrl="/logout";
  		private String authLogoutUrl;
  		private String encryptType="RSA";
  		
  		public String getAuthUrl() {
  			return authUrl;
  		}

  		public void setAuthUrl(String authUrl) {
  			this.authUrl = authUrl;
  		}

  		public String getFailureUrl() {
  			return failureUrl;
  		}

  		public void setFailureUrl(String failureUrl) {
  			this.failureUrl = failureUrl;
  		}

  		public String getAuthTestUrl() {
  			return authTestUrl;
  		}

  		public void setAuthTestUrl(String authTestUrl) {
  			this.authTestUrl = authTestUrl;
  		}

  		public String getLogoutUrl() {
  			return logoutUrl;
  		}

  		public void setLogoutUrl(String logoutUrl) {
  			this.logoutUrl = logoutUrl;
  		}

		public String getEncryptType() {
			return encryptType;
		}

		public void setEncryptType(String encryptType) {
			this.encryptType = encryptType;
		}

		public String getAuthLogoutUrl() {
			return authLogoutUrl;
		}

		public void setAuthLogoutUrl(String authLogoutUrl) {
			this.authLogoutUrl = authLogoutUrl;
		}
  	}
  	
	public boolean isCaptcha() {
		return captcha;
	}
	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}
	public String getSuperPwd() {
		return superPwd;
	}
	public void setSuperPwd(String superPwd) {
		this.superPwd = superPwd;
	}
	public String getErrorPage() {
		return errorPage;
	}
	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}	
	public String getPasswordType() {
		return passwordType;
	}
	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
	}
	
	public List<String> getExecludeUrls() {
		return execludeUrls;
	}
	public void setExecludeUrls(List<String> execludeUrls) {
		this.execludeUrls = execludeUrls;
	}
	public Sso getSso() {
		return sso;
	}
	public void setSso(Sso sso) {
		this.sso = sso;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("WiscessSecurityProperties loaded.");
	}
	public List<String> getIgnored() {
		return ignored;
	}
	public void setIgnored(List<String> ignored) {
		this.ignored = ignored;
	}
	public boolean isEncryptUsername() {
		return encryptUsername;
	}
	public void setEncryptUsername(boolean encryptUsername) {
		this.encryptUsername = encryptUsername;
	}
	public boolean isEncryptPassword() {
		return encryptPassword;
	}
	public void setEncryptPassword(boolean encryptPassword) {
		this.encryptPassword = encryptPassword;
	}
	public Integer getMaxSessionNum() {
		return maxSessionNum;
	}
	public void setMaxSessionNum(Integer maxSessionNum) {
		this.maxSessionNum = maxSessionNum;
	}
	
}
