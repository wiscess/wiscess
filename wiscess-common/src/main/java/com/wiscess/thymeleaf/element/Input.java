package com.wiscess.thymeleaf.element;

import org.thymeleaf.model.IModelFactory;

public final class Input extends AbstractInput<Input> {

	public Input(IModelFactory modelFactory){
		super(modelFactory,"text");
	}
}
