package com.wiscess.query.config.annotation;

import java.util.List;

/**
 * 配置接口，可以添加资源文件
 * @author wh
 *
 * @param <B>
 */
public interface QueryConfigurer<B extends QueryBuilder> {

	B addFilePatterns(List<String> pattern);
}
