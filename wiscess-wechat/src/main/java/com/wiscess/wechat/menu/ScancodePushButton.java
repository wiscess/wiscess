package com.wiscess.wechat.menu;

/**
 * 扫码推事件 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
public class ScancodePushButton extends Button {
	private String type = "scancode_push";
	private String key="rselfmenu_0_1";

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