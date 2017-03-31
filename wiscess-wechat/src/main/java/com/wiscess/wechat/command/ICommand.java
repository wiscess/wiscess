package com.wiscess.wechat.command;

import java.util.Map;

import com.wiscess.wechat.message.resp.IRespMessage;

/**
 * 微信命令行特殊指令
 * @author wanghai
 * @date 2014-09-29 
 */
public interface ICommand {

	/**
	 * 执行查询
	 * @param requestMap
	 * @param openId
	 * @return
	 */
	public IRespMessage excute(Map<String, String> requestMap,String openId); 
}
