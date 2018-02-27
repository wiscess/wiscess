package com.wiscess.wechat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxItemNewDto {

	//图文消息的标题
	private String title;
	//图文消息的封面图片素材id（必须是永久mediaID）
	private String thumbMediaId;
	//是否显示封面，0为false，即不显示，1为true，即显示
	private String showCoverPic;
	//作者
	private String author;
	//图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空
	private String digest;
	//图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS
	private String content;
	//图文页的URL，或者，当获取的列表是图片素材列表时，该字段是图片的URL
	private String url;
	//图文消息的原文地址，即点击“阅读原文”后的URL
	private String contentSourceUrl;
}
