package psiprobe.controllers.profm;

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
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;


public class ZipPath {
	static final int BUFFER = 2048000; // 200KB

	/**
	 * 解压文件
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws Exception
	 */
	public static void unzip(String zipFileName, String outputDirectory)
	 throws Exception { 
		ZipFile zipFile = new ZipFile(new File(zipFileName));
		Enumeration e = zipFile.getEntries();
		ZipEntry z = null;
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
					f.mkdirs();
				} else {
					String name = z.getName();
					int position = name.lastIndexOf("/");
					if (position >= 0)
					{
						String path=name.substring(0,position);
						File f = new File(outputDirectory + File.separator + path);
						f.mkdirs(); 
					}
					File f = new File(outputDirectory + File.separator
							+ name);
					
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
//		File[] file = in.listFiles();
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//System.out.println("in.name=" + in.getName());
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
//		File[] file = in.listFiles();
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (in.isFile()) {
			try {
				fin = new FileInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//System.out.println("in.name=" + in.getName());
			try {
				fout = new FileOutputStream(new File(file2 + "/" + name));
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
			copy(file1 + "/" + in.getName(), file2 + "/" + name);
		}
		return false;
	}
	
}
