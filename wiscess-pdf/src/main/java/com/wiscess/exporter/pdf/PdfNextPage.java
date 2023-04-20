package com.wiscess.exporter.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.AreaBreak;

/**
 * 分页
 * @author wh
 */
public class PdfNextPage extends AreaBreak{
	
	private PdfMargin margin;
	
	public PdfNextPage() {
		super();
	}
	
	public PdfNextPage(PageSize pageSize) {
		super(pageSize);
	}

	public PdfNextPage margin(PdfMargin margin){
		this.margin=margin;
		return this;
	}
	public PdfMargin getMargin() {
		return margin;
	}

	public void setMargin(PdfMargin margin) {
		this.margin = margin;
	}

}
