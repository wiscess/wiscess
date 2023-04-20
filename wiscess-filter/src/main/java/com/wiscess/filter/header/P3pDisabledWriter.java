package com.wiscess.filter.header;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

public class P3pDisabledWriter implements HeaderWriter{
	private static final String P3P_DISABLED_HEADER = "P3P";

	private String headerValue="CP=CAO PSA OUR";

	/**
	 * Create a new instance
	 */
	public P3pDisabledWriter() {
	}

	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader(P3P_DISABLED_HEADER, headerValue);
	}

	@Override
	public String toString() {
		return getClass().getName() + " [headerValue=" + headerValue + "]";
	}
}
