package org.wiscess.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V1.0
 * @date 2011-10-03
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
	}
	
	/**
	 * 返回公式的结果
	 * @param cell
	 * @return
	 */
	public String getFormulaValue(Cell cell){
		XSSFFormulaEvaluator ev = new XSSFFormulaEvaluator((XSSFWorkbook)sheet.getWorkbook());
		return getStringCellValueByCellValue(ev.evaluate(cell));
	}

	
} 
