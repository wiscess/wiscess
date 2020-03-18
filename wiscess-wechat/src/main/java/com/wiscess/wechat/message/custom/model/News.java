package com.wiscess.wechat.message.custom.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频model
 * 
 * @author wanghai
 * @date 2014-06-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class News {
	private List<Article> articles;
	
	public News addArticle(Article article) {
		if(articles==null) {
			articles=new ArrayList<Article>();
		}
		//只能添加一个
		if(articles.size()==0)
			articles.add(article);
		return this;
	}
}
