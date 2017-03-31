package com.wiscess.wechat.menu;

/**
 * click类型的按钮
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class ClickButton extends Button {
	private String type = "click";
	private String key="V1001_GOOD";

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