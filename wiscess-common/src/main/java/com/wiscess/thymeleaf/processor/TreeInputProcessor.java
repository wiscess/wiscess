package com.wiscess.thymeleaf.processor;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

import com.wiscess.thymeleaf.ThymeleafWebWiscessConfiguration.ExpressionUtil;
import com.wiscess.thymeleaf.element.Hidden;
import com.wiscess.thymeleaf.element.Input;

public class TreeInputProcessor extends AbstractMarkupSubstitutionElementProcessor {

	public TreeInputProcessor() {
		super("treeinput");
	}

	@Override
	protected List<Node> getMarkupSubstitutes(Arguments arguments,
			Element element) {
		final String id=ExpressionUtil.executeForString(arguments, element, "id");
		final Integer width=ExpressionUtil.executeForInt(arguments, element, "width", 140);
		final String value=ExpressionUtil.executeForString(arguments, element, "value");
		final String showValue=ExpressionUtil.executeForString(arguments, element, "showValue");
		final String submitName=ExpressionUtil.executeForString(arguments, element, "submitName",id+"Names");
		final String submitId=ExpressionUtil.executeForString(arguments, element, "submitId",id+"Ids");
		
		//
		List<Node> nodes=new ArrayList<Node>();
		
		nodes.add(new Input()
				.id(id+"Names")
				.name(submitName)
				.value(showValue)
				.readonly("readonly")
				.style("width:"+width+"px")
				.onclick("showTreeMenu(\'"+id+"\');")
				.onfocus("this.blur();")
				.build());
		nodes.add(new Hidden()
				.id(id+"Ids")
				.name(submitId)
				.value(value)
				.build());
		return nodes;
	}

	@Override
	public int getPrecedence() {
		return 10000;
	}
}
