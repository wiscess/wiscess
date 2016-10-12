package com.wiscess.thymeleaf.element;

import java.util.Collections;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
@SuppressWarnings("unchecked")
public abstract class WiscessElement<T> {
	
	protected Element _element;
	
	public WiscessElement(String documentName){
		_element=new Element(documentName);
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
	public T text(String text){
		_element.setChildren(Collections.singletonList((Node)new Text(text)));
		return (T)this;
	}
	
	protected void setAttribute(String name, String value){
		if(value!=null)
			_element.setAttribute(name, value);
	}
	public Element build(){
		return _element;
	}
}
