package com.wiscess.thymeleaf.processor;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;

import com.wiscess.thymeleaf.ExpressionUtil;
import com.wiscess.thymeleaf.WiscessDialect;
import com.wiscess.thymeleaf.element.Hidden;
import com.wiscess.thymeleaf.element.Input;
import com.wiscess.utils.StringUtils;

/**
 * 适用于<app:treeinput .../>的格式
 * 
 * @author wh
 */
public class TreeInputProcessor extends AbstractWiscessElementTagProcessor {

	private static final String TAG_NAME = "treeinput";

	public TreeInputProcessor() {
		super(WiscessDialect.DIALECT_PREFIX, TAG_NAME);
	}

	public TreeInputProcessor(String dialectPrefix) {
		super(dialectPrefix, TAG_NAME);
	}

	@Override
	public List<ITemplateEvent> doIntenalProcess(ITemplateContext context, IProcessableElementTag tag,IModelFactory modelFactory) {
		final String id = tag.getAttributeValue("id");
		final Integer width = ExpressionUtil.executeForInt(context, tag, "width", 0);
		final String value = ExpressionUtil.executeForString(context, tag, "value");
		final String showValue = ExpressionUtil.executeForString(context, tag, "showValue");
		final String submitName = ExpressionUtil.executeForString(context, tag, "submitName", id + "Names");
		final String submitId = ExpressionUtil.executeForString(context, tag, "submitId", id + "Ids");
		//获取所有属性
		IAttribute[] attributes=tag.getAllAttributes();
				
		List<ITemplateEvent> nodes=new ArrayList<>();
		//隐藏
		nodes.addAll(new Hidden(modelFactory).id(id + "Ids").name(submitId).value(value).build());
		//input
		//输入框
		Input input=new Input(modelFactory);
		if(StringUtils.isNotEmpty(id))
			input.id(id + "Names");
		if(StringUtils.isNotEmpty(submitName))
			input.name(submitName);
		if(StringUtils.isNotEmpty(showValue))
			input.value(showValue);
		if(width>0)
			input.style("width:" + width + "px");
		input.readonly("readonly")
			.onclick("showTreeMenu(\'" + id + "\');")
			.onfocus("this.blur();");
		for(IAttribute attr:attributes) {
			String attrvalue=attr.getValue();
			String attrname=attr.getAttributeCompleteName().toLowerCase();
			if(attrname.equals("width") 
					|| attrname.equals("showValue") 
					|| attrname.equals("value") 
					|| attrname.equals("id") 
					|| attrname.equals("submitName")
					|| attrname.equals("submitId"))
				continue;
			input.setAttribute(attrname, attrvalue);
		}
		nodes.addAll(input.build());
		return nodes;
	}
}
