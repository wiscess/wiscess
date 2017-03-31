package com.wiscess.wechat.message.req;

/**
 * 图片消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class ImageMessage extends BaseMessage {
	// 图片链接
	private String PicUrl;

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
}
