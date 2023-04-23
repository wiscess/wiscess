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
package com.wiscess.probe.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.wiscess.utils.RSA_Encrypt;

/**
 * A class to store data source test tool related data in a session attribute.
 */
public class DataSourceTestInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant DS_TEST_SESS_ATTR. */
	public static final String DS_TEST_SESS_ATTR = "dataSourceTestData";

	/** The results. */
	private List<Map<String, String>> results;

	/** The query history. */
	private LinkedList<String> queryHistory = new LinkedList<>();

	/** The max rows. */
	private int maxRows;

	/** The rows per page. */
	private int rowsPerPage;

	/** The history size. */
	private int historySize;

	/** isEncrypt */
	private int isEncrypt;

	/**
	 * 当前的数据源
	 */
	private String dataSourceName;
	
	/**
	 * 当前执行的sql语句
	 */
	private String sql;

	public static DataSourceTestInfo refreshSession(HttpServletRequest request) {
		HttpSession sess = request.getSession(false);
		DataSourceTestInfo sessData = (DataSourceTestInfo) sess.getAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR);

		synchronized (sess) {
			if (sessData == null) {
				sessData = new DataSourceTestInfo();
				sess.setAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR, sessData);
			}
			int maxRows = ServletRequestUtils.getIntParameter(request, "maxRows", 0);
			int rowsPerPage = ServletRequestUtils.getIntParameter(request, "rowsPerPage", 0);
			int historySize = ServletRequestUtils.getIntParameter(request, "historySize", 0);
			int isEncrypt = ServletRequestUtils.getIntParameter(request, "isEncrypt", 1);
			String dataSourceName = ServletRequestUtils.getStringParameter(request, "dataSourceName", "dataSource");
			
			String sql = ServletRequestUtils.getStringParameter(request, isEncrypt == 1 ? "sql" : "sqlWithHtml", null);
			try {
				if (isEncrypt == 1) {
					sql = RSA_Encrypt.decrypt(sql, true);
				}

			    sessData.addQueryToHistory(sql);
			} catch (Exception e) {
				// e.printStackTrace();
				sql = null;
			}
			sessData.setMaxRows(maxRows);
			sessData.setRowsPerPage(rowsPerPage);
			sessData.setHistorySize(historySize);
			sessData.setEncrypt(isEncrypt);
			sessData.setDataSourceName(dataSourceName);
			sessData.setSql(sql);
			sess.setAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR, sessData);
		}
		return sessData;
	}
	
	/**
	 * Adds the query to history.
	 *
	 * @param sql the sql
	 */
	public void addQueryToHistory(String sql) {
		queryHistory.remove(sql);
		queryHistory.addFirst(sql);

		while (historySize >= 0 && queryHistory.size() > historySize) {
			queryHistory.removeLast();
		}
	}

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public List<Map<String, String>> getResults() {
		return results;
	}

	/**
	 * Sets the results.
	 *
	 * @param results the results
	 */
	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}

	/**
	 * Gets the query history.
	 *
	 * @return the query history
	 */
	public List<String> getQueryHistory() {
		return queryHistory;
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
	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	public int isEncrypt() {
		return isEncrypt;
	}

	public void setEncrypt(int isEncrypt) {
		this.isEncrypt = isEncrypt;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
