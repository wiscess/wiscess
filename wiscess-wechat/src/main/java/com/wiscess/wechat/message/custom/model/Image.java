package com.wiscess.wechat.message.custom.model;

import com.wiscess.wechat.message.custom.model.Voice.VoiceBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片model
 * 
 * @author wanghai
 * @date 2014-06-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Image{
	// 媒体文件id
	private String media_id;
}
