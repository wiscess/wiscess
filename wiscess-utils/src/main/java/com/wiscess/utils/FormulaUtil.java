package com.wiscess.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.Fraction;

import lombok.extern.slf4j.Slf4j;

/**
 * 计算表达式
 */
@Slf4j
public class FormulaUtil {
	public static final int PLUS=1;
	public static final int SUBTRACE=2;
	public static final int MULTIPLY=3;
	public static final int DIVIDE=4;
	/**
	 * 不带变量
	 */
	public static Number calc(String formula){
		return calc(formula,new HashMap<String, Number>());
	}
	
	/**
	 * 带变量
	 */
	public static Number calc(String formula,Map<String, Number> map){
		//首先将中括号统一换成小括号
		formula=formula.replaceAll("（", "(").replaceAll("）", ")");
		formula=formula.replaceAll("\\[", "(").replaceAll("\\]", ")");
		//用正则表达式查找最先执行的语句，即最内一级小括号中的表达式
		Pattern p=Pattern.compile("([\\(]([^\\(\\)]*)[\\)])");
		Matcher m=p.matcher(formula);
		while(m.find()){
			log.debug("开始计算"+formula);
			//取得带括号的字符串
			String f0=m.group(1);
			//取得括号内的字符串
			String f1=m.group(2);
			//计算括号内的表达式
			Number n=calc(f1,map);
			//将结果替换掉原来的表达式
			//用变量记录值
			String key="x"+map.size();
			map.put(key,n);
			
			formula=formula.replace(f0, key);
			//继续找下一组表达式
			m=p.matcher(formula);
		}
		//没有找到括号，计算当前表达式
		//先计算*/,后计算+-
		Pattern p1=Pattern.compile("(.*)([\\+|-])(.*)");
		Matcher m1=p1.matcher(formula);
		while(m1.find()){
			log.debug("开始计算"+formula);
			//计算两个数加减
			Number one=calc(m1.group(1),map);
			String op=m1.group(2);
			Number two=calc(m1.group(3),map);
			Number result=calc(one,op.equals("+")?PLUS:SUBTRACE,two);
			log.debug(m1.group(1)+op+m1.group(3)+"="+result.toString());
			return result;
		}
		//计算乘法
		Pattern p2=Pattern.compile("(.*)([\\*|/])(.*)");
		Matcher m2=p2.matcher(formula);
		while(m2.find()){
			log.debug("开始计算"+formula);
			//计算两个数乘除
			Number one=calc(m2.group(1),map);
			String op=m2.group(2);
			Number two=calc(m2.group(3),map);

			Number result=calc(one,op.equals("*")?MULTIPLY:DIVIDE,two);
			log.debug(m2.group(1)+op+m2.group(3)+"="+result.toString());
			return result;
		}

		//根据表达式返回数字，处理分数
		if(formula.indexOf("\\frac")!=-1){
			Pattern p3=Pattern.compile("([0-9]*)\\\\frac\\{([0-9]*)\\}\\{([0-9]*)\\}");
			Matcher m3=p3.matcher(formula);
			if(m3.find()){
				String f1=m3.group(1);
				if(f1.equals(""))f1="0";
				String f2=m3.group(2);
				if(f2.equals(""))f2="0";
				String f3=m3.group(3);
				if(f3.equals(""))f3="0";
				//分母不能为0
				return Fraction.getFraction(Integer.parseInt(f1), 
						Integer.parseInt(f2), Integer.parseInt(f3));
			}
		}else if(formula.indexOf(".")!=-1){
			return Double.parseDouble(formula);
		}else if(map.containsKey(formula)){
			return map.get(formula);
		}
		return Integer.parseInt(formula);
	}
	
	/**
	 * 四则运算
	 * @param one
	 * @param operation
	 * @param two
	 * @return
	 */
	public static Number calc(Number one,Integer operation,Number two){
		if(one instanceof Fraction || two instanceof Fraction){
			Fraction result;
			Fraction f1=Fraction.getFraction(one.toString());
			Fraction f2=Fraction.getFraction(two.toString());
			switch (operation) {
			case PLUS: result=f1.add(f2);break;
			case SUBTRACE:result=f1.subtract(f2);break;
			case MULTIPLY:result=f1.multiplyBy(f2);break;
			case DIVIDE:result=f1.divideBy(f2);break;
			default:
				result=f1;
				break;
			}
			result.reduce(); //约分
			if(result.getNumerator()%result.getDenominator()==0){
				return result.getNumerator()/result.getDenominator();
			}
			return result;
		}else if(one instanceof Double || two instanceof Double){
			switch (operation) {
			case PLUS:
				return Double.parseDouble(one.toString())+Double.parseDouble(two.toString());
			case SUBTRACE:
				return Double.parseDouble(one.toString())-Double.parseDouble(two.toString());
			case MULTIPLY:
				return Double.parseDouble(one.toString())*Double.parseDouble(two.toString());
			case DIVIDE:
				return Double.parseDouble(one.toString())/Double.parseDouble(two.toString());
			}
			return 1;
		}
		switch (operation) {
		case PLUS:
			return Integer.parseInt(one.toString())+Integer.parseInt(two.toString());
		case SUBTRACE:
			return Integer.parseInt(one.toString())-Integer.parseInt(two.toString());
		case MULTIPLY:
			return Integer.parseInt(one.toString())*Integer.parseInt(two.toString());
		case DIVIDE:
			//如果能整除，则返回整除结果，否则返回分数
			if(Integer.parseInt(one.toString())%Integer.parseInt(two.toString())==0){
				return Integer.parseInt(one.toString())/Integer.parseInt(two.toString());
			}
			return Fraction.getReducedFraction(Integer.parseInt(one.toString()),Integer.parseInt(two.toString()));
		}
		return 1;
	}
	
	/**
	 * 两个数相加
	 * @param one
	 * @param two
	 * @return
	 */
	public static Number plus(Number one,Number two){
		if(one instanceof Fraction || two instanceof Fraction){
			return Fraction.getFraction(one.toString()).add(Fraction.getFraction(two.toString()));
		}else if(one instanceof Double || two instanceof Double){
			return Double.parseDouble(one.toString())+Double.parseDouble(two.toString());
		}
		return Integer.parseInt(one.toString())+Integer.parseInt(two.toString());
	}
	
	/**
	 * 两个数相减
	 * @param one
	 * @param two
	 * @return
	 */
	public static Number subtract(Number one,Number two){
		if(one instanceof Fraction || two instanceof Fraction){
			return Fraction.getFraction(one.toString()).subtract(Fraction.getFraction(two.toString()));
		}else if(one instanceof Double || two instanceof Double){
			return Double.parseDouble(one.toString())-Double.parseDouble(two.toString());
		}
		return Integer.parseInt(one.toString())-Integer.parseInt(two.toString());
	}
	
	/**
	 * 两个数相乘
	 * @param one
	 * @param two
	 * @return
	 */
	public static Number multiply(Number one,Number two){
		if(one instanceof Fraction || two instanceof Fraction){
			return Fraction.getFraction(one.toString()).multiplyBy(Fraction.getFraction(two.toString()));
		}else if(one instanceof Double || two instanceof Double){
			return Double.parseDouble(one.toString())*Double.parseDouble(two.toString());
		}
		return Integer.parseInt(one.toString())*Integer.parseInt(two.toString());
	}
	
	/**
	 * 两个数相除
	 * @param one
	 * @param two
	 * @return
	 */
	public static Number divide(Number one,Number two){
		if(one instanceof Fraction || two instanceof Fraction){
			Fraction result = Fraction.getFraction(one.toString()).divideBy(Fraction.getFraction(two.toString()));
			if(result.getNumerator()%result.getDenominator()==0){
				return result.getNumerator()/result.getDenominator();
			}
			return result;
		}else if(one instanceof Double || two instanceof Double){
			return Double.parseDouble(one.toString())/Double.parseDouble(two.toString());
		}
		//如果能整除，则返回整除结果，否则返回分数
		if(Integer.parseInt(one.toString())%Integer.parseInt(two.toString())==0){
			return Integer.parseInt(one.toString())/Integer.parseInt(two.toString());
		}
		return Fraction.getFraction(Integer.parseInt(one.toString()),Integer.parseInt(two.toString()));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//log.setLevel(Level.DEBUG);
		log.info("asdf");
		Number a=FormulaUtil.calc("(21+3)/(4\\frac{1}{2}+5*2)");
		System.out.println(a.toString());
		Number result=FormulaUtil.calc("14/6");
		if(result instanceof Fraction){
			Fraction f=(Fraction)result;
			System.out.println(f.toString());
			System.out.println(f.getProperWhole()+"+"+f.getProperNumerator()+"/"+f.getDenominator());
		}
		System.out.println(result.toString());
		Map<String, Number> map = new HashMap<String, Number>();
		map.put("x", 5);
		map.put("y", 3);
		Number b=FormulaUtil.calc("y/(x+y)-x/(x+y)",map);
		
		System.out.println(b.toString());
	}

}
