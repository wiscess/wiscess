package com.wiscess.common.utils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.NumberUtils;

import ognl.DefaultTypeConverter;

/**
 * Convert request parameter to specified java type.
 * @author wanghai
 */
@SuppressWarnings("unchecked")
public class RequestParamMapTypeConverter extends DefaultTypeConverter {
	private String currentParam;
	private Map<String, Object> config = new HashMap<String, Object>();
	
	public void setCurrentParam(String currentParam) {
		this.currentParam = currentParam;
	}
	
	public void setConfig(String name, Object value) {
		config.put(name, value);
	}

	@SuppressWarnings("rawtypes")
	public Object convertValue(Map context, Object value, Class toType,String df) {
		Object result = null;
        
        if (value != null) {
			if(value.equals(""))
				return null;
			if (toType.isAssignableFrom(value.getClass()))
                return value;
			if (value.getClass().isArray() && toType.isArray()) {
				Class componentType = toType.getComponentType();

				List list=new ArrayList();
				for(int i = 0, icount = Array.getLength(value); i < icount; i++) {
					Object obj=convertValue(context,Array.get(value, i), componentType,df);
					if(obj!=null) {
						list.add(obj);
					}
				}
				result = Array.newInstance(componentType, list.size());
				for(int i=0;i<list.size();i++) {
					Array.set(result, i, list.get(i));
				}
				return result;
			}
			else if (value.getClass().isArray() && !toType.isArray()) {
				return convertValue(context, Array.get(value, 0), toType,df);
			} else if (!value.getClass().isArray() && toType.isArray()) {
				Object obj = Array.newInstance(value.getClass(), 1);
				Array.set(obj, 0, value);
				return convertValue(context, obj, toType,df);
			}
			
			if (toType.isEnum()) {
				return Enum.valueOf(toType, (String) value);
			} else if (toType.isArray() && toType.getComponentType().isEnum()) {
				Class componentType = toType.getComponentType();
	            result = Array.newInstance(componentType, Array.getLength(value));
	            for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
	                Array.set(result, i, convertValue(context, Array.get(value, i), componentType,df));
	            }
	            return result;
			}
			
			// need to fix ognl 2.6.9
			if (toType == Boolean.class || toType == Boolean.TYPE) {
				if (value.getClass() == String.class) {
					return Boolean.valueOf((String) value);
				}
			}
			if (Number.class.isAssignableFrom(toType)) {
    			// Convert stringified value to target Number class.
    			return NumberUtils.parseNumber(value.toString(),(Class<Number>) toType);
    		}
			if (toType == Date.class){
				try {
					return new SimpleDateFormat(df).parse((String)value);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// escape html
			if (toType == String.class && "true".equalsIgnoreCase((String)config.get("escapeHTML"))) {
				if (!((List)config.get("htmlList")).contains(currentParam)) {
					return escapeHtml((String) value);
				}
			}
			
			// is the same type, just return it.
			if (value.getClass().equals(toType)) {
				return value;
			}
			if (toType.isAssignableFrom(value.getClass()))
	            return value;
        }
		return super.convertValue(context, value, toType);
	}
	
	private String escapeHtml(String html) {
		return (html == null) ? null : html
				.replaceAll("&", "&amp;")
				.replaceAll("\"", "&quot;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}