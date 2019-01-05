package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.filter.XssFilter;
import com.wiscess.filter.autoconfig.properties.XssFilterProperties;

/**
 * Xss过滤拦截器，对请求中的所有参数进行过滤
 * 可以通过参数配置设置排除列表
 * 
 #过滤器设置
filter:
  xss:
    isIncludeRichText: false              #是否过滤富文本框，对于指定参数名称为content或以WithHtml结尾的，认为是富文本框
    #默认值为false，表示富文本框不需要处理
    excludes:                             #已经默认排除了/css/**,/js/**,/images/**,/webjars/**
      /html/**
      /photo/**
      /attach/**

 * @author wh
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(XssFilter.class)
@EnableConfigurationProperties(XssFilterProperties.class)
@ConfigurationProperties(prefix = "filter.xss")
public class XssAutoConfiguration {
	
	private final XssFilterProperties properties;

	public XssAutoConfiguration(XssFilterProperties properties) {
		this.properties = properties;
	}
	/**
	 * 定义Xss过滤器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(XssFilter.class)
	public XssFilter xssFilter() {
		return new XssFilter(this.properties);
	}
}
