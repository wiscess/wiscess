package com.wiscess.utils;

import java.util.ArrayList;
import java.util.List;

import com.wiscess.dto.ExcelFieldDto;

/**
 * Excel导入校验类
 * 提供校验字段类型的方法
 * 1、校验数字
 * 2、校验字符串
 * 3、校验字典值
 * @author wh
 *
 */
public class ExcelCheckUtils {

	/**
	 * 校验导入文件的标题
	 * @param fieldList
	 * @param row
	 * @return
	 */
	public static List<String> checkRowTitle(List<ExcelFieldDto> fieldList, String[] row){
		
		List<String> errorList = new ArrayList<>();
		
		/**
		 * 判断标题名称，与字段定义是否一致
		 */
		int colCnt=0;
		for(int col=0;col<row.length;col++) {
			//取第几列的标题
			String title=row[col].replaceAll("\n", "");
			boolean findFlag=false;
			for(ExcelFieldDto dto:fieldList) {
				if(title.trim().equals(dto.getTitle())) {
					//标题匹配，记录col
					if(!dto.getIsShow()) {
						//第一次出现该字段
						dto.setColIndex(col);
						dto.setIsShow(true);
						colCnt++;
					}else {
						//多次出现该字段，提示错误
						errorList.add("导入文件的标题【"+title+"】重复出现");
					}
					findFlag=true;
					break;
				}
			}
			if(!findFlag) {
				//不存在的标题
				//System.out.println(title);
			}
		}

		for(ExcelFieldDto dto:fieldList) {
			if(!dto.getIsShow()) {
				//System.out.println(dto.getTitle());
			}
		}
		if(fieldList.size() != colCnt){
			//标题数量不等于必须的字段数量，也说明不是模板文件
			errorList.add("请您下载模版填写信息后重新导入！");
		}
		return errorList;
	}

	/**
	 * 返回标题的位置
	 * @param fieldList
	 * @param title
	 * @return
	 */
	public static Integer findFieldCol(List<ExcelFieldDto> fieldList, String title){
		for(ExcelFieldDto dto:fieldList) {
			if(title.trim().equals(dto.getTitle())) {
				return dto.getColIndex();
			}
		}
		return null;
	}

	/**
	 * 读取指定标题的值
	 * @param fieldList
	 * @param title
	 * @param row
	 * @return
	 */
	public static String getValue(List<ExcelFieldDto> fieldList, String title,String[] row){
		//先找到标题对应的位置
		Integer colIndex = findFieldCol(fieldList,title);
		if(colIndex!=null && row.length>colIndex){
			return row[colIndex];
		}
		return null;
	}
}
