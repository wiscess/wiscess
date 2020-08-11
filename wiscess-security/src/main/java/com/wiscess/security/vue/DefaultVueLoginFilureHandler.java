package com.wiscess.security.vue;

import java.io.IOException;

import javax.servlet.ServletException;

import com.wiscess.common.R;
import org.springframework.security.core.AuthenticationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultVueLoginFilureHandler extends AbstractVueLoginFailureHandler {

	@Override
	public void onFailure(R r, AuthenticationException e)
			throws IOException, ServletException {
		//可以设置定制化的内容
		log.info("登录失败");
	}
	
}
