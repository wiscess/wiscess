package com.wiscess.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.wechat.service.WechatBaseService;
import com.wiscess.wechat.service.WechatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WechatService.class)
public class WechatServiceAutoConfiguration {
	
	@Bean(name="wechatService")
	@ConditionalOnMissingBean(WechatService.class)
	public WechatService wechatService(){
		log.info("wechatBaseServiceConfiguration init.");
		WechatService wechatService=new WechatBaseService();
		return wechatService;
	}
	
}
