package com.wiscess.filter.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import com.wiscess.utils.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JsoupUtil {
	/**
	 * * 使用自带的basicWithImages 白名单 *
	 * 允许的便签有a,b,blockquote,br,cite,code,dd,dl,dt,em,i,li,ol,p,pre,q,small,span, *
	 * strike,strong,sub,sup,u,ul,img *
	 * 以及a标签的href,img标签的src,align,alt,height,width,title属性
	 */
	/**
	   * 标签白名单
	   * relaxed() 允许的标签:
	   *  a, b, blockquote, br, caption, cite, code, col, colgroup, dd, dl, dt, em, h1, h2, h3, h4,
	   *  h5, h6, i, img, li, ol, p, pre, q, small, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul。
	   *  结果不包含标签rel=nofollow ，如果需要可以手动添加。
	   */
	private static final Whitelist whitelist = Whitelist.relaxed();
	/** 配置过滤化参数,不对代码进行格式化 */
	private static final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
	static {		
		// 富文本编辑时一些样式是使用style来进行实现的		
		// 比如红色字体 style="color:red;"		
		// 所以需要给所有标签添加style属性		
		whitelist.addAttributes(":all", "style");	
		/**
	     * addTags() 设置白名单标签
	     * addAttributes()  设置标签需要保留的属性 ,[:all]表示所有
	     * preserveRelativeLinks()  是否保留元素的URL属性中的相对链接，或将它们转换为绝对链接,默认为false. 为false时将会把baseUri和元素的URL属性拼接起来
	     */
		whitelist.preserveRelativeLinks(true);
	} 	
	
	/**
	 * 清楚参数名称，严格过滤所有标签和不允许的字符
	 * @param name
	 * @return
	 */
	public static String cleanName(String name) {	    
		if(StringUtils.isNotEmpty(name)){
			name = name.trim();       
		}
		//先对参数进行decode
		try {
			name=URLDecoder.decode(name, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//转换失败后
			name="";
		}
		//不允许任何标签出现
		name=Jsoup.clean(name, "", Whitelist.none(), outputSettings);	
		//替换已知的不允许出现的所有字符
		name=html(name);
		return name;
	}		

	public static String clean(String content) {	    
		if(StringUtils.isNotEmpty(content)){
			content = content.trim();       
		}
		//先对参数进行decode
		try {
			content=URLDecoder.decode(content, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//转换失败后
			content="";
		}
		//不允许任何标签出现
		content=Jsoup.clean(content, "", whitelist, outputSettings);	
		//替换已知的不允许出现的所有字符
		content=html(content);
		return content;
	}	
	/**
	 * 格式化HTML文本
	 * @param content
	 * @return
	 */
	public static String html(String content) {
		if(content==null) return "";        
	    String html = content;
	    while(html.indexOf("&amp;")!=-1) {
		    html = StringUtils.replace(html, "&amp;","&");
	    }
	    html = StringUtils.replace(html, "&apos;","'");
	    while(html.indexOf("'")!=-1){
	    	html = StringUtils.replaceOnce(html, "'","‘");
	    	html = StringUtils.replaceOnce(html, "'","’");
		}
	    html = StringUtils.replace(html, "&quot;","\"");
	    while(html.indexOf("\"")!=-1){
	    	html = StringUtils.replaceOnce(html, "\"","“");
	    	html = StringUtils.replaceOnce(html, "\"","”");
	    }
	    html = StringUtils.replace(html, "&nbsp;&nbsp;","\t");// 替换跳格
	    html = StringUtils.replace(html, "&nbsp;"," ");// 替换空格
	    html = StringUtils.replace(html, "&lt;","＜");
	    html = StringUtils.replace(html, "&gt;","＞");
	    html = StringUtils.replace(html, "&times;","×");
	    html = StringUtils.replace(html, "&divide;","÷");
	    html = StringUtils.replace(html, "&ensp;","         ");
	    html = StringUtils.replace(html, "&emsp;","         ");
	    html = StringUtils.replace(html, "&","＆");
	    html = StringUtils.replace(html, "\\","＼");
	    html = StringUtils.replace(html, "#","＃");
	    return html;
	}
	
	/**
	 * 处理Json类型的Html标签,进行xss过滤
	 * 
	 * @param s
	 * @return
	 */
	public static String cleanJson(String s) {
		// 先处理双引号的问题
		s = jsonStringConvert(s);
		return Jsoup.clean(s, "", whitelist, outputSettings);	
	}

	/**
	 * 将json字符串本身的双引号以外的双引号变成单引号
	 * 
	 * @param s
	 * @return
	 */
	public static String jsonStringConvert(String s) {
//		log.info("[处理JSON字符串] [将嵌套的双引号转成单引号] [原JSON] :{}", s);
		char[] temp = s.toCharArray();
		int n = temp.length;
		for (int i = 0; i < n; i++) {
			if (temp[i] == ':' && temp[i + 1] == '"') {
				for (int j = i + 2; j < n; j++) {
					if (temp[j] == '"') {
						// 如果该字符为双引号,下个字符不是逗号或大括号,替换
						if (temp[j + 1] != ',' && temp[j + 1] != '}') {
							// 将json字符串本身的双引号以外的双引号变成单引号
							temp[j] = '\'';
						} else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
							break;
						}
					}
				}
			}
		}
		String r = new String(temp);
//		log.info("[处理JSON字符串] [将嵌套的双引号转成单引号] [处理后的JSON] :{}", r);
		return r;
	}
	public static void main(String[] args) throws IOException {		
		String text = "authAction.do?auth_url=><f>&lt;img src=''http%3A%2F%2Fcjlfxzxjypt.bjchyedu.cn%2Feduinfor%2Findex.jsp&auth_key=5B5AE13C3D05FBC6173133FE9FB6D6C2%27%22%3E%3Cscript%3Econfirm%28201308151610%29%3C%2Fscript%3E";	
		System.out.println(text);
		System.out.println(clean(text));	
		text="&amp;amp;amp;amp;asdf";
		System.out.println(html(text));
	}

}
