package com.wiscess.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.wiscess.utils.StringUtils;

public class FileUtils {

	static final int BUFFER = 2048000; // 200KB
	/**
	 * 分割路径
	 * 
	 * @param path
	 * @return 返回分割后的路径
	 */
	public static String[] separatePath(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		String[] sep = path.split("\\.");
		return new String[] { sep[0], sep[1] };
	}
	public static String encodingFileName(String fileName) {
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = StringUtils.replace(returnFileName, "+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnFileName;
    }
	/**
	 * Send file.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void sendFile(HttpServletRequest request, HttpServletResponse response, File file)
			throws IOException {

		try (
			OutputStream out = response.getOutputStream(); 
			RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			long fileSize = raf.length();
			long rangeStart = 0;
			long rangeFinish = fileSize - 1;

			// accept attempts to resume download (if any)
			String range = request.getHeader("Range");
			if (range != null && range.startsWith("bytes=")) {
				String pureRange = range.replaceAll("bytes=", "");
				int rangeSep = pureRange.indexOf('-');

				try {
					rangeStart = Long.parseLong(pureRange.substring(0, rangeSep));
					if (rangeStart > fileSize || rangeStart < 0) {
						rangeStart = 0;
					}
				} catch (NumberFormatException e) {
				}

				if (rangeSep < pureRange.length() - 1) {
					try {
						rangeFinish = Long.parseLong(pureRange.substring(rangeSep + 1));
						if (rangeFinish < 0 || rangeFinish >= fileSize) {
							rangeFinish = fileSize - 1;
						}
					} catch (NumberFormatException e) {
					}
				}
			}

			// set some headers
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename* = UTF-8''" + encodingFileName(file.getName()));
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Length", Long.toString(rangeFinish - rangeStart + 1));
			response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeFinish + "/" + fileSize);

			// seek to the requested offset
			raf.seek(rangeStart);

			// send the file
			byte[] buffer = new byte[4096];

			long len;
			int totalRead = 0;
			boolean nomore = false;
			while (true) {
				len = raf.read(buffer);
				if (len > 0 && totalRead + len > rangeFinish - rangeStart + 1) {
					// read more then required?
					// adjust the length
					len = rangeFinish - rangeStart + 1 - totalRead;
					nomore = true;
				}

				if (len > 0) {
					out.write(buffer, 0, (int) len);
					totalRead += len;
					if (nomore) {
						break;
					}
				} else {
					break;
				}
			}
		}
	}

	/**
	 * 拷贝文件(夹）到新目录中
	 * @param sourceFile in
	 * @param destDir out dir
	 * @return 成功返回true
	 */
	public static boolean copy(String sourceFile, String destDir) {
		File in = new File(sourceFile);
		File out = new File(destDir);
		if (null != in && !in.exists()) {
			return false;
		}
		if (null != out && !out.exists()) {
			out.mkdirs();
		}
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			//拷贝文件
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				fout = new FileOutputStream(new File(destDir + "/" + in.getName()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			int c;
			byte[] b = new byte[BUFFER];
			try {
				while ((c = fin.read(b)) != -1) {
					fout.write(b, 0, c);
				}
				fin.close();
				fout.flush();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			//文件夹
			for(File f1:in.listFiles()) {
				copy(sourceFile + "/" + f1.getName(), destDir + "/" + f1.getName());
			}
		}
		return false;
	}
	/**
	 * 拷贝文件并改名
	 * @param sourceFile in
	 * @param destDir out dir
	 * @param newFilename
	 * @return 成功返回true
	 */
	public static boolean copy(String sourceFile, String destDir, String newFilename) {
		File in = new File(sourceFile);
		File out = new File(destDir);
		if (null != in && !in.exists()) {
			return false;
		}
		if (null != out && !out.exists()) {
			out.mkdirs();
		}
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			//只能拷贝文件
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				fout = new FileOutputStream(new File(destDir + "/" + newFilename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			int c;
			byte[] b = new byte[BUFFER];
			try {
				while ((c = fin.read(b)) != -1) {
					fout.write(b, 0, c);
				}
				fin.close();
				fout.flush();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * 删除目录下的所有文件及其子目录
	 * @param file
	 */
	public static void deleteFile(File file)
	{
		if (file.exists() && file.isDirectory())//如果是目录
		{
			for (File delFile:file.listFiles())
			{
				deleteFile(delFile);//递归调用
			}
		}
		file.delete();
	}
}
