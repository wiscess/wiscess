package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.utils.RSA_Encrypt;

/**
 * RSA方式加密和验证
 * 如果有嵌套方式，则表示先用嵌套的处理器进行加密，最后用RSA方式加密，
 * 解密时要将原数据使用嵌套的处理器加密后，与RSA解密后的字符串进行比较
 * @author wh
 */
public class RSAEncryptEncoder implements PasswordEncoder{

	private PasswordEncoder encoder=new NoneEncryptEncoder();
	
	public RSAEncryptEncoder(PasswordEncoder encoder){
		this.encoder=encoder;
	}
	public RSAEncryptEncoder() {
		this(new NoneEncryptEncoder());
	}
	@Override
	public String encode(CharSequence rawPassword) {
		try {
			return RSA_Encrypt.encrypt(encoder.encode(rawPassword),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//对加密数据进行解密
		String pass2="";
		try {
			pass2=RSA_Encrypt.decrypt(encodedPassword.toString(),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//比较
		return encoder.matches(rawPassword, pass2);
	}

}
