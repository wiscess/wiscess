package com.wiscess.exporter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.util.CellAddress;

import java.io.Serializable;

/**
 * 指定位置
 * @author wanghai
 */
@Data
@AllArgsConstructor
@Builder
public class AssignedCell implements Serializable {
	//照片
	public static final int DATA_STYLE_NORMAL=0;
//	public static final int DATA_STYLE_HLROW_COL=1;
	public static final int DATA_STYLE_HLROW=2;
	public static final int DATA_STYLE_FORMULA=8;
	public static final int DATA_STYLE_PHOTO=9;
	public static final int DATA_STYLE_ASSIGNED=10;
	
	private int row;
	private int col;
	private int rowEnd;
	private int colEnd;
	private Object value;
	/**
	 * 使用数据样式，默认为0，使用普通数据的样式
	 * 1.使用模板中相应位置的样式
	 * 2.使用模板中特殊显示的样式
	 */
	private int dataStyle = 0; 
	/**
	 * 使用指定的单元格样式，根据excle中的顺序
	 */
	private Integer cellStyle;
	/**
	 * 默认锁定单元格
	 */
	private boolean locked = true;
	public AssignedCell(String cellStr,Object value){
		this(new CellAddress(cellStr),value);
	}
	public AssignedCell(CellAddress cell,Object value){
		this(cell.getRow(),cell.getColumn(),value,0);
	}
	public AssignedCell(int row,int col,Object value){
		this(row,col,row,col,value,0);
	}
	public AssignedCell(int row,int col,int rowEnd,int colEnd,Object value){
		this(row,col,rowEnd,colEnd,value,0);
	}
	public AssignedCell(int row,int col,Object value, int dataStyle){
		this(row,col,row,col,value,dataStyle);
	}
	public AssignedCell(int row,int col,Object value, int dataStyle,Integer cellStyle){
		this(row,col,row,col,value,dataStyle,cellStyle);
	}
	
	public AssignedCell(int row,int col,int rowEnd,int colEnd,Object value, int dataStyle){
		this(row,col,rowEnd,colEnd,value,dataStyle,null);
	}
	public AssignedCell(int row,int col,int rowEnd,int colEnd,Object value, int dataStyle,Integer cellStyle){
		this.row=row;
		this.col=col;
		this.rowEnd=rowEnd;
		this.colEnd=colEnd;
		this.value=value;
		this.dataStyle=dataStyle;
		this.cellStyle=cellStyle;
	}
}
