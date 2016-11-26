package com.wiscess.security.sso.encrypt;

public class NoneEncryptHandler implements EncryptHandler {

	@Override
	public String encryptType() {
		return "NONE";
	}

	@Override
	public boolean encrypt(String data, String sign) {
		return sign.equals(data);
	}

	@Override
	public String encode(String data) {
		return data;
	}

}
