package com.wiscess.security.sso;

import javax.sql.DataSource;

import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import com.wiscess.security.sso.encrypt.EncryptHandler;

public class SSOUserDetailsManagerConfigurer<B extends ProviderManagerBuilder<B>>
	extends UserDetailsAwareConfigurer<B, UserDetailsService> {
	
	private SSOAuthenticationProvider provider = new SSOAuthenticationProvider();
	private final UserDetailsService userDetailsService;
	
	public SSOUserDetailsManagerConfigurer(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		provider.setUserDetailsService(userDetailsService);
	}

	public SSOUserDetailsManagerConfigurer() {
		this(new JdbcUserDetailsManager());
	}

	/**
	 * 设置数据源
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> dataSource(DataSource dataSource)
			throws Exception {
		getUserDetailsService().setDataSource(dataSource);
		return this;
	}
	@Override
	public void configure(B builder) throws Exception {
		provider = postProcess(provider);
		builder.authenticationProvider(provider);
	}
	/**
	 * Sets the query to be used for finding a user by their username. For example:
	 *
	 * <code>
	 *     select username,password,enabled from users where username = ?
	 * </code>
	 * @param query The query to use for selecting the username, password, and if the user
	 * is enabled by username. Must contain a single parameter for the username.
	 * @return The {@link JdbcUserDetailsManagerConfigurer} used for additional
	 * customizations
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> usersByUsernameQuery(String query)
			throws Exception {
		getUserDetailsService().setUsersByUsernameQuery(query);
		return this;
	}

	/**
	 * Sets the query to be used for finding a user's authorities by their username. For
	 * example:
	 *
	 * <code>
	 *     select username,authority from authorities where username = ?
	 * </code>
	 *
	 * @param query The query to use for selecting the username, authority by username.
	 * Must contain a single parameter for the username.
	 * @return The {@link JdbcUserDetailsManagerConfigurer} used for additional
	 * customizations
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> authoritiesByUsernameQuery(String query)
			throws Exception {
		getUserDetailsService().setAuthoritiesByUsernameQuery(query);
		return this;
	}

	/**
	 * An SQL statement to query user's group authorities given a username. For example:
	 *
	 * <code>
	 *     select
	 *         g.id, g.group_name, ga.authority
	 *     from
	 *         groups g, group_members gm, group_authorities ga
	 *     where
	 *         gm.username = ? and g.id = ga.group_id and g.id = gm.group_id
	 * </code>
	 *
	 * @param query The query to use for selecting the authorities by group. Must contain
	 * a single parameter for the username.
	 * @return The {@link JdbcUserDetailsManagerConfigurer} used for additional
	 * customizations
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> groupAuthoritiesByUsername(String query)
			throws Exception {
		JdbcUserDetailsManager userDetailsService = getUserDetailsService();
		userDetailsService.setEnableGroups(true);
		userDetailsService.setGroupAuthoritiesByUsernameQuery(query);
		return this;
	}

	/**
	 * A non-empty string prefix that will be added to role strings loaded from persistent
	 * storage (default is "").
	 *
	 * @param rolePrefix
	 * @return
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> rolePrefix(String rolePrefix)
			throws Exception {
		getUserDetailsService().setRolePrefix(rolePrefix);
		return this;
	}

	/**
	 * Defines the {@link UserCache} to use
	 *
	 * @param userCache the {@link UserCache} to use
	 * @return the {@link JdbcUserDetailsManagerConfigurer} for further customizations
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<B> userCache(UserCache userCache)
			throws Exception {
		getUserDetailsService().setUserCache(userCache);
		return this;
	}

	@Override
	public JdbcUserDetailsManager getUserDetailsService() {
		return (JdbcUserDetailsManager) this.userDetailsService;
	}

	/**
	 * 设置加密方式
	 * @param md5PasswordEncoder
	 * @return
	 */
	public SSOUserDetailsManagerConfigurer<B> encryptType(
			String encryptType) {
		provider.setEncryptType(encryptType);
		return this;
	}
	public SSOUserDetailsManagerConfigurer<B> encryptHandler(
			EncryptHandler encryptHandler) {
		provider.setEncryptHandler(encryptHandler);
		return this;
	}
}
