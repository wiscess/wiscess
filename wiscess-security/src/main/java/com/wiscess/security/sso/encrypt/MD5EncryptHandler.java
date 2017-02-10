package com.wiscess.security.sso.encrypt;

import org.apache.tomcat.util.security.MD5Encoder;

/**
 * MD5方式加密
 * @author wh
 *
 */
public class MD5EncryptHandler implements EncryptHandler {

	private EncryptHandler encryptHandler=new NoneEncryptHandler();
	
	/**
	 * 可以使用其他加密方式
	 * @param encryptHandler
	 */
	public MD5EncryptHandler(EncryptHandler encryptHandler){
		this.encryptHandler=encryptHandler;
	}
	/**
	 * 默认无其他加密方式
	 */
	public MD5EncryptHandler(){
		this(new NoneEncryptHandler());
	}
	@Override
	public String encryptType() {
		return "MD5";
	}
	/**
	 * 验证，将原数据进行加密后与签字数据进行比较
	 */
	@Override
	public boolean encrypt(String data, String sign) {
		return sign.equals(encode(data));
	}
	/**
	 * 加密数据
	 */
	@Override
	public String encode(String data) {
		return MD5Encoder.encode(encryptHandler.encode(data).getBytes());
	}


}
