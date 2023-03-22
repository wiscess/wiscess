package com.wiscess.writer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V2.1
 * @date 2021-04-18
 *
 */
public class HssfExcelWriter extends ExcelWriter{
	
	/**
	 * 打开文件
	 * @param input
	 * @throws IOException
	 */
	public HssfExcelWriter(InputStream input) throws IOException {
		wb = new HSSFWorkbook(new POIFSFileSystem(input));
		ev = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
		input.close();
	}

} 
