package com.wiscess.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V2.1
 * @date 2021-04-18
 *
 */
public class XssfExcelReader extends ExcelReader{
	
	/**
	 * 打开文件
	 * @param input
	 * @throws IOException
	 */
	public XssfExcelReader(InputStream input) throws IOException {
		wb = new XSSFWorkbook(input);
		ev = new XSSFFormulaEvaluator((XSSFWorkbook)wb);
	}
	
} 
