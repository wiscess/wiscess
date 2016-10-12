package com.wiscess.thymeleaf.element;

public final class Option extends WiscessElement<Option>{
	
	public Option(){
		super("option");
	}
	public Option selected(boolean selected){
		setAttribute("selected", selected?"selected":null);
		return this;
	}
}
