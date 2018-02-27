package com.wiscess.common;

/**
 * 压缩文件及目录
 * @author LunwenLiu
 */
// import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipOutputStream;

import com.wiscess.common.utils.StringUtil;

public class ZipPath {
	static final int BUFFER = 2048000; // 200KB

	@SuppressWarnings("rawtypes")
	public static void unzip(String zipFileName, String outputDirectory)
	 throws Exception { 
		org.apache.tools.zip.ZipFile zipFile = new  org.apache.tools.zip.ZipFile(new File(zipFileName));
		Enumeration e = zipFile.getEntries();
		org.apache.tools.zip.ZipEntry z = null;
		InputStream in = null;
			File outFile = new File(outputDirectory);
			if (!outFile.exists())
			{
				outFile.mkdirs();
			}
			while (e.hasMoreElements()) {
				z = (org.apache.tools.zip.ZipEntry)e.nextElement();
				if (z.isDirectory()) {
					String name = z.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
				} else {
					String name = z.getName();
					int position = name.lastIndexOf("/");
					if (position >= 0)
					{
						name = name.substring(position+1);
					}
					File f = new File(outputDirectory + File.separator
							+ name.toLowerCase());
					
					f.createNewFile();
					in = zipFile.getInputStream(z);
					FileOutputStream out = new FileOutputStream(f);
					int count;
					byte[] data = new byte[BUFFER];
					
					while ((count = in.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
					}
					out.close();
				}
			}
			in.close();
			zipFile.close();
		}

	public void zip(String zipFileName, String inputFile) throws Exception {
		if (zipFileName == null || zipFileName.trim().length() == 0) {
			zipFileName = "test.zip";
		}
		zip(zipFileName, new File(inputFile));
	}

	private void zip(String zipFileName, File inputFile) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		out.setMethod(ZipOutputStream.DEFLATED);
		
		//设置编码格式
		out.setEncoding(System.getProperty("sun.jnu.encoding"));
		
		zip(out, inputFile, "");
		out.close();
	}
	/**
	 * 拷贝文件
	 * @param file1 in
	 * @param file2 out dir
	 * @return 成功返回true
	 */
	public boolean copy(String file1, String file2) {
		File in = new File(file1);
		File out = new File(file2);
		if (null != in && !in.exists()) {
			return false;
		}
		if (null != out && !out.exists()) {
			out.mkdirs();
		}
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				fout = new FileOutputStream(new File(file2 + "/" + in.getName()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//System.out.println(file2);
			int c;
			byte[] b = new byte[BUFFER];
			try {
				while ((c = fin.read(b)) != -1) {
					fout.write(b, 0, c);
					// System.out.println("复制文件中！");
				}
				fin.close();
				fout.flush();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			copy(file1 + "/" + in.getName(), file2 + "/" + in.getName());
		}
//		}
		return false;
	}
	/**
	 * 拷贝文件
	 * @param file1 in
	 * @param file2 out dir
	 * @return 成功返回true
	 */
	public boolean copy(String file1, String file2, String name) {
		File in = new File(file1);
		File out = new File(file2);
		if (null != in && !in.exists()) {
			return false;
		}
		if (null != out && !out.exists()) {
			out.mkdirs();
		}
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				fout = new FileOutputStream(new File(file2 + "/" + name));
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
			copy(file1 + "/" + in.getName(), file2 + "/" + name);
		}
		return false;
	}
	/**
	 * 压缩目录
	 * @param zipFileName
	 * @param inputFiles
	 * @throws Exception
	 */
	public void zip(String zipFileName, List<String> inputFiles) throws Exception {
		String path = System.getProperty("java.io.tmpdir") + File.separatorChar + ((new Date()).getTime()) + File.separatorChar;
		boolean flag = false;
		for (String inputFileName : inputFiles) {
			if(StringUtil.isEmpty(inputFileName))
				continue;
			File inputFile = new File(inputFileName);
			if (null != inputFile && !inputFile.exists() && !inputFile.isFile()) {
				continue;
			}
			//拷贝文件到一个临时文件目录中
			if (copy(inputFileName, path)) {
				flag=flag||true;
			}
		}
		if (flag) {
			zip(zipFileName, path);
		}
	}
	/**
	 * 压缩目录
	 * @param zipFileName
	 * @param inputFiles
	 * @throws Exception
	 */
	public void zip(String zipFileName, List<String> inputFiles,List<String> scholNames) throws Exception {
		boolean flag = false;
		if (StringUtil.isEmpty(zipFileName) || null == inputFiles || null == scholNames || scholNames.isEmpty() || inputFiles.isEmpty() || inputFiles.size() != scholNames.size()) {
			return;
		}
		String path = System.getProperty("java.io.tmpdir") + File.separatorChar + ((new Date()).getTime()) + File.separatorChar;
		int index = 0;
		for (String inputFileName : inputFiles) {
			File inputFile = new File(inputFileName);
			if (null != inputFile && !inputFile.exists() && !inputFile.isFile()) {
				continue;
			}
			//拷贝文件到一个临时文件目录中
			if (copy(inputFileName, path, scholNames.get(index))) {
				flag=flag||true;
			}
			index++;
		}
		if (flag) {
			zip(zipFileName, path);
		}
	}
	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (!StringUtil.isEmpty(base))
			{
				out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
			}
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				String fileName = fl[i].getName();
				//int position = fileName.lastIndexOf(".");
//				String extName = "";
//				if (position >= 0) {//扩展名
//					extName = fileName.substring(position + 1);	
//				}
				zip(out, fl[i], base + fileName);
			}
		} else {
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int count;
			byte[] data = new byte[BUFFER];
			while ((count = in.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			in.close();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ZipPath t = new ZipPath();
			List <String> inputFiles = new ArrayList<String>();
			inputFiles.add("C:\\Documents and Settings\\public\\桌面\\丰台\\new\\丰台教师平台项目计划.xls");
			inputFiles.add("C:\\Documents and Settings\\public\\桌面\\丰台\\new\\丰台教师平台项目计划.mpp");
			inputFiles.add("C:\\Documents and Settings\\public\\桌面\\丰台\\new\\丰台继教物理模型.pdm");
			String zipFileName = "c:\\temptest.zip";
			t.zip(zipFileName , inputFiles);
			//t.zip("d:\\temp.zip", "d:\\temp\\01");
			//ZipPath.unzip("d:\\temp.zip", "d:\\temp2");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
