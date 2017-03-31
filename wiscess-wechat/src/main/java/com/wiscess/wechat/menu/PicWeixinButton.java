package com.wiscess.wechat.menu;

/**
 * 弹出微信相册发图器 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
public class PicWeixinButton extends Button {
	private String type = "pic_weixin";
	private String key="rselfmenu_1_2";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}