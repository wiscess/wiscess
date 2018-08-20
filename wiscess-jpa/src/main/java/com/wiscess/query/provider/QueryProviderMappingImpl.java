package com.wiscess.query.provider;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QueryProviderMappingImpl implements IQueryProvider {
	private Map<String, String> queryMap = new HashMap<String, String>();

	public String getQuery(String name) {
		return queryMap.get(name);
	}
}
