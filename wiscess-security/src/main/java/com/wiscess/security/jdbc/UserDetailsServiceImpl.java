package com.wiscess.security.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wiscess.jpa.jdbc.JdbcJpaSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnWebApplication
public class UserDetailsServiceImpl extends JdbcJpaSupport implements UserDetailsService{

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	private String authoritiesByUsernameQuery="authoritiesByUsernameQuery";
	private String groupAuthoritiesByUsernameQuery;
	private String usersByUsernameQuery="usersByUsernameQuery";
	private String rolePrefix = "";
	/*锁定用户*/
	private String clearLockTimeByUser="clearLockTimeByUser";
	private String updateLockTimeByUser = "updateLockTimeByUser";
	private String updateLoginFailNumByUser = "updateLoginFailNumByUser";
	private String updateLoginSuccessByUser = "updateLoginSuccessByUser";

	private boolean usernameBasedPrimaryKey = true;
	private boolean enableAuthorities = true;
	private boolean enableGroups;

	/**
	 * @return the messages
	 */
	protected MessageSourceAccessor getMessages() {
		return this.messages;
	}

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		List<UserDetails> users = loadUsersByUsername(username);

		if (users.size() == 0) {
			throw new UsernameNotFoundException(
					this.messages.getMessage("UserJdbcJpaSupport.notFound",
							new Object[] { username }, "Username {0} not found"));
		}

		UserDetails user = users.get(0); // contains no GrantedAuthority[]

		Set<GrantedAuthority> dbAuthsSet = new HashSet<>();

		if (this.enableAuthorities) {
			dbAuthsSet.addAll(loadUserAuthorities(user.getUsername()));
		}

		if (this.enableGroups) {
			dbAuthsSet.addAll(loadGroupAuthorities(user.getUsername()));
		}

		List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);

		addCustomAuthorities(user.getUsername(), dbAuths);

		if (dbAuths.size() == 0) {
			log.debug("User '" + username
					+ "' has no authorities and will be treated as 'not found'");

			throw new UsernameNotFoundException(this.messages.getMessage(
					"JdbcDaoImpl.noAuthority", new Object[] { username },
					"User {0} has no GrantedAuthority"));
		}

		return createUserDetails(username, user, dbAuths);
	}

	/**
	 * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails
	 * objects. There should normally only be one matching user.
	 */
	protected List<UserDetails> loadUsersByUsername(String username) {
		Map<String,Object> params=new HashMap<>();
		params.put("username", username);
		return findList(usersByUsernameQuery, params, new RowMapper<UserDetails>() {
			@Override
			public UserDetails mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				if(rs.getMetaData().getColumnCount()==3) {
					String username = rs.getString(1);
					String password = rs.getString(2);
					boolean enabled = rs.getBoolean(3);
					return new User(username, password, enabled, true, true, true,
							AuthorityUtils.NO_AUTHORITIES);
				}
				//判断锁定次数
				return new SecUserDetails(SecUserInfo.builder()
						.userId(rs.getInt("user_id"))
						.loginName(rs.getString("login_name"))
						.loginPwd(rs.getString("login_pwd"))
						.name(rs.getString("name"))
						.phone(rs.getString("phone"))
						.isUsed(rs.getBoolean("is_used"))
						.lastLoginTime(rs.getTimestamp("last_login_time"))
						.loginNum(rs.getInt("login_num"))
						.loginFailNum(rs.getInt("login_fail_num"))
						.lockTime(rs.getTimestamp("lock_time"))
						.build(),
						AuthorityUtils.NO_AUTHORITIES);
			}
		});
	}

	/**
	 * Loads authorities by executing the SQL from <tt>authoritiesByUsernameQuery</tt>.
	 *
	 * @return a list of GrantedAuthority objects for the user
	 */
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		Map<String,Object> params=new HashMap<>();
		params.put("username", username);
		return findList(authoritiesByUsernameQuery, params,new RowMapper<GrantedAuthority>() {
					@Override
					public GrantedAuthority mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String roleName = getRolePrefix() + rs.getString(2);
						return new SimpleGrantedAuthority(roleName);
					}
				});
	}

	/**
	 * Loads authorities by executing the SQL from
	 * <tt>groupAuthoritiesByUsernameQuery</tt>.
	 *
	 * @return a list of GrantedAuthority objects for the user
	 */
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		Map<String,Object> params=new HashMap<>();
		params.put("username", username);
		return findList(groupAuthoritiesByUsernameQuery, params,new RowMapper<GrantedAuthority>() {
					@Override
					public GrantedAuthority mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String roleName = getRolePrefix() + rs.getString(3);
						return new SimpleGrantedAuthority(roleName);
					}
				});
	}

	/**
	 * Can be overridden to customize the creation of the final UserDetailsObject which is
	 * returned by the <tt>loadUserByUsername</tt> method.
	 *
	 * @param username the name originally passed to loadUserByUsername
	 * @param userFromUserQuery the object returned from the execution of the
	 * @param combinedAuthorities the combined array of authorities from all the authority
	 * loading queries.
	 * @return the final UserDetails which should be used in the system.
	 */
	protected UserDetails createUserDetails(String username,
			UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();

		if (!this.usernameBasedPrimaryKey) {
			returnUsername = username;
		}
		if(userFromUserQuery instanceof SecUserDetails) {
			return new SecUserDetails(((SecUserDetails) userFromUserQuery).getUser(),
					combinedAuthorities);
		}
		return new User(returnUsername, userFromUserQuery.getPassword(),
				userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities);
	}
	protected void addCustomAuthorities(String username,
			List<GrantedAuthority> authorities) {
	}

	public String getRolePrefix() {
		return rolePrefix;
	}

	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	/**
	 * 清除用户的锁定信息
	 * @param user
	 */
	public void clearLockTime(SecUserInfo user){
		Map<String,Object> params=new HashMap<>();
		params.put("userId", user.getUserId());
		update(clearLockTimeByUser,params);
	}
	/**
	 * 锁定用户
	 * @param user
	 */
	public void updateLockTime(SecUserInfo user){
		Map<String,Object> params=new HashMap<>();
		params.put("userId", user.getUserId());
		update(updateLockTimeByUser,params);
	}
	/**
	 * 记录用户登录失败次数
	 * @param user
	 */
	public void updateLoginFailNum(SecUserInfo user){
		Map<String,Object> params=new HashMap<>();
		params.put("userId", user.getUserId());
		update(updateLoginFailNumByUser,params);
	}
	/**
	 * 记录用户登录成功
	 * @param user
	 */
	public void updateLoginSuccess(SecUserInfo user){
		Map<String,Object> params=new HashMap<>();
		params.put("userId", user.getUserId());
		update(updateLoginSuccessByUser,params);
	}
}
