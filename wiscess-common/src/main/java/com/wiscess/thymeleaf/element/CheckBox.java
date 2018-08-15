package com.wiscess.thymeleaf.element;

import org.thymeleaf.model.IModelFactory;

public final class CheckBox extends AbstractInput<CheckBox> {

	public CheckBox(IModelFactory modelFactory){
		super(modelFactory,"checkbox");
	}
	public CheckBox checked(boolean checked){
		setAttribute("checked",checked?"checked":null);
		return this;
	}
}
