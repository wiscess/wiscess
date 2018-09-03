package com.wiscess.exporter.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

/**
 * 用WaterMarkEventHandler代替
 * @author wh
 */
@Deprecated
public class WaterMarkUtil {

 	/**
	 * 加水印（文字）
	 * 
	 * @param inputFile
	 *            需要加水印的PDF路径
	 * @param outputFile
	 *            输出生成PDF的路径
	 * @param waterMark
	 *            水印文字
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void stringWaterMark(String src,String dest, String waterMark) throws FileNotFoundException, IOException {
        PdfFont sysFont=PdfFontFactory.createFont(PdfExportUtil.FONT_PATH+",1",PdfEncodings.IDENTITY_H, false);
        waterMark(src, dest, null, new Paragraph(waterMark).setFont(sysFont).setFontSize(30));
	}
	/**
	 * 加水印（图片）
	 * 
	 * @param inputFile
	 *            需要加水印的PDF路径
	 * @param outputFile
	 *            输出生成PDF的路径
	 * @param imageFile
	 *            水印图片路径
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void imageWaterMark(String src,String dest, String imageFile) throws FileNotFoundException, IOException {
        //水印文件
        waterMark(src, dest, ImageDataFactory.create(imageFile), null);
	}

	/**
	 * 添加水印（文字或图片）
	 * @param src
	 * @param dest
	 * @param img
	 * @param waterMark
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void waterMark(String src,String dest,ImageData img,Paragraph waterMark)throws FileNotFoundException, IOException{
		//读原始文件，并设置写到目标文件
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
		//自动关闭doc
        try(Document doc = new Document(pdfDoc)){
	        //获取总页数
	        int n = pdfDoc.getNumberOfPages();
	        
	        //透明
	        PdfExtGState gs1 = new PdfExtGState();
	        gs1.setFillOpacity(0.5f);
	        
	        // properties
	        PdfCanvas under;
	        // loop over every page
	        for (int i = 1; i <= n; i++) {
	            PdfPage pdfPage = pdfDoc.getPage(i);
	            Rectangle pagesize = pdfPage.getPageSizeWithRotation();
	            pdfPage.setIgnorePageRotationForContent(true);
	     
	            //图片和文字方法不同
	            //图片插入到原有内容下方
	            if(img!=null){
	                //插入当前内容之前
	                under = new PdfCanvas(pdfDoc.getPage(i).newContentStreamBefore(),pdfDoc.getPage(i).getResources(), pdfDoc);
	                under.saveState();
	                under.setExtGState(gs1);
	                under.addImage(img, 0, 0, pagesize.getHeight(),false, false);
	                under.restoreState();
	            }
	            //文本覆盖到原有内容上方
	            else if(waterMark!=null){
	            	 float x, y;
	            	 //取页面中心点
	                 x = (pagesize.getLeft() + pagesize.getRight()) / 2;
	                 y = (pagesize.getTop() + pagesize.getBottom()) / 2;
	                 //插入当前内容上方
	                 under = new PdfCanvas(pdfDoc.getPage(i));
	                 under.saveState();
	                 under.setExtGState(gs1);
	                 //旋转角度30度
	                 doc.showTextAligned(waterMark, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP,(float)Math.PI/6);
	                 under.restoreState();
	            }
	        }
        }
	}
}
