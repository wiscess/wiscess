package com.wiscess.security.sso.encrypt;

import org.apache.tomcat.util.security.MD5Encoder;

public class MD5EncryptHandler implements EncryptHandler {

	private EncryptHandler encryptHandler=new NoneEncryptHandler();
	
	public MD5EncryptHandler(EncryptHandler encryptHandler){
		this.encryptHandler=encryptHandler;
	}
	public MD5EncryptHandler(){
		this(new NoneEncryptHandler());
	}
	@Override
	public String encryptType() {
		return "MD5";
	}
	@Override
	public boolean encrypt(String data, String sign) {
		return sign.equals(encode(data));
	}
	@Override
	public String encode(String data) {
		return MD5Encoder.encode(encryptHandler.encode(data).getBytes());
	}


}
