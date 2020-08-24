/**
 * 
 */
package com.wiscess.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiscess.common.R;
import com.wiscess.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author audin
 *
 */
@Controller
public class CaptchaController {
	@Autowired
	private Producer kaptchaProducer = null;

	@Autowired
	private Config config;

	@RequestMapping(value = "${app.captcha.url:/captcha.jpg}", method = RequestMethod.GET)
	public void captcha(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Set standard HTTP/1.1 no-cache headers.
		resp.setHeader("Cache-Control", "no-store, no-cache");

		// return a jpeg
		resp.setContentType("image/jpeg");

		// create the text for the image
		String capText = this.kaptchaProducer.createText();

		// store the text in the session
		req.getSession().setAttribute(config.getSessionKey(), capText);

		// store the date in the session so that it can be compared
		// against to make sure someone hasn't taken too long to enter
		// their kaptcha
		req.getSession().setAttribute(config.getSessionDate(), new Date());

		// create the image with the text
		BufferedImage bi = this.kaptchaProducer.createImage(capText);

		ServletOutputStream out = resp.getOutputStream();

		// write the data out
		ImageIO.write(bi, "jpg", out);

		// fixes issue #69: set the attributes after we write the image in case
		// the image writing fails.

		// store the text in the session
		req.getSession().setAttribute(config.getSessionKey(), capText);

		// store the date in the session so that it can be compared
		// against to make sure someone hasn't taken too long to enter
		// their kaptcha
		req.getSession().setAttribute(config.getSessionDate(), new Date());
	}

	/**
	 * 校验验证码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "${app.captcha.checkurl:/js/validateCode}", method = RequestMethod.GET)
	public R validateCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code=req.getParameter("code");
		String savedCode=(String)req.getSession().getAttribute(config.getSessionKey());
		Boolean result = StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(savedCode)
				&& code.equals(savedCode);
		return result? R.ok():
				R.error();
	}
}