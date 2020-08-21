package com.wiscess.oauth.utils;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	LocalDateTime invalidDate;
	String token;
}
