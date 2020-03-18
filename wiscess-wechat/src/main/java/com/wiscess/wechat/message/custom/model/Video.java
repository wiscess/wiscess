package com.wiscess.wechat.message.custom.model;

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
public class Video {
	// 媒体文件id
	private String media_id;
	// 缩略图的媒体id
    @Builder.Default
	private String thumb_media_id="";
	// 媒体标题
    @Builder.Default
	private String title="";
	// 媒体描述
    @Builder.Default
	private String description="";
}
