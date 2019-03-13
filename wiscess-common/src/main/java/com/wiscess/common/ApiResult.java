package com.wiscess.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResult {
	public ApiResult(String msg){
		this("200", msg);
	}
	public ApiResult(String code,String msg){
		this(code, msg, null);
	}
	public ApiResult(String code,String msg,Object data) {
		this.code=code;
		this.msg=msg;
		this.data=data;
	}
	@Builder.Default
	private String code="200";
	private String msg;
	private Object data;
}
