package com.wiscess.security.web;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.wiscess.utils.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 切换角色的过滤器
 * @author wh
 *
 */
public class SwitchRoleAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/changeRole",
			"GET");

	private String roleParameter = "roleId";
	/**
	 * 
	 * @param defaultFilterProcessesUrl
	 */
	public SwitchRoleAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String roleId = obtainRole(request);
		roleId = (roleId != null) ? roleId.trim() : "";
		//取出当前的认证信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		//获取所有的角色
//		
//		List<LoginUserDto> userList = findUserList(authentication);
//		if(userList!=null) {
//			String roleId = request.getParameter("roleId");
//			LoginUserDto dto = null;
//			for (LoginUserDto userDto : userList) {
//				if (StringUtils.isEmpty(roleId) || roleId.equals(userDto.getRoleId().toString())) {
//					// 未指定角色，取第一个；角色存在，变更角色
//					dto = userDto;
//					break;
//				}
//			}
//			
//		getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
//		UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
//				password);
		// Allow subclasses to set the "details" property
//		setDetails(request, authRequest);
		return authentication;
	}
	/**
	 * Enables subclasses to override the composition of the username, such as by
	 * including additional values and a separator.
	 * @param request so that request attributes can be retrieved
	 * @return the username that will be presented in the <code>Authentication</code>
	 * request token to the <code>AuthenticationManager</code>
	 */
	@Nullable
	protected String obtainRole(HttpServletRequest request) {
		return request.getParameter(this.roleParameter);
	}

	/**
	 * Provided so that subclasses may configure what is put into the authentication
	 * request's details property.
	 * @param request that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its details
	 * set
	 */
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
	}
}
