package com.wiscess.security.sso.encrypt;

public interface EncryptHandler {

	public static final String MD5 = "MD5";
	public static final String RSA = "RSA";
	public static final String NONE = "NONE";
	
	/**
	 * 加密类型
	 * @return
	 */
	public String encryptType();
	/**
	 * 加密
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	public String encode(String data);
	/**
	 * 验证或解密
	 * @param data
	 * @param sign
	 * @return
	 */
	public boolean encrypt(String data,String sign);
}
