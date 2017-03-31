package com.wiscess.wechat.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wiscess.wechat.service.ICoreService;
import com.wiscess.wechat.util.MessageUtil;

/**
 * 核心服务类
 * 
 * @author wanghai	
 * @date 2014-06-13
 */
public abstract class CoreService implements ICoreService{
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return xml
	 */
	public String processRequest(HttpServletRequest request) {
		// xml格式的消息数据
		String respXml = null;
		// 默认返回的文本消息内容
		try {
			// 调用parseXml方法解析请求消息
			Map<String, String> requestMap = MessageUtil.parseXml(request);
//			for (Iterator<Map.Entry<String, String>> it = requestMap.entrySet().iterator(); it.hasNext();) {
//	            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
//	            System.out.println(entry.getKey() + "--->" + entry.getValue());
//	        }
			// 消息类型
			String msgType = requestMap.get("MsgType");
			//事件推送
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 关注
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					//用户订阅后，提示欢迎关注信息
					return processSubScribe(requestMap);
				}
				// 取消关注
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
					return processUnsubscribe(requestMap);
				}
				// 扫描带参数二维码
				else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
					//处理扫描带参数二维码事件
					return processScan(requestMap);
				}
				// 上报地理位置
				else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
					//处理上报地理位置事件
					return processLocation(requestMap);
				}
				// 自定义菜单--单击事件
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					//处理菜单点击事件
					return processClick(requestMap);
				// 自定义菜单--扫码推事件
				}else if (eventType.equals(MessageUtil.EVENT_TYPE_SCANCODE_PUSH)){
					//处理菜单点击事件
					return processScancodePush(requestMap);
				}
			}else{
				//消息处理
				return processMessage(requestMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respXml;
	}
	
	/**
	 * 消息类型的处理
	 * @param msgType
	 * @param requestMap
	 * @return
	 */
	public abstract String processMessage(Map<String, String> requestMap);
	
	/**
	 * 关注事件
	 * @param fromUserName
	 * @return
	 */
	public abstract String processSubScribe(Map<String, String> requestMap);
	
	/**
	 * 取消关注事件
	 * @param fromUserName
	 */
	public abstract String processUnsubscribe(Map<String, String> requestMap);
	/**
	 * 扫描带参数二维码
	 * @param requestMap
	 */
	public String processScan(Map<String, String> requestMap){
		
		return "";
	}
	
	/**
	 * 上报地理位置
	 * @param requestMap
	 */
	public String processLocation(Map<String, String> requestMap) {
		return "";
	}
	
	/**
	 * 菜单点击事件
	 * @param fromUserName
	 * @param eventKey
	 * @return
	 */
	public abstract String processClick(Map<String, String> requestMap);
	
	/**
	 * 菜单扫码推事件
	 * @param fromUserName
	 * @param eventKey
	 * @return
	 */
	public abstract String processScancodePush(Map<String, String> requestMap);
	
	
}
