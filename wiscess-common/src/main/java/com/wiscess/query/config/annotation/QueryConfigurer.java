package com.wiscess.query.config.annotation;

/**
 * 配置接口，可以添加资源文件
 * @author wh
 *
 * @param <B>
 */
public interface QueryConfigurer<B extends QueryBuilder> {

	B addFilePattern(String pattern);
}
