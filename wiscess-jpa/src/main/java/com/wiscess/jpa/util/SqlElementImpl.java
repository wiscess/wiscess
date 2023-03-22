package com.wiscess.jpa.util;

import java.util.Map;

public class SqlElementImpl implements ISqlElement {
	private Object[] params;
	private Map<String, Object> paramsMap;
	private int[] argTypes;
	private String sql;

	public Object[] getParams() {
		return params;
	}

	@Override
	public int[] getArgTypes() {
		return argTypes;
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

	public void setArgTypes(int[] argTypes) {
		this.argTypes = argTypes;
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
