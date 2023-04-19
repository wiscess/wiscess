package com.wiscess.common;

/**
 * 压缩文件及目录
 * @author LunwenLiu
 */
// import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.wiscess.common.utils.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.wiscess.utils.StringUtils;

public class ZipPath {
	static final int BUFFER = 2048000; // 200KB


	/**
	 * 解压缩文件到一个目录
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws Exception
	 */
	public static void unzip(String zipFileName, String outputDirectory) throws Exception {
		String encoding=System.getProperty("sun.jnu.encoding");
		System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding")); //防止文件名中有中文时出错  
	    ZipFile zipFile = new ZipFile(new File(zipFileName),encoding);
		Enumeration<ZipEntry> e = zipFile.getEntries();
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

	/**
	 * 将inputFile目录下的文件都压缩到zipFile中
	 * @param zipFileName
	 * @param inputFile
	 * @throws Exception
	 */
	public void zip(String zipFileName, String inputFile) throws Exception {
		zip(zipFileName, new File(inputFile));
	}

	/**
	 * 压缩目录
	 * @param zipFileName
	 * @param inputFile
	 * @throws Exception
	 */
	private void zip(String zipFileName, File inputFile) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		out.setMethod(ZipOutputStream.DEFLATED);
		//设置编码格式
		Properties pro=System.getProperties();
        String osName=pro.getProperty("os.name");
        if("Linux".equalsIgnoreCase(osName)){
            out.setEncoding("GBK");//设置文件名编码方式
        }else{
            out.setEncoding(System.getProperty("sun.jnu.encoding"));//设置文件名编码方式
        }

		zip(out, inputFile, "");
		out.close();
	}

	/**
	 * 将list中的文件压缩到文件
	 * @param zipFileName
	 * @param inputFiles
	 * @throws Exception
	 */
	public void zip(String zipFileName, List<String> inputFiles) throws Exception {
		String path = System.getProperty("java.io.tmpdir") + File.separatorChar + ((new Date()).getTime()) + File.separatorChar;
		boolean flag = false;
		for (String inputFileName : inputFiles) {
			if(StringUtils.isEmpty(inputFileName))
				continue;
			File inputFile = new File(inputFileName);
			if (null != inputFile && !inputFile.exists() && !inputFile.isFile()) {
				continue;
			}
			//拷贝文件到一个临时文件目录中
			if (FileUtils.copy(inputFileName, path)) {
				flag=flag||true;
			}
		}
		if (flag) {
			zip(zipFileName, path);
		}
	}

	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				String fileName = fl[i].getName();
				zip(out, fl[i], base + fileName);
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int count;
			byte[] data = new byte[BUFFER];
			while ((count = in.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			in.close();
		}
	}

}
