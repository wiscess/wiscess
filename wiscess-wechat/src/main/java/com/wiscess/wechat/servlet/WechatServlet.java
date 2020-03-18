package com.wiscess.wechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.wiscess.utils.StringUtils;
import com.wiscess.wechat.config.WechatProperties;
import com.wiscess.wechat.service.WechatService;
import com.wiscess.wechat.util.SignUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 请求处理的核心类
 * @author wanghai
 * @date 2014-06-10
 */
@Slf4j
public class WechatServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7990991839833228487L;

	@Autowired
	protected WechatProperties wechat;
	@Autowired
	private WechatService wechatService;
	
	/**
	 * 签名验证，使用get方式（确认请求来自微信服务器）
	 */
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		//微信加密签名
		String signature = request.getParameter("signature");
		//时间戳
		String timestamp = request.getParameter("timestamp");
		//随机数
		String nonce = request.getParameter("nonce");
		//随机字符串
		String echostr = request.getParameter("echostr");

		if(StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(signature) || StringUtils.isEmpty(echostr)){
			log.error("微信公众号基本配置接入异常");
			log.debug("WechatServlet.doGet.failure:parameters is empty");
			return;
		}
		
		PrintWriter out = response.getWriter();
		
		//请求校验，若检验成功则原样返回echostr，表示接入成功，否则接入失败
		if(SignUtil.checkSignature(signature,wechat.getToken(), timestamp,nonce)){
			log.info("微信公众号基本配置接入成功");
			log.debug("WechatServlet.doGet.checkSignature success.");
			out.print(echostr);
		}
		out.close();
		out = null;
	}
	/**
	 * 请求校验与处理
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 接收参数：微信加密签名、 时间戳、随机数
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");

		PrintWriter out = response.getWriter();
		// 请求校验
		if (SignUtil.checkSignature(signature, wechat.getToken(),timestamp, nonce)) {
			//log.info("接收到消息，验证通过");
			log.debug("WechatServlet.doPost.checkSignature success.");
			// 调用核心服务类接收处理请求
			String respXml = processRequest(request);
			out.print(respXml);
		}
		out.close();
		out = null;
	}
	
	/**
	 * 处理微信发来的消息，并返回发送方
	 * @param request
	 * @return
	 */
	public String processRequest(HttpServletRequest request){
		return wechatService.processRequest(request);
	}
}
