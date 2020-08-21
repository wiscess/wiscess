package com.wiscess.oauth.exception;

import org.springframework.lang.Nullable;

public enum TokenCode {
	
	/**
	 * 认证失败
	 */
	AUTH_FAILURE(50001,"auth failure"),
	/**
	 * 用户信息校验失败
	 */
	//USER_AUTH_FAILURE(50002,"user auth failure"),
	/**
	 * token缺失，没有访问权限
	 */
	MISS_TOKEN(50008,"miss token"),
	/**
	 * 无效的token，token过期
	 */
	INVALID_TOKEN(50009,"invalid_token")
	
	;
	
	
	private final Integer value;

	private final String reasonPhrase;

	TokenCode(Integer value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this status code.
	 */
	public String getReasonPhrase() {
		return this.reasonPhrase;
	}

	/**
	 * Return a string representation of this status code.
	 */
	@Override
	public String toString() {
		return this.value + " " + name();
	}


	/**
	 * Return the enum constant of this type with the specified numeric value.
	 * @param statusCode the numeric value of the enum to be returned
	 * @return the enum constant with the specified numeric value
	 * @throws IllegalArgumentException if this enum has no constant for the specified numeric value
	 */
	public static TokenCode valueOf(Integer tokenCode) {
		TokenCode code = resolve(tokenCode);
		if (code == null) {
			throw new IllegalArgumentException("No matching constant for [" + tokenCode + "]");
		}
		return code;
	}

	/**
	 * Resolve the given status code to an {@code HttpStatus}, if possible.
	 * @param statusCode the HTTP status code (potentially non-standard)
	 * @return the corresponding {@code HttpStatus}, or {@code null} if not found
	 * @since 5.0
	 */
	@Nullable
	public static TokenCode resolve(Integer tokenCode) {
		for (TokenCode status : values()) {
			if (status.value == tokenCode) {
				return status;
			}
		}
		return null;
	}

}
