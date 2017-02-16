package com.wiscess.security.csrf;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CsrfSecurityRequestMatcher implements RequestMatcher{

	private Pattern allowedMethods=Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	
	/**
	 * 要排除的url列表
	 */
	private List<String> execludeUrls;
	
	@Override
	public boolean matches(HttpServletRequest request) {
		if(execludeUrls!=null && execludeUrls.size()>0){
			String servletPath=request.getServletPath();
			for(String url:execludeUrls){
				if(servletPath.contains(url)){
					log.info("++++"+servletPath);
					return false;
				}
			}
		}
		return !allowedMethods.matcher(request.getMethod()).matches();
	}

	public List<String> getExecludeUrls() {
		return execludeUrls;
	}

	public void setExecludeUrls(List<String> execludeUrls) {
		this.execludeUrls = execludeUrls;
	}
}
