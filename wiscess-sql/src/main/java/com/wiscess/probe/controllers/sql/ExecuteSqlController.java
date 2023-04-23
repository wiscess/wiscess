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
package com.wiscess.probe.controllers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.wiscess.probe.dto.DataSourceTestInfo;

/**
 * Executes an SQL query through a given datasource to test database
 * connectivity. Displays results returned by the query.
 */
@Controller
public class ExecuteSqlController {

	String viewName = "probe/ajax/sql/recordset";
	@Autowired
	private ApplicationContext context;

	@RequestMapping(path = "/sql/recordset.ajax")
	protected ModelAndView handleContext(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 刷新session数据
		DataSourceTestInfo sessData = DataSourceTestInfo.refreshSession(request);

		if (sessData.getSql() == null || sessData.getSql().trim().isEmpty()) {
			request.setAttribute("errorMessage", "需要 SQL 查询文本");

			return new ModelAndView(getViewName());
		}

		DataSource dataSource = context.getBean(sessData.getDataSourceName(), DataSource.class);

		if (dataSource == null) {
			request.setAttribute("errorMessage", "数据源不存在");
		} else {
			List<Map<String, String>> results = null;
			int rowsAffected = 0;

			try {
				try (Connection conn = dataSource.getConnection()) {
					conn.setAutoCommit(true);

					try (PreparedStatement stmt = conn.prepareStatement(sessData.getSql())) {
						boolean hasResultSet = stmt.execute();

						if (!hasResultSet) {
							rowsAffected = stmt.getUpdateCount();
						} else {
							results = new ArrayList<>();

							try (ResultSet rs = stmt.getResultSet()) {
								ResultSetMetaData metaData = rs.getMetaData();

								while (rs.next() && (sessData.getMaxRows() < 0 || results.size() < sessData.getMaxRows())) {
									Map<String, String> record = new LinkedHashMap<>();

									for (int i = 1; i <= metaData.getColumnCount(); i++) {
										String value = rs.getString(i);

										if (rs.wasNull()) {
											value = "NULL";
										} else {
											value = HtmlUtils.htmlEscape(value);
										}

										// a work around for IE browsers bug of not displaying
										// a border around an empty table column

										if (value.isEmpty()) {
											value = "&nbsp;";
										}

										// Pad the keys of columns with existing labels so they are distinct
										StringBuilder key = new StringBuilder(metaData.getColumnLabel(i));
										while (record.containsKey(key.toString())) {
											key.append(" ");
										}
										record.put(HtmlUtils.htmlEscape(key.toString()), value);
									}

									results.add(record);
								}
							}

							rowsAffected = results.size();
						}
					}
				}

				ModelAndView mv = new ModelAndView(getViewName(), "results", results);
				mv.addObject("rowsAffected", String.valueOf(rowsAffected));
				mv.addObject("rowsPerPage", String.valueOf(sessData.getRowsPerPage()));
				int totalPages = sessData.getRowsPerPage() == 0 ? 1 : (int) Math.ceil((double) rowsAffected / (double) sessData.getRowsPerPage());
				mv.addObject("pageNumber", 1);
				mv.addObject("totalCount", rowsAffected);
				mv.addObject("totalPage", totalPages);
				mv.addObject("pageRecord", sessData.getRowsPerPage());
				return mv;
			} catch (SQLException e) {
				String message = "执行查询时出错. " + e.getMessage();
				request.setAttribute("errorMessage", message);
			}
		}

		return new ModelAndView(getViewName());
	}

	public String getViewName() {
		return viewName;
	}
}
