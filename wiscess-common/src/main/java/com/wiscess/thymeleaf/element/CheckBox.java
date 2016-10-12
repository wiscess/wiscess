package com.wiscess.thymeleaf.element;


public final class CheckBox extends AbstractInput<CheckBox> {

	public CheckBox(){
		super("checkbox");
	}
	public CheckBox checked(boolean checked){
		setAttribute("checked",checked?"checked":null);
		return this;
	}
}
