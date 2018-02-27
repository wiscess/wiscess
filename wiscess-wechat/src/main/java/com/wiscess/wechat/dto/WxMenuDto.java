package com.wiscess.wechat.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WxMenuDto {

	private Integer id;
	private Integer pid;
	private String name;
	private String url;
	private Integer order;
	private Boolean isUsed;
	private Boolean isAuth;
	private Integer menuType;
	private List<WxMenuDto> funs = new ArrayList<WxMenuDto>();
	
}
