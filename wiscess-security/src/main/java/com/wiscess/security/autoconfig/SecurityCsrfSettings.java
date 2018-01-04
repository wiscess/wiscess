package com.wiscess.security.autoconfig;

import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "security")
public class SecurityCsrfSettings extends SecurityProperties{

	private String deniedPage="/deny";
	
	private String superPwd="";
	
	private boolean useJdbc=true;
	//默认带验证码
  	private boolean captcha= true;
  	//密码加密方式，前台RSA，后台MD5
  	private String passwordType="RSAMD5";

	private final Csrf csrf = new Csrf();
	
	private final JdbcAuthentication jdbc = new JdbcAuthentication();
	
	public static class Csrf{
		
		private List<String> execludeUrls;

		public List<String> getExecludeUrls() {
			return execludeUrls;
		}

		public void setExecludeUrls(List<String> execludeUrls) {
			this.execludeUrls = execludeUrls;
		}
	}

	public Csrf getCsrf() {
		return csrf;
	}

	/**
	 * 使用jdbc验证方式
	 * @author wh
	 *
	 */
	public static class JdbcAuthentication{
		
	    //查询用户的sql
	  	private String userQuery="";
		//查询权限的sql  
	  	private String authQuery="";
	  	
		public String getUserQuery() {
			return userQuery;
		}
		public void setUserQuery(String userQuery) {
			this.userQuery = userQuery;
		}
		public String getAuthQuery() {
			return authQuery;
		}
		public void setAuthQuery(String authQuery) {
			this.authQuery = authQuery;
		}
	}
	public JdbcAuthentication getJdbcAuthentication(){
		return jdbc;
	}
	public boolean isCaptcha() {
		return captcha;
	}
	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}
	
	public String getDeniedPage() {
		return deniedPage;
	}

	public void setDeniedPage(String deniedPage) {
		this.deniedPage = deniedPage;
	}

	public String getSuperPwd() {
		return superPwd;
	}

	public void setSuperPwd(String superPwd) {
		this.superPwd = superPwd;
	}
	public String getPasswordType() {
		return passwordType;
	}
	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
	}
	public boolean useJdbc() {
		return useJdbc;
	}
	public void setUseJdbc(boolean useJdbc) {
		this.useJdbc = useJdbc;
	}

}
