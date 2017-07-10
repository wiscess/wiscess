package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 扫码推事件 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class ScancodePushButton extends Button {
	@Builder.Default
	private String type = "scancode_push";
	@Builder.Default
	private String key="rselfmenu_0_1";

}