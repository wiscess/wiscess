package com.wiscess.security.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.utils.RSA_Encrypt;

/**
 * RSA方式和MD5的方式结合，原始数据用MD5处理，
 * 解密时先用RSA方式解密出的原始字符串，再进行MD5处理，然后和原始数据进行比较
 * @author wh
 *
 */
@Deprecated
public final class RSAMD5EncryptEncoder implements PasswordEncoder{

	public RSAMD5EncryptEncoder() {
	}
	@Override
	public String encode(CharSequence rawPassword) {
		try {
			return RSA_Encrypt.encrypt(rawPassword.toString(),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		try {
			rawPassword=RSA_Encrypt.decrypt(rawPassword.toString(),true);
			//将解密后的字符串再用MD5进行处理；
			return new MD5EncryptEncoder().matches(rawPassword, encodedPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
