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
package com.wiscess.cmd.dto;

import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

import org.springframework.web.bind.ServletRequestUtils;

import com.wiscess.utils.RSA_Encrypt;

/**
 * A class to store data source test tool related data in a session attribute.
 */
@Data
public class SessCmdDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant EC_DATA_SESS_ATTR. */
	public static final String EC_DATA_SESS_ATTR = "executeCmdData";

	/** isEncrypt */
	private int encrypt;

	public static SessCmdDto refreshSession(HttpServletRequest request) {
		HttpSession sess = request.getSession(false);
		SessCmdDto sessData = (SessCmdDto) sess.getAttribute(SessCmdDto.EC_DATA_SESS_ATTR);

		synchronized (sess) {
			if (sessData == null) {
				sessData = new SessCmdDto();
				sess.setAttribute(SessCmdDto.EC_DATA_SESS_ATTR, sessData);
			}
			int isEncrypt = ServletRequestUtils.getIntParameter(request, "isEncrypt", 1);
			
			String cmd = ServletRequestUtils.getStringParameter(request, isEncrypt == 1 ? "cmd" : "cmdWithHtml", null);
			try {
				if (isEncrypt == 1) {
					cmd = RSA_Encrypt.decrypt(cmd, true);
				}
			} catch (Exception e) {
				// e.printStackTrace();
				cmd = null;
			}
			sessData.setEncrypt(isEncrypt);
			sess.setAttribute(SessCmdDto.EC_DATA_SESS_ATTR, sessData);
		}
		return sessData;
	}
}
