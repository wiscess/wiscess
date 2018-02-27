package com.wiscess.wechat.util;

import net.sf.json.JSONObject;

/**
 * 基础工具类
 * @author liudongge
 *
 */
public class BaseUtil {

	public static Integer getInt(JSONObject jsonObject,String key){
		if(jsonObject.containsKey(key))
			return jsonObject.getInt(key);
		return null;
	}
	
	public static String getString(JSONObject jsonObject,String key){
		if(jsonObject.containsKey(key))
			return jsonObject.getString(key);
		return null;
	}
	
}
