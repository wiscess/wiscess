package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UpperCaseEncryptEncoder implements PasswordEncoder{

	private PasswordEncoder encoder=new NoneEncryptEncoder();
	
	public UpperCaseEncryptEncoder(PasswordEncoder encoder){
		this.encoder=encoder;
	}
	public UpperCaseEncryptEncoder() {
		this(new NoneEncryptEncoder());
	}
	public String encode(CharSequence rawPassword) {
		return encoder.encode(rawPassword).toUpperCase();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//先对原始数据进行加密，再对数据进行md5
		String pass1=rawPassword.toString().toUpperCase();
		String pass2=""+encodedPassword;
		return encoder.matches(pass1, pass2);
	}
}
