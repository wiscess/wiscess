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
public class WxItemDto {

	//该类型的素材的总数
	private Integer totalCount;
	//本次调用获取的素材的数量
	private Integer itemCount;
	//详细
	private List<WxItemsDto> items = new ArrayList<WxItemsDto>();
}
