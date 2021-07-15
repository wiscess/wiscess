package com.wiscess.probe.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.probe.filter.WhiteIpFilter;

@Configuration
public class ProbeAutoConfiguration implements WebMvcConfigurer{
	
	/**
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(WhiteIpFilter.class)
	public WhiteIpFilter whiteIpFilter(){
		return new WhiteIpFilter();
	}

}
