package com.wiscess.query.provider;

import java.util.HashMap;
import java.util.Map;

public class QueryProviderMappingImpl implements IQueryProvider {
	private Map<String, String> queryMap = new HashMap<String, String>();

	public void setQueryMap(Map<String, String> queryMap) {
		this.queryMap = queryMap;
	}

	public String getQuery(String name) {
		return queryMap.get(name);
	}

}
