package com.wiscess.query.config.processor;

import java.io.IOException;
import java.util.List;

import com.wiscess.query.provider.IQueryProvider;

public interface QueryResourceLoader {

	public String[] getFileExtensions();

	public List<IQueryProvider> load(String location) throws IOException;
}

