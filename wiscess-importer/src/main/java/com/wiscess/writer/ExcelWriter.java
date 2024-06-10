package com.wiscess.writer;

import java.io.*;
import java.math.BigDecimal;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellAddress;

import com.wiscess.reader.ExcelReader;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V2.1
 * @date 2021-04-18
 *
 */
public abstract class ExcelWriter extends ExcelReader{
	
	public static ExcelWriter getInstance(File file) throws IOException{
		String filename=file.getName();
		String fileType=filename.substring(filename.lastIndexOf(".")+1);
		return getInstance(new FileInputStream(file),fileType);
	}
	public static ExcelWriter getInstance(File file,String fileType) throws IOException{
		return getInstance(new FileInputStream(file),fileType);
	}
	public static ExcelWriter getInstance(InputStream input,String fileType) throws IOException{
		if("xls".equalsIgnoreCase(fileType)){
			return new HssfExcelWriter(input);
		}
		return new XssfExcelWriter(input);
	}
	
	public void setCellValue(int sheetNum,String cell,Object value) {
		setCellValue(sheetNum,new CellAddress(cell).getRow(),new CellAddress(cell).getColumn(), value);
	}
	/**
	 * 指定工作表、行、列下的内容
	 * @param sheetNum
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public void setCellValue(int sheetNum, int rowNum, int cellNum,Object value) {
		if (sheetNum < 0 || rowNum < 0  || cellNum<0)
			return;
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(rowNum);
			if(row==null){
				row=sheet.createRow(rowNum);
			}
			
			Cell cell = row.getCell(cellNum); 
			if (cell == null) {
				cell=row.createCell(cellNum);
			}
			if(cell!=null) {
				if(value==null) {
					cell.setCellValue("");
				}else {
					if (value instanceof Integer || value instanceof Double) {
						try {
							cell.setCellValue(new BigDecimal(value.toString())
									.doubleValue());
						} catch (Exception e) {
							cell.setCellValue(value.toString());
						}
					} else {
						cell.setCellValue(value.toString());
					}
				}
				CellStyle lastStyle=cell.getCellStyle();
				//调整行高,变为自动换行
				lastStyle.setWrapText(true);
				cell.setCellStyle(lastStyle);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public ExcelWriter write(File f) throws IOException {
		try {
			FileOutputStream excelFileOutPutStream = new FileOutputStream(f);
			return flush(excelFileOutPutStream,true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}

	public ExcelWriter flush(File f) throws IOException {
		return flush(FileUtil.getOutputStream(f),true);
	}
	public ExcelWriter flush(OutputStream out) throws IOException {
		return flush(out,false);
	}
	public ExcelWriter flush(OutputStream out, boolean isCloseOut) throws IOException {
		try {
			wb.setForceFormulaRecalculation(true);
			// 将最新的 Excel 文件写入到文件输出流中，更新文件信息！
			wb.write(out);
			// 执行 flush 操作， 将缓存区内的信息更新到文件上
			out.flush();
			// 使用后，及时关闭这个输出流对象， 好习惯，再强调一遍！
			out.close();
		} catch (IOException e) {
			throw e;
		} finally {
			if (isCloseOut) {
				IoUtil.close(out);
			}
		}
		return this;
	}

}
