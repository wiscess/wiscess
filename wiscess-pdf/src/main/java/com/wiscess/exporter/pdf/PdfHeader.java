package com.wiscess.exporter.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfHeader {
	protected String headerLeft;
	protected String headerCenter;
	protected String headerRight;
	/**
	 * 默认字体大小
	 */
	private Integer fontSize;
}
