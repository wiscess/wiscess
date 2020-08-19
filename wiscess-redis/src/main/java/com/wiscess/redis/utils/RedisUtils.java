package com.wiscess.redis.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONArray;
import com.wiscess.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisUtils {

//    @Autowired(required=true)
    private static RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
	private ApplicationContext context;
	
	@SuppressWarnings("unchecked")
	@Autowired
	public void init() {
		redisTemplate=context.getBean(RedisTemplate.class);
	}
	/**
	 * 设置字符串缓存
	 * @param key
	 * @param value
	 */
	public static void setString(String key,String value) {
		log.debug("设置Redis缓存：{}={}",key,value);
		redisTemplate.opsForValue().set(key, value);
	}
	 /**
     * 设置一个字符串值 过期时间为seconds秒
     * @param key
     * @param value
     * @param seconds 过期秒
     * @return
     */
    public static void setSecondsExpire(String key, String value, int seconds) {
    	redisTemplate.opsForValue().set(key,value,seconds, TimeUnit.SECONDS);
    }
    /**
     * 设置一个字符串值
     * @param key
     * @param value
     * @param timeout 过期时间
     * @return
     */
    public static void setExpire(String key, String value, int timeout, TimeUnit unit) {
    	redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
	/**
	 * 读取字符串缓存
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj==null?"":obj.toString();
	}
    /**
     * 删除KEY值
     * @param key
     * @return
     */
    public static void delKey(String key) {
    	redisTemplate.opsForValue().getOperations().delete(key);
    }
	/**
	 * 设置Map缓存
	 * @param key
	 * @param map
	 */
	public static void setMap(String key,Map<?, ?> map) {
		try {
			redisTemplate.opsForValue().set(key, JsonUtils.mapToJson(map));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取Map缓存
	 * @param key
	 * @return
	 */
	public static Map<?, ?> getMap(String key){
		try {
			Object object=redisTemplate.opsForValue().get(key);
			if(object!=null)
				return (Map<?, ?>)JsonUtils.jsonToMap((String)object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 设置List 无超时
	 * @param key
	 * @param list
	 */
	public static void setList(String key,List<?> list) {
		try {
			redisTemplate.opsForValue().set(key,JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置List，过期时间为seconds秒
	 * @param key
	 * @param list
	 */
	public static void setList(String key,List<?> list,int seconds) {
		setList(key, list,seconds,TimeUnit.SECONDS);
	}
	/**
	 * 设置List，过期时间为seconds秒
	 * @param key
	 * @param list
	 */
	public static void setList(String key,List<?> list,int timeout, TimeUnit unit ) {
		try {
			redisTemplate.opsForValue().set(key,JsonUtils.objectToJson(list),timeout,unit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取List缓存
	 * @param key
	 * @return
	 */
	public static List<?> getList(String key) {
		try {
			Object obj=redisTemplate.opsForValue().get(key);
			if(obj!=null) {
				return (List<?>)JsonUtils.jsonToBean((String)obj,List.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取List缓存
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<?> getList(String key,@SuppressWarnings("rawtypes") Class clazz) {
		try {
			Object obj=redisTemplate.opsForValue().get(key);
			if(obj!=null) {
				return (List<?>)JSONArray.parseArray((String)obj, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置Object
	 * @param key
	 * @param list
	 */
	public static void setObject(String key,Object obj) {
		try {
			redisTemplate.opsForValue().set(key,JsonUtils.objectToJson(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取List缓存
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(String key,Class<T> clazz) {
		try {
			return (T)JsonUtils.jsonToBean((String)redisTemplate.opsForValue().get(key),clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Set<String> keys(String pattern){
		return redisTemplate.keys(pattern);
	}
	
	/**
	 * 添加Json数据到List中
	 */
	public static void rightPushList(String key,String json) {
        redisTemplate.opsForList().rightPush(key,json);
	}

	/**
	 * 取出list中的数据
	 * @param key
	 * @return
	 */
    public static String leftPopList(String key) {
    	return  (String)redisTemplate.opsForList().leftPop(key);
    }
    
    /**
     * 获取list的size
     * @param key
     * @return
     */
	public static Long listSize(String key) {
    	return redisTemplate.opsForList().size(key);
    }

	/**
	 * 添加Json数据到List中
	 */
	public static void rightPush(String key,Object obj) {
        redisTemplate.opsForList().rightPush(key,obj);
	}

	/**
	 * 取出list中的数据
	 * @param key
	 * @return
	 */
    public static Object leftPop(String key) {
    	return  redisTemplate.opsForList().leftPop(key);
    }
    
	public static <T> List<T> range(String key,long begin,long end){
		 return (List<T>)redisTemplate.opsForList().range(key, begin, end);
	}

	/**
	 * 取出所有的list
	 * @param key
	 * @return
	 */
    public static List<String> popAllList(String key) {
    	long size=listSize(key);
    	List<String > list=new ArrayList<String>();
    	while(size>0) {
    		list.add( (String)redisTemplate.opsForList().leftPop(key));
    		size=listSize(key);
    	}
    	return list;
    }
}
