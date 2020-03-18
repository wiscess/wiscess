package com.wiscess.wechat.message.custom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {
	// 标题
	private String title;
	// 描述
    @Builder.Default
	private String description="";
	// 链接
    @Builder.Default
	private String url="";
    @Builder.Default
	private String picurl="";

}
