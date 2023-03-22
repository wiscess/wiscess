package com.wiscess.filter.xss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.wiscess.utils.RSA_Encrypt;
import com.wiscess.utils.StringUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;
	private boolean isIncludeRichText = false;
	//判断是否是上传 上传忽略
	boolean isUpData = false;

	public XssHttpServletRequestWrapper(HttpServletRequest request, boolean isIncludeRichText) {
		super(request);
		orgRequest = request;
		this.isIncludeRichText = isIncludeRichText;
	    String contentType = request.getContentType();
	    if (null != contentType) {
	      isUpData = contentType.startsWith("multipart");
	    }
	}

	/**
	 * 判断是否需要使用原始值
	 * @param name
	 * @return
	 */
	private boolean isRichText(String name) {
		Boolean flag = ("content".equals(name) || name.endsWith("WithHtml"));
		//参数名是指定的content或以WithHtml结尾的，直接使用原始值；
		return (flag && !isIncludeRichText);
	}
	/**
	 * 判断是否为RSA加密后的base64编码
	 */
	private boolean isRSA(String name) {
		return name.endsWith("WithRSA");
	}
	/**
	 * * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		name = JsoupUtil.cleanName(name);
		String value = super.getParameter(name);
//		if(isBase64(name)) {
//			//如果是base64的参数，尝试读取不带WithBase64的参数值
//			if(value==null || StringUtils.isEmpty(value)) {
//				value=super.getParameter(name.replaceAll("WithBase64", ""));
//			}
//			return value;
//		}
		if(isRSA(name)) {
			//如果是RSA加密的内容
			/*
			index    java               html                    result
			1        username           username                ok
			2        usernameWithRSA    usernameWithRSA         ok
			                            username                ok          
			*/
			if(value==null || StringUtils.isEmpty(value)) {
				value=super.getParameter(name.replaceAll("WithRSA", ""));
			}
			//取得加密字符串后先解密
			try {
				value=RSA_Encrypt.decrypt(value,true);
			} catch (Exception e) {
				//解密失败
				value="";
			}
			//解密后进行xss过滤
			value = JsoupUtil.cleanValue(value);
			return value;
		}
		if(isRichText(name)) {
			//富文本
			if(value==null || StringUtils.isEmpty(value)) {
				value=super.getParameter(name.replaceAll("WithHtml", ""));
			}
			//读取到原始值后，进行格式化
			value=JsoupUtil.cleanContent(value);
		}else if (!isRichText(name) && StringUtils.isNotEmpty(value)) {
			//普通参数
			value = JsoupUtil.cleanValue(value);
		}
		return value;
	}

	@Override
	public String[] getParameterValues(String name) {
		//
		String newName = JsoupUtil.cleanName(name);
		String[] values = super.getParameterValues(newName);
//		if(isBase64(newName)) {
//			//如果是base64的参数，尝试读取不带WithBase64的参数值
//			if(values==null) {
//				values=super.getParameterValues(newName.replaceAll("WithBase64", ""));
//			}
//			return values;
//		}
		if(isRSA(newName)) {
			//如果是RSA加密的内容
			if(values==null) {
				values=super.getParameterValues(name.replaceAll("WithRSA", ""));
			}
			if(values!=null) {
				//取得加密字符串后先解密
				values = Stream.of(values).map(s->{
					try {
						s=RSA_Encrypt.decrypt(s,true);
						//解密后进行xss过滤
						return JsoupUtil.cleanValue(s);
					} catch (Exception e) {
						s="";
					}
					return s;
				}).toArray(String[]::new);
			}
			return values;
		}
		if(isRichText(newName)) {
			//富文本
			if(values==null) {
				values=super.getParameterValues(newName.replaceAll("WithHtml", ""));
			}
			if(values!=null) {
				values = Stream.of(values).map(s -> JsoupUtil.cleanContent(s)).toArray(String[]::new);
			}
			return values;
		}
		if (values != null) {
			values = Stream.of(values).map(s -> JsoupUtil.cleanValue(s)).toArray(String[]::new);
		}
		return values;
	}

	@Override
	public Map<String, String[]> getParameterMap(){
		Map<String, String[]> parameterMap=new HashMap<>();
        Enumeration<String> enumeration = getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            name = JsoupUtil.cleanName(name);
            String[] values = getParameterValues(name);
            parameterMap.put(name, values);
        }
        return parameterMap;
	}
	/**
	 * 以下Header参数名不能进行clean
	 */
	private static List<String> headerList=new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
			add("Accept");
			add("Accept-Encoding");
			add("Accept-Language");
			add("Cache-Control");
			add("Upgrade-Insecure-Requests");
			add("User-Agent");
		}
	};
	/**
	 * * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {
		name = JsoupUtil.cleanName(name);
		String value = super.getHeader(name);
		if (!headerList.contains(name) && StringUtils.isNotEmpty(value)) {
			value = JsoupUtil.cleanValue(value);
		}
		return value;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		//if (isUpData) {
			return super.getInputStream();
		/*} else {
			// 处理原request的流中的数据
			byte[] bytes = inputHandlers(super.getInputStream()).getBytes();
			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			return new ServletInputStream() {
				@Override
				public int read() throws IOException {
					return bais.read();
				}

				@Override
				public boolean isFinished() {
					return false;
				}

				@Override
				public boolean isReady() {
					return false;
				}

				@Override
				public void setReadListener(ReadListener readListener) {
				}
			};
		}*/

	}

	public String inputHandlers(ServletInputStream servletInputStream) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(servletInputStream, Charset.forName("UTF-8")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (servletInputStream != null) {
				try {
					servletInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String finl = JsoupUtil.cleanJson(sb.toString());
		return finl;
	}
	
	/** * 获取最原始的request * * @return */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/** * 获取最原始的request的静态方法 * * @return */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getOrgRequest();
		}
		return req;
	}

}
