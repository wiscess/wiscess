package com.wiscess.security.encoder;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.util.PasswordEncoderUtils;

public class MD5EncryptEncoder implements PasswordEncoder{

	private PasswordEncoder encoder=new NoneEncryptEncoder();
	
	public MD5EncryptEncoder(PasswordEncoder encoder){
		this.encoder=encoder;
	}
	public MD5EncryptEncoder() {
		this(new NoneEncryptEncoder());
	}
	@Override
	public String encode(CharSequence rawPassword) {
		return new Md5PasswordEncoder().encodePassword(encoder.encode(rawPassword), null);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//先对原始数据进行加密，再对数据进行md5
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		return PasswordEncoderUtils.equals(pass1, pass2);
	}
}
