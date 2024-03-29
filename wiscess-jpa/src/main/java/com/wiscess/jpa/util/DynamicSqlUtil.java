package com.wiscess.jpa.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.jdbc.core.StatementCreatorUtils;

/**
 * Dynamic SQL processer, supportting Freemarker based syntax.
 * 
 * @author austin
 */
public class DynamicSqlUtil {
	protected static Configuration freeMarkerEngine = new Configuration(Configuration.VERSION_2_3_31);
	private static ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();
	
	static {
		freeMarkerEngine.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_31).build());
	}

	/**
	 * process dynamic Sql.
	 * 
	 * @param params named parameters.
	 * @param sqlTemplate dynamic sql(based on Freemarker syntax).
	 * @return Sql result.
	 * @throws Exception exception.
	 */
	public static ISqlElement processSql(Map<String, Object> params, String sqlTemplate) {
		Map<String, Object> context = new HashMap<String, Object>();
		if(params!=null){
			for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
	            context.put(paramEntry.getKey(), paramEntry.getValue());
	        }
		}
		StringWriter out = new StringWriter();
		
        Template tpl = templateCache.get(sqlTemplate);
        if (tpl == null) {
            try {
				tpl = new Template("tpl", new StringReader(sqlTemplate), freeMarkerEngine);
			} catch (IOException e) {
				throw new DynamicSqlException("动态SQL语句读取出错。", e);
			}
            templateCache.put(sqlTemplate, tpl);
        }
        
        try {
			tpl.process(context, out);
		} catch (TemplateException e) {
			throw new DynamicSqlException("动态SQL中的FreeMarker语法错误。", e);
		} catch (IOException e) {
			throw new DynamicSqlException("动态SQL语句输出出错。", e);
		}
        String sql = out.toString();
        
        // holder for avaliable parameters.
        //先替换时间转换字符串
        sql=sql.replaceAll(":mi:ss", "_MI_SS");
        sql=sql.replaceAll(":mm:ss", "_MM_SS");
        
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        List<Object> paramNamesList = new ArrayList<Object>();
        String strPattern="((%?):([a-zA-Z0-9_]+)(%?))";
        Pattern p = Pattern.compile (strPattern);
        Matcher m = p.matcher (sql);
        while (m.find()) {
        	m.group(1);
        	String name = m.group(3);
        	String preValue = m.group(2);
        	String lastValue = m.group(4);
        	if(params.containsKey(name)) {
        		//判断name是否是参数表中的key
				if(params.get(name)==null) {
					paramsMap.put(name, null);
				}else {
					paramsMap.put(name, preValue+params.get(name)+lastValue);
				}
	        	//paramValuesList.add(params.get(name));
	        	paramNamesList.add(name);
	        	//找到一个替换一个，未找到name的不替换
        	}
        }
        Object[] paramValues = new Object[paramNamesList.size()];
        int[] argTypes = new int[paramNamesList.size()];

        for(int i=0; i<paramNamesList.size(); i++)
        {
        	paramValues[i] = paramsMap.get(paramNamesList.get(i));
			argTypes[i] = StatementCreatorUtils.javaTypeToSqlParameterType(params.get(paramNamesList.get(i))==null?null:params.get(paramNamesList.get(i)).getClass());
        }
        
        // replace all named params with ?
        sql = sql.replaceAll(strPattern, "?");
        //替换回原来的时间格式
        sql=sql.replaceAll("_MI_SS",":mi:ss");
        sql=sql.replaceAll("_MM_SS",":mm:ss");
        
		SqlElementImpl s = new SqlElementImpl();
		s.setParams(paramValues);
		s.setParamsMap(paramsMap);
		s.setArgTypes(argTypes);
		s.setSql(sql);
		
		return s;
	}
}