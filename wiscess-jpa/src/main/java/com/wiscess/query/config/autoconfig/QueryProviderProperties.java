package com.wiscess.query.config.autoconfig;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QueryProvider文件类型
 * @author wh
 *
 */
@ConfigurationProperties(prefix = "query.file")
public class QueryProviderProperties {
	
	private List<String> filePatterns;
	
	public List<String> getFilePatterns() {
		return filePatterns;
	}
	public void setFilePatterns(List<String> filePatterns) {
		this.filePatterns = filePatterns;
	}

}
