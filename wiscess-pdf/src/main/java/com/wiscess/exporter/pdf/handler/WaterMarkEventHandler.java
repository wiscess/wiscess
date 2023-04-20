package com.wiscess.exporter.pdf.handler;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.wiscess.exporter.util.PdfExportUtil;

public class WaterMarkEventHandler implements IEventHandler{
	protected String waterMark;
	private Image img;
	protected PdfFont font;
	
    public WaterMarkEventHandler(String waterMark) {
        try{
        	this.waterMark=waterMark;
        	font=PdfFontFactory.createFont(PdfExportUtil.FONT_PATH+",1",PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        	img=new Image(ImageDataFactory.create(waterMark));
        }catch(Exception e){
        	img=null;
        }
    }
    @SuppressWarnings("resource")
	public void handleEvent(Event event) {
 
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        
        //透明
        PdfExtGState gs1 = new PdfExtGState();
        gs1.setFillOpacity(0.5f);
        
        // properties
        PdfCanvas pdfCanvas ;
        Rectangle pagesize = page.getPageSizeWithRotation();
        page.setIgnorePageRotationForContent(true);
 
        //图片和文字方法不同
        //图片插入到原有内容下方
        if(img!=null){
            //插入当前内容之前
        	pdfCanvas = new PdfCanvas(page.newContentStreamBefore(),
                    page.getResources(), pdfDoc);
        	Rectangle area = page.getPageSize();
        	
            //不失真，保持纵横比，铺满页面
            float horizontalScaling = area.getWidth() / img.getImageWidth();
            float verticalScaling =  area.getHeight() / img.getImageHeight();
            
            horizontalScaling=Math.min(horizontalScaling, verticalScaling);
            img.scale(horizontalScaling, horizontalScaling);
            float w=img.getImageScaledWidth();
            float h=img.getImageScaledHeight();
            //居中
            img.setFixedPosition((area.getWidth()-w)/2, (area.getHeight()-h)/2);
            pdfCanvas.saveState();
            pdfCanvas.setExtGState(gs1);
            new Canvas(pdfCanvas, area)
                    .add(img);
            pdfCanvas.restoreState();
        }
        //文本覆盖到原有内容上方
        else if(waterMark!=null){
        	 float x, y;
        	 //取页面中心点
             x = (pagesize.getLeft() + pagesize.getRight()) / 2;
             y = (pagesize.getTop() + pagesize.getBottom()) / 2;
             //插入当前内容上方
             pdfCanvas = new PdfCanvas(page);
             pdfCanvas.saveState();
             pdfCanvas.setExtGState(gs1);
             //旋转角度30度
             Canvas canvas = new Canvas(pdfCanvas, page.getPageSize());
             //设置水印的字体
             canvas.setFontColor(ColorConstants.BLACK);
             canvas.setFontSize(60);
             canvas.setFont(font);
             canvas.showTextAligned(waterMark, x, y,  TextAlignment.CENTER, VerticalAlignment.MIDDLE,(float)Math.PI/6);
             pdfCanvas.restoreState();
        }
    }
}
