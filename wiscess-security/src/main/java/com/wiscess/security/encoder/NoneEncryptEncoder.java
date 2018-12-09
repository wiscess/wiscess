package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.util.PasswordEncoderUtils;

public final class NoneEncryptEncoder implements PasswordEncoder{
	
	public NoneEncryptEncoder(){
	}
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		return PasswordEncoderUtils.equals(pass1, pass2);
	}

}
