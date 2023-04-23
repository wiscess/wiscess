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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wiscess.probe.dto.DataSourceTestInfo;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Retrieves a history list of executed queries from a session variable.
 */
@Controller
public class QueryHistoryController {

	private String viewName = "probe/ajax/sql/queryHistory";

	@RequestMapping(path = "/sql/queryHistory.ajax")
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession sess = request.getSession(false);
		List<String> queryHistory = null;

		if (sess != null) {
			DataSourceTestInfo sessData = (DataSourceTestInfo) sess.getAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR);

			if (sessData != null) {
				queryHistory = sessData.getQueryHistory();
			}
		}

		return new ModelAndView(getViewName(), "queryHistory", queryHistory);
	}

	protected String getViewName() {
		return viewName;
	}
}
