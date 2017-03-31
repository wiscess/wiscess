package com.wiscess.wechat.menu;

/**
 * 系统拍照发图 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
public class PicSysphotoButton extends Button {
	private String type = "pic_sysphoto";
	private String key="rselfmenu_1_0";

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