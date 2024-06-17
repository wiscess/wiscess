package com.wiscess.cmd.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.cmd.filter.CmdWhiteIpFilter;

@Configuration
@ConditionalOnWebApplication
public class CmdAutoConfiguration implements WebMvcConfigurer{
	
	/**
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(CmdWhiteIpFilter.class)
	public CmdWhiteIpFilter cmdWhiteIpFilter(){
		return new CmdWhiteIpFilter();
	}

}
