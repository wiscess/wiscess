package com.wiscess.filter.autoconfig.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 静态资源配置
 * @author wh
 */
@ConfigurationProperties(prefix = "spring.resources")
public class StaticResourceProperties {
	
	private List<String> location=new ArrayList<>();

	public List<String> getLocation() {
		return location;
	}

	public void setLocation(List<String> location) {
		this.location = location;
	}

}
