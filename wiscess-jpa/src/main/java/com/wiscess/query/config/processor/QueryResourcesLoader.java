package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import com.wiscess.query.provider.IQueryProvider;

public class QueryResourcesLoader{

	private List<QueryResourceLoader> processors;
	
	public QueryResourcesLoader(){
		this.processors=SpringFactoriesLoader.loadFactories(QueryResourceLoader.class,
				getClass().getClassLoader());
	}
	
	public List<IQueryProvider> processor(Resource resource)
			throws IOException {
		for (QueryResourceLoader loader : this.processors) {
			if (canLoadFileExtension(loader, resource)) {
				return loader.load(resource);
			}
		}
		return null;
	}
	
	private boolean canLoadFileExtension(QueryResourceLoader loader, Resource resource) {
		String filename = resource.getFilename().toLowerCase();
		for (String extension : loader.getFileExtensions()) {
			if (filename.endsWith("." + extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
