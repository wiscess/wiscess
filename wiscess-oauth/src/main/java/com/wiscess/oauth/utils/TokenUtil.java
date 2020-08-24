package com.wiscess.oauth.utils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.CollectionUtils;

import com.wiscess.redis.utils.RedisUtils;

import lombok.Getter;

/**
 * token控制工具类
 * @author 大仙
 */
public class TokenUtil {

	public static String ACCESS_TOKEN="access_token";
	
	public static String REFRESH_TOKEN="refresh_token";
	
	@Getter
	private static Long maxOnlineUser;
	
	/**
	 * 根据request获取用户名
	 * @param request
	 * @return
	 */
	public static String getUsername(HttpServletRequest request) {
		//先读取认证的用户信息
		Principal principal=request.getUserPrincipal();
		return principal!=null?principal.getName():null;
	}

	@Value("${security.oauth.maxOnlineUser:-1}")
	public void setMaxOnlineUser(Long maxOnlineUser) {
		TokenUtil.maxOnlineUser=maxOnlineUser;
	}
	
	/**
	 * 存储token对象，并将已经超出用户限制的用户信息返回，进行销毁
	 * @param username
	 * @param token
	 * @return
	 */
	public static List<TokenEntity> pushToken(String username,OAuth2AccessToken token){
		Date invalid=token.getExpiration();
		//失效时间
		LocalDateTime invalidDate = invalid.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		//读取已经有了几个用户
		long size = RedisUtils.listSize(username);

		//记录当前token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setInvalidDate(invalidDate);
		tokenEntity.setToken(token.getValue());
		tokenEntity.setRefreshToken(token.getRefreshToken().getValue());
		if (size <= 0) {
			//不存在用户时，将用户对应的token添加到redis
			RedisUtils.rightPush(username, tokenEntity);
		} else {
			//取出该用户的所有token
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);
			List<TokenEntity> expirationTokenEntities =Collections.emptyList();
			//收集已经过期的Token
//			List<TokenEntity> expirationTokenEntities= tokenEntities.stream().filter(te -> te.getInvalidDate().isBefore(LocalDateTime.now()))
//					.collect(Collectors.toList());
//			
//			//保留未过期的token
//			tokenEntities = tokenEntities.stream().filter(te -> te.getInvalidDate().isAfter(LocalDateTime.now()))
//					.collect(Collectors.toList());
			//将用户保存起来
			//加入Redis队列
			tokenEntities.add(tokenEntity);
			//超过最大人数时
			if (maxOnlineUser!=-1 && tokenEntities.size() > maxOnlineUser) {
				//将未过期的前几个信息加到已过期的队列中，准备销毁
				while(tokenEntities.size() >maxOnlineUser) {
					//取第一个元素加到已过期列表中
					expirationTokenEntities.add(tokenEntities.get(0));
					//移除第一个元素
					tokenEntities.remove(0);
				}
			}
			RedisUtils.delKey(username);
			tokenEntities.forEach(te -> {
				RedisUtils.rightPush(username, te);
			});
			//返回已过期token
			return expirationTokenEntities;
		}
		return Collections.emptyList();
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
	 * @return 未登出的用户数
	 */
	public static int logout(String username, String token) {

		long size = RedisUtils.listSize(username);
		if (size <= 0) {
			RedisUtils.delKey(username);
			return 0;
		} else {
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);
			//查找其他待保留的token信息
			tokenEntities = tokenEntities.stream().filter(te -> !te.getToken().equals(token))
					.collect(Collectors.toList());

			RedisUtils.delKey(username);
			tokenEntities.forEach(te -> {
				RedisUtils.rightPush(username, te);
			});
			return tokenEntities.size();
		}
	}

	/**
	 * 移除refreshToken
	 * @param string
	 * @param containsKey
	 */
	public static void removeRefreshToken(String username, String refreshToken) {
		long size = RedisUtils.listSize(username);
		if (size <= 0) {
			RedisUtils.delKey(username);
		} else {
			List<TokenEntity> tokenEntities = RedisUtils.range(username, 0, size);
			//查找其他待保留的token信息
			tokenEntities = tokenEntities.stream().filter(te -> !te.getRefreshToken().equals(refreshToken))
					.collect(Collectors.toList());

			RedisUtils.delKey(username);
			tokenEntities.forEach(te -> {
				RedisUtils.rightPush(username, te);
			});
		}
	}
	
}
