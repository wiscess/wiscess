package com.wiscess.security.autoconfig;

import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "security")
public class SecurityCsrfSettings extends SecurityProperties{

	private String deniedPage="/deny";
	
	private String superPwd="";
	
	private final Csrf csrf = new Csrf();
	
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
}
