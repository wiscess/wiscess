package com.wiscess.wechat.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权model
 * @author liudongge
 * @date 2014-07-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeixinOauth2Token {

	//网页授权接口调用凭证
	private String accessToken;
	//凭证有效时长
	private int expiresIn;
	//用于刷新凭证
	private String refreshToken;
	//用户标识
	private String openId;
	//用户授权作用域
	private String scope;
	//union标识
	private String unionId;

}
