package com.wiscess.exporter.pdf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PdfMargin {
	private float left;
	private float top;
	private float right;
	private float bottom;
	
	public PdfMargin(float size){
		this(size,size);
	}
	public PdfMargin(float topBottom,float leftRight){
		this(topBottom,leftRight,topBottom,leftRight);
	}
	public PdfMargin(float top,float right,float bottom,float left){
		this.top=top;
		this.right=right;
		this.bottom=bottom;
		this.left=left;
	}
}
