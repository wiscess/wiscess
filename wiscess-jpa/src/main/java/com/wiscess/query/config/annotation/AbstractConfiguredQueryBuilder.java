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
	public QueryBuilder addFilePattern(String pattern) {
		sqlResourceFiles.add(pattern);
		return this;
	}

}
