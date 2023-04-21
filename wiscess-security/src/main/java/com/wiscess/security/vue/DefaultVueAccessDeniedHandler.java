package com.wiscess.security.vue;

import java.io.IOException;

import jakarta.servlet.ServletException;

import com.wiscess.common.R;
import org.springframework.security.access.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultVueAccessDeniedHandler extends AbstractVueAccessDeniedHandler{

	@Override
	public void onFailure(R r, AccessDeniedException e)
			throws IOException, ServletException {
		//可以设置定制化的内容
		log.info("权限不足。");
	}
}
