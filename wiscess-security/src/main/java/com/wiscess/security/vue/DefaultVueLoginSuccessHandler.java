package com.wiscess.security.vue;

import javax.servlet.http.HttpServletRequest;

import com.wiscess.common.R;

import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultVueLoginSuccessHandler extends AbstractVueLoginSuccessHandler {

	@Override
	public void onSuccess(HttpServletRequest request, R r, Authentication auth) {
		log.info("登录成功");
	}
}
