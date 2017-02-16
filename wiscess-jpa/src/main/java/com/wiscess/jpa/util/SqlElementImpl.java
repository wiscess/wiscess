package com.wiscess.jpa.util;

import java.util.Map;

public class SqlElementImpl implements ISqlElement {
	private Object[] params;
	private Map<String, Object> paramsMap;
	private String sql;

	public Object[] getParams() {
		return params;
	}

	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}

	public String getSql() {
		return sql;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Object[] params) {
		this.params = params;
	}

	/**
	 * @param paramsMap the paramsMap to set
	 */
	public void setParamsMap(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

}
