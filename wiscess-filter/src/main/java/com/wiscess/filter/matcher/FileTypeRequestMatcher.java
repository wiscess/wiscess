package com.wiscess.filter.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件类型过滤器
 * @author wh
 * 配置指定的某个目录下可以访问的文件类型，不符合类型的禁止访问
application.yml中配置如下
#过滤器设置
filter:
  filetype:
    errorPage: /deny.html
    urlPatterns:
      /html/**=jpg|bmp|png
      /photo/**=jpg|png
      /attach/**=doc|zip|txt
 */
@Slf4j
public class FileTypeRequestMatcher implements RequestMatcher{

	private Pattern allowedMethods=Pattern.compile("^(POST|HEAD|TRACE|OPTIONS|PUT|DELETE|PATCH)$");
	
	/**
	 * 校验的路径
	 */
	private List<String> urlPatterns;
	public FileTypeRequestMatcher(){
		
	}
	public FileTypeRequestMatcher(List<String> urlPatterns){
		setUrlPatterns(urlPatterns);
	}
	/**
	 * 路径匹配器和文件类型匹配器
	 */
	private Map<RequestMatcher,RequestMatcher> matchers=new HashMap<RequestMatcher,RequestMatcher>();
	
	public static List<RequestMatcher> antMatchers(HttpMethod httpMethod,
			String... antPatterns) {
		String method = httpMethod == null ? null : httpMethod.toString();
		List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
		for (String pattern : antPatterns) {
			matchers.add(new AntPathRequestMatcher(pattern, method));
		}
		return matchers;
	}
	@Override
	public boolean matches(HttpServletRequest request) {
		/**
		 * 首先用匹配器对路径进行匹配
		 */
		for(RequestMatcher matcher:matchers.keySet()){
			if(matcher.matches(request)){
				//访问路径匹配成功，判断访问方法
				if(!allowedMethods.matcher(request.getMethod()).matches()){
					//request方法，如果是GET，则需要进行类型匹配，否则允许通过
					RequestMatcher fileExtmatcher=matchers.get(matcher);
					//对文件类型进行判断，如果匹配成功则允许访问
					return fileExtmatcher.matches(request);
				}
				//该路径禁止其他方法访问
				return false;
			}
		}
		//路径未匹配上的允许访问
		return true;
	}

	public List<String> getUrlPatterns() {
		return urlPatterns;
	}

	public void setUrlPatterns(List<String> urlPatterns) {
		this.urlPatterns = urlPatterns;
		if(urlPatterns!=null && urlPatterns.size()>0){
			/**
			 * 如果配置了urlPatterns，则对每一行进行分析，获取每一行的匹配路径和允许的文件类型
			 */
			for(String urlPattern:urlPatterns){
				if(urlPattern.contains("=")){
					//得到匹配路径（可能是多个，以,分隔）
					String urls=urlPattern.split("=")[0];
					//得到允许的文件类型
					String allowExt=urlPattern.split("=")[1];
					for(String url:urls.split(",")){
						matchers.put(new AntPathRequestMatcher(url),new FileExtMatcher(allowExt));
						log.debug("Match url=({}),allowFileExt=({})",url,allowExt);
					}
				}
			}
		}
	}
	
	private static class FileExtMatcher implements RequestMatcher{
		private final Pattern matcher;

		private FileExtMatcher(String allowExt) {
			this.matcher = createMatcher(allowExt);
		}

		@Override
		public boolean matches(HttpServletRequest request) {
			String url = getRequestPath(request);
			return this.matcher.matcher(url).matches();		
		}

		private static Pattern createMatcher(String allowExt) {
			Pattern matcher = Pattern.compile("^.*?\\.("+allowExt+")$");
			return matcher;
		}
		private String getRequestPath(HttpServletRequest request) {
			String url = request.getServletPath();
			if (request.getPathInfo() != null) {
				url += request.getPathInfo();
			}
			return url;
		}
	}

	public static void main(String[] a){
		Pattern allowedMethods=Pattern.compile("^.*?\\.(jpg|jpeg|bmp|gif)$");
		
		String method="/html/test.jpg";
		System.out.println(allowedMethods.matcher(method).matches());
		//System.out.println(matcher.matches(request));
	}
}
