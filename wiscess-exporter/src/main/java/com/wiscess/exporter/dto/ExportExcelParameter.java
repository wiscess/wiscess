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
public class ExportExcelParameter extends AssignedSheet{

	/**
	 * 模板文件名称
	 */
	private String templateName;
	
	/**
	 * Sheet列表
	 */
	private List<AssignedSheet> sheets;
	
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<AssignedSheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<AssignedSheet> sheets) {
		this.sheets = sheets;
	}

}
