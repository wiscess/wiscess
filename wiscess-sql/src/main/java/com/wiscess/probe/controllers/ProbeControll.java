package com.wiscess.probe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.probe.dto.DataSourceTestInfo;
import com.wiscess.probe.webapp.util.NoServiceUtil;

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
	  
	@Autowired
	private ApplicationContext context;
	/**
	 * 页面入口
	 * @return
	 */
	@RequestMapping({"/probe"})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		HttpSession sess = request.getSession(false);

		DataSourceTestInfo sessData = null;

		//自动获取datasource
		String[] dataSourceNames = context.getBeanNamesForType(DataSource.class);
		
		
		if (sess != null) {
			sessData = (DataSourceTestInfo) sess.getAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR);
		}
		String ip= NoServiceUtil.getIpAddress(request);
		
		model
				.addAttribute("ip", ip)
				.addAttribute("maxRows", sessData == null ? getMaxRows() : sessData.getMaxRows())
				.addAttribute("rowsPerPage",	sessData == null ? getRowsPerPage() : sessData.getRowsPerPage())
				.addAttribute("historySize",	sessData == null ? getHistorySize() : sessData.getHistorySize())
				.addAttribute("isEncrypt",	sessData == null ? isEncrypt() : sessData.isEncrypt())
				.addAttribute("dataSourceName",	sessData == null ? "dataSource" : sessData.getDataSourceName())
				.addAttribute("dataSourceNames",dataSourceNames)
				;
		return "probe/index";
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
	   * @return isEncrypt
	   */
	  public int isEncrypt() {
	    return isEncrypt;
	  }

	  /**
	   * @param isEncrypt
	   */
	  @Value("1")
	  public void setEncrypt(int isEncrypt) {
	    this.isEncrypt = isEncrypt;
	  }

}
