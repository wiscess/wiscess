package com.wiscess.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import cn.hutool.core.lang.reflect.MethodHandleUtil;
import cn.hutool.core.util.ObjectUtil;


/**
 * Excel导入校验类
 * 提供校验字段类型的方法
 * 1、校验数字
 * 2、校验字符串
 * 3、校验字典值
 * @author wh
 *
 */
public class ObjectUtils {
	/**
	 * 校验对象字段是否为字符串
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static String setString(Object obj,String fieldName,Object value) {
		return setString(obj,fieldName,value,null);
	}
	public static String setString(Object obj,String fieldName,Object value,Object defaultValue) {
		Method setter;
		try {
			setter = setMethod(obj.getClass(),fieldName,String.class);
			if(ObjectUtil.isEmpty(value)) {
				MethodHandleUtil.invoke(obj, setter, defaultValue);
			}else {
				MethodHandleUtil.invoke(obj, setter, value);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 校验值是否为数字
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static String setInteger(Object obj,String fieldName,Object value){
		return setInteger(obj,fieldName,value,null);
	}
	public static String setInteger(Object obj,String fieldName,Object value,Integer defaultValue) {
		Method setter;
		try {
			setter = setMethod(obj.getClass(),fieldName,Integer.class); 
			if(ObjectUtil.isEmpty(value)) {
				MethodHandleUtil.invoke(obj, setter, defaultValue);
			}else {
				MethodHandleUtil.invoke(obj, setter, Integer.parseInt(value.toString()));
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}catch(Exception e) {
			return "请填写数字型";
		}
		
		return "";
	}
	/**
	 * 校验值是否为浮点型
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static String setBigDecimal(Object obj,String fieldName,Object value){
		return setBigDecimal(obj,fieldName,value,null);
	}
	public static String setBigDecimal(Object obj,String fieldName,Object value, BigDecimal defaultValue) {
		Method setter;
		try {
			setter = setMethod(obj.getClass(),fieldName,BigDecimal.class);
			if(ObjectUtil.isEmpty(value)) {
				MethodHandleUtil.invoke(obj, setter, defaultValue);
			}else {
				MethodHandleUtil.invoke(obj, setter, new BigDecimal(value.toString()));
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}catch(Exception e) {
			return "请填写数字型";
		}
		return "";
	}

	public static <E> String setObject(Object obj,String fieldName,Object value,Class<E> classType){
		return setObject(obj,fieldName,value,classType,null);
	}
	public static <E> String setObject(Object obj,String fieldName,Object value,Class<E> classType, Object defaultValue) {
		Method setter;
		try {
			setter = setMethod(obj.getClass(),fieldName,classType);
			if(ObjectUtil.isEmpty(value)) {
				//传入值为null或空串，使用默认值保存
				value=defaultValue;
			}
			//转换为泛型
			MethodHandleUtil.invoke(obj, setter, value==null
					?null
					:JsonUtils.parseObject(value.toString(),classType));
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}catch(Exception e) {
			return "对象类型不正确";
		}
		return "";
	}

	/**
	 * 获取对象字段的get方法
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Method getMethod(Class<?> object,String fieldName) {
		String propertyName = convertUnderscoreNameToPropertyName(fieldName);
		String getterMethodName = "get"+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
		Method getter=null;
		try {
			getter = object.getMethod(getterMethodName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return getter;
	}
	/**
	 * 获取对象字段的set方法
	 * @param object
	 * @param fieldName
	 * @param classType
	 * @return
	 */
	public static Method setMethod(Class<?> object,String fieldName, Class<?> classType) {
		String propertyName = convertUnderscoreNameToPropertyName(fieldName);
		String setterMethodName = "set"+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
		Method setter=null;
		try {
			setter = object.getMethod(setterMethodName,classType);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return setter;
	}

	/**
	 * See org.springframework.jdbc.support.JdbcUtils
	 * Convert a column name with underscores to the corresponding property name using "camel case".
	 * A name like "customer_number" would match a "customerNumber" property name.
	 * @param name the column name to be converted
	 * @return the name using "camel case"
	 */
	public static String convertUnderscoreNameToPropertyName(String name) {
		StringBuilder result = new StringBuilder();
		boolean nextIsUpper = false;
		if (name != null && name.length() > 0) {
			if (name.length() > 1 && name.charAt(1) == '_') {
				result.append(Character.toUpperCase(name.charAt(0)));
			}
			else {
				result.append(Character.toLowerCase(name.charAt(0)));
			}
			for (int i = 1; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c == '_') {
					nextIsUpper = true;
				}
				else {
					if (nextIsUpper) {
						result.append(Character.toUpperCase(c));
						nextIsUpper = false;
					}
					else {
						result.append(Character.toLowerCase(c));
					}
				}
			}
		}
		return result.toString();
	}

}
