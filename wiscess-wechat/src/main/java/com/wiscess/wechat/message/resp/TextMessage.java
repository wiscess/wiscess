package com.wiscess.wechat.message.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本消息
 * 
 * @author wanghai
 * @date 2014-06-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TextMessage implements IRespMessage{
	// 接收方帐号（收到的OpenID）
	private String ToUserName;
	// 开发者微信号
	private String FromUserName;
	// 消息创建时间 （整型）
	private long CreateTime;
	// 消息类型（text/music/news）
	@Builder.Default
	private String MsgType="text";
	// 回复的消息内容
	private String Content;
	
	private Long MsgId;

}
