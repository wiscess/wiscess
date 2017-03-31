package com.wiscess.wechat.menu;

/**
 * 弹出微信相册发图器 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
public class LocationSelectButton extends Button {
	private String type = "location_select";
	private String key = "rselfmenu_2_0";

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