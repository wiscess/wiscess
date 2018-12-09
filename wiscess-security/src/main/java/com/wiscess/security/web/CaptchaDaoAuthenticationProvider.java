/**
 * 
 */
package com.wiscess.security.web;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.wiscess.security.WiscessSecurityProperties;
import com.wiscess.security.exception.BadCodeAuthenticationServiceException;
import com.wiscess.utils.StringUtils;

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
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken token)
			throws AuthenticationException {
		if(wiscessSecurityProperties.isCaptcha()) {
			//有验证码时，进行验证码的校验
			Object obj = token.getDetails();
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
		//没有超级密码时，用原始的方法校验
		super.additionalAuthenticationChecks(userDetails, token);
		
	}
}
