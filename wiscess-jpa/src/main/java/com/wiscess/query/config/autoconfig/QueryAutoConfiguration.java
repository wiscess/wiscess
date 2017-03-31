package com.wiscess.query.config.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.query.config.Query;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(QueryProviderProperties.class)
@ConditionalOnWebApplication
@ConditionalOnClass(Query.class)
@ConditionalOnProperty(prefix = "query.file", value = "enabled", matchIfMissing = true)
public class QueryAutoConfiguration {
	
	private final QueryProviderProperties properties;
	
	public QueryAutoConfiguration(QueryProviderProperties properties){
		this.properties=properties;
	}
	
	@Bean
	@ConditionalOnMissingBean(Query.class)
	public Query query(){
		log.debug("QueryAutoConfiguration init.");
		Query query=Query.getBuilder();
		query.addFilePatterns(this.properties.getFilePatterns());
		//创建
		query.build();
		return query;
	}
}
