package com.wiscess.wechat.message.custom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本消息
 * @author wh
 * @date 2020-03-02
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Text {
	private String content;
}
