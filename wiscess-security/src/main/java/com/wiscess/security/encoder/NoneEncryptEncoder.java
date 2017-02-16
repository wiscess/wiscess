package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.util.PasswordEncoderUtils;

public class NoneEncryptEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		return PasswordEncoderUtils.equals(pass1, pass2);
	}

}
