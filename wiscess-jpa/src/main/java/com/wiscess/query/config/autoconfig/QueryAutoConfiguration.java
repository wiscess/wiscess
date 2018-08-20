package com.wiscess.query.config.autoconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.wiscess.jpa.config.MultiDataSourceConfig;
import com.wiscess.query.config.Query;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@AutoConfigureBefore({MultiDataSourceConfig.class})
@EnableConfigurationProperties(QueryProperties.class)
@ConditionalOnWebApplication
@ConditionalOnClass(Query.class)
@Order(1)
public class QueryAutoConfiguration {
	@Autowired
	private QueryProperties properties;
	
	@Bean
	public Query query(){
		log.info("QueryAutoConfiguration init.");
		Query query=Query.getBuilder();
		//创建
		query.addFilePatterns(properties.getFiles());
		query.build();
		return query;
	}

}
