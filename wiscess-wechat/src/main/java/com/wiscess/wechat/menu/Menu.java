package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 菜单
 * 
 * @author wanghai
 * @date 2014-06-11
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class Menu {
	private Button[] button;
}
