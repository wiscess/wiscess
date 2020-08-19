package com.wiscess.oauth.config.handler;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.wiscess.common.R;
import com.wiscess.security.vue.AbstractVueAccessDeniedHandler;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@ConditionalOnMissingBean(AccessDeniedHandler.class)
@Configuration
public class OauthAccessDeniedHandler extends AbstractVueAccessDeniedHandler{

	@Override
	public void onFailure(R r, AccessDeniedException e) throws IOException, ServletException {
		r.message("访问资源时权限不足。");
	}

}
