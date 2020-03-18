/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: VoiceCustomMessage
 * Author:   wh
 * Date:     2020/3/2 12:12
 * Description: 客服文本消息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.wechat.message.custom;

import com.wiscess.wechat.message.custom.model.Voice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class VoiceCustomMessage extends BaseCustomMessage{
/**
 * {
 *     "touser":"OPENID",
 *     "msgtype":"text",
 *     "text":
 *     {
 *          "content":"Hello World"
 *     }
 * }
 */
    // 接收方帐号（收到的OpenID）
    private String touser;
    // 消息类型（text/music/news）
    @Builder.Default
    private String msgtype="voice";
    // 回复的消息内容
    private Voice voice;

    public VoiceCustomMessage(String touser,String media_id) {
    	this.msgtype="voice";
    	this.touser=touser;
    	this.voice=Voice.builder()
    			.media_id(media_id)
    			.build();
    }
}
