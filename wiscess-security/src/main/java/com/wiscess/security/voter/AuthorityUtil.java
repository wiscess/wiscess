package com.wiscess.security.voter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * 用户权限越权校验工具类
 * 项目需实现数据提供者接口，并在项目初始化时添加到该类中
 * @author wh
 *
 */
public class AuthorityUtil {

	private static int ACCESS_GRANTED = 1;
	private static int ACCESS_ABSTAIN = 0;
	private static int ACCESS_DENIED = -1;

	/**
	 * 角色权限数据提供者
	 */
	private static List<AuthorityProvider> providerList = new ArrayList<>();
	
	/**
	 * 添加数据提供者
	 * @param provider
	 */
	public static void addProvider(AuthorityProvider provider) {
		providerList.add(provider);
	}
	/**
	 * 进行权限的认证
	 * @param url
	 * @param authorities
	 * @return
	 */
	public static int checkAuthority(String url, Collection<? extends GrantedAuthority> authorities) {
		// 默认弃权
		int result = ACCESS_ABSTAIN;

		// 由角色权限数据提供者返回url对应的权限集合
		List<String> cachedAuthority = new ArrayList<>();
		for(AuthorityProvider provider:providerList) {
			//从提供者获取访问路径对应的角色；
			cachedAuthority.addAll(provider.getAuthorityByUrl(url));
		}
		if (!cachedAuthority.isEmpty()) {
			// 定义了权限控制，不满足时拒绝
			result = ACCESS_DENIED;
			// 判断是否在列表中
			for (GrantedAuthority authority : authorities) {
				for (String auth : cachedAuthority) {
					if (auth.equals(authority.getAuthority())) {
						return ACCESS_GRANTED;
					}
				}
			}
		}
		return result;
	}

}
