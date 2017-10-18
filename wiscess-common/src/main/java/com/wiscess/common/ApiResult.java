package com.wiscess.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResult {
	public ApiResult(String msg){
		this.msg=msg;
		this.code="200";
	}
	private String code;
	private String msg;
}
