/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: JwtAuthenticationTokenFilter
 * Author:   wh
 * Date:     2020/8/10 9:09
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wiscess.utils.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    protected UserDetailsService userDetailsService;

	@Autowired(required=false)
	public UserRepository userRepository;

    @Autowired(required=false)
    private JwtTokenUtils jwtTokenUtils;

    private String tokenHeader = "Authorization";

    private String tokenHead = "Bearer ";

    private String getJwtToken(HttpServletRequest request) {
        String jwtToken = request.getParameter("access_token");
        String authHeader = request.getHeader(this.tokenHeader);
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith(tokenHead)) {
            //如果header中存在token，则覆盖掉url中的token
        	jwtToken = authHeader.substring(tokenHead.length()); // "Bearer "之后的内容
        }
        return jwtToken;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        //先从url中取token
		String jwtToken = getJwtToken(request);
        if (StringUtils.isNotEmpty(jwtToken)) {
            String username = jwtTokenUtils.getUsernameFromToken(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //从已有的user缓存中取了出user信息
                UserDetails user = userDetailsService.loadUserByUsername(username);
                
                String cacheUsername=userRepository.findByToken(jwtToken);
                
                //检查token是否有效
                if (jwtTokenUtils.validateToken(jwtToken, user) && cacheUsername!=null && cacheUsername.equals(username)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //设置用户登录状态
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }else {
                	
                }
            }
        }
        chain.doFilter(request, response);
    }
}
