package com.wiscess.wechat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiResult {
	private String code;
	private String error;
	private String msg;
}
