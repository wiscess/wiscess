package com.wiscess.wechat.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateMessageDataItem {
	private String value;
	private String color="#173177";
}
