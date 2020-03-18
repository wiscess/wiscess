/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: NewsCustomMessage
 * Author:   wh
 * Date:     2020/3/2 12:12
 * Description: 客服文本消息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.wechat.message.custom;

import com.wiscess.wechat.message.custom.model.Mpnews;

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
public class MpnewsCustomMessage extends BaseCustomMessage{
/**
{
    "touser":"OPENID",
    "msgtype":"mpnews",
    "mpnews":
    {
         "media_id":"MEDIA_ID"
    }
}
 */
    // 接收方帐号（收到的OpenID）
    private String touser;
    // 消息类型（text/music/news）
    @Builder.Default
    private String msgtype="mpnews";
    // 图文消息
    private Mpnews mpnews;

    public MpnewsCustomMessage(String touser,String media_id) {
    	this.msgtype="mpnews";
    	this.touser=touser;
    	this.mpnews=Mpnews.builder()
    			.media_id(media_id)
    			.build();
    }
    
}
