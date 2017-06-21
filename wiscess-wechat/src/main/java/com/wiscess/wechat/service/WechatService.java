package com.wiscess.wechat.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface WechatService {

	/**
	 * 处理接收到的微信消息
	 * @param request
	 * @return
	 */
	public String processRequest(HttpServletRequest request);

	/**
	 * 微信支付回掉函数
	 */
	public void payResult(Map<String,String> map)throws Exception;
}
