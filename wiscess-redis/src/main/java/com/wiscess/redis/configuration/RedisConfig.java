package com.wiscess.redis.configuration;
/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: RedisConfig
 * Author:   wh
 * Date:     2020/3/19 19:44
 * Description: 配置Redis
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.wiscess.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import jakarta.annotation.Priority;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis的配置文件
 *
 */
@Configuration
@EnableCaching
@Slf4j
@Priority(-1000)
public class RedisConfig implements CachingConfigurer {
    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<String>(String.class));
        return new StringRedisTemplate(factory);
    }
    @Primary
	@Bean(name = "redisTemplate")
    public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory factory) {
    	log.info("Redis配置完成。");
    	RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
    	redisTemplate.setKeySerializer(new StringRedisSerializer());
         //redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
         redisTemplate.setConnectionFactory(factory);
         //redisTemplate.setConnectionFactory(new JedisConnectionFactory());
 
         //下面代码解决LocalDateTime序列化与反序列化不一致问题
         ObjectMapper om = new ObjectMapper();
         om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
         // 解决jackson2无法反序列化LocalDateTime的问题
         om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         om.registerModule(new JavaTimeModule());
         om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
         Jackson2JsonRedisSerializer<Object> j2jrs = new Jackson2JsonRedisSerializer<>(om,Object.class);
         // 序列化 value 时使用此序列化方法
         redisTemplate.setValueSerializer(j2jrs);
         redisTemplate.setHashValueSerializer(j2jrs);

         redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "redisUtils")
    public RedisUtils redisUtils(RedisTemplate<?, ?> redisTemplate){
        log.info("RedisUtils配置完成。");
        return new RedisUtils(redisTemplate);
    }
    /**
     * 解决持久化乱码问题，在redistemlate中指定了持久化的策略
     *
     * @param factory
     * @return
     */
	@Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        ObjectMapper om = new ObjectMapper();
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXXX
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer<Object> j2jrs = new Jackson2JsonRedisSerializer<>(om,Object.class);
        // 配置序列化
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(j2jrs))
                .computePrefixWith(cacheName -> "basic:" + cacheName + ":");//设置key的前缀生成规则
//				.computePrefixWith(new CacheKey());
        RedisCacheManager cacheManager = new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(factory), defaultCacheConfiguration);
        return cacheManager;
    }

    /**
     * 分页的时候使用jackson无法反序列化
     *
     * @param factory
     * @return
     */
    @Bean("pageCacheManager")
    public CacheManager pageCacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        // 配置序列化
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializationRedisSerializer))
                .computePrefixWith(cacheName -> "basic:" + cacheName + ":");//设置key的前缀生成规则
//				.computePrefixWith(new CacheKey());
//        RedisCacheManager cacheManager =
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(factory), defaultCacheConfiguration);
    }

}
