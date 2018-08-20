package com.wiscess.security.sso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;

import com.wiscess.security.WiscessSecurityProperties;

@Slf4j
public class SSOLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private WiscessSecurityProperties wiscessSecurityProperties;

	public SSOLogoutSuccessHandler() {
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if(authentication!=null){
			String ssoLogoutTag = request.getParameter("sso_logout");
			if (null == ssoLogoutTag) {
				String thisUrl = UrlUtils.buildFullRequestUrl(request);
				log.debug("logout url:"+thisUrl);
				if (thisUrl.indexOf('?') != -1) {
					thisUrl += "&sso_logout=1";
				} else {
					thisUrl += "?sso_logout=1";
				}
				try {
					thisUrl = URLEncoder.encode(thisUrl, "UTF-8");
				} catch (UnsupportedEncodingException e) {}
				String key = request.getSession().getId();
				log.info("logout session id:"+key);
				String logoutUrl=wiscessSecurityProperties.getSso().getAuthLogoutUrl();
				String redirectUrl = logoutUrl + "?authlogout_url=" + thisUrl + "&authlogout_key=" + key;
				log.debug("redirectUrl:"+redirectUrl);
			}
		}
		response.sendRedirect("/");

	}

}
