package com.wiscess.query.config.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.wiscess.jpa.config.MultiDataSourceConfig;
import com.wiscess.query.config.Query;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@AutoConfigureBefore({MultiDataSourceConfig.class})
@ConditionalOnWebApplication
@ConditionalOnClass(Query.class)
@Order(1)
public class QueryAutoConfiguration {
	
	@Bean
	public Query query(){
		log.info("QueryAutoConfiguration init.");
		return Query.getInstance();
	}
}
