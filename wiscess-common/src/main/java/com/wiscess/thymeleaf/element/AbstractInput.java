package com.wiscess.thymeleaf.element;

import org.thymeleaf.model.IModelFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractInput<T> extends WiscessElement<T>{

	public AbstractInput(IModelFactory modelFactory,String type){
		super(modelFactory,"input");
		
		setAttribute("type",type);
	}

	public T type(String type){
		setAttribute("type",type);
		return (T)this;
	}
	public T readonly(String readonly){
		setAttribute("readonly",readonly);
		return (T)this;
	}
	public T style(String style){
		setAttribute("style",style);
		return (T)this;
	}
	public T onclick(String onclick){
		setAttribute("onclick",onclick);
		return (T)this;
	}
	public T onfocus(String onfocus){
		setAttribute("onfocus",onfocus);
		return (T)this;
	}
}
