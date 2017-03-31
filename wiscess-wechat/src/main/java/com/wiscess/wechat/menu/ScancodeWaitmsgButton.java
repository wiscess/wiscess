package com.wiscess.wechat.menu;

/**
 * 扫码推事件且弹出“消息接收中”提示框 类型按钮
 * @author Administrator
 *
 */
public class ScancodeWaitmsgButton  extends Button{
	private String type = "scancode_waitmsg";
	private String key="rselfmenu_0_0";

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
