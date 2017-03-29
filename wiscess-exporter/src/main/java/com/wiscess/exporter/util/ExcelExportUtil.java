package com.wiscess.exporter.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wiscess.common.utils.StringUtil;
import com.wiscess.exporter.dto.AssignedCell;
import com.wiscess.exporter.dto.AssignedSheet;
import com.wiscess.exporter.dto.ExportExcelParameter;
import com.wiscess.exporter.exception.ManagerException;

public class ExcelExportUtil {
	public static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 根据模板导出文件
	 */
	public static Object exportExcelByTemplate(ExportExcelParameter para,String filename, HttpServletResponse res,
			List<AssignedCell[]> data){
		try {
			res.setContentType("APPLICATION/ms-excel");
			res.setHeader("Content-Disposition", "attachment; filename="
					+ new String(filename.getBytes("gbk"), "iso8859-1"));
			ServletOutputStream os = res.getOutputStream();
			ExcelExportUtil.export(para,os, data);
			os.flush();
			os.close();
			return null;
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}

	}

	/**
	 * 根据模板导出文件,支持多个sheet的导出文件
	 */
	public static Object exportExcelByTemplate(ExportExcelParameter para,String filename, HttpServletResponse res){
		try {
			res.setContentType("APPLICATION/ms-excel");
			res.setHeader("Content-Disposition", "attachment; filename="
					+ new String(filename.getBytes("gbk"), "iso8859-1"));
			ServletOutputStream os = res.getOutputStream();
			ExcelExportUtil.export(para,os);
			os.flush();
			os.close();
			return null;
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}
	}
	
	/**
	 * 从map对象中获取值
	 * @param cell
	 * @param obj
	 * @param propertyName
	 */
	private static void setValueFormMap(Cell cell,Map obj,String propertyName){
		if(obj.containsKey(propertyName)){
			Object returnValue=obj.get(propertyName);
			if(returnValue==null){
    			cell.setCellValue("");
    			return;
    		}
    		if(returnValue instanceof Date){
				cell.setCellValue(sdf.format(returnValue));
    		}else if(returnValue instanceof Integer){
				cell.setCellValue((Integer)returnValue);
    		}else{
    			cell.setCellValue(returnValue.toString());
    		}
		}else{
			cell.setCellValue("");
		}
	}

	@SuppressWarnings("unchecked")
	private static void setValueFromObj(Cell cell,Object obj,String propertyName){
		try {
			Class clazz=obj.getClass();
			Field f = clazz.getDeclaredField(propertyName);
			//根据字段名来获取字段   
	        if(f!=null){   
	        	//构建方法的后缀   
	        	String methodEnd = propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);   
	        	String getMethodName="get"+methodEnd;//构建get方法   
	        	Method getMethod=clazz.getMethod(getMethodName, new Class[]{});
	        	if(getMethod==null){
	        		getMethodName="is"+methodEnd;//构建get方法   
		        	getMethod=clazz.getMethod(getMethodName, new Class[]{});
	        	}
	        	if(getMethod!=null){
	        		Object returnValue = getMethod.invoke(obj,  new Class[]{});
	        		if(returnValue==null){
	        			cell.setCellValue("");
	        			return;
	        		}
	        		if(f.getType()==Date.class){
						cell.setCellValue(sdf.format(returnValue));
	        		}else{
	        			cell.setCellValue(returnValue.toString());
	        		}
	        	}
	        }   
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出数据，支持多个sheet的导出文件
	 * 
	 * @param para
	 * @param os
	 * @param session
	 * @param sheetMap
	 */
	public static void export(ExportExcelParameter para, OutputStream os) {
		InputStream ins = null;
		try {
			// 读模版文件
			Workbook wb = null;
			ins=ExcelExportUtil.class.getResourceAsStream(para.getTemplateName());
			if(para.getTemplateName().endsWith(".xls")){
				wb=new HSSFWorkbook(ins);
			}else{
				wb=new XSSFWorkbook(ins);
			}
			List<String> sheetNames = new ArrayList<String>();
			
			//记录当前sheet和要追加到的sheet
			Map<String,List<String>> mergedSheets=new HashMap<String,List<String>>();
			
			//先去掉不存在的Sheet页，只保留使用的Sheet
			List<String> usedSheetNameList=new ArrayList<String>();
			for (AssignedSheet aSheet : para.getSheets()) {
				if(!usedSheetNameList.contains(aSheet.getTemplateSheetName()))
					usedSheetNameList.add(aSheet.getTemplateSheetName());
			}
			int j = 0;
			while (j < wb.getNumberOfSheets()) {
				String sheetName = wb.getSheetName(j);
				if (usedSheetNameList.contains(sheetName)) {
					j++;
					continue;
				} else {
					wb.removeSheetAt(j);
				}
			}
			// 创建sheet，并复制模板页中的所有行
			for (AssignedSheet aSheet : para.getSheets()) {
				//
				Sheet sheet = null;
				String newSheetName="";
				if(aSheet.getTemplateSheetName()
						.equals(aSheet.getSheetName())){
					//导出SheetName和模板Sheet名一致
					sheet = wb.getSheet(aSheet.getTemplateSheetName());
					sheetNames.add(aSheet.getSheetName());
					newSheetName=aSheet.getSheetName();
				}else{
					sheet = wb.cloneSheet(wb.getSheetIndex(aSheet
						.getTemplateSheetName()));
					String sName=aSheet.getSheetName();
					//System.out.println(sName+":"+sName.length());
					sName=subSheetName(sheetNames,sName);
					newSheetName=sName;
					wb.setSheetName(wb.getNumberOfSheets() - 1, sName);
					sheetNames.add(sName);
				}
				if(aSheet.isHidden()){
					//该页要隐藏
					wb.setSheetHidden(wb.getSheetIndex(sheet), true);
				}
				//没有数据，略过处理
				if(aSheet.getData()==null)
					continue;
				// 开始处理
				int rowNumber = aSheet.getDataRow().getRow();
				Row templateDataRow = sheet.getRow(rowNumber);
				Row templateHlDataRow = null;
				int templateHlDataCol = 0;
				if (aSheet.getHighLightRow() != null) {
					templateHlDataRow = sheet.getRow(aSheet.getHighLightRow().getRow());
					templateHlDataCol = aSheet.getHighLightRow().getCol();
				} else {
					templateHlDataRow = templateDataRow;
				}
				if (templateHlDataRow == null)
					templateHlDataRow = sheet.getRow(rowNumber);

				// 输出数据
				outputData(wb, sheet, templateDataRow, templateHlDataRow, aSheet.getDataRow(),
						aSheet.getColumnWidths(),
						aSheet.getData(), 
						aSheet.getAssignedCells(), 
						aSheet.isNeedCopyTemplateRow(),
						aSheet.isAutoHeight(),
						aSheet.getDataRowSpan(), aSheet.getTotalCol(),
						templateHlDataCol);
				
				//处理合并sheet的记录
				if(StringUtil.isEmpty(aSheet.getAppendToSheet()) || 
						!mergedSheets.containsKey(aSheet.getAppendToSheet())){
					//如果该sheet没有指定追加目标，则该sheet作为一个被保留的sheet，建立一个空列表
					List<String> list=new ArrayList<String>();
					list.add(newSheetName);
					mergedSheets.put(aSheet.getSheetName(),list);
				}else{
					//如果该sheet指定了要追加到某个sheet上，则先取出目标sheet的列表，并追加到sheet中
					//先检查目标sheet是否存在
					if(mergedSheets.containsKey(aSheet.getAppendToSheet())){
						List<String> list=mergedSheets.get(aSheet.getAppendToSheet());
						list.add(newSheetName);
					}
				}
			}
			
			//合并sheet
			for(String sn:mergedSheets.keySet()){
				List<String> slist=mergedSheets.get(sn);
				//列表中第一个为目标sheet，
				Sheet targetSheet=wb.getSheet(slist.get(0));
				//其他为sourceSheet
				for(int i=1;i<slist.size();i++){
					Sheet sourceSheet = wb.getSheet(slist.get(i));
					copySheet(sourceSheet, targetSheet);
					adjustformula(targetSheet);
					wb.removeSheetAt(wb.getSheetIndex(slist.get(i)));
				}
			}

			
			// 删除文件中除了有所需数据的sheet页外的所有sheet，包括模板sheet和空白sheet
			int i = 0;
			while (i < wb.getNumberOfSheets()) {
				String sheetName = wb.getSheetName(i);
				if (sheetNames.contains(sheetName)) {
					i++;
					continue;
				} else {
					wb.removeSheetAt(i);
				}
			}
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagerException("导出出错。", e);
		}
	}
	private static String subSheetName(List<String> sheetNames, String sName){
		String result=sName;
		if((result.length()<=31 && !sheetNames.contains(result))){
			//不包括并且长度不超长
			result=sName;
		}else if(result.length()<=27 && sheetNames.contains(result)){
			//已经存在，并且长度小于27，可以直接加两位序号
			int i=1;
			while(sheetNames.contains(result)){
				result=sName+"("+i+")";
				i++;
			}
		}else{
			boolean flag=true;
			while(flag){
				sName=sName.substring(0,sName.length()-1);
				result=sName+"...";
				int i=1;
				while(sheetNames.contains(result)){
					result=result+"("+i+")";
					i++;
				}
				if(result.length()<=31)
					flag=false;
			}
		}
			
		return result;
	}
	/**
	 * 单sheet页的导出处理
	 */
	public static void export(ExportExcelParameter para, OutputStream os,
			List<AssignedCell[]> data) {
		InputStream ins = null;
		try {
			// 读模版文件
			// 模版文件的绝对路径
			Workbook wb = null;
			ins=ExcelExportUtil.class.getResourceAsStream(para.getTemplateName());
			if(para.getTemplateName().endsWith(".xls")){
				wb=new HSSFWorkbook(ins);
			}else{
				wb=new XSSFWorkbook(ins);
			}
			Sheet sheet = wb.getSheetAt(0);
			// 开始处理
			Row templateDataRow = sheet.getRow(para.getDataRow().getRow());

			Row templateHlDataRow = null;
			int hldatacol = 0;
			if (para.getHighLightRow() != null) {
				templateHlDataRow = sheet.getRow(para.getHighLightRow().getRow());
				hldatacol = para.getHighLightRow().getCol();
			} else {
				templateHlDataRow = templateDataRow;
			}

			// 输出数据
			if(templateDataRow!=null)
				outputData(wb, sheet, templateDataRow, templateHlDataRow, para.getDataRow(), 
					para.getColumnWidths(),
					data,
					para.getAssignedCells(), para.isNeedCopyTemplateRow(), 
					para.isAutoHeight(),
					para.getDataRowSpan(), para.getTotalCol(), hldatacol);

			//单页模式不处理合并sheet操作
			wb.write(os);
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}
	}

	/**
	 * 输出数据
	 * @param wb
	 * @param sheet
	 * @param datarow
	 * @param hldatarow
	 * @param dataRow
	 * @param data
	 * @param assignedCells
	 * @param isNeedCopyTemplateRow
	 * @param dataRowSpan
	 * @param totalCol
	 * @param hldatacol
	 */
	private static void outputData(Workbook wb, Sheet sheet, Row templateDataRow,
			Row templateHlDataRow, AssignedCell dataRow, 
			Integer[] columnWidths,
			List<AssignedCell[]> data,
			List<AssignedCell> assignedCells, boolean isNeedCopyTemplateRow,
			boolean autoHeight,
			int dataRowSpan, int totalCol, int hldatacol) {
		Drawing patriarch = sheet.createDrawingPatriarch();

		int rowNumber = 0;
		int rowNum = 0;
		if (templateDataRow != null)
			rowNumber = templateDataRow.getRowNum();
		else
			rowNumber = dataRow.getRow();
		rowNum = rowNumber;

		Row currRow = null;
		if(columnWidths!=null){
			//设置列宽
			for (int j = 0; j < totalCol; j++) {
				sheet.setColumnWidth(j, columnWidths[j]*40);
			}
		}
		// 输出数据
		for (AssignedCell[] rowData : data) {
			if(totalCol==0)
				totalCol=rowData.length;
			// 如果是用复制行的模式，则调用copyRows复制出需要的内容行，否则，创建新行，并初始化每列数据
			if (isNeedCopyTemplateRow) {
				copyRows(sheet, rowNum, rowNum + dataRowSpan - 1, rowNumber,
						totalCol);
			} else {
				// 创建多行，把所有列都创建出来，并使用样式处理
				for (int i = 0; i < dataRowSpan; i++) {
					currRow=sheet.getRow(rowNumber + i);
					if(currRow==null)
						currRow = sheet.createRow(rowNumber + i);
					// 设置行高，模板行存在并且未指定自动行高，则使用模板行的行高
					if (templateDataRow != null && !autoHeight){
						currRow.setHeight(templateDataRow.getHeight());
					}
					// 创建所有的列
					for (int j = 0; j < totalCol; j++) {
						Cell cell = currRow.getCell(j);
						if(cell==null)
							cell = currRow.createCell(j);
						if (templateDataRow != null){
							if(dataRow.getUseStyle()==AssignedCell.CELL_STYLE_HLROW){
								cell.setCellStyle(templateHlDataRow.getCell(hldatacol).getCellStyle());
								if(j>hldatacol && columnWidths==null)
									sheet.setColumnWidth(j, sheet.getColumnWidth(hldatacol));							
							}else{
								cell.setCellStyle(templateDataRow.getCell(j).getCellStyle());
							}
						}
					}
				}
			}

			int lastUserStyle=0;
			// 根据列总数处理所有列
			for (int k = 0; k < rowData.length; k++) {
				CellStyle lastStyle=null;
				AssignedCell acell = rowData[k];
				if (acell == null)
					continue;
				// 对特殊样式的处理
				
				if (acell.getUseStyle() == AssignedCell.CELL_STYLE_PHOTO) {
					// 写照片
					// 处理照片
					ClientAnchor anchor = null;
					if(wb instanceof HSSFWorkbook){
						anchor = new HSSFClientAnchor(0, 0, 0, 0,
							(short) acell.getCol(), rowNumber + acell.getRow(),
							(short) (acell.getColEnd() + 1), rowNumber
									+ acell.getRowEnd() + 1);
					}else{
						anchor = new XSSFClientAnchor(0, 0, 0, 0,
								(short) acell.getCol(), rowNumber + acell.getRow(),
								(short) (acell.getColEnd() + 1), rowNumber
										+ acell.getRowEnd() + 1);
					}
					anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
					// 2008-09-19 
					if (StringUtil.isNotEmpty((String) acell.getValue())) {
						if (((String) acell.getValue()).startsWith("http")) {
							try {
								patriarch.createPicture(anchor, loadPicture(
												new URL((String) acell.getValue()), wb));
							} catch (MalformedURLException e) {
							}
						} else {
							patriarch.createPicture(anchor, loadPicture(
									(String) acell.getValue(), wb));
						}
					}
					continue;
				}

				// 根据属性合并单元格
				if (acell.getRow() != acell.getRowEnd()
						|| acell.getCol() != acell.getColEnd())
					sheet.addMergedRegion(new CellRangeAddress(rowNumber
							+ acell.getRow(), rowNumber + acell.getRowEnd(),
							acell.getCol(), acell.getColEnd()));

				Row row = sheet.getRow(rowNumber + acell.getRow());
				if (row == null)
					row = sheet.createRow(rowNumber + acell.getRow());
				Cell cell = row.getCell(acell.getCol());
				if (cell == null)
					cell = row.createCell(acell.getCol());
				// 根据类型设置
				int cType = HSSFCell.CELL_TYPE_STRING;
				
				Object value = acell.getValue();
				if (acell.getUseStyle() == AssignedCell.CELL_STYLE_FORMULA) {
					//公式,根据当前行解析公式,如R[-2]C/R[-1]C
					//Pattern p
					try {
						String cformula=value.toString();
						if(cformula.startsWith("="))
							cformula=cformula.substring(1);
						if(StringUtil.isNotEmpty(cformula))
							cell.setCellFormula(cformula);
						else
							cell.setCellFormula(cell.getCellFormula());
						
						lastStyle=getLastStyle(wb,lastUserStyle, templateDataRow, templateHlDataRow, acell.getCol(), hldatacol,acell.isLocked());
						if(lastStyle!=null)
							cell.setCellStyle(lastStyle);
					}catch(Exception e) {
					}
					continue;
				}
				if (value == null) {
					cell.setCellValue("");
				} else {
					if (value instanceof Integer || value instanceof Double) {
						cType = HSSFCell.CELL_TYPE_NUMERIC;
						try {
							cell.setCellValue(new BigDecimal(value.toString())
									.doubleValue());
						} catch (Exception e) {
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(value.toString());
						}
					} else {
						cType = HSSFCell.CELL_TYPE_STRING;
						cell.setCellType(cType);
						cell.setCellValue(value.toString());
					}
				}
				
				lastStyle=getLastStyle(wb,acell.getUseStyle(), templateDataRow, templateHlDataRow, acell.getCol(), hldatacol,acell.isLocked());
				lastUserStyle=acell.getUseStyle();
				if(lastStyle==null)
					lastStyle=cell.getCellStyle();
				//调整行高,变为自动换行
				if(autoHeight){
					lastStyle.setWrapText(true);
				}
				cell.setCellStyle(lastStyle);
			}
			rowNumber += dataRowSpan;
		}
		// 输出指定位置的值
		if (assignedCells != null && assignedCells.size() > 0) {
			for (AssignedCell cell : assignedCells) {
				if (cell.getValue() == null)
					continue;
				if (cell.getRow() > sheet.getLastRowNum()) {
					currRow = sheet.createRow(cell.getRow());
					for (int j = 0; j < totalCol; j++) {
						currRow.createCell(j);
					}
				}
				if (sheet.getRow(cell.getRow()) == null) {
					sheet.createRow(cell.getRow());
				}
				// 根据属性合并单元格
				if (cell.getRow() != cell.getRowEnd()
						|| cell.getCol() != cell.getColEnd()) {
					sheet.addMergedRegion(new CellRangeAddress(cell.getRow(), cell.getRowEnd(),
							cell.getCol(), cell.getColEnd()));
				}
				Cell assignCell = sheet.getRow(cell.getRow()).getCell(
						cell.getCol());
				if (assignCell == null) {
					assignCell = sheet.getRow(cell.getRow()).createCell(
							cell.getCol());
				}
				if(cell.isLocked()==false){
					CellStyle cs=wb.createCellStyle();
					cs.cloneStyleFrom(assignCell.getCellStyle());
					cs.setLocked(false);
					assignCell.setCellStyle(cs);
				}
				assignCell.setCellValue(cell.getValue().toString());
			}
		}
		//重新计算公式
		adjustformula(sheet);
	}

	/**
	 * 根据样式定义，获取使用的样式
	 * @param useStyle
	 * @param templateDataRow
	 * @param templateHlDataRow
	 * @param col
	 * @param hldatacol
	 * @return
	 */
	private static CellStyle getLastStyle(Workbook wb,int useStyle,Row templateDataRow,Row templateHlDataRow,int col,int hldatacol,boolean locked){
		// 使用样式
		CellStyle lastStyle=null;
		if (useStyle == AssignedCell.CELL_STYLE_NORMAL) {
			// 使用默认样式
			if(locked)
				lastStyle=(templateDataRow.getCell(col).getCellStyle());
			else {
				lastStyle=wb.createCellStyle();
				lastStyle.cloneStyleFrom(templateDataRow.getCell(col).getCellStyle());
			}
		} else if (useStyle == AssignedCell.CELL_STYLE_HLROW_COL) {
			// 使用数据样式
			if(locked)
				lastStyle=(templateHlDataRow.getCell(hldatacol).getCellStyle());
			else{
				lastStyle=wb.createCellStyle();
				lastStyle.cloneStyleFrom(templateHlDataRow.getCell(hldatacol).getCellStyle());
			}
		} else if (useStyle == AssignedCell.CELL_STYLE_HLROW) {
			// 使用高亮样式
			if(locked)
				lastStyle=(templateHlDataRow.getCell(col).getCellStyle());
			else{
				lastStyle=wb.createCellStyle();
				lastStyle.cloneStyleFrom(templateHlDataRow.getCell(col).getCellStyle());
			}
		}
		if(!locked)
			lastStyle.setLocked(false);

		return lastStyle;
	}
	/**
	 * 处理照片
	 * 
	 * @param filePath
	 * @param wb
	 * @return
	 */
	private static int loadPicture(URL filePath, Workbook wb) {
		int result = 0;
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			java.awt.image.BufferedImage bufferImg = ImageIO.read(filePath);
			ImageIO.write(bufferImg, "jpg", byteArrayOut);
			result = wb
					.addPicture(
							byteArrayOut.toByteArray(),
							Workbook.PICTURE_TYPE_JPEG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 处理照片
	 * 
	 * @param filePath
	 * @param wb
	 * @return
	 */
	private static int loadPicture(String filePath, Workbook wb) {
		int result = 0;
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			File url = new File(filePath);
			java.awt.image.BufferedImage bufferImg = ImageIO.read(url);
			ImageIO.write(bufferImg, "jpg", byteArrayOut);
			result = wb
					.addPicture(
							byteArrayOut.toByteArray(),
							Workbook.PICTURE_TYPE_JPEG);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(filePath);
		}
		return result;
	}

	/**
	 * 复制sheet中的行数据
	 * 
	 * @param sheet
	 * @param pStartRow
	 * @param pEndRow
	 * @param pPosition
	 */
	protected static void copyRows(Sheet sheet, int pStartRow, int pEndRow,
			int pPosition, int colTotal) {
		Row sourceRow = null;
		Row targetRow = null;
		Cell sourceCell = null;
		Cell targetCell = null;
		CellRangeAddress region = null;

		if ((pStartRow == -1) || (pEndRow == -1)) {
			return;
		}
		if (pStartRow == pPosition)
			return;
		// 拷贝合并的单元格
		int numregions = sheet.getNumMergedRegions();
		for (int i = 0; i < numregions; i++) {
			region = sheet.getMergedRegion(i);
			CellRangeAddress region2 = null;
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				int targetRowFrom = region.getFirstRow() - pStartRow
						+ pPosition;
				int targetRowTo = region.getLastRow() - pStartRow + pPosition;
				region2 = new CellRangeAddress(targetRowFrom, targetRowTo,
						region.getFirstColumn(), region.getLastColumn());
				sheet.addMergedRegion(region2);
			}
		}
		// 拷贝行并填充数据
		for (int i = pStartRow; i <= pEndRow; i++) {
			sourceRow = sheet.getRow(i);
			if (sourceRow == null) {
				continue;
			}
			targetRow = sheet.createRow(i - pStartRow + pPosition);
			targetRow.setHeight(sourceRow.getHeight());
			for (int j = sourceRow.getFirstCellNum(); j < colTotal; j++) {
				sourceCell = sourceRow.getCell(j);
				if (sourceCell == null) {
					continue;
				}
				targetCell = targetRow.createCell(j);
				targetCell.setCellStyle(sourceCell.getCellStyle());
				int cType = sourceCell.getCellType();
				switch (cType) {
				case Cell.CELL_TYPE_BOOLEAN:
					targetCell.setCellValue(sourceCell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_ERROR:
					targetCell
							.setCellErrorValue(sourceCell.getErrorCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					//调整公式
					int dataRowSpan=pPosition-pStartRow;
					targetCell.setCellFormula(adjustFormula(sourceCell.getCellFormula(),dataRowSpan));
					break;
				case Cell.CELL_TYPE_NUMERIC:
					targetCell.setCellValue(sourceCell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					targetCell.setCellValue(sourceCell.getStringCellValue());
					break;
				}
			}
		}
	}

	/**
	 * 调整公式
	 * @param cellFormula
	 * @param dataRowSpan
	 * @return
	 */
	private static String adjustFormula(String cellFormula, int dataRowSpan) {
		Pattern   p   =   Pattern.compile("[a-zA-Z]+[\\d]*");
		Pattern   p2   =   Pattern.compile("[\\d]+");
		String str=cellFormula;
	    Matcher   m   =   p.matcher(str);
	    Matcher   m2;
	    while(m.find()){ 
	    	String s=m.group();
	    	m2=p2.matcher(s);
	    	
	    	while(m2.find()){
	    		String newS=m2.replaceAll(Integer.toString(Integer.parseInt(m2.group())+dataRowSpan));
	    		str=str.replaceFirst(m.group(), newS);
	    	}
	    } 
		return str;
	}
	/**
	 * 将sheet中的所有行复制到目标sheet中，并删除原sheet
	 * 
	 * @param oriSheet
	 * @param descSheet
	 */
	protected static void copySheet(Sheet sourceSheet, Sheet targetSheet) {
		Row sourceRow = null;
		Row targetRow = null;
		Cell sourceCell = null;
		Cell targetCell = null;
		CellRangeAddress region = null;

		// 首先获取sourceSheet的最后一行行数
		int pStartRow = 0;
		int pEndRow = sourceSheet.getLastRowNum();

		// 获取targetSheet的最后一行
		int pPosition = targetSheet.getLastRowNum() + 3;

		// 拷贝合并的单元格
		for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
			region = sourceSheet.getMergedRegion(i);
			CellRangeAddress region2 = null;
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				int targetRowFrom = region.getFirstRow() - pStartRow
						+ pPosition;
				int targetRowTo = region.getLastRow() - pStartRow + pPosition;
				region2 = new CellRangeAddress(targetRowFrom, targetRowTo,
						region.getFirstColumn(), region.getLastColumn());
				targetSheet.addMergedRegion(region2);
			}
		}
		// 拷贝行并填充数据
		for (int i = 0; i <= pEndRow; i++) {
			sourceRow = sourceSheet.getRow(i);
			if (sourceRow == null) {
				continue;
			}
			targetRow = targetSheet.createRow(i - pStartRow + pPosition);
			targetRow.setHeight(sourceRow.getHeight());
			for (int j = sourceRow.getFirstCellNum(); j < sourceRow.getLastCellNum(); j++) {
				sourceCell = sourceRow.getCell(j);
				if (sourceCell == null) {
					continue;
				}
				targetCell = targetRow.createCell(j);
				targetCell.setCellStyle(sourceCell.getCellStyle());
				int cType = sourceCell.getCellType();
				targetCell.setCellType(cType);
				switch (cType) {
				case Cell.CELL_TYPE_BOOLEAN:
					targetCell.setCellValue(sourceCell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_ERROR:
					targetCell
							.setCellErrorValue(sourceCell.getErrorCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					int offset=targetCell.getRowIndex()-sourceCell.getRowIndex();
					targetCell.setCellFormula(adjustFormula(sourceCell.getCellFormula(),offset));
					break;
				case Cell.CELL_TYPE_NUMERIC:
					targetCell.setCellValue(sourceCell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					targetCell.setCellValue(sourceCell.getStringCellValue());
					break;
				}
			}
		}
	}

	/**
	 * 重新计算公式
	 */
	private static void adjustformula(Sheet sourceSheet) {
		Row sourceRow = null;
		Cell sourceCell = null;

		// 首先获取sourceSheet的最后一行行数
		int pEndRow = sourceSheet.getLastRowNum();

		// 拷贝行并填充数据
		for (int i = 0; i <= pEndRow; i++) {
			sourceRow = sourceSheet.getRow(i);
			if (sourceRow == null) {
				continue;
			}
			for (int j = sourceRow.getFirstCellNum(); j < sourceRow
					.getPhysicalNumberOfCells(); j++) {
				sourceCell = sourceRow.getCell(j);
				if (sourceCell == null) {
					continue;
				}
				int cType = sourceCell.getCellType();
				switch (cType) {
				case Cell.CELL_TYPE_FORMULA:
					sourceCell.setCellFormula(sourceCell.getCellFormula());
					break;
				}
			}
		}
		
	}

}
