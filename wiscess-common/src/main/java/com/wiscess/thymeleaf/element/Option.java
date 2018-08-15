package com.wiscess.thymeleaf.element;

import org.thymeleaf.model.IModelFactory;

public final class Option extends WiscessElement<Option>{
	
	public Option(IModelFactory modelFactory){
		super(modelFactory,"option");
	}
	public Option selected(boolean selected){
		setAttribute("selected", selected?"selected":null);
		return this;
	}
}
