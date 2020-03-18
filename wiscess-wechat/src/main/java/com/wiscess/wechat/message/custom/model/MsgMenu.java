package com.wiscess.wechat.message.custom.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MsgMenu
 * 
 * @author wanghai
 * @date 2020-03-02
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MsgMenu {
	
	private String head_content;
	
	private String tail_content;
	
	private List<MsgMenuItem> list;
	
	public MsgMenu addItem(MsgMenuItem item) {
		if(list==null) {
			list=new ArrayList<MsgMenuItem>();
		}
		list.add(item);
		return this;
	}
}
