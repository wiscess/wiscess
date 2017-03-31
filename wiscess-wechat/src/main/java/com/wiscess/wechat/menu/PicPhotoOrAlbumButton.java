package com.wiscess.wechat.menu;

/**
 * 弹出拍照或者相册发图 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
public class PicPhotoOrAlbumButton extends Button {
	private String type = "pic_photo_or_album";
	private String key="rselfmenu_1_1";

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