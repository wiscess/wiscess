package com.wiscess.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V2.0
 * @date 2018-08-18
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
		ev = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
	}

} 
