package com.wiscess.query.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.wiscess.query.config.annotation.AbstractConfiguredQueryBuilder;
import com.wiscess.query.config.annotation.QueryBuilder;
import com.wiscess.query.provider.IQueryProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Query extends AbstractConfiguredQueryBuilder {

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
			Query.rebuild();
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
	@Override
	public QueryBuilder build(){
		//加载文件
		if(sqlResourceFiles!=null && sqlResourceFiles.size()>0){
			String[] files=(String[])sqlResourceFiles.toArray(new String[sqlResourceFiles.size()]);
			FileSystemXmlApplicationContext ctx = null;
			try{
				ctx=new FileSystemXmlApplicationContext(files);
				Map<String, IQueryProvider> map=ctx.getBeansOfType(IQueryProvider.class);
				
				queryProviderList=new ArrayList<IQueryProvider>();
				
				for(IQueryProvider queryProvider:map.values()){
					queryProviderList.add(queryProvider);
				}
			}finally{
				ctx.close();
			}
		}
		return this;
	}

	/**
	 * 重新刷新
	 */
	public static void rebuild()  {
		log.debug("Query.rebuild()");
		Query query=new Query();
		query.addFilePattern("classpath:queryProviderMapping-*.xml");
		query.build();
	}
}
