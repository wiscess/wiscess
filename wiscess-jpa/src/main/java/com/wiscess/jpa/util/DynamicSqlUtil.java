package com.wiscess.jpa.util;

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

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;

/**
 * Dynamic SQL processer, supportting Freemarker based syntax.
 * 
 * @author austin
 */
@SuppressWarnings("deprecation")
public class DynamicSqlUtil {
	protected static Configuration freeMarkerEngine = new Configuration();
	private static ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();
	
	static {
		freeMarkerEngine.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
	}
	
	/**
	 * process dynamic Sql.
	 * 
	 * @param params named parameters.
	 * @param sqlTemplate dynamic sql(based on Freemarker syntax).
	 * @return Sql result.
	 * @throws Exception exception.
	 */
	public static ISqlElement processSql(Map<String, Object> params, String sqlTemplate) throws Exception {
		Map<String, Object> context = new HashMap<String, Object>();
		if(params!=null){
			for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
	            context.put(paramEntry.getKey(), paramEntry.getValue());
	        }
		}
		StringWriter out = new StringWriter();
		
        Template tpl = templateCache.get(sqlTemplate);
        if (tpl == null) {
            tpl = new Template("tpl", new StringReader(sqlTemplate), freeMarkerEngine);
            templateCache.put(sqlTemplate, tpl);
        }
        
        tpl.process(context, out);
        String sql = out.toString();
        
        // holder for avaliable parameters.
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        List<Object> paramNamesList = new ArrayList<Object>();
        String strPattern="((%?):([a-zA-Z0-9_]+)(%?))";
        Pattern p = Pattern.compile (strPattern);
        Matcher m = p.matcher (sql);
        while (m.find()) {
        	String name = m.group(3);
        	String preValue = m.group(2);
        	String lastValue = m.group(4);
        	paramsMap.put(name, preValue+params.get(name)+lastValue);
        	//paramValuesList.add(params.get(name));
        	paramNamesList.add(name);
        }
        Object[] paramValues = new Object[paramNamesList.size()];
       
        for(int i=0; i<paramNamesList.size(); i++)
        {
        	paramValues[i] = paramsMap.get(paramNamesList.get(i));
        }
        
        // replace all named params with ?
        sql = sql.replaceAll(strPattern, "?");
        
		SqlElementImpl s = new SqlElementImpl();
		s.setParams(paramValues);
		s.setParamsMap(paramsMap);
		s.setSql(sql);
		
		return s;
	}
}