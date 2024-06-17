package com.wiscess.cmd.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import lombok.extern.slf4j.Slf4j;

import com.google.common.cache.Cache;

/**
 * 命令行工具类
 * @author wh
 *
 */
@Slf4j
public class CommandUtils {

	/**
	 * 进程缓存
	 */
	private static Cache<String, MyProcess> processCache = createManualCache(20L);
	        
	/**
	 * 5分钟超时
	 */
	private static long expireTime = 5*60*1000;
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
	        //.expireAfterWrite(minutes, TimeUnit.SECONDS)
	        //设置缓存容器的初始容量为100
	        .initialCapacity(100)
	        //设置缓存最大容量为100000，超过1000之后就会按照LRU最近虽少使用算法来移除缓存项
	        .maximumSize(100000)
	        .build();
	}
	/**
	 * 通过sessionId获取到一个进程
	 * @param sessionId
	 * @return
	 */
	public static MyProcess getProcess(String sessionId) {
		//取缓存
		MyProcess p = processCache.getIfPresent(sessionId);
    	if(p==null) {
    		//缓存不在，创建进程
    		final MyProcess process = new MyProcess();
    		process.start();
    		processCache.put(sessionId, process);
    		new Thread(()->{
				//判断是否超时
				while(process.isAlive() && System.currentTimeMillis()-process.lastActionTime<expireTime) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(process.isAlive()) {
					//超时，发送退出命令
					process.close();
				}
				processCache.invalidate(sessionId);
			}).start();
    		p=process;
        }
    	return p;
	}
	
}
