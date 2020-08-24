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

import java.util.Map;

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
    public static Object jsonToBean(String jsonString,Class clazz) throws Exception{
    	return mapper.readValue(jsonString, clazz);
    }
    
}
