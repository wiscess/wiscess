package com.wiscess.exporter.dto;

/**
 * Word的元素，可以包含：表格、段落、内容、图片等
 * 表格内可以是段落、内容、图片
 * 段落内可以是内容、图片
 * 内容是最小的显示区域
 * 图片可以是http或绝对路径
 * @author wh
 *
 */
public abstract class AssignedElement {
	/**
	 * 是否在后面加一个换行
	 */
	private boolean hasCR=false;
	
	public boolean hasCR() {
		return hasCR;
	}

	public AssignedElement addCR(){
		this.hasCR=true;
		return this;
	}
	
}
