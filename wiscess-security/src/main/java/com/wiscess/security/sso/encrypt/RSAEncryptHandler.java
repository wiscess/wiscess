package com.wiscess.security.sso.encrypt;

import com.wiscess.security.util.RSA_Encrypt;

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
	 */
	@Override
	public String encode(String data) {
		return RSA_Encrypt.sign(encryptHandler.encode(data));
	}

	/**
	 * RSA方式验证签名
	 */
	@Override
	public boolean encrypt(String data, String sign) {
		return RSA_Encrypt.verify(encryptHandler.encode(data),sign);
	}

}
