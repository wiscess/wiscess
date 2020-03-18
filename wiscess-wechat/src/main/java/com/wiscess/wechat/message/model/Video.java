package com.wiscess.wechat.message.model;

/**
 * 视频model
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class Video {
	// 媒体文件id
	private String MediaId;
	// 音乐标题
	private String Title;
	// 音乐描述
	private String Description;
	
	public String getMediaId() {
		return MediaId;
	}
	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}

}
