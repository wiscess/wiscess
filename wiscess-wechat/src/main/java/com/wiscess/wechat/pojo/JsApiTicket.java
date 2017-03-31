package com.wiscess.wechat.pojo;

/**
 * Jsapi_ticket实体类
 * @author liudongge
 *
 */
public class JsApiTicket {
	
	private Integer errcode;
	private String errmsg;
	private String ticket;
	private Integer expiresIn;
	
	public Integer getErrcode() {
		return errcode;
	}
	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Integer getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}
}
