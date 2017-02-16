package com.wiscess.jpa.util;

import java.util.Map;

/**
 * sql result.
 * 
 * @author austin
 *
 */
public interface ISqlElement {
	/**
	 * get all avaliable parameters.
	 * @return
	 */
	public Object[] getParams();
	
	/**
	 * get all named avaliable parameters.
	 * @return
	 */
	public Map<String, Object> getParamsMap();
	
	/**
	 * get the sql processed.
	 * @return
	 */
	public String getSql();
}