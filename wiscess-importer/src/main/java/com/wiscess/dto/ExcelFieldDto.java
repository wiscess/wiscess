package com.wiscess.dto;

import java.sql.Types;

import lombok.Builder;
import lombok.Data;

/**
 * 定义导入字段的名称
 * @author wh
 *
 */
@Builder
@Data
public class ExcelFieldDto {

	private String title;
	
	private Integer colIndex;
	
	private String fieldName;
	
	@Builder.Default
	private int fieldType=Types.VARCHAR;
	
	@Builder.Default
	private Integer width=80;
	
	@Builder.Default
	private Boolean isShow=false;
	
	@Builder.Default
	private Boolean empty=false; //导入时必须有值
	
}
