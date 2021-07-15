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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wiscess.probe.dto.DataSourceTestInfo;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Displays a result set cached in an attribute of HttpSession object to support
 * result set pagination feature without re-executing a query that created the
 * result set.
 */
@Controller
public class CachedRecordSetController {

	String viewName = "probe/ajax/sql/recordset";

	@RequestMapping(path = "/sql/cachedRecordset.ajax")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int rowsPerPage = ServletRequestUtils.getIntParameter(request, "rowsPerPage", 0);
		int pageNumber = ServletRequestUtils.getIntParameter(request, "pageNo", 1);
		List<Map<String, String>> results = null;
		int rowsAffected = 0;
		HttpSession sess = request.getSession(false);

		if (sess == null) {
			request.setAttribute("errorMessage", "无法获取缓存的结果集");
		} else {
			DataSourceTestInfo sessData = (DataSourceTestInfo) sess.getAttribute(DataSourceTestInfo.DS_TEST_SESS_ATTR);

			if (sessData == null) {
				request.setAttribute("errorMessage", "无法获取缓存的结果集");
			} else {
				synchronized (sess) {
					sessData.setRowsPerPage(rowsPerPage);
				}

				results = sessData.getResults();

				if (results == null) {
					request.setAttribute("errorMessage", "无法获取缓存的结果集");
				} else {
					rowsAffected = results.size();
				}
			}
		}

		ModelAndView mv = new ModelAndView(getViewName(), "results", results);
		mv.addObject("rowsAffected", String.valueOf(rowsAffected));
		mv.addObject("rowsPerPage", String.valueOf(rowsPerPage));
        int totalPages = rowsPerPage == 0 ? 1 : (int) Math.ceil((double) rowsAffected / (double) rowsPerPage);
        mv.addObject("pageNumber",pageNumber);
        mv.addObject("totalCount",rowsAffected);
        mv.addObject("totalPage",totalPages);
        mv.addObject("pageRecord",rowsPerPage);

		return mv;
	}

	public String getViewName() {
		return viewName;
	}

}
