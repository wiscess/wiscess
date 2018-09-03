package com.wiscess.exporter.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

/**
 * 表格单元格
 * @author wh
 *
 */
public class AssignedTableCell extends AssignedElement {

	//单元格属性
	private int width;
	private int height=0;
	private String bgColor;
	private String color;
	private ParagraphAlignment alignment;
	
	//单元格内容
	private List<AssignedElement> cellList;
	
	public AssignedTableCell(){
		cellList=new ArrayList<AssignedElement>();
	}
	public AssignedTableCell(String cellValue){
		this();
		cellList.add(new AssignedRun(cellValue));
	}
	public int getWidth() {
		return width;
	}
	public AssignedTableCell setWidth(int width) {
		this.width = width;
		return this;
	}
	public int getHeight() {
		return height;
	}
	public AssignedTableCell setHeight(int height) {
		this.height = height;
		return this;
	}
	public String getBgColor() {
		return bgColor;
	}
	public AssignedTableCell setBgColor(String bgColor) {
		this.bgColor = bgColor;
		return this;
	}
	public String getColor() {
		return color;
	}
	public AssignedTableCell setColor(String color) {
		this.color = color;
		return this;
	}
	public AssignedTableCell addElement(AssignedElement ae){
		if(ae instanceof AssignedPicture || ae instanceof AssignedRun){
			this.cellList.add(ae);
		}else{
			//
			this.cellList.add(ae);
		}
		return this;
	}
	public List<AssignedElement> getCellList(){
		return cellList;
	}
	public ParagraphAlignment getAlignment() {
		return alignment;
	}
	public AssignedTableCell setAlignment(ParagraphAlignment alignment) {
		this.alignment = alignment;
		return this;
	}
	public String toString(){
		return cellList.toString();
	}
}
