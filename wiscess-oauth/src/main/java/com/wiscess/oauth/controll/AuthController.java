package com.wiscess.oauth.controll;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiscess.common.R;
import com.wiscess.oauth.utils.TokenUtil;

/**
 * 自定义Oauth2获取令牌接口
 * Created by macro on 2020/7/17.
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {

	@Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = {RequestMethod.GET,RequestMethod.POST})
    public R postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        //调用原oauth/token的方法，获取token的内容
    	OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
    	if(oAuth2AccessToken!=null) {
    		String username=oAuth2AccessToken.getAdditionalInformation().get("userName").toString();
    		String token=oAuth2AccessToken.getValue();
    		//判断token的和方法性
            if(!TokenUtil.pushToken(username,token,oAuth2AccessToken.getExpiration())){
                return null;
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

}
