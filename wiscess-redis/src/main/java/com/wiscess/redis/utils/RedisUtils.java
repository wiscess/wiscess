package com.wiscess.redis.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import jakarta.annotation.Priority;

import com.alibaba.fastjson2.JSON;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.wiscess.redis.configuration.RedisConfig;
import com.wiscess.utils.JsonUtils;
import com.wiscess.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Slf4j
@Configuration
@AutoConfigureAfter(RedisConfig.class)
@Priority(-999)
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RedisUtils(RedisTemplate _redisTemplate) {
		RedisUtils.redisTemplate=_redisTemplate;
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
				return (List<?>) JSON.parseArray((String)obj, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置Object
	 * @param key
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
	public static <T> T getObject(String key,Class<T> clazz) {
		try {
			String jsonStr=(String)redisTemplate.opsForValue().get(key);
			if(StringUtils.isEmpty(jsonStr)) {
				return null;
			}
			return (T)JsonUtils.jsonToBean(jsonStr,clazz);
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
    
	public static List<?> range(String key,long begin,long end){
		 return (List<?>)redisTemplate.opsForList().range(key, begin, end);
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
	/**
	 * 尝试获取分布式锁
	 */
	public static Boolean lock(String key, String value, Long expireSeconds) {
		// 判断是不是可以加锁成功
		if (redisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 解锁
	 * @param key
	 * @param value
	 */
	public static void unlock(String key, String value) {
		String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
				"then\n" +
				"    return redis.call(\"del\",KEYS[1])\n" +
				"else\n" +
				"    return 0\n" +
				"end";
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(lua,Long.class);
		redisScript.setScriptText(lua);
		redisTemplate.execute(redisScript, Collections.singletonList(key), value);
	}

	/**
	 * 锁定并执行后解锁
	 * @param key
	 * @param expireSeconds
	 * @param action
	 */
	public static void lockAction(String key, Long expireSeconds, Consumer<Long> action) {
		String value= UUID.randomUUID().toString();
		/**
		 * 调用Redis锁定资源，设定超时时间，并执行代码段
		 */
		if(RedisUtils.lock(key, value, expireSeconds)) {
			try {
				action.accept(expireSeconds);
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				/**
				 * 执行完解锁资源
				 */
				RedisUtils.unlock(key, value);
			}
		}
	}
}
