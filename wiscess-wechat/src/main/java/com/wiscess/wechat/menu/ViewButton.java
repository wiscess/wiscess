package com.wiscess.wechat.menu;

/**
 * view类型的按钮
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class ViewButton extends Button {
	private String type = "view";
	private String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
