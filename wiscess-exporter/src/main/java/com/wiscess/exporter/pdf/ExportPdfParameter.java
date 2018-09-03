package com.wiscess.exporter.pdf;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExportPdfParameter {
	/**
	 * 模板文件名称
	 */
	private String templateName;
	private String title;
	private String author;
	private String subject;
	private String keyword;
	private String creator;
	
	private PageSize pageSize=PageSize.A4;
	private PdfFont pdfFont;
	private PdfMargin margin;  //默认页边距36
	private PageSize currentPageSize;
	private PdfMargin currentMargin;
	private Float fontSize=12f;
	
	/**
	 * 背景图片
	 */
	private String backgroundImage;
	/**
	 * 页眉设置
	 */
	private PdfHeader header;
	/**
	 * 页码设置
	 */
	private PdfFooter footer;
	/**
	 * 水印设置
	 */
	private String waterMark;
	
	private List<IElement> dataList=new ArrayList<>();
	
	public void addData(IElement e){
		if(dataList==null){
			dataList=new ArrayList<>();
		}
		dataList.add(e);
	}
	public void addData(String text){
		if(dataList==null){
			dataList=new ArrayList<>();
		}
		dataList.add(new Paragraph().add(text));
	}
	
}
