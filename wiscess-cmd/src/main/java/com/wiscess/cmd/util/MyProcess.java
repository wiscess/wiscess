package com.wiscess.cmd.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义我的进程
 * @author wh
 *
 */
public class MyProcess extends Thread{

	/**
	 * 进程
	 */
	public Process process;

	/**
	 * 输入命令
	 */
	public PrintWriter writer;
	
	/**
	 * 输出结果
	 */
	public List<String> outList = new ArrayList<>();
	
	/**
	 * 最后一次命令时间
	 */
	public long lastActionTime;
	
	public boolean isWindows;
	
	/**
	 * 创建进程
	 */
	public MyProcess() {
		// 启动进程
		try {
	        String osName = System.getProperty("os.name");
	        isWindows = osName.toLowerCase().contains("windows");
	        ProcessBuilder processBuilder=new ProcessBuilder(isWindows?"cmd":"/bin/sh");
	        // 设置环境变量确保编码为UTF-8
			process = processBuilder
					.redirectErrorStream(true)
					.start();
			// 获取进程的输入流和输出流
			writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream(),(isWindows?"GBK":"UTF-8")), true);
			//记录最后一次操作时间
			lastActionTime = System.currentTimeMillis();
		} catch (Exception e) {
			System.out.println("ERROR:"+e.getMessage());
			e.printStackTrace();
		}
	}
	public void run() {
		try {
	        // 读取命令的输出和错误流
	        InputStream inputStream = process.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,(isWindows?"GBK":"UTF-8")));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	outList.add(line);
	        }
	        //结束
	        writer.close();
		} catch (Exception e) {
			System.out.println("ERROR:"+e.getMessage());
			e.printStackTrace();
		}
		
	}
	/**
	 * 执行命令
	 * @param command
	 * @return
	 */
	public List<String> exec(String command) {
		synchronized (outList) {
			outList.clear();
			writer.println(command);
			//记录最后一次操作时间
			lastActionTime = System.currentTimeMillis();
			int size=-1;
			while(outList.size()>size) {
				try {
					size=outList.size();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return outList;
		}
	}
	
	public void close() {
        writer.close();
	}
}