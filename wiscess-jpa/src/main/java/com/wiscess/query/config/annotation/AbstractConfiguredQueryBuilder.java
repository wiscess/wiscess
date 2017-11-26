package com.wiscess.query.config.annotation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfiguredQueryBuilder implements QueryConfigurer<QueryBuilder>,QueryBuilder{
	
	/**
	 * 配置文件列表
	 */
	protected static List<String> sqlResourceFiles=new ArrayList<String>();
	/**
	 * 添加资源文件
	 */
	public QueryBuilder addFilePatterns(List<String> patterns) {
		if(patterns==null){
			patterns=new ArrayList<>();
			patterns.add("classpath*:queryProviderMapping-*.xml");
		}else{
			for(String p:patterns){
				if(!p.startsWith("classpath")){
					patterns.set(patterns.indexOf(p), "classpath*:"+p);
				}
			}
		}
		sqlResourceFiles.addAll(patterns);
		return this;
	}
}
