package com.wiscess.exporter.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 导出Excel文件使用的配置参数
 * @author wanghai
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class ExportExcelParameter<K> extends AssignedSheet<K>{

	/**
	 * 模板文件名称
	 */
	private String templateName;
	
	/**
	 * Sheet列表
	 */
	private List<AssignedSheet<K>> sheets;
	
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<AssignedSheet<K>> getSheets() {
		return sheets;
	}

	public void setSheets(List<AssignedSheet<K>> sheets) {
		this.sheets = sheets;
	}

}
