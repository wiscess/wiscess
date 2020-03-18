package com.wiscess.wechat.message.custom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服消息
 * @author wh
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomService {
	private String kf_account;
}
