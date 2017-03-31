package com.wiscess.wechat.message.req;

/**
 * 文本消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class TextMessage extends BaseMessage {
	// 消息内容
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
