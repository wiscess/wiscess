package com.wiscess.wechat.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ICoreService {
	
	public String processRequest(HttpServletRequest request);
	
	public String processMessage(Map<String, String> requestMap);
	
	public String processSubScribe(Map<String, String> requestMap);
	
	public String processUnsubscribe(Map<String, String> requestMap);
	
	public String processClick(Map<String, String> requestMap);
	
	public String processLocation(Map<String, String> requestMap);
	
	public String processScan(Map<String, String> requestMap) ;
	
	
}
