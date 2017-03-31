package com.wiscess.wechat.message.resp;

import com.wiscess.wechat.message.model.Image;

/**
 * 图片消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class ImageMessage extends BaseMessage {
	// 图片
	private Image Image;

	public Image getImage() {
		return Image;
	}

	public void setImage(Image image) {
		Image = image;
	}
}
