package com.wiscess.wechat.menu;

/**
 * 复合类型的按钮
 * 
 * @author wanghai
 * @date 2014-06-11
 */
public class ComplexButton extends Button {
	private Button[] sub_button;

	public Button[] getSub_button() {
		return sub_button;
	}

	public void setSub_button(Button[] sub_button) {
		this.sub_button = sub_button;
	}

}
