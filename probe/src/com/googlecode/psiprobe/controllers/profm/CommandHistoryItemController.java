package com.googlecode.psiprobe.controllers.profm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CommandHistoryItemController extends AbstractController
{
	  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    int commandId = ServletRequestUtils.getIntParameter(request, "id", -1);

	    HttpSession sess = request.getSession(false);

	    if (sess != null) {
	      ProfmInfo sessData = (ProfmInfo)sess.getAttribute(ProfmInfo.PROFM_SESS_ATTR);

	      if (sessData != null) {
	        List<String> commandHistory = sessData.getCommandHistory();

	        if (commandHistory != null) {
	          try {
	            String commandLine = (String)commandHistory.get(commandId);
	            String dt=(new SimpleDateFormat("yyyyMMdd").format(new Date()));
	            commandLine=commandLine.replace("$[rootPath]", sessData.getRootPath());
		    	commandLine=commandLine.replace("$[dt]", dt);
	            response.setCharacterEncoding("UTF-8");
	            response.getWriter().print(commandLine);
	          } catch (IndexOutOfBoundsException e) {
	            this.logger.error("Cannot find a command history entry for history item id = " + commandId);
	          }
	        }
	      }
	    }

	    return null;
	  }

}
