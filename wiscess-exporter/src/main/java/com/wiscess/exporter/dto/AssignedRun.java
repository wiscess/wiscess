package com.wiscess.exporter.dto;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;

/**
 * 指定的内容
 * @author wh
 *
 */
public class AssignedRun extends AssignedElement{
	//必填内容
	private String text;
	
	//附加内容
	private boolean isBold=false;
	private boolean isItalic=false;
	private String fontFamily="宋体";
	private int fontSize=10;
	private int textPosition=0;
	private String color="";
	private UnderlinePatterns underline=null;
	
	public AssignedRun(){
	}

	public AssignedRun(String text){
		this.text=text;
	}
	public String getText() {
		return text;
	}

	public AssignedRun setText(String text) {
		this.text = text;
		return this;
	}

	public boolean isBold() {
		return isBold;
	}

	public AssignedRun setBold(boolean isBold) {
		this.isBold = isBold;
		return this;
	}

	public boolean isItalic() {
		return isItalic;
	}

	public AssignedRun setItalic(boolean isItalic) {
		this.isItalic = isItalic;
		return this;
	}

	public int getTextPosition() {
		return textPosition;
	}

	public AssignedRun setTextPosition(int textPosition) {
		this.textPosition = textPosition;
		return this;
	}

	public UnderlinePatterns getUnderline() {
		return underline;
	}

	public AssignedRun setUnderline(UnderlinePatterns underline) {
		this.underline = underline;
		return this;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public AssignedRun setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
		return this;
	}

	public int getFontSize() {
		return fontSize;
	}

	public AssignedRun setFontSize(int fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public String getColor() {
		return color;
	}

	public AssignedRun setColor(String color) {
		this.color = color;
		return this;
	}
}
