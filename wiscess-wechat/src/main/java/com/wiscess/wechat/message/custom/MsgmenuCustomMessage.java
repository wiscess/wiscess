/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: MsgmenuCustomMessage
 * Author:   wh
 * Date:     2020/3/2 12:12
 * Description: 客服文本消息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.wechat.message.custom;

import com.wiscess.wechat.message.custom.model.MsgMenu;
import com.wiscess.wechat.message.custom.model.MsgMenuItem;

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
public class MsgmenuCustomMessage extends BaseCustomMessage{
/**
{
  "touser": "OPENID"
  "msgtype": "msgmenu",
  "msgmenu": {
    "head_content": "您对本次服务是否满意呢? "
    "list": [
      {
        "id": "101",
        "content": "满意"
      },
      {
        "id": "102",
        "content": "不满意"
      }
    ],
    "tail_content": "欢迎再次光临"
  }
}
 */
    // 接收方帐号（收到的OpenID）
    private String touser;
    // 消息类型（text/music/news）
    @Builder.Default
    private String msgtype="msgmenu";
    // 消息
    private MsgMenu msgmenu;

    public MsgmenuCustomMessage(String touser,MsgMenu msgmenu) {
    	this.msgtype="msgmenu";
    	this.touser=touser;
    	this.msgmenu=msgmenu;
    }
    
    public MsgmenuCustomMessage addItem(MsgMenuItem item) {
    	msgmenu.addItem(item);
		return this;
    }
}
