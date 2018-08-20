package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wiscess.query.provider.IQueryProvider;

public class XmlResourceLoader implements QueryResourceLoader{
	public String[] getFileExtensions() {
		return new String[] { "xml" };
	}

	public List<IQueryProvider> load(String location) throws IOException {
		List<IQueryProvider> queryProviderList=new ArrayList<>();
		
		ClassPathXmlApplicationContext ctx = null;
		try{
			ctx=new ClassPathXmlApplicationContext(location);
			Map<String, IQueryProvider> map=ctx.getBeansOfType(IQueryProvider.class);
			for(IQueryProvider queryProvider:map.values()){
				queryProviderList.add(queryProvider);
			}
		}finally{
			ctx.close();
		}
		return queryProviderList;
	}

}
