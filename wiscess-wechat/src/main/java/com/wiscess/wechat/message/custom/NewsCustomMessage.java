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

import com.wiscess.wechat.message.custom.model.Article;
import com.wiscess.wechat.message.custom.model.News;

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
public class NewsCustomMessage extends BaseCustomMessage{
/**
{
    "touser":"OPENID",
    "msgtype":"news",
    "news":{
        "articles": [
         {
             "title":"Happy Day",
             "description":"Is Really A Happy Day",
             "url":"URL",
             "picurl":"PIC_URL"
         }
         ]
    }
}
 */
    // 接收方帐号（收到的OpenID）
    private String touser;
    // 消息类型（text/music/news）
    @Builder.Default
    private String msgtype="news";
    // 图文消息
    private News news;

    public NewsCustomMessage(String touser,Article article) {
    	this.msgtype="news";
    	this.touser=touser;
    	this.news=News.builder()
    			.build()
    			.addArticle(article);
    }
    public NewsCustomMessage addArticle(Article article) {
    	if(news==null) {
    		news=News.builder().build();
    	}
		news.addArticle(article);
		return this;
    }
}
