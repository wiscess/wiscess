package com.wiscess.cmd.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wiscess.cmd.dto.SessCmdDto;
import com.wiscess.cmd.util.NoServiceUtil;


/**
 * 模拟命令行
 * @author wh
 *
 */
@Controller
public class CmdControll {

	  /**是否加密传输*/
	  private int isEncrypt;
	/**
	 * 页面入口
	 * @return
	 */
	@RequestMapping({"/cmd"})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		HttpSession sess = request.getSession(false);
		
		SessCmdDto sessData = null;
		if (sess != null) {
			sessData = (SessCmdDto) sess.getAttribute(SessCmdDto.EC_DATA_SESS_ATTR);
		}
		String ip= NoServiceUtil.getIpAddress(request);
		
		model
				.addAttribute("ip", ip)
				.addAttribute("isEncrypt",	sessData == null ? isEncrypt() : sessData.getEncrypt())
				;
		return "cmd/index";
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
