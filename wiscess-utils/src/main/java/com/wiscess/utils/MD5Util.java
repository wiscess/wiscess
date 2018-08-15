package com.wiscess.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class MD5Util {
	
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is unsupported", e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MessageDigest不支持MD5Util", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/**
	 * 
	 * md5签名
	 * 按参数名称升序，将参数值进行连接 签名
	 * @param appSecret
	 * @param params
	 * @return
	 */
	public static String sign(String appSecret, TreeMap<String, String> params) {
		StringBuilder paramValues = new StringBuilder();
		params.put("appSecret", appSecret);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramValues.append(entry.getValue());
		}
		return md5(paramValues.toString());
	}

	/**
	 * 请求参数签名验证
	 * @param appSecret
	 * @param request
	 * @return true 验证通过 false 验证失败
	 * @throws Exception
	 */
	public static boolean verifySign(String appSecret, TreeMap<String, String> params) throws Exception {
		String signStr = params.get("sign");

		if (StringUtils.isEmpty(signStr)) {
			throw new RuntimeException("There is no signature field in the request parameter!");
		}
		params.remove("sign");

		if (!sign(appSecret, params).equals(signStr)) {
			return false;
		}
		return true;
	}
}
