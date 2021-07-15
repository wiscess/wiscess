package com.wiscess.probe.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.probe.dto.DataSourceTestInfo;
import com.wiscess.utils.StringUtils;

/**
 * 数据源管理
 * @author wh
 *
 */
@Controller
public class ProbeControll {

	  /** The max rows. */
	  private int maxRows;

	  /** The rows per page. */
	  private int rowsPerPage;

	  /** The history size. */
	  private int historySize;
	  
	  /**是否加密传输*/
	  private int isEncrypt;
		/**
		 * 指定允许查看审计记录的用户
		 */
		@Value("${probe.username:admin}")
		private String probeUsernames;
	/**
	 * 页面入口
	 * @return
	 */
	@RequestMapping({"/probe"})
	public String list(HttpServletRequest request,Model model) {
		if(!isProbeUser(request)) {
			//获取不到userName，未登录或者未设置当前登录用户信息，跳到登录页
			//未发现
			return "redirect:/login";
		}
		HttpSession sess = request.getSession(false);

		DataSourceTestInfo sessData = null;

		if (sess != null) {
			sessData = (DataSourceTestInfo) sess.getAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR);
		}
		model
			.addAttribute(probeUsernames, sessData)
			.addAttribute("maxRows", sessData == null ? getMaxRows() : sessData.getMaxRows())
			.addAttribute("rowsPerPage",	sessData == null ? getRowsPerPage() : sessData.getRowsPerPage())
			.addAttribute("historySize",	sessData == null ? getHistorySize() : sessData.getHistorySize())
			.addAttribute("isEncrypt",	sessData == null ? isEncrypt() : sessData.isEncrypt())
			;
		return "probe/index";
//		return new ModelAndView("probe/datasourcetest")
//				.addObject("username","admin")
//				.addObject("ip",request.getRemoteAddr())
//				.addObject("maxRows", sessData == null ? getMaxRows() : sessData.getMaxRows())
//				.addObject("rowsPerPage",	sessData == null ? getRowsPerPage() : sessData.getRowsPerPage())
//				.addObject("historySize",	sessData == null ? getHistorySize() : sessData.getHistorySize())
//				.addObject("isEncrypt",	sessData == null ? isEncrypt() : sessData.isEncrypt())
//				;
	}
	/**
	 * 检查是否是允许的查看用户
	 * @param request
	 * @return
	 */
	private boolean isProbeUser(HttpServletRequest request) {
		HttpSession session=request.getSession();
 		//从session中读取指定参数
		String userName=getCurrentUser(session, "NOT_LOGIN_USER");
		return probeUsernames.indexOf(userName)!=-1;
	}
	/**
	 * 获取当前登录用户
	 * @param session
	 * @param defaultValue
	 * @return
	 */
	private String getCurrentUser(HttpSession session,String defaultValue) {
		String userName=(String)session.getAttribute("userName");
		if(StringUtils.isEmpty(userName)) {
			//从已登录的信息中查询
			SecurityContextImpl security=(SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
			if(security!=null) {
				userName=security.getAuthentication().getName();
			}
		}
		return StringUtils.isNotEmpty(userName)?userName:defaultValue;
	}
	  /**
	   * Gets the max rows.
	   *
	   * @return the max rows
	   */
	  public int getMaxRows() {
	    return maxRows;
	  }

	  /**
	   * Sets the max rows.
	   *
	   * @param maxRows the new max rows
	   */
	  @Value("1000")
	  public void setMaxRows(int maxRows) {
	    this.maxRows = maxRows;
	  }

	  /**
	   * Gets the rows per page.
	   *
	   * @return the rows per page
	   */
	  public int getRowsPerPage() {
	    return rowsPerPage;
	  }

	  /**
	   * Sets the rows per page.
	   *
	   * @param rowsPerPage the new rows per page
	   */
	  @Value("50")
	  public void setRowsPerPage(int rowsPerPage) {
	    this.rowsPerPage = rowsPerPage;
	  }

	  /**
	   * Gets the history size.
	   *
	   * @return the history size
	   */
	  public int getHistorySize() {
	    return historySize;
	  }

	  /**
	   * Sets the history size.
	   *
	   * @param historySize the new history size
	   */
	  @Value("30")
	  public void setHistorySize(int historySize) {
	    this.historySize = historySize;
	  }

	  /**
	   * Gets the replace pattern.
	   *
	   * @return the replace pattern
	   */
	  public int isEncrypt() {
	    return isEncrypt;
	  }

	  /**
	   * Sets the replace pattern.
	   *
	   * @param replacePattern the new replace pattern
	   */
	  @Value("1")
	  public void setEncrypt(int isEncrypt) {
	    this.isEncrypt = isEncrypt;
	  }

}
