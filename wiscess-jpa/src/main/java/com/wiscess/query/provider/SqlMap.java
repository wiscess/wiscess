/**
 * 
 */
package com.wiscess.query.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author audin
 *
 */
public class SqlMap implements IQueryProvider{
	private Map<String, String> sqls = new HashMap<>();
	
	@Override
	public String getQuery(String name) {
		return sqls.get(name);
	}

	public Map<String, String> getSqls() {
		return sqls;
	}

	public void setSqls(Map<String, String> sqls) {
		this.sqls = sqls;
	}
}
