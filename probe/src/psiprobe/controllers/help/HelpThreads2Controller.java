package psiprobe.controllers.help;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class HelpThreads2Controller extends ParameterizableViewController{
	public HelpThreads2Controller()
	  {
	    setSupportedMethods(new String[] { HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.HEAD.name() });
	  }
}
