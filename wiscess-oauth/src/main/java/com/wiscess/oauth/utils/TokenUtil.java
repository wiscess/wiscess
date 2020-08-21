package com.wiscess.oauth.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.wiscess.redis.utils.RedisUtils;

import lombok.Getter;

/**
 * token控制工具类
 * @author 大仙
 */
public class TokenUtil {

	@Getter
	private static Long maxOnlineUser;
	

	@Value("${security.oauth.maxOnlineUser:-1}")
	public void setMaxOnlineUser(Long maxOnlineUser) {
		this.maxOnlineUser=maxOnlineUser;
	}
	/**
	 * 存储token
	 * @param username
	 * @param redisTemplate
	 * @param token
	 * @return
	 */
	public static Boolean pushToken(String username, String token,
			Date invalid) {
		//失效时间
		LocalDateTime invalidDate = invalid.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		//读取已经有了几个用户
		long size = RedisUtils.listSize(username);

		TokenEntity tokenEntity = new TokenEntity();

		tokenEntity.setInvalidDate(invalidDate);
		tokenEntity.setToken(token);

		if (size <= 0) {
			//不存在用户时，将用户对应的token添加到redis
			RedisUtils.rightPush(username, tokenEntity);
		} else {
			//取出该用户的所有token
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);
			//去掉过期的token
			tokenEntities = tokenEntities.stream().filter(te -> te.getInvalidDate().isAfter(LocalDateTime.now()))
					.collect(Collectors.toList());
			//超过最大人数时
			if (maxOnlineUser!=-1 && tokenEntities.size() >= maxOnlineUser) {
				return false;
			}
			//加入Redis队列
			tokenEntities.add(tokenEntity);
			RedisUtils.delKey(username);
			tokenEntities.forEach(te -> {
				RedisUtils.rightPush(username, te);
			});
		}
		return true;
	}

	/**
	 * 判断token是否有效
	 * @param username
	 * @param token
	 * @return true 有效 false: 无效
	 */
	public static Boolean judgeTokenValid(String username, String token) {
		long size = RedisUtils.listSize(username);
		if (size <= 0) {
			return false;
		} else {
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);
			//查找用户的token
			tokenEntities = tokenEntities.stream().filter(te -> te.getToken().equals(token))
					.collect(Collectors.toList());
			if (CollectionUtils.isEmpty(tokenEntities)) {
				return false;
			}
			TokenEntity tokenEntity = tokenEntities.get(0);
			if (tokenEntity.getInvalidDate().isAfter(LocalDateTime.now())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 登出
	 * @param username
	 * @param token
	 */
	public static void logout(String username, String token) {

		long size = RedisUtils.listSize(username);
		if (size <= 0) {
			RedisUtils.delKey(username);
		} else {
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);

			tokenEntities = tokenEntities.stream().filter(te -> !te.getToken().equals(token))
					.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(tokenEntities)) {
				RedisUtils.delKey(username);
			}
			RedisUtils.delKey(username);
			tokenEntities.forEach(te -> {
				RedisUtils.rightPush(username, te);
			});
		}
	}

}
