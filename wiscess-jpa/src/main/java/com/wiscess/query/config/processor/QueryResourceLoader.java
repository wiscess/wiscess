package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;

import com.wiscess.query.provider.IQueryProvider;

public interface QueryResourceLoader {

	public String[] getFileExtensions();

	public List<IQueryProvider> load(Resource resource) throws IOException;
}

