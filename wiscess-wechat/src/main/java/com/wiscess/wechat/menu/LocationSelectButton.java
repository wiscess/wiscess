package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 弹出微信相册发图器 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class LocationSelectButton extends Button {
	@Builder.Default
	private String type = "location_select";
	@Builder.Default
	private String key = "rselfmenu_2_0";
	
}