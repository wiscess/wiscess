package com.wiscess.filter.autoconfig;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件类型过滤器配置
 * @author wh
 */
@ConfigurationProperties(prefix = "filter.filetype")
public class FileTypeFilterProperties {
	private static final String DEFAULT_ERROR_PAGE = "/403.html";
	
	private String errorPage=DEFAULT_ERROR_PAGE;
	
	private List<String> urlPatterns;

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	public List<String> getUrlPatterns() {
		return urlPatterns;
	}

	public void setUrlPatterns(List<String> urlPatterns) {
		this.urlPatterns = urlPatterns;
	}

}
