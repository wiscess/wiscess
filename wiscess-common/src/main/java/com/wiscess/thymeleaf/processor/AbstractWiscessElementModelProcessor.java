package com.wiscess.thymeleaf.processor;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import com.wiscess.thymeleaf.WiscessDialect;

/**
 * 自定义标签，可以包含body的内容，body中的内容作为数据加入到结果中
 * @author wh
 *
 */
public abstract class AbstractWiscessElementModelProcessor extends AbstractElementModelProcessor {

	private static final int PRECEDENCE = 300;
	
	public AbstractWiscessElementModelProcessor(String tagName) {
		this(WiscessDialect.DIALECT_PREFIX,tagName);
	}

	public AbstractWiscessElementModelProcessor(String dialectPrefix, String tagName) {
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

	@Override
    protected final void doProcess(
            final ITemplateContext context,
            final IModel model,
            final IElementModelStructureHandler structureHandler) {
        IOpenElementTag openTag = null;
        ICloseElementTag closeTag = null;
    	//获取第一个openTag
    	openTag = (IOpenElementTag) model.get(0);
    	//获取最后一个closeTag
    	closeTag = (ICloseElementTag) model.get(model.size()-1);
    	//获取内容
    	List<ITemplateEvent> bodyEvents=getBodyEvents(model,openTag,closeTag);
    	
    	//处理openTag，并将结果插入到model中
    	List<ITemplateEvent> list=doIntenalProcess(context,openTag,bodyEvents);

    	model.reset();
		list.forEach((item)->model.add(item));
	}
	
	protected abstract List<ITemplateEvent> doIntenalProcess(
			ITemplateContext context, 
			IProcessableElementTag openTag, 
			List<ITemplateEvent> bodyEvents);

	/**
	 * 获取
	 * @param model
	 * @param openTag
	 * @param closeTag
	 * @return
	 */
	private List<ITemplateEvent> getBodyEvents(IModel model, IOpenElementTag openTag,
			ICloseElementTag closeTag) {
		List<ITemplateEvent> bodyEvents=new ArrayList<ITemplateEvent>();
		boolean find=false;
		final int modelSize = model.size();
		for(int i=0;i<modelSize;i++) {
			if(openTag==model.get(i)) {
				find=true;
				continue;
			}
			if(closeTag==model.get(i)) {
				find=false;
				break;
			}
			if(find) {
				bodyEvents.add(model.get(i));
			}
		}
		return bodyEvents;
	}

}
