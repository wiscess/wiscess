package com.wiscess.thymeleaf.processor;

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import com.wiscess.thymeleaf.element.Option;

public abstract class AbstractWiscessElementTagProcessor extends AbstractElementTagProcessor {

	private static final int PRECEDENCE = 300;

	public AbstractWiscessElementTagProcessor(String dialectPrefix, String tagName) {
		super(TemplateMode.HTML, // 此处理器将仅应用于HTML模式
				dialectPrefix, // 要应用于名称的匹配前缀
				// 适用于<app:treeinput .../>的格式
				tagName, // 标签名称：匹配此名称的特定标签
				true, // 将标签前缀应用于标签名称
				// 适用于< app:treeinput="" .../>的格式
				null, // 无属性名称：将通过标签名称匹配
				false, // 没有要应用于属性名称的前缀
				PRECEDENCE); // 优先(内部方言自己的优先)
	}

	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		/*
		 * 创建将替换自定义标签的DOM结构。 logo将显示在“<div>”标签内, 因此必须首先创建, 然后必须向其中添加一个节点。
		 */
		final IModelFactory modelFactory = context.getModelFactory();
		final IModel model = modelFactory.createModel();

		List<ITemplateEvent> list=doIntenalProcess(context, tag, modelFactory);
		
		list.forEach((item)->model.add(item));
		/*
		 * 指示引擎用指定的模型替换整个元素。
		 */
		structureHandler.insertImmediatelyAfter(model, false);
	}

	protected abstract List<ITemplateEvent> doIntenalProcess(ITemplateContext context, IProcessableElementTag tag, IModelFactory modelFactory);

	protected void addOption(List<ITemplateEvent> list, IModelFactory modelFactory, String dictId, String name, boolean matched) {
		list.addAll(new Option(modelFactory).value(dictId).selected(matched).title(name).text(name).build());
	}

}
