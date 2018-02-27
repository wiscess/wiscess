package com.wiscess.wechat.dto;


import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxItemsDto {

	//素材id（必须是永久mediaID）
	private String mediaId;
	//文件名称
	private String name;
	//素材的最后更新时间
	private String updateTime;
	//素材url
	private String url;
	
	//图片消息列表
	private List<WxItemNewDto> contents = new ArrayList<WxItemNewDto>();
	
	
}
