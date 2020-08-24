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
	@Builder.Default
	private Map<String, String> queryMap = new HashMap<String, String>();

	@Override
	public String getQuery(String name) {
		return queryMap.get(name);
	}
}
