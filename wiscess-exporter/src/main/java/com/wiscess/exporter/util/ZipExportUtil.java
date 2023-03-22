package com.wiscess.exporter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.ZipPath;
import com.wiscess.common.utils.FileUtils;

/**
 * 导出zip文件
 * @author wh
 *
 */
public class ZipExportUtil {

	/**
	 * 导出zip文件,不区分目录，将所有文件都放在一个目录下压缩
	 * @param files
	 * @param zipName
	 */
	public static boolean exportZip(String basePath, String zipName, List<String> files,HttpServletResponse rep){
		ZipPath zp = new ZipPath();
		rep.setCharacterEncoding("utf-8");
		rep.setContentType("multipart/form-data");
		String zipFile = basePath + "/" + System.currentTimeMillis()+".zip";
		try {
			if(files!=null && files.size()>0){
				zp.zip(zipFile, files);
				rep.setCharacterEncoding("utf-8");
				rep.setContentType("multipart/form-data");
				rep.setHeader("Content-Disposition", "attachment;fileName* = UTF-8''" + FileUtils.encodingFileName(zipName));
				InputStream is = new FileInputStream(new File(zipFile));
				OutputStream os = rep.getOutputStream();
				byte[] b = new byte[1024*1024];
				int length;
				while ((length = is.read(b)) > 0) {
					os.write(b, 0, length);
				}
				os.close();
				is.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 导出zip文件，在压缩时，使用原始文件名
	 * 输出到浏览器，使用临时zip文件
	 * @param basepath
	 * @param zipName
	 * @param inputFilePath
	 * @param rep
	 */
	public static void exportZip(String basepath,String zipName,String inputFilePath,HttpServletResponse rep){
		ZipPath zp = new ZipPath();
		rep.setCharacterEncoding("utf-8");
		rep.setContentType("multipart/form-data");
		String zipFile = basepath + "/" + System.currentTimeMillis()+".zip";
		try {
			zp.zip(zipFile, inputFilePath);
			rep.setCharacterEncoding("utf-8");
			rep.setContentType("multipart/form-data");
			rep.setHeader("Content-Disposition", "attachment;fileName* = UTF-8''" + FileUtils.encodingFileName(zipName));
			InputStream is = new FileInputStream(new File(zipFile));
			OutputStream os = rep.getOutputStream();
			byte[] b = new byte[1024*1024];
			int length;
			while ((length = is.read(b)) > 0) {
				os.write(b, 0, length);
			}
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
