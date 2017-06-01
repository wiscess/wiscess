package psiprobe.controllers.profm;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CommandExecuteController extends AbstractController
{
	 protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			    throws Exception
	  {
		 String rootPath = ServletRequestUtils.getStringParameter(request, "rootPath");
		  String commandLine = ServletRequestUtils.getStringParameter(request, "commandLine", null);

		    if ((commandLine == null) || (commandLine.equals("")) || (commandLine.trim().equals(""))) {
		      return null;
		    }
		    
		    int historySize = ServletRequestUtils.getIntParameter(request, "historySize", 0);
		    
		    HttpSession sess = request.getSession();
		    ProfmInfo sessData = (ProfmInfo)sess.getAttribute(ProfmInfo.PROFM_SESS_ATTR);

		    synchronized (sess) {
		      if (sessData == null) {
		    	  sessData = new ProfmInfo();
		    	  sess.setAttribute(ProfmInfo.PROFM_SESS_ATTR, sessData);
		      }

		      sessData.setHistorySize(historySize);
		      sessData.setRootPath(rootPath);
		      sessData.addCommandToHistory(commandLine);
		    }

		    try{
				String msg=exec(commandLine);
				response.setCharacterEncoding("UTF-8");
	            response.getWriter().print(msg);
			}catch(Exception e){
			}
	    return null;
	  }
	  public static String exec(String command){
			Process p;
			String message="";
			try{
				p=Runtime.getRuntime().exec("cmd /c "+command);
				InputStream is=p.getInputStream();
				//得到相应的控制台输出信息
			    InputStreamReader bi = new InputStreamReader(is,"GBK");
			    BufferedReader br = new BufferedReader(bi);
			    
			    String msg =  br.readLine();      
			    while(msg != null){
			    	//将信息输出
			        message+=msg+"\r\n";
			        msg =  br.readLine();
			    }
			} catch (Exception e) {			
				System.out.println("ERROR:"+e.getMessage());
			      e.printStackTrace();
			}
			return message;
		}
	
}
