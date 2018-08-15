package com.wiscess.thymeleaf.element;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.ITemplateEvent;

import com.wiscess.utils.StringUtils;
@SuppressWarnings("unchecked")
public abstract class WiscessElement<T>{
	
	protected String elementName;
	protected IModelFactory modelFactory;
	protected String text="";
	
    final Map<String,String> attributes = new LinkedHashMap<String,String>();

	public WiscessElement(IModelFactory _modelFactory,String _elementName){
		modelFactory=_modelFactory;
		elementName=_elementName;
	}
	public T id(String id){
		setAttribute("id",id);
		return (T)this;
	}
	public T name(String name){
		setAttribute("name",name);
		return (T)this;
	}
	public T value(String value){
		setAttribute("value",value);
		return (T)this;
	}
	public T className(String className){
		setAttribute("class",className);
		return (T)this;
	}
	public T title(String title){
		setAttribute("title",title);
		return (T)this;
	}
	public T text(String _text){
		text=_text;
		return (T)this;
	}
	
	protected void setAttribute(String name, String value){
		if(value!=null){
			attributes.put(name, value);
		}
	}
	public List<ITemplateEvent> build(){
		List<ITemplateEvent> nodes=new ArrayList<>();
		if(StringUtils.isEmpty(text)){
	        //没有内容
			nodes.add(modelFactory.createStandaloneElementTag(elementName, attributes, AttributeValueQuotes.DOUBLE, false, true));
		}else{
			//有内容
			nodes.add(modelFactory.createOpenElementTag(elementName, attributes, AttributeValueQuotes.DOUBLE, false));
			nodes.add(modelFactory.createText(text));
			nodes.add(modelFactory.createCloseElementTag(elementName));
		}
        return nodes;
	}
}
