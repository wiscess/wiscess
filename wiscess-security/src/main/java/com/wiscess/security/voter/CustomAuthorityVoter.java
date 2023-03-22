package com.wiscess.security.voter;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义的访问投票器
 * 只对配置authenticated的资源进行控制
 * 优先顺序：
 * 1.ServiceWebSecurityConfig的.antMatchers().hasAnyAuthority()
 * 2.anyRequest().authenticated()，由CustomAuthorityVoter从数据库中判断权限
 * 3.Action中的@PreAuthorize(value="hasAnyAuthority()")
 * @author wh
 */

@Slf4j
public class CustomAuthorityVoter implements AccessDecisionVoter<FilterInvocation>{

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return attribute.toString().equals("authenticated");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
		/**
		 * authentication 登录用户已存在的功能和角色
		 * object 当前的访问路径
		 * attributes 从配置中获取到的当前访问路径的权限，
		 */
		//未登录，拒绝
		if (authentication == null) {
			return ACCESS_DENIED;
		}
		//默认弃权
		int result = ACCESS_ABSTAIN;
		Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);
		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				log.debug(fi.getRequestUrl());
				//从数据库或redis中读取当前路径的权限，与用户的权限authorities进行比较，
				//如果未定义，则弃权
				//如果存在则同意
				//如果不存在则拒绝
				return AuthorityUtil.checkAuthority(fi.getRequestUrl(),authorities);
			}
		}

		return result;
	}

	Collection<? extends GrantedAuthority> extractAuthorities(
			Authentication authentication) {
		return authentication.getAuthorities();
	}
}
