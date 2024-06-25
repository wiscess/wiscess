package com.wiscess.exporter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.http.HttpServletResponse;

import com.wiscess.common.utils.FileUtils;
import com.wiscess.exporter.dto.ThreadDto;
import org.springframework.util.FileCopyUtils;

import com.wiscess.exporter.dto.AssignedCell;
import com.wiscess.exporter.dto.ExportExcelParameter;
import com.wiscess.exporter.exception.ManagerException;

public class CSVExportUtil {
	
	/**
	 * 导出CSV文件
	 * @param <K>
	 * @param para
	 * @param filename
	 * @param res
	 * @return
	 */
	public static <K> String exportCSV(ExportExcelParameter<K> para,String filename, HttpServletResponse res) {
		try {

			//创建临时csv文件
	        File tempFile = createTempFile(para);

	        FileInputStream in = new FileInputStream(tempFile);
	        OutputStream out = null;
			if(res!=null){
				//输出到浏览器
				out = res.getOutputStream();
		        res.reset();
		        res.setContentType("application/csv");
				res.setHeader("Content-Disposition", "attachment; filename* = UTF-8''"
						+ FileUtils.encodingFileName(filename));
			}else{
				//输出到目录
				out = Files.newOutputStream(new File(filename).toPath());
			}
	        //为了保证excel打开csv不出现中文乱码
	        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

			FileCopyUtils.copy(in, out);
	        //删除临时文件
	        deleteFile(tempFile);
			return filename;
		} catch (Exception e) {
			throw new ManagerException("导出出错。", e);
		}
	}
		
	/**
     * 创建临时的csv文件
     *
     * @return
     * @throws IOException
     */
    public static <K> File createTempFile(ExportExcelParameter<K> para) throws IOException {
        File tempFile = File.createTempFile("vehicle", ".csv");
        CsvWriter csvWriter = new CsvWriter(tempFile.getCanonicalPath(), ',', StandardCharsets.UTF_8);
        // 写表头
        para.getTitleData().forEach(td->{
            try {
				csvWriter.writeRecord(td);
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
		// 输出数据
    	if(para.isUserMultiThread()) {
    		//多线程方式，查询
    		//采用多线程方式处理
    		List<ThreadDto> threadCntList = para.getThreadCntList();
    		
    		int threadNum=threadCntList.size();
    		// 创建一个线程池
            ExecutorService exec = Executors.newFixedThreadPool(threadNum);
            // 定义一个任务集合
            List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();

            //存储数据
            Map<String, List<K>> allData=new HashMap<>();
            
            // 确定每条线程的数据
            for (int i = 0; i < threadNum; i++) {
            	final Integer threadIndex = i;
            	final String threadName = threadCntList.get(i).getThreadName();
            	//任务线程，只查询数据  
    	        tasks.add(new Callable<Integer>() {
    				@Override
    				public Integer call() throws Exception {
    					try {
    						//获取第i个线程的数据
    			        	final List<K> taskData = para.getThreadListAction().apply(threadName, threadIndex);
    			        	//如果没有数据，退出，停止后续线程的处理
    			        	if(taskData==null || taskData.size()==0) {
    			        		return 0;
    			        	}
    			        	allData.put(threadName, taskData);
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
            
            int index = 0;
            for (int i = 0; i < threadNum; i++) {
            	final String threadName = threadCntList.get(i).getThreadName();
        		//输出每行数据
                for (K oneData : allData.get(threadName)) {
            		index++;
            		writeData(para,csvWriter,oneData,index);
                }
            }
    	}else {
    		List<K> data = para.getData();
    		if(para.getData()==null && para.getThreadListAction()!=null) {
    			//一次性获取数据
    			data = para.getThreadListAction().apply("", 0);
    		}
            int index = 0;
            for (K oneData : data) {
        		index++;
        		writeData(para,csvWriter,oneData,index);
            }
    	}
        
        csvWriter.close();
        return tempFile;
    }

    
    private static <K> void writeData(ExportExcelParameter<K> para,CsvWriter csvWriter,K oneData,int index) throws IOException {
    	AssignedCell[] rowData = null;
		if(oneData instanceof AssignedCell[]) {
			rowData = (AssignedCell[])oneData;
		}else {
			rowData = (AssignedCell[])para.getAction().apply(oneData,index);
		}
    	for (AssignedCell data : rowData) {
            //这里如果数据不是String类型，请进行转换
            csvWriter.write(data.getValue().toString(), true);
        }
        csvWriter.endRecord();
    }
    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }
}
