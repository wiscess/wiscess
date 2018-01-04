package com.wiscess.query.config.autoconfig;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Query文件类型
 * @author wh
 *
 */
@ConfigurationProperties(prefix = "query")
public class QueryProperties {
	
	private List<String> files;
	
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}

}
