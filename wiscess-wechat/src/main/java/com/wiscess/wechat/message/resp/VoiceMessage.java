package com.wiscess.wechat.message.resp;

import com.wiscess.wechat.message.model.Voice;

/**
 * 语音消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class VoiceMessage extends BaseMessage {
	// 语音
	private Voice Voice;

	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}
}
