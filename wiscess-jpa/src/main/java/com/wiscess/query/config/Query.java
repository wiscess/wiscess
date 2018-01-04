package com.wiscess.query.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.wiscess.cache.CacheClearable;
import com.wiscess.query.config.processor.QueryResourcesLoader;
import com.wiscess.query.provider.IQueryProvider;

public final class Query implements CacheClearable{
	
	private static Query builder;
	
	/**
	 * 创建QueryBuilder
	 * @return
	 */
 	public static Query getBuilder() {
		if(builder!=null)
			return builder;
		builder=new Query();
		return builder;
	}
	
	/**
	 * 缓存
	 */
	protected static List<IQueryProvider> queryProviderList=new ArrayList<IQueryProvider>();
	/**
	 * 查询queryName
	 * @param queryName
	 * @return
	 */
	public static String getQuery(String queryName) {
		if(queryProviderList==null || queryProviderList.size()==0){
			getBuilder().build();
		}
		if(queryProviderList!=null){
			for (IQueryProvider p : queryProviderList) {
				String q = p.getQuery(queryName);
				if (q != null) {
					return q;
				}
			}
		}
		return null;
	}
	private static List<String> sqlResourceFiles=new ArrayList<String>();
	/**
	 * 添加资源文件
	 */
	public void addFilePatterns(List<String> patterns) {
		if(patterns==null){
			patterns=new ArrayList<>();
			patterns.add("classpath:queryProviderMapping-*.xml");
		}else{
			for(String p:patterns){
				if(!p.startsWith("classpath")){
					patterns.set(patterns.indexOf(p), "classpath:"+p);
				}
			}
		}
		sqlResourceFiles.addAll(patterns);
	}
	public Query build(){
		//加载文件
		if(sqlResourceFiles!=null && sqlResourceFiles.size()>0){
			//根据资源文件类型进行读取
			queryProviderList=new ArrayList<IQueryProvider>();
			QueryResourcesLoader loader=new QueryResourcesLoader();
			ResourceLoader resourceLoader=new DefaultResourceLoader();
			for (String location : sqlResourceFiles) {
				Resource resource = resourceLoader.getResource(location);
				try {
					queryProviderList.addAll(loader.processor(resource));
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return this;
	}

	@Override
	public void clearCache() {
		getBuilder().build();
	}
}
