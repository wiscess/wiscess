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
	 * 消息类型的处理
	 * @param msgType
	 * @param requestMap
	 * @return
	 */
	public String processMessage(Map<String, String> requestMap);
	
	/**
	 * 关注事件
	 * @param fromUserName
	 * @return
	 */
	public String processSubScribe(Map<String, String> requestMap);
	
	/**
	 * 取消关注事件
	 * @param fromUserName
	 */
	public String processUnsubscribe(Map<String, String> requestMap);
	/**
	 * 扫描带参数二维码
	 * @param requestMap
	 */
	public String processScan(Map<String, String> requestMap);
	
	/**
	 * 上报地理位置
	 * @param requestMap
	 */
	public String processLocation(Map<String, String> requestMap);
	
	/**
	 * 菜单点击事件
	 * @param fromUserName
	 * @param eventKey
	 * @return
	 */
	public String processClick(Map<String, String> requestMap);
	/**
	 * 菜单VIEW事件
	 * @param fromUserName
	 * @param eventKey
	 * @return
	 */
	public String processView(Map<String, String> requestMap);
	
	/**
	 * 菜单扫码推事件
	 * @param fromUserName
	 * @param eventKey
	 * @return
	 */
	public String processScancodePush(Map<String, String> requestMap);
	
	/**
	 * 微信支付回掉函数
	 */
	public void payResult(Map<String,String> map)throws Exception;
}
