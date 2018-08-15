package com.wiscess.thymeleaf.element;

import org.thymeleaf.model.IModelFactory;

public final class Hidden extends AbstractInput<Hidden> {

	public Hidden(IModelFactory modelFactory){
		super(modelFactory,"hidden");
	}

}
