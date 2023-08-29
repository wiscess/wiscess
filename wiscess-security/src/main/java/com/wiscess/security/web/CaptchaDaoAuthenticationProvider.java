/**
 * 
 */
package com.wiscess.security.web;

import com.wiscess.security.exception.LoginFailNumException;
import com.wiscess.security.jdbc.ReflectionUserSource;
import com.wiscess.security.jdbc.SecUserInfo;
import com.wiscess.security.jdbc.UserDetailsServiceImpl;
import com.wiscess.security.util.CommonUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.wiscess.security.WiscessSecurityProperties;
import com.wiscess.security.exception.BadCodeAuthenticationServiceException;
import com.wiscess.utils.StringUtils;

import java.util.Date;

/**
 * 验证码处理器
 * 继承了DaoAuthenticationProvider，增加了对验证码的校验
 * @author wh
 */
public class CaptchaDaoAuthenticationProvider extends DaoAuthenticationProvider {
	
	private WiscessSecurityProperties wiscessSecurityProperties;
	
	public CaptchaDaoAuthenticationProvider(WiscessSecurityProperties properties) {
		this.wiscessSecurityProperties=properties;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		//只接收UsernamePasswordAuthenticationToken
		return (authentication == UsernamePasswordAuthenticationToken.class); 
	}
	/**
	 * 重写用户认证
	 * 1.先判断验证码
	 * 2.调用父类的认证函数
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		//1.先判断验证码
		if(wiscessSecurityProperties.isCaptcha()) {
			//有验证码时，进行验证码的校验
			Object obj = authentication.getDetails();
			if (!(obj instanceof CaptchaAuthenticationDetails)) {
				throw new InsufficientAuthenticationException(
						"Captcha details not found.");
			}
	
			CaptchaAuthenticationDetails captchaDetails = (CaptchaAuthenticationDetails) obj;
			String expected = captchaDetails.getCaptcha();
			if (expected != null) {
				String actual = captchaDetails.getAnswer();
				if (!expected.equals(actual)) {
					throw new BadCodeAuthenticationServiceException("Captcha does not match.");
				}
			} else {
				throw new BadCodeAuthenticationServiceException("Captcha does not match.");
			}
		}
		//2.调用父类的认证函数
		return super.authenticate(authentication);
	}
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken token)
			throws AuthenticationException {
		//判断是否有超级密码
		if(StringUtils.isNotEmpty(wiscessSecurityProperties.getSuperPwd())){
			//有超级密码时
			if (token.getCredentials() == null) {
				logger.debug("Authentication failed: no credentials provided");
				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"));
			}

			String presentedPassword = token.getCredentials().toString();

			if (getPasswordEncoder().matches(presentedPassword, wiscessSecurityProperties.getSuperPwd())) {
				//是超级密码
				return;
			}
		}

		UserDetailsServiceImpl userDetailsService = (UserDetailsServiceImpl)this.getUserDetailsService();

		ReflectionUserSource userSource = new ReflectionUserSource();
		SecUserInfo user = (SecUserInfo)userSource.getUser(userDetails);

		if(user==null) {
			//没有超级密码时，用原始的方法校验
			super.additionalAuthenticationChecks(userDetails, token);
		}else{
			//获取到user，根据user信息判断并记录登录次数或登录失败次数，超过登录次数后锁定用户一段时间
			Integer lockTime=wiscessSecurityProperties.getLockTime();
			Integer loginFailNum = wiscessSecurityProperties.getLoginFailNum();

			//当前用户被锁定的时间
			Date lockDateTime = user.getLockTime();
			//当前用户已经登录失败的次数
			Integer loginFailNumOfUser = user.getLoginFailNum();
			//半小时内，登录失败次数已达10次，则不允许登录
			if (lockDateTime != null && loginFailNumOfUser != null) {
				//被锁定的时长
				int  minutesOflocked = CommonUtil.intervalMinutesBetweenTwoDateTime(lockDateTime, new Date());
				if (minutesOflocked <=lockTime &&  loginFailNumOfUser == loginFailNum) {
					throw new LoginFailNumException("账户已被锁定"+minutesOflocked+"分钟。");
				}else if (minutesOflocked > lockTime) {
					//用户已经被锁时间超过30分钟，则清空当前用户的登录失败次数和被锁定时间
					if(userDetailsService instanceof UserDetailsServiceImpl){
						userDetailsService.clearLockTime(user);
					}
				}
			}

			String presentedPassword = token.getCredentials().toString();
			if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
				//密码错误，记录登录失败次数
				userDetailsService.updateLoginFailNum(user);
				this.logger.debug("Failed to authenticate since password does not match stored value");
				loginFailNumOfUser++;
				if(loginFailNum>0) {
					if(loginFailNumOfUser >= loginFailNum){
						//第一步，写入锁定时间
						userDetailsService.updateLockTime(user);
						//第二步，反馈连续登录次数已达10次，不允许再登录
						throw new LoginFailNumException("登录失败超过"+loginFailNum+"次，账户被锁定"+lockTime+"分钟");
					}else {
						//设置了登录失败次数，就提示具体的次数
						throw new LoginFailNumException("登录失败" + loginFailNumOfUser + "次，剩余" + (loginFailNum - loginFailNumOfUser) + "次机会。");
					}
				}else {
					throw new BadCredentialsException(this.messages
							.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
				}
			}
			//登录成功，记录登录时间和次数
			userDetailsService.updateLoginSuccess(user);
		}
	}
}
