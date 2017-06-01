package psiprobe.controllers.profm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import psiprobe.controllers.AbstractTomcatContainerController;

public class UploadFileController extends AbstractTomcatContainerController {
	@SuppressWarnings("rawtypes")
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (FileUpload.isMultipartContent(new ServletRequestContext(request))) {
			String rootPath = null;
			File tmpFile = null;

			FileItemFactory factory = new DiskFileItemFactory(1048000, new File(System.getProperty("java.io.tmpdir")));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(-1L);
			upload.setHeaderEncoding("UTF8");
			try {
				for (Iterator it = upload.parseRequest(request).iterator(); it.hasNext();) {
					FileItem fi = (FileItem) it.next();
					if (!fi.isFormField()) {
						if ((fi.getName() != null) && (fi.getName().length() > 0)) {
							tmpFile = new File(System.getProperty("java.io.tmpdir"),
									FilenameUtils.getName(fi.getName()));
							fi.write(tmpFile);
						}
					} else if ("rootPath".equals(fi.getFieldName())) {
						rootPath = fi.getString();
					}
				}
			} catch (Exception e) {
				this.logger.error("Could not process file upload", e);
				request.setAttribute("errorMessage", getMessageSourceAccessor()
						.getMessage("probe.src.deploy.war.uploadfailure", new Object[] { e.getMessage() }));

				if ((tmpFile != null) && (tmpFile.exists())) {
					tmpFile.delete();
				}
				tmpFile = null;
			}

			String errMsg = null;

			if (tmpFile != null) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					if (tmpFile.getName().endsWith(".zip")) {
						//保存zip文件
						String zipName=rootPath+sdf.format(new Date())+"\\"+tmpFile.getName();
						File zipFile=new File(zipName);
						FileUtils.copyFile(tmpFile, zipFile);
						if(zipFile.exists()){
							//解压zip到当天的目录中
							String path=zipName.toLowerCase().replace(".zip", "");
							deleteFolder(path);
								
							ZipPath.unzip(zipName, path);
			                //request.setAttribute("success", Boolean.TRUE);
						}
					} else {
						//非zip文件，直接保存
						String filename=rootPath+sdf.format(new Date())+"\\"+tmpFile.getName();
						FileUtils.copyFile(tmpFile, new File(filename));
		                //request.setAttribute("success", Boolean.TRUE);
					}
				} catch (Exception e) {
					errMsg = getMessageSourceAccessor().getMessage("probe.src.deploy.war.failure",
							new Object[] { e.getMessage() });
					this.logger.error("Tomcat throw an exception when trying to deploy", e);
				} finally {
					if (errMsg != null) {
						request.setAttribute("errorMessage", errMsg);
					}
				}
			}
		}
		return new ModelAndView(new InternalResourceView(getViewName()));
	}
	public boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) {// 不存在返回 false
			return flag;
		}else{
			// 判断是否为文件
			if (file.isFile()){// 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else {// 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}
	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()){
			file.delete();
			flag = true;
			}
		return flag;
	}
	public boolean deleteDirectory(String sPath){
		boolean flag = false;
		//如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
			//如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()){
			return false;
		}
		flag = true;
		//删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++){
			//删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			} //删除子目录
			else {
			flag = deleteDirectory(files[i].getAbsolutePath());
			if (!flag) break;
			}
		}
		if (!flag) return false;
		//删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
