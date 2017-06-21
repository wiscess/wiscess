package com.wiscess.wechat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

	private String servletUrl="/wechat";
	private String payNotifyServletUrl="/notify_pay";
	private String appId="";
	private String appSecret;
	private String token;
	private String templateId;
	private final Pay pay=new Pay();
	
	@Data
	public static class Pay{
		private String appId="";
		private String appSecret;
		private String mchId;
		private String paternerKey;
		private String notifyUrlWx;
	}

}
