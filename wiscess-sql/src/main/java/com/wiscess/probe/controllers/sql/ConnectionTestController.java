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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Verifies if a database connection can be established through a given datasource. Displays basic
 * information about the database.
 */
@Controller
@Slf4j
public class ConnectionTestController  {

	@Autowired
	private DataSource dataSource;
	
	private String viewName="probe/ajax/sql/connection";

  @RequestMapping(path = "/sql/connection.ajax")
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
	  if (dataSource == null) {
		  request.setAttribute("errorMessage", "数据源不存在");
	  } else {
			try {
				// TODO: use Spring's jdbc template?
				try (Connection conn = dataSource.getConnection()) {
					DatabaseMetaData md = conn.getMetaData();

					List<Map<String, String>> dbMetaData = new ArrayList<>();

					addDbMetaDataEntry(dbMetaData, "Database Product Name",
							md.getDatabaseProductName());
					addDbMetaDataEntry(dbMetaData, "Database Product Version",
							md.getDatabaseProductVersion());
					addDbMetaDataEntry(dbMetaData, "JDBC Driver Name",
							md.getDriverName());
					addDbMetaDataEntry(dbMetaData, "JDBC Driver Version",
							md.getDriverVersion());
					addDbMetaDataEntry(dbMetaData, "JDBC Version",
							String.valueOf(md.getJDBCMajorVersion()));

					return new ModelAndView(getViewName(), "dbMetaData", dbMetaData);
				}
			} catch (SQLException e) {
				String message = e.getMessage();
				request.setAttribute("errorMessage", message);
			}
		}

		return new ModelAndView(getViewName());
  }

  protected String getViewName() {
    return viewName;
  }

  /**
   * Adds the db meta data entry.
   *
   * @param list the list
   * @param name the name
   * @param value the value
   */
  private void addDbMetaDataEntry(List<Map<String, String>> list, String name, String value) {
    Map<String, String> entry = new LinkedHashMap<>();
    entry.put("propertyName", name);
    entry.put("propertyValue", value);
    list.add(entry);
  }

}
