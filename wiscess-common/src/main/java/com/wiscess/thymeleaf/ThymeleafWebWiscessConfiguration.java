package com.wiscess.thymeleaf;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import com.wiscess.thymeleaf.processor.TreeInputProcessor;

@Slf4j
@Configuration
@EnableAutoConfiguration
public class ThymeleafWebWiscessConfiguration {
	
	public void configure(WiscessDialect dialect){
	}
	@Bean(name="wiscessDialect")
	@ConditionalOnMissingBean
	public WiscessDialect wiscessDialect(){
		log.debug("ThymeleafWebWiscessConfiguration init.");
		WiscessDialect wiscessDialect=new WiscessDialect();
		configure(wiscessDialect);
		wiscessDialect.getProcessors().add(new TreeInputProcessor());
		return wiscessDialect;
	}
	
	public static class ExpressionUtil{
		
		/**
		 * 获取bean
		 * @param arguments
		 * @param requiredType
		 * @return
		 * @throws BeansException
		 */
		public static <T> T getBean(Arguments arguments, Class<T> requiredType) throws BeansException{
			final ApplicationContext appCtx = ((SpringWebContext)arguments.getContext()).getApplicationContext();
	        return appCtx.getBean(requiredType);
		}
		
		
		/**
		 * 将属性表达式转换成值
		 * @param arguments
		 * @param element
		 * @param attributeName
		 * @return
		 */
		public static Object execute(final Arguments arguments, final Element element, final String attributeName,Object defaultValue){
			final String attributeValue = element.getAttributeValue(attributeName);
			if(attributeValue!=null){
		        final org.thymeleaf.Configuration configuration = arguments.getConfiguration();
		        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
		        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
		        final Object result = expression.execute(configuration, arguments);
		        return result==null?defaultValue:result;
			}
			return defaultValue;
		}
		public static Object execute(final Arguments arguments, final Element element, final String attributeName){
			return execute(arguments, element, attributeName,null);
		}
		/**
		 * 读取字符串
		 * @param arguments
		 * @param element
		 * @param attributeName
		 * @param defaultValue
		 * @return
		 */
		public static String executeForString(final Arguments arguments, final Element element, final String attributeName,String defaultValue){
			final Object result = execute(arguments, element, attributeName,defaultValue);
			return result==null?defaultValue:result.toString();
		}
		public static String executeForString(final Arguments arguments, final Element element, final String attributeName){
	        return executeForString(arguments, element, attributeName,"");
		}
		
		/**
		 * 读取整形
		 * @param arguments
		 * @param element
		 * @param attributeName
		 * @param defaultValue
		 * @return
		 */
		public static Integer executeForInt(final Arguments arguments, final Element element, final String attributeName,Integer defaultValue){
	        final Object result = execute(arguments, element, attributeName,defaultValue);
	        if(result==null)
	        	return defaultValue;
	        else if(result instanceof String){
	        	return new Integer(result.toString());
	        }else if(result instanceof Integer){
	        	return (Integer)result;
	        }else if(result instanceof Long){
	        	return ((Long)result).intValue();
	        }else if(result instanceof BigDecimal){
	        	return ((BigDecimal)result).intValue();
	        }else{
	        	return new Integer(result.toString());
	        }
		}
		public static Integer executeForInt(final Arguments arguments, final Element element, final String attributeName){
			return executeForInt(arguments, element, attributeName,null);
		}
		
		/**
		 * 读取长整形
		 * @param arguments
		 * @param element
		 * @param attributeName
		 * @param defaultValue
		 * @return
		 */
		public static Long executeForLong(final Arguments arguments, final Element element, final String attributeName,Long defaultValue){
	        final Object result = execute(arguments, element, attributeName,defaultValue);
	        if(result==null)
	        	return defaultValue;
	        else if(result instanceof String){
	        	return new Long(result.toString());
	        }else if(result instanceof Long){
	        	return (Long)result;
	        }else if(result instanceof Integer){
	        	return new Long((Integer)result);
	        }else if(result instanceof BigDecimal){
	        	return ((BigDecimal)result).longValue();
	        }else{
	        	return new Long(result.toString());
	        }
		}
		public static Long executeForLong(final Arguments arguments, final Element element, final String attributeName){
			return executeForLong(arguments, element, attributeName,null);
		}
		/**
		 * 读取浮点型
		 * @param arguments
		 * @param element
		 * @param attributeName
		 * @param defaultValue
		 * @return
		 */
		public static Double executeForDouble(final Arguments arguments, final Element element, final String attributeName,Double defaultValue){
	        final Object result = execute(arguments, element, attributeName,defaultValue);
	        if(result==null)
	        	return defaultValue;
	        else if(result instanceof String){
	        	return new Double(result.toString());
	        }else if(result instanceof Integer){
	        	return ((Integer)result).doubleValue();
	        }else if(result instanceof Long){
	        	return ((Long)result).doubleValue();
	        }else if(result instanceof BigDecimal){
	        	return ((BigDecimal)result).doubleValue();
	        }else{
	        	return new Double(result.toString());
	        }
		}
		public static Double executeForDouble(final Arguments arguments, final Element element, final String attributeName){
			return executeForDouble(arguments, element, attributeName,null);
		}
	}
	
}
