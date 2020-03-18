package com.wiscess.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.wechat.webapp.action.WeChatMenuControll;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnWebApplication
public class WeChatMenuAutoConfiguration {
	
	@Bean(name="weChatMenuController")
	@ConditionalOnMissingBean(WeChatMenuControll.class)
	public WeChatMenuControll weChatMenuController() {
		log.info("WeChatMenuController init.");
		return new WeChatMenuControll();
	}
}
