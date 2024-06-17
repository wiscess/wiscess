package com.wiscess.cmd.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 使用google的缓存技术
 * @author wh
 *
 */
public class GoogleCacheUtils {
	/**
	 * 创建默认的缓存，10并发，30分钟过期，最大1000容量
	 * @param <K>
	 * @param <V>
	 * @param action
	 * @return
	 */
	public static <K,V> LoadingCache<K,V> createLoadingCache(Function<K,V> action) {
		return createLoadingCache(30L,action);
	}
	/**
	 * 创建默认的缓存，10并发，30分钟过期，最大1000容量
	 * @param <K>
	 * @param <V>
	 * @param action
	 * @return
	 */
	public static <K,V> LoadingCache<K,V> createLoadingCache(Long minutes,Function<K,V> action) {
		//CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        return CacheBuilder.newBuilder()
	        //设置并发级别为10，并发级别是指可以同时写缓存的线程数
	        .concurrencyLevel(10)
	        //设置写缓存后30分钟过期
	        .expireAfterWrite(minutes, TimeUnit.MINUTES)
	        //设置缓存容器的初始容量为50
	        .initialCapacity(50)
	        //设置缓存最大容量为100，超过1000之后就会按照LRU最近虽少使用算法来移除缓存项
	        .maximumSize(1000)
	        //设置要统计缓存的命中率
	        .recordStats()
	        //设置缓存的移除通知
	        //.removalListener(notification -> log.debug(notification.getKey() + " 被移除了，原因： " + notification.getCause()))
	        //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
	        .build(new CacheLoader<K, V>() {
	            @Override
	            public V load(K name) throws Exception {
	            	return (V)action.apply(name);
	            }
	        });
	}
	/**
	 * 可以自动刷新的缓存，常用于从redis里获取数据
	 * @param <K>
	 * @param <V>
	 * @param action
	 * @return
	 */
	public static <K,V> LoadingCache<K,V> createAutoLoadingCache(Long refresh,Function<K,V> action) {
		//CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        return CacheBuilder.newBuilder()
	        //设置并发级别为10，并发级别是指可以同时写缓存的线程数
	        .concurrencyLevel(10)
	        //设置写缓存后refresh分钟刷新
	        .refreshAfterWrite(refresh, TimeUnit.MINUTES)
	        //设置缓存容器的初始容量为50
	        .initialCapacity(50)
	        //设置缓存最大容量为100，超过1000之后就会按照LRU最近虽少使用算法来移除缓存项
	        .maximumSize(10000)
	        //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
	        .build(new CacheLoader<K, V>() {
	            @Override
	            public V load(K name) throws Exception {
	            	return (V)action.apply(name);
	            }
	        });
	}
	
	/**
	 * 创建手工缓存，10并发，自定义多少分钟过期，最大1000容量
	 * @param <K>
	 * @param <V>
	 * @param action
	 * @return
	 */
	public static <K,V> Cache<K,V> createManualCache(Long minutes) {
		//CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        return CacheBuilder.newBuilder()
	        //设置并发级别为10，并发级别是指可以同时写缓存的线程数
	        .concurrencyLevel(10)
	        //设置写缓存后*分钟过期
	        .expireAfterWrite(minutes, TimeUnit.MINUTES)
	        //设置缓存容器的初始容量为100
	        .initialCapacity(100)
	        //设置缓存最大容量为100000，超过1000之后就会按照LRU最近虽少使用算法来移除缓存项
	        .maximumSize(100000)
	        .build();
	}
}
