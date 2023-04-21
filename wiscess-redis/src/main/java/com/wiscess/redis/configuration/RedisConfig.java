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


import jakarta.annotation.Priority;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis的配置文件
 *
 */
@Configuration
@EnableCaching
@Slf4j
@Priority(-1000)
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig implements CachingConfigurer {

    @Bean(name = "redisUtils")
    public RedisUtils redisUtils(RedisTemplate<?, ?> redisTemplate){
    	//key使用String序列化
    	redisTemplate.setKeySerializer(new StringRedisSerializer());
 
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
    	log.info("RedisUtils配置完成。");
        return new RedisUtils(redisTemplate);
    }

}
