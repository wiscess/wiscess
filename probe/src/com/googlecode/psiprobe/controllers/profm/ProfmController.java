package com.googlecode.psiprobe.controllers.profm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ProfmController extends ParameterizableViewController {

	private int historySize = 0;
	private String rootPath;
	private String webPath;
	private List<String> defaultCommand;
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession sess = request.getSession();

	    ProfmInfo sessData = null;

	    if (sess != null) {
	      sessData = (ProfmInfo)sess.getAttribute(ProfmInfo.PROFM_SESS_ATTR);
	      
	      synchronized (sess) {
		      if (sessData == null) {
		    	  sessData = new ProfmInfo();
		    	  sess.setAttribute(ProfmInfo.PROFM_SESS_ATTR, sessData);
		      }

		      sessData.setHistorySize(historySize);
		      sessData.setRootPath(rootPath);
		      sessData.setWebPath(webPath);
		      //String dt=(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		      for(String commandLine:defaultCommand){
		    	  //commandLine=commandLine.replace("$[rootPath]", rootPath);
		    	  //commandLine=commandLine.replace("$[dt]", dt);
		    	  sessData.addCommandToHistory(commandLine);
		      }
		    }
	      
	    }

	    return new ModelAndView(getViewName())
	    		.addObject("historySize", String.valueOf(sessData == null ? getHistorySize() : sessData.getHistorySize()))
	    		.addObject("rootPath", String.valueOf(sessData == null ? getRootPath() : sessData.getRootPath()))
	    		.addObject("webPath", String.valueOf(sessData == null ? getWebPath() : sessData.getWebPath()));
	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public List<String> getDefaultCommand() {
		return defaultCommand;
	}

	public void setDefaultCommand(List<String> defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

}
