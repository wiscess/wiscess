package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

public class BinaryUploader {

	/**
	 * 修改保存文件
	 * 
	 * @param request
	 * @param conf
	 * @return
	 */
	public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		Boolean multipartEnabled=true;
		InputStream is = null;
		try {
	        //重新命名文件名字
			String originFileName = "";
			if(multipartEnabled) {
				//默认为true时
				StandardServletMultipartResolver multipartResolver=new StandardServletMultipartResolver();

				//检查form中是否有enctype="multipart/form-data"
				if(!multipartResolver.isMultipart(request)) {
					return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
				}
				//将request变成多部分request
	            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
	            //获取multiRequest 中所有的文件名
	            Iterator<String>it=multiRequest.getFileNames();
	            //遍历文件
	            MultipartFile file = null;
	            while(it.hasNext()) {
	            	file=multiRequest.getFile(it.next().toString());
	            	if(file != null){
	            		break;
	            	}
	            }

				if (file == null) {
					return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
				}

				originFileName = file.getOriginalFilename();
				is = file.getInputStream();
				
			}else {
				FileItemStream fileStream = null;
				
				boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;
				RequestContext rc =new Rc(request);
				
				if (!ServletFileUpload.isMultipartContent(rc)) {
					return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
				}
				ServletFileUpload upload = new ServletFileUpload(
						new DiskFileItemFactory());

				if ( isAjaxUpload ) {
		            upload.setHeaderEncoding( "UTF-8" );
		        }

				FileItemIterator iterator = upload.getItemIterator(rc);
	
				while (iterator.hasNext()) {
					fileStream = iterator.next();
	
					if (!fileStream.isFormField())
						break;
					fileStream = null;
				}
	
				if (fileStream == null) {
					return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
				}
				
				originFileName = fileStream.getName();

				is = fileStream.openStream();
			}
			
			//保存文件流
			String savePath = (String) conf.get("savePath");
			
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;

			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (IOException e) {
		} catch (Exception e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
