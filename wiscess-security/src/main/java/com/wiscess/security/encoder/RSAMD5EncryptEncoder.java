package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.util.PasswordEncoderUtils;
import com.wiscess.security.util.RSA_Encrypt;

/**
 * RSA方式和MD5的方式结合，原始数据用MD5处理，
 * 解密时先用RSA方式解密出的原始字符串，再进行MD5处理，然后和原始数据进行比较
 * @author wh
 *
 */
public final class RSAMD5EncryptEncoder implements PasswordEncoder{

	public RSAMD5EncryptEncoder() {
	}
	@Override
	public String encode(CharSequence rawPassword) {
		try {
			return RSA_Encrypt.encrypt(rawPassword.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//原始数据用MD5加密
		String pass1=""+encodedPassword;
		//对加密数据进行解密
		String pass2="";
		try {
			pass2=RSA_Encrypt.decryptBase64(rawPassword.toString());
			//将解密后的字符串再用MD5进行处理；
			pass2=new MD5EncryptEncoder().encode(pass2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//比较
		return PasswordEncoderUtils.equals(pass1, pass2);
	}

}
