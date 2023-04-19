package com.wiscess.thymeleaf;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring6.context.SpringContextUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.EvaluationUtils;

public class ExpressionUtil {
	
	/**
	 * 获取bean
	 * @param arguments
	 * @param requiredType
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBean(ITemplateContext context, Class<T> requiredType) throws BeansException{
		final ApplicationContext appCtx = SpringContextUtils.getApplicationContext(context);
        return appCtx.getBean(requiredType);
	}
	
	/**
	 * 将属性表达式转换成值
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return
	 */
	public static Object execute(final ITemplateContext context,final IProcessableElementTag element, final String attributeName,Object defaultValue){
		final String attributeValue = element.getAttributeValue(attributeName);
		if(attributeValue!=null){
			final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

	        final IStandardExpression expression =
	                expressionParser.parseExpression(context, attributeValue);
	        final Object value = expression.execute(context);
	        return value==null?defaultValue:value;
		}
		return defaultValue;
	}
	public static Object execute(final ITemplateContext context,final IProcessableElementTag element, final String attributeName){
		return execute(context, element, attributeName,null);
	}
	/**
	 * 读取字符串
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public static String executeForString(final ITemplateContext context,final IProcessableElementTag element, final String attributeName,String defaultValue){
		final Object result = execute(context, element, attributeName,defaultValue);
		return result==null?defaultValue:result.toString();
	}
	public static String executeForString(final ITemplateContext context,final IProcessableElementTag element, final String attributeName){
        return executeForString(context, element, attributeName,"");
	}
	
	/**
	 * 读取整形
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public static Integer executeForInt(final ITemplateContext context,final IProcessableElementTag element, final String attributeName,Integer defaultValue){
        final Object result = execute(context, element, attributeName,defaultValue);
        if(result==null)
        	return defaultValue;
        return EvaluationUtils.evaluateAsNumber(result).intValue();
	}
	public static Integer executeForInt(final ITemplateContext context,final IProcessableElementTag element, final String attributeName){
		return executeForInt(context, element, attributeName,null);
	}
	
	/**
	 * 读取长整形
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public static Long executeForLong(final ITemplateContext context,final IProcessableElementTag element, final String attributeName,Long defaultValue){
        final Object result = execute(context, element, attributeName,defaultValue);
        return (result==null)
        		? defaultValue
        		: EvaluationUtils.evaluateAsNumber(result).longValue();
	}
	public static Long executeForLong(final ITemplateContext context,final IProcessableElementTag element, final String attributeName){
		return executeForLong(context, element, attributeName,null);
	}
	/**
	 * 读取浮点型
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public static Double executeForDouble(final ITemplateContext context,final IProcessableElementTag element, final String attributeName,Double defaultValue){
        final Object result = execute(context, element, attributeName,defaultValue);
        return (result==null)
        		? defaultValue
        		: EvaluationUtils.evaluateAsNumber(result).doubleValue();
	}
	public static Double executeForDouble(final ITemplateContext context,final IProcessableElementTag element, final String attributeName){
		return executeForDouble(context, element, attributeName,null);
	}
}
