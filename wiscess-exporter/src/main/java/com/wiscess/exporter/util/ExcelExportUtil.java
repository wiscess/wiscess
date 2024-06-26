package com.wiscess.exporter.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.wiscess.common.utils.FileUtils;
import com.wiscess.exporter.dto.ThreadDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wiscess.exporter.dto.AssignedCell;
import com.wiscess.exporter.dto.AssignedSheet;
import com.wiscess.exporter.dto.ExportExcelParameter;
import com.wiscess.exporter.exception.ManagerException;
import com.wiscess.utils.StringUtils;

public class ExcelExportUtil {
	public static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 根据模板导出文件
	 */
	public static <K> String exportExcelByTemplate(ExportExcelParameter<K> para,String filename, HttpServletResponse res,
			List<K> data) {
		para.setData(data);
		return exportExcelByTemplate(para, filename, res);
	}


	/**
	 * 分段查询数据和导出数据
	 * @param <K>
	 * @param para
	 * @param filename
	 * @param data
	 * @param action
	 * @return
	 */
	public static <K> void exportSXSSFExcel(ExportExcelParameter<K> para, String filename,
											List<K> data, BiFunction<K,Integer,AssignedCell[]> action){

	}


	/**
	 * 根据模板导出文件,支持多个sheet的导出文件
	 */
	public static <K> String exportExcelByTemplate(ExportExcelParameter<K> para,String filename, HttpServletResponse res){
		try {
			if(res!=null){
				//输出到浏览器
				filename=filename.substring(filename.lastIndexOf("\\")+1);
				res.setContentType("APPLICATION/ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename* = UTF-8''"
						+ FileUtils.encodingFileName(filename));
				res.setHeader("FileName", FileUtils.encodingFileName(filename));
				res.setHeader("Access-Control-Expose-Headers", "FileName");
				ServletOutputStream os = res.getOutputStream();
				if(para.getSheets()==null) {
					ExcelExportUtil.exportSingleExcel(para, os);
				}else {
					ExcelExportUtil.exportMultiExcel(para, os);
				}
				os.flush();
				os.close();
				return null;
			}else{
				//输出到目录
				FileOutputStream fos = new FileOutputStream(filename);
				if(para.getSheets()==null) {
					ExcelExportUtil.exportSingleExcel(para, fos);
				}else {
					ExcelExportUtil.exportMultiExcel(para, fos);
				}
				fos.flush();
				fos.close();	
				return filename;
			}
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}
	}
	
	/**
	 * 导出数据，支持多个sheet的导出文件
	 * 
	 * @param para
	 * @param os
	 */
	public static <K> void exportMultiExcel(ExportExcelParameter<K> para, OutputStream os) {
		InputStream ins = null;
		try {
			// 读模版文件
			Workbook wb = null;
			ins=ExcelExportUtil.class.getResourceAsStream(para.getTemplateName());
			if(para.getTemplateName().endsWith(".xls")){
				wb=new HSSFWorkbook(ins);
			}else{
				wb = new SXSSFWorkbook(new XSSFWorkbook(ins),1000);
			}
			List<String> sheetNames = new ArrayList<String>();
			
			//记录当前sheet和要追加到的sheet
			Map<String,List<String>> mergedSheets=new HashMap<String,List<String>>();
			
			//先去掉不存在的Sheet页，只保留使用的Sheet
			List<String> usedSheetNameList=new ArrayList<String>();
			for (AssignedSheet<K> aSheet : para.getSheets()) {
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
			for (AssignedSheet<K> aSheet : para.getSheets()) {
				//
				Sheet sheet = null;
				String newSheetName="";
				if(aSheet.getTemplateSheetName()
						.equals(aSheet.getSheetName())){
					//导出SheetName和模板Sheet名一致
					sheet = getSheet(wb,aSheet.getTemplateSheetName());
					sheetNames.add(aSheet.getSheetName());
					newSheetName=aSheet.getSheetName();
				}else{
					sheet = cloneSheet(wb,wb.getSheetIndex(aSheet
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
				if (aSheet.getHighLightRow() != null) {
					templateHlDataRow = sheet.getRow(aSheet.getHighLightRow().getRow());
				} else {
					templateHlDataRow = templateDataRow;
				}
				if (templateHlDataRow == null)
					templateHlDataRow = sheet.getRow(rowNumber);

				//使用指定位置的样式
				List<AssignedCell> cellStyleList=aSheet.getCellStyleList();
				if(cellStyleList==null)
					cellStyleList=new ArrayList<>();
				// 输出数据
				if(aSheet.isUserMultiThread()) {
					//使用多线程导出
					outputDataThread(wb, sheet, templateDataRow, templateHlDataRow, para.getDataRow(),
							aSheet.getColumnWidths(),
							aSheet.getThreadCntList(),
							aSheet.getThreadListAction(),
							aSheet.getAction(),
							aSheet.getAssignedCells(),
							cellStyleList);
				}else {
					outputData(wb, sheet, templateDataRow, templateHlDataRow, aSheet.getDataRow(),
							aSheet.getColumnWidths(),
							aSheet.getData(),
							aSheet.getThreadListAction(),
							aSheet.getAction(),
							aSheet.getAssignedCells(),
							aSheet.isNeedCopyTemplateRow(),
							aSheet.isAutoHeight(),
							aSheet.getDataRowSpan(), aSheet.getTotalCol(),
							cellStyleList);
				}

				//处理合并sheet的记录
				if(StringUtils.isEmpty(aSheet.getAppendToSheet()) || 
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
            wb.setForceFormulaRecalculation(true);
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagerException("导出出错。", e);
		}
	}
	private static Sheet getSheet(Workbook wb,String sheetName) {
		if(wb instanceof SXSSFWorkbook) {
			return ((SXSSFWorkbook) wb).getXSSFWorkbook().getSheet(sheetName);
		}
		return wb.getSheet(sheetName);
	}
	private static Sheet cloneSheet(Workbook wb,Integer sheetIndex) {
		if(wb instanceof SXSSFWorkbook) {
			return ((SXSSFWorkbook) wb).getXSSFWorkbook().cloneSheet(sheetIndex);
		}
		return wb.cloneSheet(sheetIndex);
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
	public static <K> void exportSingleExcel(ExportExcelParameter<K> para, OutputStream os) {
		InputStream ins = null;
		try {
			// 读模版文件
			// 模版文件的绝对路径
			Workbook wb = null;
			Sheet sheet = null;
			ins=ExcelExportUtil.class.getResourceAsStream(para.getTemplateName());
			if(para.getTemplateName().endsWith(".xls")){
				wb=new HSSFWorkbook(ins);
				sheet = wb.getSheetAt(0);
			}else{
				wb = new SXSSFWorkbook(new XSSFWorkbook(ins),100);
				XSSFWorkbook xssfWorkbook = ((SXSSFWorkbook)wb).getXSSFWorkbook();
				sheet = xssfWorkbook.getSheetAt(0);
			}
			// 开始处理
			Row templateDataRow = sheet.getRow(para.getDataRow().getRow());

			Row templateHlDataRow = null;
			if (para.getHighLightRow() != null) {
				templateHlDataRow = sheet.getRow(para.getHighLightRow().getRow());
			} else {
				templateHlDataRow = templateDataRow;
			}

			//使用指定的样式
			List<AssignedCell> cellStyleList=para.getCellStyleList();
			if(cellStyleList==null)
				cellStyleList=new ArrayList<>();
			
			// 输出数据
			if(templateDataRow!=null)
				if(para.isUserMultiThread()) {
					//使用多线程导出
					outputDataThread(wb, sheet, templateDataRow, templateHlDataRow, para.getDataRow(),
							para.getColumnWidths(),
							para.getThreadCntList(),para.getThreadListAction(),para.getAction(),
							para.getAssignedCells(),
							cellStyleList);
				}else {
					outputData(wb, sheet, templateDataRow, templateHlDataRow, para.getDataRow(),
							para.getColumnWidths(),
							para.getData(),para.getThreadListAction(),para.getAction(),
							para.getAssignedCells(), para.isNeedCopyTemplateRow(),
							para.isAutoHeight(),
							para.getDataRowSpan(), para.getTotalCol(), cellStyleList);
				}
			//单页模式不处理合并sheet操作
            wb.setForceFormulaRecalculation(true);
			wb.write(os);
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}
	}

	/**
	 * 输出数据
	 * @param wb
	 * @param sheet
	 * @param templateDataRow
	 * @param templateHlDataRow
	 * @param dataRow
	 * @param data
	 * @param assignedCells
	 * @param isNeedCopyTemplateRow
	 * @param dataRowSpan
	 * @param totalCol
	 * @param cellStyleList
	 */
	private static <K> void outputData(Workbook wb, Sheet sheet, Row templateDataRow,
			Row templateHlDataRow, AssignedCell dataRow, 
			Integer[] columnWidths,
			List<K> data,
		    BiFunction<Object,Integer,List<K>> threadListAction,
		    BiFunction<K,Integer,AssignedCell[]> action,
			List<AssignedCell> assignedCells, boolean isNeedCopyTemplateRow,
			boolean autoHeight,
			int dataRowSpan, int totalCol, List<AssignedCell> cellStyleList) {

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
		CellStyle[] normalCs=new CellStyle[templateDataRow.getPhysicalNumberOfCells()];
		CellStyle[] highlightCs=new CellStyle[templateHlDataRow.getPhysicalNumberOfCells()];
		//指定位置的样式
		CellStyle[] assignedCs=new CellStyle[cellStyleList.size()];
		for(int i=0;i<cellStyleList.size();i++) {
			AssignedCell cs=cellStyleList.get(i);
			Cell cell=sheet.getRow(cs.getRow()).getCell(cs.getCol());
			CellStyle lastStyle=wb.createCellStyle();
			if(cell!=null) {
				lastStyle.cloneStyleFrom(cell.getCellStyle());
			}
			assignedCs[i]=lastStyle;
		}
		//记录各位置的样式
		// 创建所有的列
		for (int j = 0; j < templateDataRow.getPhysicalNumberOfCells(); j++) {
			if (templateDataRow != null){
				//记录样式
				CellStyle lastStyle=wb.createCellStyle();
				if(templateDataRow.getCell(j)!=null)
					lastStyle.cloneStyleFrom(templateDataRow.getCell(j).getCellStyle());
				normalCs[j]=lastStyle;
				CellStyle lastHlStyle=wb.createCellStyle();
				if(templateHlDataRow.getCell(j)!=null)
					lastHlStyle.cloneStyleFrom(templateHlDataRow.getCell(j).getCellStyle());
				highlightCs[j]=lastHlStyle;
			}
		}
		// 输出数据
		int index=0;
		if(data==null && threadListAction!=null) {
			//一次性获取数据
			data = threadListAction.apply("", index);
		}
		//单线程方式
		for (K oneData : data) {
			index++;
			AssignedCell[] rowData = null;
			if(oneData instanceof AssignedCell[]) {
				rowData = (AssignedCell[])oneData;
			}else {
				rowData = (AssignedCell[])action.apply(oneData,index);
			}
			if(totalCol==0)
				totalCol=rowData.length;
			// 如果是用复制行的模式，则调用copyRows复制出需要的内容行，否则，创建新行，并初始化每列数据
			if (isNeedCopyTemplateRow) {
				copySheetRows(sheet, rowNum, rowNum + dataRowSpan - 1, rowNumber);
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
						CellStyle lastStyle=normalCs[0];
						cell.setCellStyle(lastStyle);
					}
				}
			}
			//输出一行数据
			singleOneRowData(wb,sheet,rowNumber,rowData,autoHeight,normalCs,highlightCs,assignedCs);

			rowNumber += dataRowSpan;
		}
		// 输出指定位置的值
		assignedCellsData(assignedCells,wb,sheet);

		//重新计算公式
		adjustformula(sheet);
	}

	private static void assignedCellsData(List<AssignedCell> assignedCells, Workbook wb, Sheet sheet) {
		if (assignedCells != null && assignedCells.size() > 0) {
			for (AssignedCell acell : assignedCells) {
				if (acell.getValue() == null)
					continue;

				if (sheet.getRow(acell.getRow()) == null) {
					sheet.createRow(acell.getRow());
				}
				// 根据属性合并单元格
				if (acell.getRow() != acell.getRowEnd()
						|| acell.getCol() != acell.getColEnd()) {
					sheet.addMergedRegion(new CellRangeAddress(acell.getRow(), acell.getRowEnd(),
							acell.getCol(), acell.getColEnd()));
				}
				Cell assignCell = getCell(getRow(sheet,acell.getRow()), acell.getCol());

				if(!acell.isLocked()){
					CellStyle cs=wb.createCellStyle();
					cs.cloneStyleFrom(assignCell.getCellStyle());
					cs.setLocked(false);
					assignCell.setCellStyle(cs);
				}
				setCell(assignCell,acell);
			}
		}
	}

	/**
	 * 多线程方式导出文件，只处理基础的表格数据，不含图片、公式等操作
	 * @param <K>
	 * @param wb
	 * @param sheet
	 * @param templateDataRow
	 * @param templateHlDataRow
	 * @param dataRow
	 * @param columnWidths
	 * @param action
	 * @param assignedCells
	 * @param cellStyleList
	 */
	private static <K> void outputDataThread(Workbook wb, Sheet sheet, Row templateDataRow,
											 Row templateHlDataRow, AssignedCell dataRow,
											 Integer[] columnWidths,
											 List<ThreadDto> threadCntList,
											 BiFunction<Object,Integer,List<K>> threadListAction,
											 BiFunction<K,Integer,AssignedCell[]> action,
											 List<AssignedCell> assignedCells,
											 List<AssignedCell> cellStyleList) {
		//带格式的模板行
		int rowNum = templateDataRow.getRowNum();
		int colNum = templateDataRow.getLastCellNum();
		//设置列宽
		if(columnWidths!=null){
			for (int j = 0; j < columnWidths.length; j++) {
				sheet.setColumnWidth(j, columnWidths[j]*40);
			}
		}
		//设置样式
		CellStyle[] normalCs=new CellStyle[templateDataRow.getPhysicalNumberOfCells()];
		CellStyle[] highlightCs=new CellStyle[templateHlDataRow.getPhysicalNumberOfCells()];
		//指定位置的样式
		CellStyle[] assignedCs=new CellStyle[cellStyleList.size()];
		for(int i=0;i<cellStyleList.size();i++) {
			AssignedCell cs=cellStyleList.get(i);
			Cell cell=sheet.getRow(cs.getRow()).getCell(cs.getCol());
			CellStyle lastStyle=wb.createCellStyle();
			if(cell!=null) {
				lastStyle.cloneStyleFrom(cell.getCellStyle());
			}
			assignedCs[i]=lastStyle;
		}
		//记录各位置的样式
		// 创建所有的列
		for (int j = 0; j < templateDataRow.getPhysicalNumberOfCells(); j++) {
			//记录样式
			CellStyle lastStyle=wb.createCellStyle();
			if(templateDataRow.getCell(j)!=null)
				lastStyle.cloneStyleFrom(templateDataRow.getCell(j).getCellStyle());
			normalCs[j]=lastStyle;
			CellStyle lastHlStyle=wb.createCellStyle();
			if(templateHlDataRow.getCell(j)!=null)
				lastHlStyle.cloneStyleFrom(templateHlDataRow.getCell(j).getCellStyle());
			highlightCs[j]=lastHlStyle;
		}

		//每个线程处理的最大的数量
//		//采用多线程方式处理
		int threadNum=threadCntList.size();
		// 创建一个线程池
		ExecutorService exec = Executors.newFixedThreadPool(threadNum);
		// 定义一个任务集合
		List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();

		int totalCnt=0;
		// 确定每条线程的数据
		for (int i = 0; i < threadNum; i++) {
			//任务线程
			//每个线程的数据量已经预置到threadCntList中，根据数据计算当前显示的起始行
			final Integer threadIndex = i;
			final String threadName = threadCntList.get(i).getThreadName();
			final Integer threadStartIndex = totalCnt;
			totalCnt+=threadCntList.get(i).getThreadSize();
			tasks.add(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						//获取第i个线程的数据
						final List<K> taskData = threadListAction.apply(threadName, threadIndex);
						//如果没有数据，退出，停止后续线程的处理
						if(taskData==null || taskData.size()==0) {
							return 0;
						}

						//计算当前线程的序号
						int index = threadStartIndex;
						//计算当前线程的处理行号
						int rowNumber = rowNum+threadStartIndex;
						//多线程方式下，SXSSFWorkbook不支持多线程写入同一个sheet,因此创建新的sheet保存数据
						//rowNumber=0;
						for (K oneData : taskData) {
							index++;
							AssignedCell[] rowData = null;
							if(oneData instanceof AssignedCell[]) {
								rowData = (AssignedCell[])oneData;
							}else {
								rowData = (AssignedCell[])action.apply(oneData,index);
							}
							//输出一行数据
							singleOneRowData(wb,sheet,rowNumber,rowData,false,normalCs,highlightCs,assignedCs);
							rowNumber ++;
						}

						return 0;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
					}
					return null;
				}
			});
		}
		try {
			exec.invokeAll(tasks);

		}catch(Exception e) {
			e.printStackTrace();
		}
		// 关闭线程池
		exec.shutdown();

		// 输出指定位置的值
		assignedCellsData(assignedCells,wb,sheet);
		if (assignedCells != null && assignedCells.size() > 0) {
			for (AssignedCell acell : assignedCells) {
				if (acell.getValue() == null)
					continue;
				Row currRow = null;
				if (acell.getRow() > sheet.getLastRowNum()) {
					currRow = getRow(sheet,acell.getRow());
					for (int j = 0; j < colNum; j++) {
						getCell(currRow,j);
					}
				}
				currRow = getRow(sheet,acell.getRow());
				// 根据属性合并单元格
				if (acell.getRow() != acell.getRowEnd()
						|| acell.getCol() != acell.getColEnd()) {
					sheet.addMergedRegion(new CellRangeAddress(acell.getRow(), acell.getRowEnd(),
							acell.getCol(), acell.getColEnd()));
				}
				Cell cell = getCell(currRow, acell.getCol());

				setCell(cell,acell);
			}
		}
		//重新计算公式
		adjustformula(sheet);
	}
	private static void singleOneRowData(Workbook wb,Sheet sheet,int rowNumber,AssignedCell[] rowData,boolean autoHeight,
										 CellStyle[] normalCs,CellStyle[] highlightCs,CellStyle[] assignedCs){
		// 根据列总数处理所有列
		for (int k = 0; k < rowData.length; k++) {
			CellStyle lastStyle=null;
			AssignedCell acell = rowData[k];
			if (acell == null)
				continue;
			// 对特殊样式的处理
			if (acell.getDataStyle() == AssignedCell.DATA_STYLE_PHOTO) {
				Drawing<?> patriarch = sheet.createDrawingPatriarch();
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
				if (StringUtils.isNotEmpty((String) acell.getValue())) {
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

			Row row = getRow(sheet,rowNumber + acell.getRow());

			// 创建所有的列
			Cell cell = getCell(row,acell.getCol());

			//设置单元格样式
			lastStyle=getLastStyle(wb,acell, normalCs, highlightCs, assignedCs);
			if(lastStyle==null)
				lastStyle=cell.getCellStyle();
			//调整行高,变为自动换行
			if(autoHeight){
				lastStyle.setWrapText(true);
			}
			cell.setCellStyle(lastStyle);

			setCell(cell, acell);
		}
	}
	private static synchronized Row getRow(Sheet sheet, int rownum) {
		Row currRow = sheet.getRow(rownum);
		if(currRow==null)
			currRow = sheet.createRow(rownum);
		return currRow;
	}
	private static synchronized Cell getCell(Row row, int col) {
		Cell cell = row.getCell(col);
		if(cell==null)
			cell = row.createCell(col);
		return cell;
	}
	private static synchronized void setCell(Cell cell,AssignedCell acell) {
		if (acell.getDataStyle() == AssignedCell.DATA_STYLE_FORMULA) {
			//公式,根据当前行解析公式,如R[-2]C/R[-1]C
			try {
				String cformula=acell.getValue().toString();
				if(cformula.startsWith("="))
					cformula=cformula.substring(1);
				if(StringUtils.isNotEmpty(cformula))
					cell.setCellFormula(cformula);
				else
					cell.setCellFormula(cell.getCellFormula());
			}catch(Exception e) {
			}
			return;
		}
		Object value=acell.getValue();
		if (value == null) {
			cell.setCellValue("");
		} else {
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
	}
	/**
	 * 
	 * @param wb
	 * @param acell
	 * @param normalCs
	 * @param highlightCs
	 * @param assignedCs
	 * @return
	 */
	private static CellStyle getLastStyle(Workbook wb,AssignedCell acell,
										  CellStyle[] normalCs,CellStyle[] highlightCs,CellStyle[] assignedCs){
		// 使用样式
		CellStyle lockedStyle=null;
		Integer dataStyle=acell.getDataStyle();
		Integer cellStyle=acell.getCellStyle();
		int col=acell.getCol();
		boolean locked=acell.isLocked();
		
		if (dataStyle == AssignedCell.DATA_STYLE_NORMAL || dataStyle == AssignedCell.DATA_STYLE_FORMULA) {
			// 使用默认样式，公式也使用默认的样式
			lockedStyle=(normalCs[col]);
		} else if (dataStyle == AssignedCell.DATA_STYLE_HLROW) {
			// 使用高亮样式
			lockedStyle=(highlightCs[col]);
		} else if (dataStyle == AssignedCell.DATA_STYLE_ASSIGNED) {
			//使用指定的样式
			if(assignedCs.length==0) {
				//没有指定时，使用Normal
				lockedStyle=normalCs[0];
			}else if(cellStyle==null) {
				lockedStyle=assignedCs[0];
			}else {
				lockedStyle=assignedCs[cellStyle];
			}
		}
		//如果指定了单元格样式，强制覆盖
		if(cellStyle!=null) {
			lockedStyle=(assignedCs[cellStyle]);
		}
		
		if(!locked && lockedStyle!=null) {
			CellStyle lastStyle=wb.createCellStyle();
			lastStyle.cloneStyleFrom(lockedStyle);
			lastStyle.setLocked(false);
			return lastStyle;
		}
		return lockedStyle;
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
	 * 复制sheet中的行数据
	 * 
	 * @param sheet
	 * @param pStartRow
	 * @param pEndRow
	 * @param pPosition
	 */
	protected static void copySheetRows(Sheet sheet, int pStartRow, int pEndRow,
			int pPosition) {
		if ((pStartRow == -1) || (pEndRow == -1)) {
			return;
		}
		if (pStartRow == pPosition)
			return;
		copyRows(sheet, pStartRow, pEndRow, sheet, pPosition);
	}

	/**
	 * 将sheet中的所有行复制到目标sheet中，并删除原sheet
	 * 
	 * @param sourceSheet
	 * @param targetSheet
	 */
	protected static void copySheet(Sheet sourceSheet, Sheet targetSheet) {
		// 首先获取sourceSheet的最后一行行数
		int pStartRow = 0;
		int pEndRow = sourceSheet.getLastRowNum();

		// 获取targetSheet的最后一行
		int pPosition = targetSheet.getLastRowNum() + 3;

		// 拷贝行并填充数据
		copyRows(sourceSheet,pStartRow,pEndRow,targetSheet,pPosition);
	}


	/**
	 * 重新计算公式
	 */
	private static void adjustformula(Sheet sourceSheet) {
		Row sourceRow = null;
		Cell sourceCell = null;

		// 首先获取sourceSheet的最后一行行数
		int pEndRow = sourceSheet.getLastRowNum();

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
				if(sourceCell.getCellTypeEnum()==CellType.FORMULA){
					sourceCell.setCellFormula(sourceCell.getCellFormula());
				}
			}
		}
		
	}
	/**
	 * 拷贝合并的单元格
	 */
	private static void copyMergedRegions(Sheet sourceSheet,int pStartRow, int pEndRow,
			Sheet targetSheet,int pPosition){
		CellRangeAddress region = null;
		for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
			region = sourceSheet.getMergedRegion(i);
			CellRangeAddress region2 = null;
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				int targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
				int targetRowTo   = region.getLastRow()  - pStartRow + pPosition;
				region2 = new CellRangeAddress(targetRowFrom, targetRowTo,
						region.getFirstColumn(), region.getLastColumn());
				targetSheet.addMergedRegion(region2);
			}
		}
	}

	/**
	 * 复制行数据到targetSheet的指定位置
	 * @param sourceSheet
	 * @param pStartRow
	 * @param pEndRow
	 * @param targetSheet
	 * @param pPosition
	 */
	private static void copyRows(Sheet sourceSheet, int pStartRow, int pEndRow, Sheet targetSheet, int pPosition) {
		Row sourceRow = null;
		Row targetRow = null;
		Cell sourceCell = null;
		Cell targetCell = null;
		
		// 拷贝合并的单元格
		copyMergedRegions(sourceSheet,pStartRow,pEndRow,targetSheet,pPosition);
		// 拷贝行并填充数据
		for (int i = pStartRow; i <= pEndRow; i++) {
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
				CellType cType = sourceCell.getCellTypeEnum();
				switch (cType) {
				case BOOLEAN:
					targetCell.setCellValue(sourceCell.getBooleanCellValue());
					break;
				case ERROR:
					targetCell
							.setCellErrorValue(sourceCell.getErrorCellValue());
					break;
				case FORMULA:
					//调整公式
					int dataRowSpan=pPosition-pStartRow;
					targetCell.setCellFormula(adjustFormula(sourceCell.getCellFormula(),dataRowSpan));
					break;
				case NUMERIC:
					targetCell.setCellValue(sourceCell.getNumericCellValue());
					break;
				case STRING:
					targetCell.setCellValue(sourceCell.getStringCellValue());
					break;
				default:
					targetCell.setCellValue(sourceCell.getStringCellValue());
					break;
				}
			}
		}	
	}
}
