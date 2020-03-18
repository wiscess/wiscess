package com.wiscess.wechat.message.custom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 音乐model
 * 
 * @author wanghai
 * @date 2014-06-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Music {
	// 音乐标题
	private String title;
	// 音乐描述
    @Builder.Default
	private String description="";
	// 音乐链接
    @Builder.Default
	private String musicurl="";
	// 高质量音乐链接，WIFI环境优先使用该链接播放音乐
    @Builder.Default
	private String hqmusicurl="";
	// 缩略图的媒体id，通过上传多媒体文件得到的id
    @Builder.Default
	private String thumb_media_id="";
	
}
