package com.wiscess.web.controller;

import com.wiscess.common.R;
import com.wiscess.utils.RSA_Encrypt;
import io.swagger.annotations.Api;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * 公共接口，用于向所有用户提供token，publickey等方法
 * @author wh
 *
 */
@RequestMapping("/")
@RestController
@Api(value = "PublicApiControll",tags = "框架公用接口")
public class PublicApiControll {

	public static String publicKey=getPublicKey();
	
	@GetMapping("/getPublicKey")
	public R getPublicKey(HttpServletRequest req) {
		return R.ok().data(publicKey);
	}
	@GetMapping("/getToken")
	public R getToken(HttpServletRequest req) {
		CsrfToken csrfToken = (CsrfToken)req.getAttribute(CsrfToken.class.getName());
		return R.ok().data(csrfToken);
	}
	
	private static String getPublicKey() {
		String s = "";
		try {
			InputStream in = RSA_Encrypt.class
					.getResourceAsStream("/PublicKey.base64");
			byte b[] = new byte[in.available()];
			in.read(b);
			s = new String(b);
			s=s.replaceAll("-------------PUBLIC_KEY-------------", "").replaceAll("\r\n", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
}
