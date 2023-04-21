package com.wiscess.security.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * 统一认证登录的过滤器
 * @author wh
 *
 */
public class SSOAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	public static final String SSO_LAST_USERNAME_KEY = "SSO_LAST_USERNAME_KEY";
	
	// ~ Static fields/initializers
	// =====================================================================================
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "auth_username";
	public static final String SPRING_SECURITY_FORM_TESTNAME_KEY = "authtest_username";
	public static final String SPRING_SECURITY_FORM_REMOTE_KEY = "auth_key";

	private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
	private String testnameParameter = SPRING_SECURITY_FORM_TESTNAME_KEY;
	private String authkeyParameter = SPRING_SECURITY_FORM_REMOTE_KEY;

	// ~ Constructors
	// ===================================================================================================
	public SSOAuthenticationFilter() {
	}
	// ~ Methods
	// ========================================================================================================

	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String loginName=obtainUsername(request);
		String testName=obtainTestname(request);
		String remoteKey=obtainRemoteKey(request);
		String clientKey=request.getSession().getId();
		
		if(loginName==null){
			loginName="";
		}
		
		SSOAuthenticationToken authRequest = new SSOAuthenticationToken(loginName, testName, remoteKey, clientKey);
		
		setDetails(request,authRequest);
		
		Authentication authentication=this.getAuthenticationManager().authenticate(authRequest);
		
		request.getSession().setAttribute(SSO_LAST_USERNAME_KEY, loginName);
		
		afterAuth(request,loginName);
		
		return authentication;
	}
	
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}
	
	protected String obtainTestname(HttpServletRequest request) {
		return request.getParameter(testnameParameter);
	}

	protected String obtainRemoteKey(HttpServletRequest request) {
		return request.getParameter(authkeyParameter);
	}
	protected void setDetails(HttpServletRequest request,
			SSOAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}
	public void setTestnameParameter(String testnameParameter) {
		Assert.hasText(testnameParameter, "testname parameter must not be empty or null");
		this.testnameParameter = testnameParameter;
	}
	public void setAuthKeyParameter(String authkeyParameter) {
		Assert.hasText(authkeyParameter, "authKey parameter must not be empty or null");
		this.authkeyParameter = authkeyParameter;
	}
	protected void afterAuth(HttpServletRequest request, String loginName) {
	}
	protected boolean requiresAuthentication(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("P3P", "CP=CAO PSA OUR");
		return null != request.getParameter(authkeyParameter);
	}
}
