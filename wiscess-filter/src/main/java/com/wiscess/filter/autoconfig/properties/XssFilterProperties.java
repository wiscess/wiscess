package com.wiscess.filter.autoconfig.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 防XSS过滤器配置
 * @author wh
 */
@ConfigurationProperties(prefix = "filter.xss")
public class XssFilterProperties {
	
	private List<String> excludes=new ArrayList<>();
	
	private Boolean isIncludeRichText=false;
	
	private List<String> allowHosts=new ArrayList<>();

	public Boolean getIsIncludeRichText() {
		return isIncludeRichText;
	}

	public void setIsIncludeRichText(Boolean isIncludeRichText) {
		this.isIncludeRichText = isIncludeRichText;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public List<String> getAllowHosts() {
		return allowHosts;
	}

	public void setAllowHosts(List<String> allowHosts) {
		this.allowHosts = allowHosts;
	}
}
