package com.wiscess.wechat.message.resp;

import com.wiscess.wechat.message.model.Video;

/**
 * 视频消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class VideoMessage extends BaseMessage {
	// 视频
	private Video Video;

	public Video getVideo() {
		return Video;
	}

	public void setVideo(Video video) {
		Video = video;
	}
}
