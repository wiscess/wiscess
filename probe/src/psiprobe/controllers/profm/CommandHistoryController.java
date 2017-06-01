package psiprobe.controllers.profm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import psiprobe.controllers.AbstractTomcatContainerController;

public class CommandHistoryController extends AbstractTomcatContainerController {
	
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession sess = request.getSession(false);
		  String rootPath = ServletRequestUtils.getStringParameter(request, "rootPath");
		List<String> commandHistory = null;

		if (sess != null) {
			ProfmInfo sessData = (ProfmInfo) sess.getAttribute(ProfmInfo.PROFM_SESS_ATTR);
			synchronized (sess) {
				if (sessData == null) {
					sessData = new ProfmInfo();
					sess.setAttribute(ProfmInfo.PROFM_SESS_ATTR, sessData);
				}

				sessData.setRootPath(rootPath);
			}
			if (sessData != null) {
				commandHistory = sessData.getCommandHistory();
			}
		}

		return new ModelAndView(getViewName(), "commandHistory", commandHistory);
	}

}
