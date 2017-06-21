package com.wiscess.wechat.template;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateMessage {

	private String touser;
	private String template_id;
	private String url;
	private Map<String, TemplateMessageDataItem> Data;
}
