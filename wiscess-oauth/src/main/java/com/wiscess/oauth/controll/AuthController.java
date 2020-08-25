package com.wiscess.oauth.controll;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiscess.common.R;
import com.wiscess.oauth.delegate.OAuth2LogoutDelegate;
import com.wiscess.oauth.utils.TokenEntity;
import com.wiscess.oauth.utils.TokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义Oauth2获取令牌接口
 * Created by macro on 2020/7/17.
 */
@Api(value = "Oauth登录登出接口",tags = "Oauth登录登出接口")
@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthController {

	@Autowired
    private TokenEndpoint tokenEndpoint;

	@Autowired
    private DefaultTokenServices defaultTokenServices;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired(required = false)
	private OAuth2LogoutDelegate logoutDelegate;
	
    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = {RequestMethod.GET,RequestMethod.POST})
	@ApiOperation(value = "Oauth2登录")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "client_id",value = "客户端id",paramType = "query"),
		@ApiImplicitParam(name = "client_secret",value = "客户端密码",paramType = "query"),
		@ApiImplicitParam(name = "grant_type",value = "授权模式",paramType = "query"),
		@ApiImplicitParam(name = "username",value = "用户名",paramType = "query"),
		@ApiImplicitParam(name = "password",value = "密码",paramType = "query"),
		@ApiImplicitParam(name = "refresh_token",value = "RefreshToke",paramType = "query")
	})
    public R postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        //调用原oauth/token的方法，获取token的内容
    	OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
    	if(oAuth2AccessToken!=null) {
    		//认证成功后，用redis记录当前用户的token信息
    		String clientId = ((Authentication) principal).getName();
    		String username=oAuth2AccessToken.getAdditionalInformation().get("userName").toString();
    		//将当前client下的用户进行存储
    		/**
    		 * 当尝试保存当前token时，先将token保存进入，然后判断是否已经超过最大限制人数，如果已经超过，则将前面超出部分的用户的token移除，确保后登录的用户有效
    		 * 
    		 */
    		if(parameters.containsKey("refresh_token")) {
    			//当刷新token请求时，需要移除缓存的用户
    			TokenUtil.removeRefreshToken(clientId+"@"+username,parameters.get("refresh_token"));
    		}
    		List<TokenEntity> expirationTokenList=TokenUtil.pushToken(clientId+"@"+username,oAuth2AccessToken);
    		if(!expirationTokenList.isEmpty()) {
    			//销毁token
    			expirationTokenList.forEach(token->{
        	    	if(defaultTokenServices.revokeToken(token.getToken())==false) {
        	    		//移除token返回false，说明token已经过期，需要手动移除refreshToken
	        			if (token.getRefreshToken() != null) {
	        				OAuth2RefreshToken refreshToken= tokenStore.readRefreshToken(token.getRefreshToken());
	        				if(refreshToken!=null)
	        					tokenStore.removeRefreshToken(refreshToken);
	        			}
        	    	}    				
    			});
    		}
	        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
	                .token(oAuth2AccessToken.getValue())
	                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
	                .expiresIn(oAuth2AccessToken.getExpiresIn())
	                .tokenHead("Bearer ").build();
	        return R.ok().data(oauth2TokenDto);
    	}
    	return R.ok();
    }
    
    @RequestMapping(value = "/logout", method = {RequestMethod.GET,RequestMethod.POST})
	@ApiOperation(value = "Oauth2登出")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "access_token",value = "accessToken",paramType = "query")
	})
    public R oauthLogout(HttpServletRequest request,@RequestParam("access_token")String accessToken) throws HttpRequestMethodNotSupportedException {
		//认证成功后，用redis记录当前用户的token信息
		String clientId = defaultTokenServices.getClientId(accessToken);
		OAuth2AccessToken oAuth2AccessToken = defaultTokenServices.readAccessToken(accessToken);
		if(oAuth2AccessToken!=null) {
    		String username=oAuth2AccessToken.getAdditionalInformation().get("userName").toString();
    		//移除当前AccessToken对应的用户
    		int lastUser=TokenUtil.logout(clientId+"@"+username,accessToken);
    		//
    		if(logoutDelegate!=null && lastUser==0) {
    			log.debug("用户{}已退出。",username);
    			logoutDelegate.doLogout(request,username);
    		}
		}
		//移除token信息
    	defaultTokenServices.revokeToken(accessToken);
    	return R.ok();
    }
}
