package com.wiscess.security.voter;

import java.util.Collection;

/**
 * 角色权限数据提供者
 * @author wh
 *
 */
public interface AuthorityProvider {

	/**
	 * 获取访问路径对应的权限
	 * @param url
	 * @return
	 */
	public Collection<? extends String> getAuthorityByUrl(String url);

}
