package com.wiscess.security.sso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;

import com.wiscess.security.sso.encrypt.EncryptHandler;
import com.wiscess.security.sso.encrypt.MD5EncryptHandler;

@Slf4j
public class SSOLogoutSuccessHandler implements LogoutSuccessHandler {

	private String logoutUrl;
	private EncryptHandler encryptHandler=new MD5EncryptHandler();
	private boolean invalidateHttpSession=true;
	
	public SSOLogoutSuccessHandler(String logoutUrl,EncryptHandler encryptHandler) {
		this(logoutUrl,encryptHandler,true);
	}
	public SSOLogoutSuccessHandler(String logoutUrl,EncryptHandler encryptHandler,boolean invalidateHttpSession) {
		this.logoutUrl=logoutUrl;
		this.encryptHandler=encryptHandler;
		this.invalidateHttpSession=invalidateHttpSession;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
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
			log.debug("id:"+key);
			String redirectUrl = logoutUrl + "?authlogout_url=" + thisUrl + "&authlogout_key=" + key;
			log.debug("redirectUrl:"+redirectUrl);
			response.sendRedirect(redirectUrl);
		}else{
			String remoteKey = request.getParameter("authlogout_key");
			String key = request.getSession().getId();
			log.debug("id:"+key);
			if (encryptHandler.encrypt(request.getSession().getId(),remoteKey)) {
				// successfull logout
				if (invalidateHttpSession) {
					HttpSession session = request.getSession(false);
					if (session != null) {
						log.debug("Invalidating session: " + session.getId());
						session.invalidate();
					}
				}
			}
			response.sendRedirect("/");
		}
	}

}
