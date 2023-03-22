package com.wiscess.exporter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.utils.FileUtils;

/**
 * 导出工具类
 * @author wh
 * 2019-04-04
 */
public class ExportUtil {

	@SuppressWarnings("serial")
	private static Map<String, String> contentTypeMap=new HashMap<String, String>(){{
		//文本
		put("html", "text/html");
		put("txt", "text/plain");
		put("xml", "text/xml");
		//图片
		put("gif", "image/gif");
		put("jpg", "image/jpeg");
		put("png", "image/png");
		//office
		put("doc",  "application/ms-word");
		put("docx", "application/ms-word");
		put("xls",  "application/ms-excel");
		put("xlsx", "application/ms-excel");
		put("ppt",  "application/vnd.ms-powerpoint");
		//pdf
		put("pdf", "application/pdf");
		//zip
		put("zip", "application/zip");
		//其他类型，统一返回
		put("", "multipart/form-data");
	}};
	/**
	 * 根据文件名输出到浏览器，自动判断文件类型
	 * @param res
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	public static OutputStream getOutputStream(HttpServletResponse res,String filename) throws IOException {
		//输出到浏览器
		filename=filename.substring(filename.lastIndexOf("\\")+1);
		res.setContentType(getContentType(filename));
		res.setHeader("Content-Disposition", "attachment; filename* = UTF-8''"
				+ FileUtils.encodingFileName(filename));
		res.setCharacterEncoding("utf-8");
		res.setHeader("FileName", FileUtils.encodingFileName(filename));
		res.setHeader("Access-Control-Expose-Headers", "FileName");
		return res.getOutputStream();
	}
	
	/**
	 * 获取文件扩展名
	 * @param filename
	 * @return
	 */
	public static String getExtName(String filename) {
		String extName="";
		if(filename.contains(".")) {
			extName=filename.substring(filename.lastIndexOf(".")+1);
		}
		return extName;
	}
	
	/**
	 * 根据扩展名获取下载的文件类型
	 * @param filename
	 * @return
	 */
	public static String getContentType(String filename) {
		String extName=getExtName(filename);
		return contentTypeMap.containsKey(extName)
				?contentTypeMap.get(extName)
				:contentTypeMap.get("");
	}

	public static void downloadFile(HttpServletResponse res,String filename,File file){
		try {
            FileInputStream inputStream = new FileInputStream(file);   
            //通过response获取ServletOutputStream对象(out)   
            OutputStream out = getOutputStream(res,filename);   
            int b = 0;   
            byte[] buffer = new byte[8196];   
            while (b != -1){   
                b = inputStream.read(buffer);   
                //4.写到输出流(out)中   
                out.write(buffer,0,b);   
            }   
            inputStream.close();   
            out.close();   
            out.flush();   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
