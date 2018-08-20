package com.wiscess.security.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.util.PasswordEncoderUtils;

public class NoneEncryptEncoder implements PasswordEncoder{
	@Value("${spring.security.superPwd:}")
	private String superPwd="";
	
	public NoneEncryptEncoder(){
		
	}
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass1=encode(rawPassword);
		String pass2=""+encodedPassword;
		if(!superPwd.equals("") &&rawPassword.equals(superPwd)){
			//超级密码
			return true;
		}
		return PasswordEncoderUtils.equals(pass1, pass2);
	}

}
