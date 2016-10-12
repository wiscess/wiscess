package org.wiscess.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V1.0
 * @date 2011-10-03
 *
 */
public class HssfExcelReader extends ExcelReader{
	
	/**
	 * 打开文件
	 * @param input
	 * @throws IOException
	 */
	public HssfExcelReader(InputStream input) throws IOException {
		wb = new HSSFWorkbook(new POIFSFileSystem(input));
	}
	/**
	 * 返回公式的结果
	 * @param cell
	 * @return
	 */
	public String getFormulaValue(Cell cell){
		HSSFFormulaEvaluator ev = new HSSFFormulaEvaluator((HSSFWorkbook)sheet.getWorkbook());
		return getStringCellValueByCellValue(ev.evaluate(cell));
	}
} 
