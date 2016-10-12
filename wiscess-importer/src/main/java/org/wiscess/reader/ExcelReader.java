package org.wiscess.reader;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作Excel表格的功能类 
 * @author wanghai
 * @version V1.0
 * @date 2016-08-18
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
		}else if("xlsx".equalsIgnoreCase(fileType)){
			return new XssfExcelReader(input);
		}
		return null;
	}
	
	public Workbook wb = null;

	public Sheet sheet = null;

	public Row row = null;

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
		int sheetCount = -1;
		sheetCount = wb.getNumberOfSheets();
		return sheetCount;
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
		if (wb == null){
			log.debug("=============>WorkBook为空");
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
		int rowCount = -1;
		rowCount = sheet.getLastRowNum();
		return rowCount;
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
				log.debug("=============>行为空或不存在");
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
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING :
					strExcelCell = cell.getRichStringCellValue().toString();
				    break;
				case Cell.CELL_TYPE_NUMERIC:// 数字类型
					int dataFormat = cell.getCellStyle().getDataFormat();
					if(dateFormatList.contains(dataFormat)){
						strExcelCell = getDateValue(cell);
						log.debug(strExcelCell);
					} else {
						//System.out.print("dataFormat:"+dataFormat+"  ");
						double value = cell.getNumericCellValue();
						DecimalFormat format = new DecimalFormat("#############0.######");
						strExcelCell = format.format(value);
					}
					//log.debug(strExcelCell);
					break;
				case Cell.CELL_TYPE_FORMULA :
					strExcelCell = getFormulaValue(cell);
					
				    break;
				case Cell.CELL_TYPE_BLANK :
					strExcelCell = String.valueOf(cell.getRichStringCellValue().toString());
				    break;
				case Cell.CELL_TYPE_BOOLEAN :
					strExcelCell = String.valueOf(cell.getBooleanCellValue());
				    break;
				case Cell.CELL_TYPE_ERROR :
					strExcelCell = String.valueOf(cell.getErrorCellValue());
				    break;
				default:
					strExcelCell = "";
					break;
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
	public List<String[]> getRowList(int sheetNum,int forEachNum) {
		List<String[]> list = new ArrayList<String[]>();
		// 总行数
		int count = getRowCount(sheetNum);
		if(count>0){
			for (int i = forEachNum; i <= count; i++) {
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
	public List<String[]> getRowList(int sheetNum,int forEachNum,int toEachNum){
		List<String[]> list = new ArrayList<String[]>();
		for (int i = forEachNum; i <= toEachNum; i++) {
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
	public static String getDateValue(Cell cell){
		try{
			Date d= cell.getDateCellValue();
			return DEFAULT_DATE_FORMAT.format(d);
		}catch(Exception e){
			return cell.getStringCellValue();
		}
	}

	/**
	 * 返回公式的结果
	 * @param cell
	 * @return
	 */
	public abstract String getFormulaValue(Cell cell);

	public String getStringCellValueByCellValue(CellValue cell)
	{
		if(cell == null) return "";
		int cType = cell.getCellType();
		String res = null;
		switch (cType) {
		case Cell.CELL_TYPE_BOOLEAN:
			res = cell.getBooleanValue() +"" ;
			break;
		case Cell.CELL_TYPE_ERROR:
			res = null;
			break;
		case Cell.CELL_TYPE_NUMERIC:
			res = new DecimalFormat("############.##########").format(cell.getNumberValue());
			break;
		case Cell.CELL_TYPE_STRING:
			res = cell.getStringValue();
			break;
		}
		return res;
	} 
	protected static boolean isNotEmpty(String str) {
        return (str != null && str.trim().length() > 0);
    }
	
	public final static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public final static List<Integer> dateFormatList=new ArrayList<Integer>(){
		private static final long serialVersionUID = 1L;
	{
		add(14);
		add(20);
		add(31);
		add(32);
		add(57);
		add(58);
		//add(177);
//		add(181);
//		add(182);
//		add(185);
//		add(186);
//		add(187);
//		add(188);
//		add(189);
//		add(190);
//		add(191);
//		add(192);
//		add(193);
//		add(194);
//		add(195);
//		add(196);
//		add(197);
//		add(198);
//		add(199);
//		add(200);
//		add(201);
//		add(202);
//		add(203);
//		add(204);
//		add(205);
	}};
	
} 
