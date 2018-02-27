package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统拍照发图 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class PicSysphotoButton extends Button {
	@Builder.Default
	private String type = "pic_sysphoto";
	@Builder.Default
	private String key="rselfmenu_1_0";

}