package com.wiscess.exporter.pdf.handler;


import org.slf4j.helpers.MessageFormatter;

import com.itextpdf.html2pdf.jsoup.helper.StringUtil;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.wiscess.exporter.pdf.PdfFooter;
import com.wiscess.exporter.pdf.PdfFooter.PageNoPosition;
import com.wiscess.exporter.pdf.PdfHeader;

public class HeadertFooterHandler implements IEventHandler {
	
	protected Document document;
	protected PdfHeader header;
	
	protected PdfFooter footer;
	
	public HeadertFooterHandler(Document document){
		this.document=document;
	}
    @Override
    public void handleEvent(Event event) {
    	//获取基础对象
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        PdfDocument pdfDoc = ((PdfDocumentEvent) event).getDocument();
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
        int pageNumber = pdfDoc.getPageNumber(page);
        //
        float leftMargin=document.getLeftMargin();
        float rightMargin=document.getRightMargin();
        float topMargin=document.getTopMargin();
        float bottomMargin=document.getBottomMargin();
        
        try(Canvas canvas=new Canvas(pdfCanvas, pageSize)){
            //header
        	if(topMargin>0 && header!=null){
	        	//上页边距有空间时，显示页眉内容
        		float fontSize=header.getFontSize()!=null?header.getFontSize():12;
        		float top=pageSize.getTop() - topMargin + (float)(fontSize*1.5);
        		if(!StringUtil.isBlank(header.getHeaderLeft())){
        			showText(canvas, header.getHeaderLeft(), leftMargin, top ,fontSize, TextAlignment.LEFT);
	        	}
        		if(!StringUtil.isBlank(header.getHeaderCenter())){
        			showText(canvas, header.getHeaderCenter(), pageSize.getWidth() / 2, top, fontSize, TextAlignment.CENTER);
	        	}
        		if(!StringUtil.isBlank(header.getHeaderRight())){
        			showText(canvas, header.getHeaderRight(), pageSize.getWidth()-rightMargin, top, fontSize, TextAlignment.RIGHT);
	        	}
        	}
        	if(bottomMargin>0 && footer!=null){
        		//下边距有空间时，显示页脚内容
        		float fontSize=footer.getFontSize();
        		float bottom=bottomMargin /2;
        		float left=0;
        		/**
        		 * 页码显示位置
        		 */
        		TextAlignment position=null;
        		//先处理页码的显示
        		if(!StringUtil.isBlank(footer.getPageFormat())){
        			//
        			int realPageNo=pageNumber-(1-footer.getFirstPageNumber());
        			String pageNo=MessageFormatter.format(footer.getPageFormat(), realPageNo).getMessage();
        			if(footer.getFirstPageShow() || pageNumber>1){
        				//首页显示或者非首页时
        				//计算显示位置
        				switch (footer.getPosition()) {
						case LEFT:
							//居左显示
							left=leftMargin;
							position=TextAlignment.LEFT;
							break;
						case CENTER:
							//居中显示
							left=pageSize.getWidth() / 2;
							position=TextAlignment.CENTER;
							break;
						case RIGHT:
							//居右显示:
							left=pageSize.getWidth()-rightMargin;
							position=TextAlignment.RIGHT;
							break;
						case BOOK:
							//根据当前页码的奇偶页显示
							left=realPageNo%2==0?leftMargin:(pageSize.getWidth()-rightMargin);
							position=realPageNo%2==0?TextAlignment.LEFT:TextAlignment.RIGHT;
							break;
						default:
							break;
						}
        				showText(canvas, pageNo,left , bottom,fontSize,position);
        			}
        		}
        		
        		if(!StringUtil.isBlank(footer.getFooterLeft()) && position!=TextAlignment.LEFT){
        			showText(canvas, footer.getFooterLeft(), leftMargin, bottom,fontSize, TextAlignment.LEFT);
	        	}
        		if(!StringUtil.isBlank(footer.getFooterCenter()) && position!=TextAlignment.CENTER){
        			showText(canvas, footer.getFooterCenter(), pageSize.getWidth() / 2, bottom,fontSize, TextAlignment.CENTER);
	        	}
        		if(!StringUtil.isBlank(footer.getFooterRight()) && position!=TextAlignment.RIGHT){
        			showText(canvas, footer.getFooterRight(), pageSize.getWidth()-rightMargin, bottom,fontSize, TextAlignment.RIGHT);
	        	}
        	}
        }
    }

    private void showText(Canvas canvas,String text,float x,float y,float fontSize,TextAlignment align){
    	Paragraph p = new Paragraph(text).setMultipliedLeading(1).setMargin(0);
    	//设置默认字体
    	p.setFont((PdfFont)document.getProperty(Property.FONT)).setFontSize(fontSize);
    	canvas.showTextAligned(p, x, y, align, VerticalAlignment.MIDDLE);
    }
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public PdfHeader getHeader() {
		return header;
	}
	public void setHeader(PdfHeader header) {
		this.header = header;
	}
	public PdfFooter getFooter() {
		return footer;
	}
	public void setFooter(PdfFooter footer) {
		//初始化默认参数
		if(footer.getFirstPageNumber()==null){
			footer.setFirstPageNumber(1);
		}
		if(footer.getFontSize()==null){
			footer.setFontSize(12);
		}
		if(footer.getFirstPageShow()==null){
			footer.setFirstPageShow(true);
		}
		if(footer.getPosition()==null){
			footer.setPosition(PageNoPosition.CENTER);
		}
		this.footer = footer;
	}
}
