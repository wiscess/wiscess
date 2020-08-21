package com.wiscess.oauth.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

	private List<String> ignored;
}
