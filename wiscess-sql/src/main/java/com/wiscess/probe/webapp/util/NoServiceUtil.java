package com.wiscess.probe.webapp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.wiscess.utils.StringUtils;

public class NoServiceUtil {

	private static SimpleDateFormat chineseSdf=new SimpleDateFormat("yyyy年MM月dd日");
	
	/**
	 * 日期转换成中文年月日形式
	 * @param date
	 * @return
	 */
	public static String  getChineseDate(Object date) {
		if(date instanceof Date) {
			return chineseSdf.format(date);
		}else if(date instanceof String) {
			String[] dates=((String) date).split("-");
			return dates[0]+"年"+dates[1]+"月"+dates[2]+"日";
		}
		return "";
	}
	
	/**
	 * 随机生成验证码
	 * 
	 */
	public static String createPhoneCode() {
		// 验证码
		String phoneCode = "";
		for (int i = 0; i < 4; i++) {
			phoneCode = phoneCode + (int) (Math.random() * 9);
		}
		return phoneCode;
	}
	
    public static LocalDateTime setZone(LocalDateTime ldt){
        return ldt.atZone(ZoneId.of("Asiz/Shanghai")).toLocalDateTime();
    }

    public static LocalDateTime nowLDT(){
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
    public static Date now(){
        return Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }
    
    public static LocalDate getLocalDate(Date date) {
    	Instant instant = date.toInstant();
    	ZoneId zone = ZoneId.systemDefault();
    	LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    	return localDateTime.toLocalDate();
    }

    public static String getIpAddress(HttpServletRequest request){     
    	 String ip = "";
 		try {
// 			ip = request.getHeader("X-Forwarded-For");
 			ip = request.getHeader("x-real-ip");
 			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
 			    ip = request.getHeader("x-original-forwarded-for");
 			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
 			        ip = request.getRemoteAddr();
 			    }
 			} else if (ip.length() > 15) {
 			    String[] ips = ip.split(",");
 			    for (int index = 0; index < ips.length; index++) {
 			        String strIp = (String) ips[index];
 			        if (!("unknown".equalsIgnoreCase(strIp))) {
 			            ip = strIp;
 			            break;
 			        }
 			    }
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
        return ip;     
    }

	public static Object getCurrDate() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        return sd.format(new Date());
	}
	public static String dateFormat(Date date,String pattern) {
		if(date==null || StringUtils.isEmpty(pattern)){
			return "";
		}
		SimpleDateFormat sd = new SimpleDateFormat(pattern);
		return sd.format(date);
	}

	public static Date dateFormat(String date,String pattern) {
		if(StringUtils.isEmpty(pattern)){
			return null;
		}
		SimpleDateFormat sd = new SimpleDateFormat(pattern);
		try {
			return sd.parse(date);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断数组是否都为空
	 * @param rows
	 * @return
	 */
	public static boolean isEmpty(String[] rows) {
		if(rows==null)
			return true;
		for(String row:rows) {
			if(StringUtils.isNotEmpty(row))
				return false;
		}
		return true;
	}

	/**
	 * 两个数字相除
	 * @param number1 数字1
	 * @param number1 数字2
	 * @param  scale 保留小数位数
	 * @return
	 */
	public static String divide(int number1,int number2,int scale) {
		try {
			Double d1 = Double.valueOf(number1);
			Double d2 = Double.valueOf(number2);
			BigDecimal result = new BigDecimal((d1/d2)+"");
			return result.setScale(scale, RoundingMode.HALF_UP).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 将数字转百分比
	 * @param number
	 * @return
	 */
	public static String formatPercent(String number) {
		if(StringUtils.isEmpty(number)){
			return "";
		}
		try {
			BigDecimal bd = new BigDecimal(number);
			bd = bd.multiply(BigDecimal.valueOf(100));
			return bd.setScale(2, RoundingMode.HALF_UP).toString()+"%";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 数字相除后返回百分比
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static String divide2Percent(int number1,int number2) {
		String divide = divide(number1, number2, 4);
		return formatPercent(divide).toString();
	}
	
	public static String[] resetCells(int cellCount, String[] cells){
		if(cells == null){
			return null;
		}
        String[] strings = new String[cellCount];
        for (int i = 0; i < cellCount; i++) {
        	if(cells[i]!=null){
				strings[i] = cells[i].trim();
			}else{
				strings[i] = "";
			}
        }
        return strings;
    }
	
	/**
	 * Excel表格标题行是否正确
	 */
	public static List<String> validExcelTitle(String[] titles,String[] readExcelLine) {
		List<String> msgs = new ArrayList<>();
        try {
        	if (readExcelLine==null||readExcelLine.length<titles.length){
                msgs.add("标题行异常，请按要求下载模板并填写");
            }else{
				String[] titleCells = new String[titles.length];
		        for (int i = 0; i < readExcelLine.length; i++) {
		        	titleCells[i] = readExcelLine[i].trim();
		        }
				//校验数据列书否相同
				if(titles.length != titleCells.length){
					msgs.add("上传文件与模板文件【列】不同，请使用模板文件重新上传");
					return msgs;
				}
				//判断列头名称是否相同
				for(int i = 0 ; i < titles.length ; i ++){
					if(!titles[i].equals(titleCells[i])){
						msgs.add("第2行 "+(i+1)+"列： [列头名称] 应为 ["+titles[i]+"]");
					}
				}
            }
		} catch (Exception e) {
            msgs.add("标题行异常，请按要求下载模板并填写");
		}
        
        return msgs;
	}
	/**
	 *	对象转string
	 * @param str
	 * @return
	 */
	public static String objToStr(Object str) {
		String content = String.valueOf(str);
		if (str == null || "".equals(content) || "null".equals(content)) {
			return "";
		}
		return content;
	}
	/**
	 * 根据数字字符串（是/否）返回数字
	 * @param str
	 * @return
	 */
	public static String yesOrNo(Object str) {
		String content = String.valueOf(str);
		if (str == null || "".equals(content) || "null".equals(content)) {
			return "不确定";
		}
		if (Integer.valueOf(content).intValue() == 1) {
			return "是";
		}else if(Integer.valueOf(content).intValue() == 0) {
			return "否";
		}else if(Integer.valueOf(content).intValue() == 2) {
			return "不确定";
		}else {
			return "";
		}
	}
	/**
	 * 根据 是/否/不确定 返回1/0/2；
	 * @param str
	 * @return
	 */
	public static Integer fromYesOrNo(String str) {
		if(str==null) {
			return 2;
		}else if(str.equals("是")) {
			return 1;
		}else if(str.equals("否")) {
			return 0;
		}
		return 2;
	}
	
	/**
	 * 判断字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) { 
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
        return pattern.matcher(str).matches(); 
	}
	
	/**
	 * 判断是否为日期数字化
	 * 如43831为2020-01-01
	 * 47484为2030-01-01
	 */
	public static boolean isNumberDate(String str) {
		return StringUtils.isNotEmpty(str) 
				&& isInteger(str) 
				&& Integer.parseInt(str)>=43831 
				&& !str.startsWith("20");
	}
	/**
	 * 深度复制
	 * @param obj
	 * @return
	 */
	public static Object deepClone(Object obj){
        //将对象写入流中
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        ObjectOutputStream oos= null;
        try {
            oos = new ObjectOutputStream(bao);
            oos.writeObject(obj);
 
 
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将对象从流中取出
        ByteArrayInputStream bis=new ByteArrayInputStream(bao.toByteArray());
        ObjectInputStream ois= null;
        try {
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
 
    }
}
