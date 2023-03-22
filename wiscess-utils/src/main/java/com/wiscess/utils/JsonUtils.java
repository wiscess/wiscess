/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: JsonUtils
 * Author:   wh
 * Date:     2020/2/24 11:04
 * Description: Json工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();
	
    /**
     * 将map对象的内容转换成json
     * @param map
     * @return
     */
    public static String mapToJson(Map map)throws Exception{
    	// 将对象转成字符串
		return mapper.writeValueAsString(map);
    }
    /**
     *  将对象转成字符串
     * @param obj
     * @return
     * @throws Exception
     */
    public static String objectToJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
    
    /**
     *  将Map转成指定的Bean
     * @param map
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object mapToBean(Map map, Class clazz) throws Exception {
        return mapper.readValue(objectToJson(map), clazz);
    }
    /**
     *  将Bean转成Map
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map beanToMap(Object obj) throws Exception {
        return mapper.readValue(objectToJson(obj), Map.class);
    }
    
    /**
     * 字符串转map
     * @param jsonString
     * @return
     */
    public static Map jsonToMap(String jsonString) throws Exception{
    	return mapper.readValue(jsonString, Map.class);
    }
    /**
     * 字符串转Bean
     * @param jsonString
     * @return
     */
    @Deprecated
    public static Object jsonToBean(String jsonString,Class clazz) throws Exception{
    	return mapper.readValue(jsonString, clazz);
    }

    /**
     * 转换成未知对象
     * @param json
     * @return
     */
    public static Object parseObject(String json){
        return JSON.parseObject(json);
    }

    /**
     * 转换成已知对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json,Class<T> clazz){
        return JSONObject.parseObject(json,clazz);
    }

    /**
     * 转换成数组
     * @param json
     * @return
     */
    public static JSONArray parseArray(String json){
        return JSON.parseArray(json);
    }
    public static List<?> parseArray(String json,Class<?> clazz){
        return JSON.parseArray(json,clazz);
    }
    
    /**
     * 转成成List
     * @param json
     * @return
     */
    public static List<Map<String, Object>> jsonToList(String json){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();

		if(!StringUtils.isEmpty(json)){
			JSONArray array = JsonUtils.parseArray(json);
			
			int total=array.size();
			for(int i=0;i<total;i++) {
				list.add(array.getJSONObject(i));
			}
		}
		return list;
	}
}
