package com.wiscess.audit.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

	private Integer logId;
	private String applicationName;
	private String userId;
	private String userName;
	private String method;
	private String url;
	private String remoteAddr;
	private String sessionId;
	private String parameters;
	private Timestamp createTime;
	private Timestamp returnTime;
	private Integer statusCode;
	private Long timeCousuming;
}
