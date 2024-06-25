package com.wiscess.exporter.dto;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;

import com.wiscess.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指定的Sheet页属性
 * @author wanghai
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignedSheet<K> implements Serializable {
	/**
	 * Sheet名称
	 */
	private String sheetName="Sheet1";
	/**
	 * 使用模板Sheet名称
	 */
	private String templateSheetName="Sheet1";
	
	/**
	 * 模板文件的总列数(实际数据)
	 */
	private int totalCol;
	
	/**
	 * 在指定位置显示数据
	 */
	private List<AssignedCell> assignedCells;
	
	/**
	 * 数据行，数据区读取该行样式
	 */
	private AssignedCell dataRow;
	
	/**
	 * 样式表
	 */
	private List<AssignedCell> cellStyleList;
	/**
	 * 特殊显示行，特殊行读取该行样式
	 */
	private AssignedCell highLightRow;
	/**
	 * 数据行所占行数，默认为1
	 */
	private int dataRowSpan=1;
	
	/**
	 * 新数据按新建或copy模板的方式
	 */
	private boolean needCopyTemplateRow=false;
	
	/**
	 * 追加到Sheet
	 */
	private String AppendToSheet;
	
	/**
	 * 是否隐藏
	 */
	private boolean hidden=false;
	
	/**
	 * 设置是否自动调整行高，默认为false，使用模板中指定的行高
	 */
	private boolean autoHeight = false;
	/**
	 * 每列宽度
	 */
	private Integer[] columnWidths;
	
	/**
	 * 每行高度
	 */
	private Integer[] rowHeights;
//	/**
//	 * 表头内容
//	 */
	private List<String[]> titleData;
	/**
	 * 数据内容
	 */
	private List<K> data;
	/**
	 * 线程数量和线程内数据数量
	 */
	private List<ThreadDto> threadCntList;
	/**
	 * 动态数据获取
	 */
	private BiFunction<Object,Integer,List<K>> threadListAction;

	private BiFunction<K,Integer,AssignedCell[]> action;

	/**
	 * 使用多线程
	 */
	private boolean userMultiThread=false;
	
	public AssignedSheet(String sheetName,String templateSheetName){
		this.sheetName=sheetName;
		this.templateSheetName=templateSheetName;
	}
	
	public void setSheetName(String sheetName) {
		if(StringUtils.isNotEmpty(sheetName)){
			sheetName=sheetName.replaceAll("/", "|");
		}
		this.sheetName = sheetName;
	}
	public void setData(List<K> _data) {
		data = _data;
	}
	public void setDataAction(List<K> _data,BiFunction<K,Integer,AssignedCell[]> _action) {
		data = _data;
		action = _action;
	}
}