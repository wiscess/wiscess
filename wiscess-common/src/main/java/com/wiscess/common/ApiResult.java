package com.wiscess.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResult {
	public ApiResult(String msg){
		this.msg=msg;
		this.code="200";
	}
	@Builder.Default
	private String code="200";
	private String msg;
}
