package com.wiscess.query.config.annotation.configuration;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.wiscess.cache.CacheClearable;
import com.wiscess.query.config.Query;

/**
 * 查询sql语句的适配器
 * @author wh
 * @param <O>
 *
 */
@Slf4j
@Order(100)
public class QueryConfigurerAdapter implements CacheClearable {

	private Query builder;
	
	private String filePattern="classpath:queryProviderMapping-*.xml";
	
	public void configureGlobal(Query query){
		query.addFilePattern(filePattern);
	}
	@Autowired
	public void init(){
		log.debug("QueryConfigurerAdapter init.");
		Query query=getBuilder();
		configureGlobal(query);
		//创建
		query.build();
	}
	/**
	 * 创建QueryBuilder
	 * @return
	 */
	public Query getBuilder() {
		if(builder!=null)
			return builder;
		builder=new Query();
		return builder;
	}

	@Override
	public void clearCache() {
		init();
	}
}
