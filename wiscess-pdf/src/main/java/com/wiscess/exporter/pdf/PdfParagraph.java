package com.wiscess.exporter.pdf;

import java.io.IOException;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.wiscess.exporter.util.PdfExportUtil;

/**
 * Pdf的段落
 * @author wh
 */
public class PdfParagraph extends Paragraph{
	
	private float fontSize;
	private PdfFont pdfFont;
	private int firstLineIndent=0;
	
	public PdfParagraph(){
		this("");
	}
	
	public PdfParagraph(String text) {
		this(text, 12f);
	}
	public PdfParagraph(String text,float fontSize) {
		this(text,fontSize,ColorConstants.BLACK);
	}
	public PdfParagraph(String text,float fontSize,Color fontColor) {
		super(text);
		this.fontSize=fontSize;
		//设置默认字体
		try {
			pdfFont=PdfFontFactory.createFont(PdfExportUtil.FONT_PATH+",1",PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Style style=new Style();
		style.setFontSize(fontSize);
		style.setFontColor(fontColor);
		this.addStyle(style);
	}

	public PdfParagraph setFont(PdfFont font){
		super.setFont(font);
		super.setFirstLineIndent(4);
		this.pdfFont=font;
		//重新设置缩进
		firstLineIndent(firstLineIndent);
		return this;
	}
	public PdfParagraph setFontSize(float fontSize){
		super.setFontSize(fontSize);
		this.fontSize=fontSize;
		//重新设置缩进
		firstLineIndent(firstLineIndent);
		return this;
	}
	public PdfParagraph firstLineIndent(int number){
		/**
		 * 计算一个中文字的长度
		 */
		firstLineIndent=number;
		setFirstLineIndent(number*pdfFont.getWidth("王", this.fontSize));
		return this;
	}
	
	/**
	 * 居中
	 * @return
	 */
	public PdfParagraph alignCenter(){
		setTextAlignment(TextAlignment.CENTER);
		return this;
	}
	/**
	 * 居右
	 * @return
	 */
	public PdfParagraph alignRight(){
		setTextAlignment(TextAlignment.RIGHT);
		return this;
	}
}
