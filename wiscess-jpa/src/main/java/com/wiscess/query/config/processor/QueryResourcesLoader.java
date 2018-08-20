package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.support.SpringFactoriesLoader;

import com.wiscess.query.provider.IQueryProvider;

public class QueryResourcesLoader{

	private List<QueryResourceLoader> processors;
	
	public QueryResourcesLoader(){
		this.processors=SpringFactoriesLoader.loadFactories(QueryResourceLoader.class,
				getClass().getClassLoader());
	}
	
	public List<IQueryProvider> processor(String location)
			throws IOException {
		for (QueryResourceLoader loader : this.processors) {
			if (canLoadFileExtension(loader, location)) {
				return loader.load(location);
			}
		}
		return null;
	}
	
	private boolean canLoadFileExtension(QueryResourceLoader loader, String location) {
		String filename = location.toLowerCase();
		for (String extension : loader.getFileExtensions()) {
			if (filename.endsWith("." + extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
