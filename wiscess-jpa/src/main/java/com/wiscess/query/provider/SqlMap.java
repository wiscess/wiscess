/**
 * 
 */
package com.wiscess.query.provider;


import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wh
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SqlMap implements IQueryProvider{
	private Map<String, String> sqls = new HashMap<>();

	public String getQuery(String name) {
		return sqls.get(name);
	}
}
