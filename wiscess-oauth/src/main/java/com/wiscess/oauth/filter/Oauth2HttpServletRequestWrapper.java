package com.wiscess.oauth.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class Oauth2HttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;

	public Oauth2HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}
	/**
	 * * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		if(name.equalsIgnoreCase("access_token")) {
			return null;
		}
		return super.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		//
		if(name.equalsIgnoreCase("access_token")) {
			return null;
		}
		return super.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap(){
		Map<String, String[]> parameterMap=new HashMap<>();
        Enumeration<String> enumeration = getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if(name.equalsIgnoreCase("access_token")) {
            	continue;
            }
            String[] values = getParameterValues(name);
            parameterMap.put(name, values);
        }
        return parameterMap;
	}

	/**
	 * * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {
        if(name.equalsIgnoreCase("Authorization")) {
        	return null;
        }
        return  super.getHeader(name);
	}
	public Enumeration<String> getHeaders(String name) {
        if(name.equalsIgnoreCase("Authorization")) {
        	return new Enumeration<String>() {
				@Override
				public boolean hasMoreElements() {
					return false;
				}
				@Override
				public String nextElement() {
					return null;
				}};
        }
		return super.getHeaders(name);
	}
	
	/** * 获取最原始的request * * @return */

	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/** * 获取最原始的request的静态方法 * * @return */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof Oauth2HttpServletRequestWrapper) {
			return ((Oauth2HttpServletRequestWrapper) req).getOrgRequest();
		}
		return req;
	}


}
