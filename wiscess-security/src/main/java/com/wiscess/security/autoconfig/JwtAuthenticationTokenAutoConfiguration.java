package com.wiscess.security.autoconfig;

import com.wiscess.security.jwt.JwtAuthenticationTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 # jwt过滤器设置
 * @author wh
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(JwtAuthenticationTokenFilter.class)
@ConditionalOnProperty(prefix = "security.jwt", value = "enabled", matchIfMissing = true)
public class JwtAuthenticationTokenAutoConfiguration {

	/**
	 * 定义Xss过滤器
	 * @return
	 */
	@ConditionalOnMissingBean(JwtAuthenticationTokenFilter.class)
	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		log.info("JwtAuthenticationTokenFilter inited.");
		return new JwtAuthenticationTokenFilter();
	}
}
