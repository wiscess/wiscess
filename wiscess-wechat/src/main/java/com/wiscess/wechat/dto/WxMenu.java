package com.wiscess.wechat.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手工处理微信菜单
 * @author wh
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WxMenu implements Serializable, Comparable<WxMenu>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer menuId;
	private Integer parentMenuId;
	private String menuName;
	private String menuUrl;
	private Integer menuOrder;
	private Integer menuType;
	private Boolean isUsed;
	private Boolean isAuth;
	@Override
	public int compareTo(WxMenu menu) {
		return value(this).compareTo(value(menu));
	}
	
	private Integer value(WxMenu m){
		Integer i=10000;
		if(m.getParentMenuId()==0){
			i+=m.getMenuOrder()*100;
			i+=m.getParentMenuId();
		}else{
			i+=m.getParentMenuId()*100;
			i+=m.getMenuOrder();
		}
		return i;
	}
}
