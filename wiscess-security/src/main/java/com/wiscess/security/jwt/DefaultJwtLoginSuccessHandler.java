package com.wiscess.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.wiscess.common.R;
import com.wiscess.security.vue.AbstractVueLoginSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.WebAttributes;

@Slf4j
public class DefaultJwtLoginSuccessHandler extends AbstractVueLoginSuccessHandler {

	@Autowired(required=false)
	public UserRepository userRepository;
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Override
	public void onSuccess(HttpServletRequest request, R r, Authentication auth) {
		User userDetails=(User)auth.getPrincipal();

		//根据auth查询用户，生成token
		final String token = jwtTokenUtils.generateToken(userDetails);
		//缓存token
		userRepository.insert(token, userDetails.getUsername());
		r.data(token);
		
		clearAuthenticationAttributes(request);
		log.info("登录成功");
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
