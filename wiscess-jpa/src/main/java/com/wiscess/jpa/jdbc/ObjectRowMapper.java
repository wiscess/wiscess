/**
 * 
 */
package com.wiscess.jpa.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 将结果集的行数据转换为对象
 * @author audin
 *
 */
public class ObjectRowMapper<T> implements RowMapper<T> {
	protected Class<?> clazz;
	private Map<String, Method> methodMap = new ConcurrentHashMap<>();
	private String[] propertyNames;
	private String[] setterMethodNames;
	private int columnCount = -1;
	
	public ObjectRowMapper(Class<?> clazz) {
		this.clazz = clazz;
		Method[] declaredMethods = ReflectionUtils.getAllDeclaredMethods(clazz);
		for (Method method : declaredMethods) {
			if (method.getModifiers() == Method.PUBLIC && method.getTypeParameters().length == 1) {
				methodMap.put(method.getName(), method);
			}
		}
	}
	
	private synchronized void initProps(ResultSetMetaData meta) throws SQLException {
		if (columnCount == -1) {
			columnCount = meta.getColumnCount();
			propertyNames = new String[columnCount+1];// 数组第一个不用
			setterMethodNames = new String[columnCount+1];// 数组第一个不用
			for (int i = 1; i <= columnCount; i++) {
//				String columnName = meta.getColumnName(i);
				String columnName=meta.getColumnLabel(i);
				String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
				String setterMethodName = "set"+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
				propertyNames[i] = propertyName;
				setterMethodNames[i] = setterMethodName;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			Object obj = clazz.getDeclaredConstructors()[0].newInstance();
			initProps(rs.getMetaData());
			for (int i = 1; i <= columnCount; i++) {
				String propertyName = propertyNames[i];
				String setterMethodName = setterMethodNames[i];
				boolean set = false;
				Method setter = methodMap.get(setterMethodName);
				if (setter != null) { // 优先调用set方法
					// invoke
					Class<?>[] parameterTypes = setter.getParameterTypes();
					if (parameterTypes.length == 1) { // 只处理一个参数的setter
						if (! setter.trySetAccessible()) {
							setter.setAccessible(true);
						}
						Object value = JdbcUtils.getResultSetValue(rs, i, parameterTypes[0]);
						ReflectionUtils.invokeMethod(setter, obj, value);
						set = true;
					}
				}
				if (! set) { // 没有set方法，则直接操作属性
					Field field = ReflectionUtils.findField(clazz, propertyName);
					if (field != null) {
						if (! field.trySetAccessible()) {
							field.setAccessible(true);
						}
						Object value = JdbcUtils.getResultSetValue(rs, i, field.getType());
						field.set(obj, value);
					}
				}
			}
			return (T)obj;
		} catch (InstantiationException e) {
			throw new SQLException("类初始化异常", e);
		} catch (IllegalAccessException e) {
			throw new SQLException("非法访问异常", e);
		} catch (SecurityException e) {
			throw new SQLException("安全异常", e);
		} catch (IllegalArgumentException e) {
			throw new SQLException("非法参数异常", e);
		} catch (InvocationTargetException e) {
			throw new SQLException("类初始化异常", e);
		}
	}

}
