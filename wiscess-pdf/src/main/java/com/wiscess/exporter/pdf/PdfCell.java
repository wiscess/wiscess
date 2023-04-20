package com.wiscess.exporter.pdf;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.wiscess.exporter.pdf.renderer.ImageBackgroundCellRenderer;

public class PdfCell extends Cell{
	
	public PdfCell(){
		super();
	}
	public PdfCell(int rowspan, int colspan) {
		super(rowspan,colspan);
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
	/**
	 * 设置背景图片
	 * @param imagePath
	 * @return
	 */
	public PdfCell backgroundImage(String imagePath,float width){
		Image img = PdfImage.getImage(imagePath);
		setNextRenderer(new ImageBackgroundCellRenderer(this, img));
		//计算高度
		if(!this.hasProperty(Property.HEIGHT)){
    		//未指定高度，根据宽度进行等比例缩放
			setHeight(width * img.getXObject().getHeight() / img.getXObject().getWidth());
    	}
		return this;
	}
}
