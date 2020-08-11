/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: DefaultVueLogoutSuccessHandler
 * Author:   wh
 * Date:     2020/8/10 13:11
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.jwt;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiscess.common.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class DefaultJwtLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired(required=false)
	public UserRepository userRepository;
	
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		String jwtToken=request.getParameter("token");
		userRepository.remove(jwtToken);
        //登出后清除用户缓存信息
		R r = null;
		r=R.ok("退出成功!");
        //输出json内容
		ObjectMapper om = new ObjectMapper();
		PrintWriter out = response.getWriter();
		out.write(om.writeValueAsString(r));
		out.flush();
		out.close();
    }
}
