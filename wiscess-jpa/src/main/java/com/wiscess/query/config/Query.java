package com.wiscess.query.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wiscess.cache.CacheClearable;
import com.wiscess.query.config.processor.QueryResourcesLoader;
import com.wiscess.query.provider.IQueryProvider;

/**
 * sql语句缓存
 * @author wh
 */
public final class Query implements CacheClearable{
	
	private static Query query;
	
	/**
	 * 创建QueryBuilder
	 * @return
	 */
 	public static Query getInstance() {
		if(query!=null)
			return query;
		query=new Query();
		query.build();
		return query;
	}
 	
	private Query(){
		/**
		 * 添加所有sqls开头的yml文件和queryProviderMpping-开头的xml文件
		 */
		sqlResourceFiles.add("classpath*:sqls-*.yml");
		sqlResourceFiles.add("classpath*:sqls/*.yml");
		sqlResourceFiles.add("classpath*:sqls/*/*.yml");
		sqlResourceFiles.add("classpath*:query*.xml");
		sqlResourceFiles.add("classpath*:sqls/query*.xml");
		sqlResourceFiles.add("classpath*:sqls/*/query*.xml");
	}
	/**
	 * 缓存
	 */
	protected static List<IQueryProvider> queryProviderList=new ArrayList<IQueryProvider>();
	protected static List<String> sqlResourceFiles=new ArrayList<String>();
	
	/**
	 * 查询queryName
	 * @param queryName
	 * @return
	 */
	public static String getQuery(String queryName) {
		if(queryProviderList==null || queryProviderList.size()==0){
			getInstance();
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

	public Query build(){
		//加载文件
		if(sqlResourceFiles!=null && sqlResourceFiles.size()>0){
			//根据资源文件类型进行读取
			queryProviderList=new ArrayList<IQueryProvider>();
			QueryResourcesLoader loader=new QueryResourcesLoader();
			for (String location : sqlResourceFiles) {
				try {
					queryProviderList.addAll(loader.processor(location));
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return this;
	}

	@Override
	public void clearCache() {
		build();
	}
}
