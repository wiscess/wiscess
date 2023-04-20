package com.wiscess.exporter.pdf.handler;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;

public class BackgroundEventHandler implements IEventHandler{
	protected Image img;
	 
    public BackgroundEventHandler(Image img) {
        this.img = img;
    }
    @SuppressWarnings("resource")
	public void handleEvent(Event event) {
 
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        //放在现有内容的底层
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(),
                page.getResources(), pdfDoc);
        Rectangle area = page.getPageSize();

        //不失真，保持纵横比，铺满页面
        float horizontalScaling = area.getWidth() / img.getImageWidth();
        float verticalScaling =  area.getHeight() / img.getImageHeight();
        
        horizontalScaling=Math.max(horizontalScaling, verticalScaling);
        img.scale(horizontalScaling, horizontalScaling);
        float w=img.getImageScaledWidth();
        float h=img.getImageScaledHeight();
        //居中
        img.setFixedPosition((area.getWidth()-w)/2, (area.getHeight()-h)/2);
        new Canvas(canvas, area)
                .add(img);
    }

}
