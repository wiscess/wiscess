package com.wiscess.thymeleaf.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;

public class TestProcessor extends AbstractElementProcessor {

	public TestProcessor(String elementName) {
		super(elementName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ProcessorResult processElement(Arguments arguments,
			Element element) {
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
