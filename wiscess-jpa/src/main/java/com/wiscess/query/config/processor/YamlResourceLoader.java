package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.wiscess.query.provider.IQueryProvider;
import com.wiscess.query.provider.SqlMap;

/**
 * yml文件处理器
 * 处理sql语句，yml资源文件中可以直接使用sqlName，或者用sqls
 * 推荐直接写sqlname的形式
 * 
 * @author wh
 */
public class YamlResourceLoader implements QueryResourceLoader{

	public String[] getFileExtensions() {
		return new String[] { "yml", "yaml" };
	}
	@SuppressWarnings("unchecked")
	public List<IQueryProvider> load(String location) throws IOException {
		List<IQueryProvider> queryProviderList=new ArrayList<>();
		Resource[] resources = new PathMatchingResourcePatternResolver().getResources(location);
		if(resources.length>0){
		    YamlMapFactoryBean yaml=new YamlMapFactoryBean();
		    yaml.setResources(resources);
		    Map<String, String> map=new HashMap<>();
		    yaml.getObject().forEach((k,v)->{
		    	if(k.equals("sqls") && v instanceof Map){
		    		map.putAll((Map<String, String>)v);
		    	}else if(v instanceof Map) {
		    		for(String key:((Map<String, String>)v).keySet()) {
		    			map.put(k+"."+key, ((Map<String, String>)v).get(key));
		    		}
		    	}else{
			    	if(v!=null)
			    		map.put(k, v.toString());	
		    	}
		    });
			queryProviderList.add(SqlMap.builder().sqls(map).build());
			return queryProviderList;
		}
		return Collections.EMPTY_LIST;
	}
}
