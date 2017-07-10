package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 扫码推事件且弹出“消息接收中”提示框 类型按钮
 * @author Administrator
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class ScancodeWaitmsgButton  extends Button{
	@Builder.Default
	private String type = "scancode_waitmsg";
	@Builder.Default
	private String key="rselfmenu_0_0";
}
