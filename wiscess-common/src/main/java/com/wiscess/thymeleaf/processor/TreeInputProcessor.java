package com.wiscess.thymeleaf.processor;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;

import com.wiscess.thymeleaf.ExpressionUtil;
import com.wiscess.thymeleaf.element.Hidden;
import com.wiscess.thymeleaf.element.Input;

/**
 * 适用于<app:treeinput .../>的格式
 * 
 * @author wh
 */
public class TreeInputProcessor extends AbstractWiscessElementTagProcessor {

	private static final String TAG_NAME = "treeinput";

	public TreeInputProcessor(String dialectPrefix) {
		super(dialectPrefix, TAG_NAME);
	}

	public List<ITemplateEvent> doIntenalProcess(ITemplateContext context, IProcessableElementTag tag,IModelFactory modelFactory) {
		final String id = tag.getAttributeValue("id");
		final Integer width = ExpressionUtil.executeForInt(context, tag, "width", 140);
		final String value = ExpressionUtil.executeForString(context, tag, "value");
		final String showValue = ExpressionUtil.executeForString(context, tag, "showValue");
		final String submitName = ExpressionUtil.executeForString(context, tag, "submitName", id + "Names");
		final String submitId = ExpressionUtil.executeForString(context, tag, "submitId", id + "Ids");

		List<ITemplateEvent> nodes=new ArrayList<>();
		nodes.addAll(new Input(modelFactory).id(id + "Names").name(submitName).value(showValue).readonly("readonly")
				.style("width:" + width + "px").onclick("showTreeMenu(\'" + id + "\');").onfocus("this.blur();")
				.build());
		nodes.addAll(new Hidden(modelFactory).id(id + "Ids").name(submitId).value(value).build());
		return nodes;
	}
}
