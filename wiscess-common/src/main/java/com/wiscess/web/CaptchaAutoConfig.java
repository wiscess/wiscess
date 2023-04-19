package com.wiscess.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

/**
 * @author wanghai
 */
@AutoConfiguration
@EnableConfigurationProperties
public class CaptchaAutoConfig {
	/**
	 * 验证码的配置
	 * @return
	 */
	@Bean
	public Config captchaConfig() {
		Properties properties = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("captcha.properties");
		try {
			properties.load(is);
		} catch (IOException e) {
		}
		return new Config(properties);
	}
	@Bean
	public Producer captchaProducer() {
		return captchaConfig().getProducerImpl();
	}
	
}
