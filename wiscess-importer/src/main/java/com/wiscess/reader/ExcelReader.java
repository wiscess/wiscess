package com.wiscess.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V2.0
 * @date 2018-08-18
 *
 */
@Slf4j
public abstract class ExcelReader{
	
	public static ExcelReader getInstance(File file) throws IOException{
		String filename=file.getName();
		String fileType=filename.substring(filename.lastIndexOf(".")+1);
		return getInstance(new FileInputStream(file),fileType);
	}
	public static ExcelReader getInstance(MultipartFile fileItem) throws IOException{
		String filename=fileItem.getOriginalFilename();
		String fileType=filename.substring(filename.lastIndexOf(".")+1);
		return getInstance(fileItem.getInputStream(),fileType);
	}
	public static ExcelReader getInstance(InputStream input,String fileType) throws IOException{
		if("xls".equalsIgnoreCase(fileType)){
			return new HssfExcelReader(input);
		}
		return new XssfExcelReader(input);
	}
	
	protected Workbook wb = null;

	protected Sheet sheet = null;

	protected Row row = null;
	
	protected FormulaEvaluator ev =null;

	public void close(){
		sheet=null;
		row=null;
		wb=null;
	}
	
	/**
	 * 返回sheet表数目
	 * @return int
	 */
	public int getSheetCount() {
		return wb.getNumberOfSheets();
	}
	
	/**
	 * 根据SheetName返回SheetNum
	 * @param sheetName
	 * @return
	 */
	public int getSheetNumByName(String sheetName){
		if (wb == null){
			return -1;
		}
		return wb.getSheetIndex(sheetName);
	}
	
	/**
	 * 根据sheetIndex获取SheetName
	 * @param sheetIndex
	 * @return
	 */
	public String getSheetNameByNum(int sheetIndex){
		if (wb == null || sheetIndex>=getSheetCount()){
			return "";
		}
		return wb.getSheetName(sheetIndex);
	}
	/**
	 * 读取默认sheetNum的rowCount
	 * @param sheetNum
	 * @return int
	 */
	public int getRowCount(int sheetNum) {
		if (wb == null){
			log.debug("=============>WorkBook为空");
			return -1;
		}
		Sheet sheet = wb.getSheetAt(sheetNum);
		if(sheet==null){
			log.debug("=============>Sheet不存在");
			return -1;
		}
		return sheet.getLastRowNum();
	}


	/**
	 * 指定工作表和行数的内容
	 * @param sheetNum
	 * @param lineNum
	 * @return String[]
	 */
	public String[] readExcelLine(int sheetNum, int rowNum) {
		//如果指定工作表和指定行数小于0，或者指定行数大于该工作表的总行数
		if (sheetNum < 0 || rowNum < 0 || rowNum>getRowCount(sheetNum))
			return null;
		String[] strExcelLine = null;
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(rowNum);

			if(row==null){
				log.debug("=============>Sheet{}的第{}行为空或不存在",sheetNum,rowNum);
				return null;
			}
			//根据标题行读取总列数
			int cellCount = row.getLastCellNum();
			if(cellCount<=0)
				return null;
			strExcelLine = new String[cellCount];
			for (int i = 0; i < cellCount; i++) {
				//读取指定行列的数据
				strExcelLine[i] = readStringExcelCell(sheetNum, rowNum, i);
			}
		} catch (Exception e) {
			log.error("系统：excelReader:" + e.getMessage() + ";"
					+ e.fillInStackTrace());
		}
		return strExcelLine;
	}

	/**
	 * 指定工作表、行、列下的内容
	 * @param sheetNum
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int sheetNum, int rowNum, int cellNum) {
		if (sheetNum < 0 || rowNum < 0  || cellNum<0)
			return null;
		String strExcelCell = "";
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(rowNum);
			if(row==null){
				log.debug("=============>行为空或不存在");
				return null;
			}
			
			Cell cell = row.getCell(cellNum); 
			if (cell != null) { // add this condition
				// 判断列类型
				CellValue cellValue=ev.evaluate(cell);
				if(cellValue==null){
					strExcelCell="";
				}else{
					switch (cellValue.getCellTypeEnum()) {
			            case BOOLEAN:
			            	strExcelCell = cellValue.getBooleanValue() ? "TRUE" : "FALSE";
			            	break;
			            case ERROR:
			            	strExcelCell = String.valueOf(cellValue.getErrorValue());
			            	break;
						case NUMERIC:// 数字类型
							if(DateUtil.isCellDateFormatted(cell)){
								strExcelCell = getDateValue(cell);
							} else {
								double value = cell.getNumericCellValue();
								strExcelCell = numberFormat.format(value);
							}
							break;
			            case STRING:
			            	strExcelCell = cellValue.getStringValue();
			            	break;
			            case BLANK:
			            	strExcelCell = String.valueOf(cell.getRichStringCellValue().toString());
			            default:
			            	strExcelCell = "";
			        }
				}
			}
			
		} catch (Exception e) {
			log.error("系统:excelReader:" + e.getMessage() + ";"
					+ e.fillInStackTrace());
		}
		return strExcelCell;
	}

	

	/**
	 * 获取所有行内容(读取索引为0的工作表)
	 * @return
	 */
	public List<String[]> getRowList(int sheetNum,int fromRowNum) {
		List<String[]> list = new ArrayList<String[]>();
		// 总行数
		int count = getRowCount(sheetNum);
		if(count>=0){
			for (int i = fromRowNum; i <= count; i++) {
				//每行内容
				String[] rows = readExcelLine(sheetNum,i);
				list.add(rows);
			}
		}
		return list;
	}
	/**
	 * 读取excel
	 * @param input
	 * @param sheetNum
	 * @param forEachNum
	 * @return
	 */
	public List<String[]> getRowList(int sheetNum,int fromRowNum,int toRowNum){
		List<String[]> list = new ArrayList<String[]>();
		for (int i = fromRowNum; i <= toRowNum; i++) {
			//每行内容
			String[] rows = readExcelLine(sheetNum, i);
			if(row!=null)
				list.add(rows);
		}
		return list;
	}
	
	/**
	  * 返回时间内的特殊时间格式 OFFICE2003
	  * @param cell
	  * @return
	  */
	protected static String getDateValue(Cell cell){
		try{
			Date d= cell.getDateCellValue();
			return DEFAULT_DATE_FORMAT.format(d);
		}catch(Exception e){
			return cell.getStringCellValue();
		}
	}

	public boolean isCellDateFormatted(Cell cell){
		return DateUtil.isCellDateFormatted(cell);
	}

	
	public final static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public final static DecimalFormat numberFormat = new DecimalFormat("#############0.######");
} 
