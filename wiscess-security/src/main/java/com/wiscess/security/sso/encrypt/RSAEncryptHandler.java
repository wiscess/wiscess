package com.wiscess.security.sso.encrypt;

import com.wiscess.util.RSA_Encrypt;

/**
 * RSA方式加密验证
 * @author wh
 *
 */
public class RSAEncryptHandler implements EncryptHandler {

	private EncryptHandler encryptHandler=new NoneEncryptHandler();
	
	public RSAEncryptHandler(EncryptHandler encryptHandler){
		this.encryptHandler=encryptHandler;
	}
	public RSAEncryptHandler() {
		this(new NoneEncryptHandler());
	}
	@Override
	public String encryptType() {
		return RSA;
	}

	/**
	 * RSA签名
	 * @throws Exception 
	 */
	@Override
	public String encode(String data) {
		try {
			return RSA_Encrypt.sign(encryptHandler.encode(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * RSA方式验证签名
	 * @throws Exception 
	 */
	@Override
	public boolean encrypt(String data, String sign){
		try {
			return RSA_Encrypt.verify(encryptHandler.encode(data),sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
