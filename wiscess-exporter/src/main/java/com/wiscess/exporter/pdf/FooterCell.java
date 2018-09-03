package com.wiscess.exporter.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

public class FooterCell extends PdfCell{
	public FooterCell(){
		this(1,1);
	}
	public FooterCell(int rowspan, int colspan) {
		super(rowspan,colspan);
		setKeepTogether(true);
		setTextAlignment(TextAlignment.CENTER);
		setBackgroundColor(ColorConstants.GRAY);
		setFontSize(12);
	}

	public PdfCell add(IBlockElement item){
		super.add(item);
		return this;
	}
	
	public PdfCell add(Image img) {
		img.setAutoScale(true);
		super.add(img);
		return this;
	}
	public PdfCell add(String item){
		super.add(new Paragraph(item));
		return this;
	}
}
