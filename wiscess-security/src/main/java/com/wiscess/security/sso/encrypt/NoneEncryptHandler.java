package com.wiscess.security.sso.encrypt;

/**
 * 无加密
 * @author wh
 *
 */
public class NoneEncryptHandler implements EncryptHandler {

	@Override
	public String encryptType() {
		return "NONE";
	}

	/**
	 * 验证，原数据等于加密后的数据
	 */
	@Override
	public boolean encrypt(String data, String sign) {
		return sign.equals(data);
	}

	/**
	 * 不进行加密，直接返回数据
	 */
	@Override
	public String encode(String data) {
		return data;
	}

}
