package com.wiscess.wechat.message.resp;

/**
 * 文本消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class TextMessage extends BaseMessage {
	// 回复的消息内容
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
