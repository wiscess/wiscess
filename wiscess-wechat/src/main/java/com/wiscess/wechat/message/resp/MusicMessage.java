package com.wiscess.wechat.message.resp;

import com.wiscess.wechat.message.model.Music;

/**
 * 音乐消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class MusicMessage extends BaseMessage {
	// 音乐
	private Music Music;

	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}
}
