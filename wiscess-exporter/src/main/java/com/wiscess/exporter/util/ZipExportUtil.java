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
	public static boolean exportZip(String basePath, List<String> files,String zipName,HttpServletResponse rep){
		ZipPath zp = new ZipPath();
		rep.setCharacterEncoding("utf-8");
		rep.setContentType("multipart/form-data");
		String zipFile = basePath + zipName;
		try {
			if(files!=null && files.size()>0){
				zp.zip(zipFile, files);
				rep.setCharacterEncoding("utf-8");
				rep.setContentType("multipart/form-data");
				rep.setHeader("Content-Disposition", "attachment;fileName=" + FileUtils.encodingFileName(zipName));
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
	 * 导出zip文件，文件存储位置和文件名不用，在压缩时，使用原始文件名
	 * @param basePath
	 * @param inputFiles
	 * @param scholNames
	 * @param zipName
	 * @param rep
	 */
	public static void exportZip(String basePath, List<String> inputFiles,List<String> attachNames,String zipName,HttpServletResponse rep){
		ZipPath zp = new ZipPath();
		rep.setCharacterEncoding("utf-8");
		rep.setContentType("multipart/form-data");
		String zipFile = basePath + zipName;
		try {
			zp.zip(zipFile, inputFiles,attachNames);
			rep.setCharacterEncoding("utf-8");
			rep.setContentType("multipart/form-data");
			rep.setHeader("Content-Disposition", "attachment;fileName=" + FileUtils.encodingFileName(zipName));
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
