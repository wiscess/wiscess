package com.wiscess.exporter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 指定位置
 * @author wanghai
 */
@Data
@AllArgsConstructor
@Builder
public class AssignedCell{
	//照片
	public static final int CELL_STYLE_NORMAL=0;
	public static final int CELL_STYLE_HLROW_COL=1;
	public static final int CELL_STYLE_HLROW=2;
	public static final int CELL_STYLE_FORMULA=8;
	public static final int CELL_STYLE_PHOTO=9;
	
	private int row;
	private int col;
	private int rowEnd;
	private int colEnd;
	private Object value;
	/**
	 * 使用样式，默认为0，使用普通数据的样式
	 * 1.使用模板中相应位置的样式
	 * 2.使用模板中特殊显示的样式
	 */
	private int useStyle = 0; 
	/**
	 * 默认锁定单元格
	 */
	private boolean locked = true; 
	
	public AssignedCell(int row,int col,Object value){
		this(row,col,row,col,value,0);
	}
	public AssignedCell(int row,int col,int rowEnd,int colEnd,Object value){
		this(row,col,rowEnd,colEnd,value,0);
	}
	public AssignedCell(int row,int col,Object value, int useStyle){
		this(row,col,row,col,value,useStyle);
	}
	
	public AssignedCell(int row,int col,int rowEnd,int colEnd,Object value, int useStyle){
		this.row=row;
		this.col=col;
		this.rowEnd=rowEnd;
		this.colEnd=colEnd;
		this.value=value;
		this.useStyle=useStyle;
	}
}
