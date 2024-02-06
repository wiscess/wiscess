package com.wiscess.exporter.util;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.wiscess.common.utils.FileUtils;
import com.wiscess.exporter.pdf.ExportPdfParameter;
import com.wiscess.exporter.pdf.PdfImage;
import com.wiscess.exporter.pdf.PdfMargin;
import com.wiscess.exporter.pdf.PdfNextPage;
import com.wiscess.exporter.pdf.PdfTable;
import com.wiscess.exporter.pdf.handler.BackgroundEventHandler;
import com.wiscess.exporter.pdf.handler.HeadertFooterHandler;
import com.wiscess.exporter.pdf.handler.WaterMarkEventHandler;
import com.wiscess.utils.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;

import jakarta.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class PdfExportUtil {
	
	public static String FONT_PATH = null;

	/**
	 * 导出到文件
	 * @param para
	 * @param filename
	 * @throws Exception 
	 */
	public static void exportPdf(ExportPdfParameter para,String filename) throws Exception{
		exportPdf(para, filename, null);
	}
	
	/**
	 * 导出Pdf文件到文件或浏览器
	 * @param filename
	 * @param res
	 * @return
	 */
	public static void exportPdf(ExportPdfParameter para,String filename,HttpServletResponse res) throws Exception{
		OutputStream os=null;
		try{
			if(res!=null){
				//输出到浏览器
				filename=filename.substring(filename.lastIndexOf("\\")+1);
				res.setContentType("APPLICATION/pdf");
				res.setHeader("Content-Disposition", "attachment; filename="
						+ FileUtils.encodingFileName(filename));
				os = res.getOutputStream();
			}else{
				//输出到目录
				os = new FileOutputStream(filename);
			}
			export(para,os);
		}catch(Exception e){
			throw new Exception("导出Pdf出错。", e);
		}finally {
			if(os!=null) {
				os.flush();
				os.close();
			}
		}
	}
	
	/**
	 * 根据自制的模板导出，往模板里添加数据
	 * @param templatePath
	 * @param filename
	 * @param dataMap
	 * @throws FileNotFoundException 
	 */
	public static void exportPdfByTemplate(String templatePath,String filename,Map<String,String> dataMap) {
		OutputStream os=null;
		try{
			os=new FileOutputStream(filename);
			exportPdfByTemplate(templatePath,filename,dataMap,os);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(os!=null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 输出到浏览器
	 * @param templatePath
	 * @param filename
	 * @param dataMap
	 * @param res
	 */
	public static void exportPdfByTemplate(String templatePath,String filename,Map<String,String> dataMap,HttpServletResponse res) {
		OutputStream os=null;
		try{
			//输出到浏览器
			filename=filename.substring(filename.lastIndexOf("\\")+1);
			res.setContentType("APPLICATION/pdf");
			res.setHeader("Content-Disposition", "attachment; filename="
					+ FileUtils.encodingFileName(filename));
			os = res.getOutputStream();
			exportPdfByTemplate(templatePath,filename,dataMap,os);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(os!=null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 预览到浏览器
	 * @param templatePath
	 * @param filename
	 * @param dataMap
	 * @param res
	 */
	public static void previewPdfByTemplate(String templatePath,String filename,Map<String,String> dataMap,HttpServletResponse res) {
		OutputStream os=null;
		try{
			//输出到浏览器
			filename=filename.substring(filename.lastIndexOf("\\")+1);
			res.setContentType("APPLICATION/pdf");
			res.setHeader("Content-Disposition", "filename="
					+ FileUtils.encodingFileName(filename));
			os = res.getOutputStream();
			exportPdfByTemplate(templatePath,filename,dataMap,os);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(os!=null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 根据模板输出pdf
	 * @param templatePath
	 * @param filename
	 * @param dataMap
	 * @param os
	 * @throws IOException 
	 */
	private static void exportPdfByTemplate(String templatePath,String filename,Map<String,String> dataMap,OutputStream os) throws IOException {
		PdfWriter writer = new PdfWriter(os);
		//如果使用了pdf模板，则需要根据模板来生成新pdf
		PdfReader reader=new PdfReader(new DefaultResourceLoader().getResource(templatePath).getInputStream());
		PdfDocument pdfDoc = new PdfDocument(reader, writer);
		if(dataMap!=null && !dataMap.isEmpty()) {
			//获取Pdf的表单域
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			//获取所有的表单
			Map<String,PdfFormField> fields = form.getAllFormFields();
			for(String param:dataMap.keySet()) {
				PdfFormField formField = fields.get(param);
				if(formField != null) {
					formField.setValue(dataMap.get(param));
				}
			}
			form.flattenFields();// 锁定表单，不让修改
		}
		pdfDoc.close();
	}

	/**
	 * 导出Pdf
	 * @param para
	 * @param os
	 * @throws Exception 
	 */
	private static void export(ExportPdfParameter para,OutputStream os) throws Exception{
		try(Document document = createPdfDoc(para,os)){
	        //基本属性设置
			para.getDataList().forEach(item->
				//预处理元素内容，将元素转换成pdf专用的对象
				fillElement(document,item,para)
			);
		}
	}
	/**
	 * 生成文件
	 * @param para
	 * @param os
	 * @return
	 * @throws Exception
	 */
	protected static Document createPdfDoc(ExportPdfParameter para,OutputStream os) throws Exception{
		PdfWriter writer = new PdfWriter(os);
		//如果使用了pdf模板，则需要根据模板来生成新pdf
		PdfDocument pdfDoc = StringUtils.isNotEmpty(para.getTemplateName())
				?new PdfDocument(new PdfReader(new DefaultResourceLoader().getResource(para.getTemplateName()).getInputStream()), writer)
				:new PdfDocument(writer);
		if (para.getPageSize()==null) {
			//默认A4纵向
			para.setPageSize(PageSize.A4);
		}
		//修改页边距
		if(para.getMargin()==null){
			para.setMargin(new PdfMargin(36f));
		}
		//默认中文
        pdfDoc.getCatalog().setLang(new PdfString("zh-CN"));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDoc.setDefaultPageSize(para.getPageSize());
		//创建文档
        Document document = new Document(pdfDoc);
        //设置默认字体
        if(para.getPdfFont()==null){
        	PdfFont sysFont=PdfFontFactory.createFont(para.getFontPath()+",1",PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        	para.setPdfFont(sysFont);
        }
        document.setFont(para.getPdfFont());
		
		if(para.getFontSize()==null){
			para.setFontSize(12f);
		}
		document.setFontSize(para.getFontSize());
		//文档信息
		PdfDocumentInfo info = pdfDoc.getDocumentInfo();
		if(StringUtils.isNotEmpty(para.getTitle()))
			info.setTitle(para.getTitle());//标题
		if(StringUtils.isNotEmpty(para.getAuthor()))
			info.setAuthor(para.getAuthor());//作者
		if(StringUtils.isNotEmpty(para.getSubject()))
			info.setSubject(para.getSubject());//主题
		if(StringUtils.isNotEmpty(para.getKeyword()))
			info.setKeywords(para.getKeyword());//关键词
		if(StringUtils.isNotEmpty(para.getCreator()))
			info.setCreator(para.getCreator()); //创建者

		document.setMargins(para.getMargin().getTop(), 
				para.getMargin().getRight(), 
				para.getMargin().getBottom(),
				para.getMargin().getLeft());
		//记录当前页面设置
        para.setCurrentPageSize(para.getPageSize());
        para.setCurrentMargin(para.getMargin());
        
        //设置页眉或页脚
        if(para.getFooter()!=null || para.getHeader()!=null){
	        HeadertFooterHandler handler=new HeadertFooterHandler(document);
	        handler.setFooter(para.getFooter());
	        handler.setHeader(para.getHeader());
	        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
        }        
        
		//设置背景
        if(para.getBackgroundImage()!=null){
        	Image img = new Image(ImageDataFactory.create(para.getBackgroundImage()));
	        BackgroundEventHandler handler = new BackgroundEventHandler(img);
	        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, handler);
        }	        
        //设置水印
        if(para.getWaterMark()!=null){
        	WaterMarkEventHandler handler = new WaterMarkEventHandler(para.getWaterMark());
	        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, handler);
        }
        
		return document;
	}

	/**
 	 * 将Element添加到doc中，根据Element不同，调用不同的方法
	 * @param para 
 	 * @param doc
 	 * @param item
 	 */
 	protected static void fillElement(Document doc,IElement item, ExportPdfParameter para){
 		//TODO 后期版本预计将word和pdf的元素进行合并，形成自己的元素对象，在各自的导出工具中再转换成内置的元素
 		if (item instanceof PdfImage){
 			//只处理自定义的图片
 			doc.add(((PdfImage)item).processImage(para.getCurrentPageSize(),para.getCurrentMargin()));
 		}
 		else if(item instanceof PdfTable){
 			//只处理自定义的表格
 			((PdfTable)item).processTable(doc,para.getCurrentPageSize(),para.getCurrentMargin());
 		}
 		else if(item instanceof IBlockElement){
 			//添加的是PDF内置的元素
			doc.add((IBlockElement)item);
 		}
 		else if (item instanceof Image){
			//内置的image
 			doc.add((Image)item);
 		}
 		else if(item instanceof AreaBreak){
			//内置的分页
 			AreaBreak page=(AreaBreak)item;
 			//记录当前页的页码设置和页边距
 			para.setCurrentPageSize(page.getPageSize()!=null
 					?page.getPageSize()  //指定了页面
 					:para.getPageSize());//未指定页面大小，使用模板默认的属性
 			if(item instanceof PdfNextPage){
 				//新页面使用的新的页边距
 				para.setCurrentMargin(((PdfNextPage)item).getMargin()!=null
 						?((PdfNextPage)item).getMargin()
 						:para.getMargin());
 			}
 			//设置页边距
 			doc.setMargins(para.getCurrentMargin().getLeft(), 
					para.getCurrentMargin().getRight(),
					para.getCurrentMargin().getBottom(),
					para.getCurrentMargin().getLeft());
			doc.getPdfDocument().setDefaultPageSize(para.getCurrentPageSize());
 			doc.add((AreaBreak)item);
		}
 	}
}
