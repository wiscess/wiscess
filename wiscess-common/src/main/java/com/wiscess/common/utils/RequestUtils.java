package com.wiscess.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wiscess.utils.StringUtils;

import ognl.Ognl;

public class RequestUtils {

	private static String sourceEnc = "ISO-8859-1";
	private static String defaultDestEnc = "UTF-8";
	private static boolean doDecode = false;
	private static boolean forceDefault = false;
	private static boolean escapeHTML = false;

	public static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";
	private static RequestParamMapTypeConverter converter;
	private static Map<?, ?> ognlContext;
	
	public static RequestParamMapTypeConverter getConverter(){
		if(converter==null){
			ognlContext = Ognl.createDefaultContext(null);
			converter = new RequestParamMapTypeConverter();
			converter.setConfig("escapeHTML", escapeHTML ? "true" : "false");
			converter.setConfig("htmlList", Collections.singletonList(String.class));
		}
		return converter;
	}
	
	/**
	 * 返回指定的参数
	 * @param request
	 * @param name
	 * @return
	 */
	public static Object parseParameters(HttpServletRequest request,String name,Class<?> toType){
		String df="";
		if(toType==Date.class)
			df="yyyy-MM-dd";
		return parseParameters(request, name, toType,df);
	}
	public static Object parseParameters(HttpServletRequest request,String name,Class<?> toType,String df){
		boolean ifDecode = doDecode && "GET".equalsIgnoreCase(request.getMethod());
        String encoding = null;

        if (ifDecode) {
        	if (forceDefault) {
        		encoding = defaultDestEnc;
        	} else {
	        	encoding = request.getCharacterEncoding();
	        	if(encoding.equals(defaultDestEnc)){
	        		ifDecode=false;
	        	}
	        	if (StringUtils.isEmpty(encoding)) {
	        		encoding = defaultDestEnc;
	        	}
        	}
        }
        
        
        String[] values=request.getParameterValues(name);
        
		if (values != null && ifDecode) {
			for (int i = 0; i < values.length; i++) {
				try {
					values[i] = new String(values[i].getBytes(sourceEnc), encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return  convert(values,toType,df);
	}
	
	/**
	 * 将request中指定的参数返回成map的形式
	 * @param request
	 * @param pageParameters
	 * @return
	 */
	public static Map<String, Object> parseParameters(HttpServletRequest request, String[] parameters) {
		/**
		 * parameters中包含以下几种形式
		 * name							：只有一个名称，默认为字符串
		 * dictId:Integer				：包含字段名称和字段类型
		 * createTime:Date:yyyy-MM-dd	：数据类型为Date，包含日期格式，默认为yyyy-MM-dd
		 * 
		 * 字段类型包含：String/Integer/Long/Double/Float/Date/及数组格式，如Integer[]
		 */
		Map<String, Object> map=new HashMap<String, Object>();
		for(String p:parameters){
			//为p增加两个:，用于字符串拆分时确保至少有3个
			if(p.indexOf(":")==-1) {
				p=p+"::";
			}else if(p.indexOf(":")==p.lastIndexOf(":")) {
				p=p+":";
			}
			String[] ps=p.split(":",3);
			//添加默认值
			if(StringUtils.isEmpty(ps[1]))
				ps[1]="String";
			else if(ps[1].equalsIgnoreCase("Date") && StringUtils.isEmpty(ps[2])){
				ps[2]="yyyy-MM-dd";
			}
			
			Class<?> toType=String.class;
			boolean isArray=false;
			String type=ps[1];
			if(ps[1].indexOf("[]")>=0){
				//数组类型
				type=ps[1].replaceAll("\\[\\]", "");
				isArray=true;
			}
			//判断数据类型
			if(type.equalsIgnoreCase("String")){
				toType=isArray?String[].class:String.class;
			}else if(type.equalsIgnoreCase("Integer")){
				toType=isArray?Integer[].class:Integer.class;
			}else if(type.equalsIgnoreCase("Long")){
				toType=isArray?Long[].class:Long.class;
			}else if(type.equalsIgnoreCase("Double")){
				toType=isArray?Double[].class:Double.class;
			}else if(type.equalsIgnoreCase("Float")){
				toType=isArray?Float[].class:Float.class;
			}else if(type.equalsIgnoreCase("Date")){
				toType=isArray?Date[].class:Date.class;
			}
			map.put(ps[0], parseParameters(request,ps[0],toType,ps[2]));
		}
		return map;
	}
	
	private static Object convert(String[] values, Class<?> toType, String df) {
		return getConverter().convertValue(ognlContext, values, toType,df);
	}

	public static boolean isMultipart(HttpServletRequest request) {
		return request.getContentType() != null
				&& request.getContentType().startsWith(MULTIPART_CONTENT_TYPE);
	}
}
