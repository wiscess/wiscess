package com.wiscess.utils;

import javax.crypto.Cipher;

import org.springframework.util.Base64Utils;

import sun.security.rsa.RSAPublicKeyImpl;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * RSA encrypt
 * 
 * @author wanghai
 * @version 1.0.0
 */
@SuppressWarnings("restriction")
public class RSA_Encrypt {
	/** assign to RSA */
	private static String ALGORITHM = "RSA";

	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/** assign key size */
	private static int KEYSIZE = 1024;
	/** assign publicKey filename */
	private static String PUBLIC_KEY_FILE = "PublicKey";
	/** assign privateKey filename */
	private static String PRIVATE_KEY_FILE = "PrivateKey";

	private static RSAPublicKey publicKey;

	private static RSAPrivateKey privateKey;

	/**
	 * generate keypair(publicKey and privateKey)
	 */
	public static void generateKeyPair() throws Exception {
		/** RSA need SecureRandom */
		SecureRandom sr = new SecureRandom();
		/** create a KeyPairGenerator object */
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
		/** initialized this KeyPairGenerator object use SecureRandom */
		kpg.initialize(KEYSIZE, sr);
		/** create keyPair */
		KeyPair kp = kpg.generateKeyPair();
		/** get public key */
		publicKey = (RSAPublicKey) kp.getPublic();
		/** get private key */
		privateKey = (RSAPrivateKey) kp.getPrivate();
		/** save public key to file as text */

		writePublicKey(PUBLIC_KEY_FILE, false);
		writePublicKey(PUBLIC_KEY_FILE + ".base64", true);
		writePrivateKey(PRIVATE_KEY_FILE, false);
		writePrivateKey(PRIVATE_KEY_FILE + ".base64", true);
	}

	/**
	 * write publicKey to Txt file
	 * 
	 * @param publicKeyPath
	 * @throws IOException
	 */
	public static void writePublicKey(String publicKeyPath, boolean useBase64) throws IOException {
		PrintWriter pw;
		pw = new PrintWriter(new FileWriter(publicKeyPath));
		pw.println("-------------PUBLIC_KEY-------------");
		if (useBase64) {
			// 使用base64编码
			pw.println(encryptBASE64(publicKey.getEncoded()));
		} else {
			BigInteger m = ((RSAPublicKeyImpl) publicKey).getModulus();
			BigInteger e = ((RSAPublicKeyImpl) publicKey).getPublicExponent();
			pw.println("bitlen=" + m.bitLength() + ";");
			String mStr = m.toString(16);
			if ((mStr.length() % 2) == 1)
				mStr = "0" + mStr;
			pw.println("m=" + mStr + ";");
			String eStr = e.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("e=" + eStr + ";");
		}
		pw.println("-------------PUBLIC_KEY-------------");

		pw.close();
	}

	/**
	 * write publicKey to Txt file
	 * 
	 * @param publicKeyPath
	 */
	public static void writePrivateKey(String privateKeyPath, boolean useBase64) throws IOException {
		PrintWriter pw;
		pw = new PrintWriter(new FileWriter(privateKeyPath));
		pw.println("-------------PRIVATE_KEY-------------");
		if (useBase64) {
			// 使用Base64编码
			pw.println(encryptBASE64(privateKey.getEncoded()));
		} else {
			BigInteger m = ((RSAPrivateCrtKey) privateKey).getModulus();
			BigInteger e = ((RSAPrivateCrtKey) privateKey).getPublicExponent();
			BigInteger privateExponent = ((RSAPrivateCrtKey) privateKey).getPrivateExponent();
			BigInteger p = ((RSAPrivateCrtKey) privateKey).getPrimeP();
			BigInteger q = ((RSAPrivateCrtKey) privateKey).getPrimeQ();
			BigInteger dP = ((RSAPrivateCrtKey) privateKey).getPrimeExponentP();
			BigInteger dQ = ((RSAPrivateCrtKey) privateKey).getPrimeExponentQ();
			BigInteger qInv = ((RSAPrivateCrtKey) privateKey).getCrtCoefficient();

			pw.println("bitlen=" + m.bitLength() + ";");

			String mStr = m.toString(16);
			if ((mStr.length() % 2) == 1)
				mStr = "0" + mStr;
			pw.println("m=" + mStr + ";");

			String eStr = e.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("e=" + eStr + ";");

			eStr = privateExponent.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("privateExponent=" + eStr + ";");

			eStr = p.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("p=" + eStr + ";");

			eStr = q.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("q=" + eStr + ";");

			eStr = dQ.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("dQ=" + eStr + ";");

			eStr = dP.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("dP=" + eStr + ";");

			eStr = qInv.toString(16);
			if ((eStr.length() % 2) == 1)
				eStr = "0" + eStr;
			pw.println("qInv=" + eStr + ";");
		}
		pw.println("-------------PRIVATE_KEY-------------");

		pw.close();
	}

	/**
	 * get privateKey from default path
	 * 
	 * @throws Exception
	 */
	public static void getPrivateKey(boolean useBase64) throws Exception {
		if (privateKey == null) {
			InputStream in = RSA_Encrypt.class
					.getResourceAsStream("/" + PRIVATE_KEY_FILE + (useBase64 ? ".base64" : ""));
			getPrivateKey(in, useBase64);
		}
	}

	/**
	 * get privateKey from assigned path
	 * 
	 * @param privateKeyPath
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	public static void getPrivateKey(InputStream in, boolean useBase64) throws Exception {
		/** 将文件中的私钥对象读出 */
		String s = ReadFile(in);
		getPrivateKey(s, useBase64);
	}

	public static void getPrivateKey(String s, boolean useBase64) throws Exception {
		/** 将文件中的私钥对象读出 */
		KeySpec keySpec = null;
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		if (useBase64) {
			s = s.replaceAll("-------------PRIVATE_KEY-------------", "");
			keySpec = new PKCS8EncodedKeySpec(decryptBASE64(s));
		} else {
			BigInteger modules = new BigInteger(GetValue("m=", s), 16);
			BigInteger publicExponent = new BigInteger(GetValue("e=", s), 16);
			BigInteger privateExponent = new BigInteger(GetValue("privateExponent=", s), 16);
			BigInteger primeP = new BigInteger(GetValue("p=", s), 16);
			BigInteger primeQ = new BigInteger(GetValue("q=", s), 16);
			BigInteger primeExponentP = new BigInteger(GetValue("dP=", s), 16);
			BigInteger primeExponentQ = new BigInteger(GetValue("dQ=", s), 16);
			BigInteger crtCoefficient = new BigInteger(GetValue("qInv=", s), 16);
			keySpec = new RSAPrivateCrtKeySpec(modules, publicExponent, privateExponent, primeP, primeQ, primeExponentP,
					primeExponentQ, crtCoefficient);
		}
		privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * get publicKey from default path
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void getPublicKey(boolean useBase64) throws Exception {
		if (publicKey == null) {
			InputStream in = RSA_Encrypt.class
					.getResourceAsStream("/" + PUBLIC_KEY_FILE + (useBase64 ? ".base64" : ""));
			getPublicKey(in, useBase64);
		}
	}

	/**
	 * get publicKey from assigned path
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public static void getPublicKey(InputStream in, boolean useBase64) throws Exception {
		String s = ReadFile(in);
		getPublicKey(s, useBase64);
	}

	public static void getPublicKey(String s, boolean useBase64) throws Exception {
		/** 将文件中的公钥对象读出 */
		KeySpec keySpec = null;
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		if (useBase64) {
			s = s.replaceAll("-------------PUBLIC_KEY-------------", "");
			keySpec = new X509EncodedKeySpec(decryptBASE64(s));
		} else {
			String mod = GetValue("m=", s);
			String pubExp = GetValue("e=", s);
			BigInteger m = new BigInteger(mod, 16);
			BigInteger e = new BigInteger(pubExp, 16);
			keySpec = new RSAPublicKeySpec(m, e);
		}
		publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	private static String ReadFile(InputStream f) {
		String s = "";
		try {
			byte b[] = new byte[f.available()];
			f.read(b);
			s = new String(b);
		} catch (Exception exception) {
		}
		return s;
	}

	private static String GetValue(String n, String src) {
		String s = "";
		int i1 = src.indexOf(n);
		i1 += n.length();
		int i2 = src.indexOf(";", i1);
		s = src.substring(i1, i2);
		return s;
	}

	/**
	 * encrypt source data use publicKey
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String source, boolean useBase64) throws Exception {
		getPublicKey(false);
		/**  */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 分段加密
		int MaxBlockSize = KEYSIZE / 8;
		String[] datas = splitString(source, MaxBlockSize - 11);
		String mi = "";
		for (String s : datas) {
			byte[] b = s.getBytes();
			/** do encrypt */
			byte[] b1 = cipher.doFinal(b);
			if (useBase64) {
				mi += encryptBASE64(b1);
			} else {
				mi += HexConver.byte2HexStr(b1, b1.length);
			}
		}
		return mi;
	}

	/**  
	*/
	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}

	/**
	 * decrypt cryptograph use privateKey
	 */
	public static String decrypt(String cryptograph, boolean useBase64) throws Exception {
		getPrivateKey(true);
		/** get Cipher object */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bytes = null;
		if (useBase64) {
			bytes = decryptBASE64(cryptograph);
		} else {
			bytes = HexConver.hexStr2Bytes(cryptograph);
		}
		int key_len = KEYSIZE / 8;
		//System.err.println(bytes.length);
		String ming = "";
		byte[][] arrays = splitArray(bytes, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr),"utf-8");
		}
		return ming;
	}

	/**  
	*/
	public static byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

	/**
	 * 
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(String data) throws Exception {
		return sign(data,true);
	}
	public static String sign(String data,boolean useBase64) throws Exception {
		getPrivateKey(false);
		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		signature.initSign(privateKey);

		signature.update(data.getBytes());
		byte[] b1 = signature.sign();
		if(useBase64){
			return encryptBASE64(b1);
		}else{
			return HexConver.byte2HexStr(b1, b1.length);
		}
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param sign
	 *            数字签名
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 */
	public static boolean verify(String data, String sign) throws Exception {
		return verify(data, sign,!HexConver.checkHexStr(sign));
	}
	public static boolean verify(String data, String sign,boolean useBase64) throws Exception {
		// 取公钥匙对象
		getPublicKey(false);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		signature.initVerify(publicKey);
		byte[] bytes = null;
		if (useBase64) {
			bytes = decryptBASE64(sign);
		} else {
			bytes = HexConver.hexStr2Bytes(sign);
		}
		signature.update(data.getBytes());

		// 验证签名是否正常
		return signature.verify(bytes);
	}

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) {
		key=key.trim().replaceAll("\r\n", "");
		return Base64Utils.decodeFromString(key);
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key)  {
		return Base64Utils.encodeToString(key);
	}

	// 编码返回字符串
	public static void main(String[] args) throws Exception {
		// 生成密钥对
		// generateKeyPair();
		// getPrivateKey();
		// getPublicKey();
		// String publicBase64=encryptBASE64(publicKey.getEncoded());
		// String priveteBase64=encryptBASE64(privateKey.getEncoded());
		// System.out.println(encryptBASE64(publicKey.getEncoded()));
		// System.out.println(encryptBASE64(privateKey.getEncoded()));
		// getPrivateKey();
		// 我自己的public，java可用，js端可用
		String tra = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdbXAZGOkzbdvMkwV+wURc82YWmFwq5VPw3G7fDpYRjDmgV/MVDCX25Rv8Lgp5xIkajW3x7jMNSKAbsBQIGyyWFcbnfGAPJjFMyy6uNe9B1FT9isZ8+AyMgO7f6pQdrp4upHQ7CHGnPMsyujY3e9OWWHxvqNBTlV6j9fEQ8iJjsQIDAQAB";
		String pra = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ1tcBkY6TNt28yTBX7BRFzzZhaYXCrlU/Dcbt8OlhGMOaBX8xUMJfblG/wuCnnEiRqNbfHuMw1IoBuwFAgbLJYVxud8YA8mMUzLLq4170HUVP2Kxnz4DIyA7t/qlB2uni6kdDsIcac8yzK6Njd705ZYfG+o0FOVXqP18RDyImOxAgMBAAECgYA1aTreGPiNzVkEaGE15yZljuL1CY5Ds3iuQGuRXCaIH5Yxk4VSf8Olp4e+IHTHaWnGy3Mg4NsLR7eijTPOqEGQxI7QUDwpBOq0JDJ0oVJupQHKz6YPsiCwcvgTZdDMGRb5L1XhKhZt2VqWBwxUkyG4PS87np+0NewLJwnxo+uqjQJBAOc8GeQXz9Z0X6ZmLBGGQ2rAySdpy19fRWNt/7webUQsqmeVsZh0eDX74vrGjtmi6wvib4loQPZ8q9LV1Jp3p/8CQQCuSbgt7XwCVjQLUf3bJlfdx0ez15JzVaSP0FBVpiZiGGxAeOQYHK9eZgpQl727dWZVzoy/rjnFOxfvpuiA6nRPAkBND9GNG4ZvcQ8jdG+BU56KKD+he/eEDEsirNkKh5VgoAYWSWQLa91YGF73tk6LJ2lv54HGaFEmFDxrIkodRH1fAkEAlZyO0E4mv9LEBlux8SfvEWB5+rW47+y6wQFvlLZ2CIsykf20v8YP/JbXj+tSYdMbr9kJFZo32Ukq+PxsZg3dHQJBAIFZWsw3u/2P6B8VO+zzEPO6K/g5hJN05BA0a/l7dHp0ZBtriGbMzhK5C0GM96O5tEFmrs9Kqh5xIeS0F3MmMc8=";
		// 网络1，java不可用，js端可用
		// String
		// tra="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlOJu6TyygqxfWT7eLtGDwajtNFOb9I5XRb6khyfD1Yt3YiCgQWMNW649887VGJiGr/L5i2osbl8C9+WJTeucF+S76xFxdU6jE0NQ+Z+zEdhUTooNRaY5nZiu5PgDB0ED/ZKBUSLKL7eibMxZtMlUDHjm4gwQco1KRMDSmXSMkDwIDAQAB";
		// String
		// pra="MIICXQIBAAKBgQDlOJu6TyygqxfWT7eLtGDwajtNFOb9I5XRb6khyfD1Yt3YiCgQWMNW649887VGJiGr/L5i2osbl8C9+WJTeucF+S76xFxdU6jE0NQ+Z+zEdhUTooNRaY5nZiu5PgDB0ED/ZKBUSLKL7eibMxZtMlUDHjm4gwQco1KRMDSmXSMkDwIDAQABAoGAfY9LpnuWK5Bs50UVep5c93SJdUi82u7yMx4iHFMc/Z2hfenfYEzu+57fI4fvxTQ//5DbzRR/XKb8ulNv6+CHyPF31xk7YOBfkGI8qjLoq06V+FyBfDSwL8KbLyeHm7KUZnLNQbk8yGLzB3iYKkRHlmUanQGaNMIJziWOkN+N9dECQQD0ONYRNZeuM8zd8XJTSdcIX4a3gy3GGCJxOzv16XHxD03GW6UNLmfPwenKu+cdrQeaqEixrCejXdAFz/7+BSMpAkEA8EaSOeP5Xr3ZrbiKzi6TGMwHMvC7HdJxaBJbVRfApFrE0/mPwmP5rN7QwjrMY+0+AbXcm8mRQyQ1+IGEembsdwJBAN6az8Rv7QnD/YBvi52POIlRSSIMV7SwWvSK4WSMnGb1ZBbhgdg57DXaspcwHsFV7hByQ5BvMtIduHcT14ECfcECQATeaTgjFnqE/lQ22Rk0eGaYO80cc643BXVGafNfd9fcvwBMnk0iGX0XRsOozVt5AzilpsLBYuApa66NcVHJpCECQQDTjI2AQhFc1yRnCU/YgDnSpJVm1nASoRUnU8Jfm3Ozuku7JUXcVpt08DFSceCEX9unCuMcT72rAQlLpdZir876";
		// 网络2，java可用，js端可用
		// String
		// tra="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqPvovSfXcwBbW8cKMCgwqNpsYuzF8RPAPFb7LGsnVo44JhM/xxzDyzoYtdfNmtbIuKVi9PzIsyp6rg+09gbuI6UGwBZ5DWBDBMqv5MPdOF5dCQkB2Bbr5yPfURPENypUz+pBFBg41d+BC+rwRiXELwKy7Y9caD/MtJyHydj8OUwIDAQAB";
		// String
		// pra="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKo++i9J9dzAFtbxwowKDCo2mxi7MXxE8A8VvssaydWjjgmEz/HHMPLOhi1182a1si4pWL0/MizKnquD7T2Bu4jpQbAFnkNYEMEyq/kw904Xl0JCQHYFuvnI99RE8Q3KlTP6kEUGDjV34EL6vBGJcQvArLtj1xoP8y0nIfJ2Pw5TAgMBAAECgYAGGB8IllMwxceLhjf6n1l0IWRH7FuHIUieoZ6k0p6rASHSgWiYNRMxfecbtX8zDAoG0QAWNi7rn40ygpR5gS1fWDAKhmnhKgQIT6wW0VmD4hraaeyP78iy8BLhlvblri2nCPIhDH5+l96v7D47ZZi3ZSOzcj89s1eS/k7/N4peEQJBAPEtGGJY+lBoCxQMhGyzuzDmgcS1Un1ZE2pt+XNCVl2b+T8fxWJH3tRRR8wOY5uvtPiK1HM/IjT0T5qwQeH8Yk0CQQC0tcv3d/bDb7bOe9QzUFDQkUSpTdPWAgMX2OVPxjdq3Sls9oA5+fGNYEy0OgyqTjde0b4iRzlD1O0OhLqPSUMfAkEAh5FIvqezdRU2/PsYSR4yoAdCdLdT+h/jGRVefhqQ/6eYUJJkWp15tTFHQX3pIe9/s6IeT/XyHYAjaxmevxAmlQJBAKSdhvQjf9KAjZKDEsa7vyJ/coCXuQUWSCMNHbcR5aGfXgE4e45UtUoIE1eKGcd6AM6LWhx3rR6xdFDpb9je8BkCQB0SpevGfOQkMk5i8xkEt9eeYP0fi8nv6eOUcK96EXbzs4jV2SAoQJ9oJegPtPROHbhIvVUmNQTbuP10Yjg59+8=";

		getPublicKey(tra, true);
		// System.out.println(encryptBASE64(publicKey.getEncoded()));
		getPrivateKey(pra, true);

		String source = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ1tcBkY6TNt28yTBX7BRFzzZhaYXCrlU/Dcbt8OlhGMOaBX8xUMJfblG/wuCnnEiRqNbfHuMw1IoBuwFAgbLJYVxud8YA8mMUzLLq4170HUVP2Kxnz4DIyA7t/qlB2uni6kdDsIcac8yzK6Njd705ZYfG+o0FOVXqP18RDyImOxAgMBAAECgYA1aTreGPiNzVkEaGE15yZljuL1CY5Ds3iuQGuRXCaIH5Yxk4VSf8Olp4e+IHTHaWnGy3Mg4NsLR7eijTPOqEGQxI7QUDwpBOq0JDJ0oVJupQHKz6YPsiCwcvgTZdDMGRb5L1XhKhZt2VqWBwxUkyG4PS87np+0NewLJwnxo+uqjQJBAOc8GeQXz9Z0X6ZmLBGGQ2rAySdpy19fRWNt/7webUQsqmeVsZh0eDX74vrGjtmi6wvib4loQPZ8q9LV1Jp3p/8CQQCuSbgt7XwCVjQLUf3bJlfdx0ez15JzVaSP0FBVpiZiGGxAeOQYHK9eZgpQl727dWZVzoy/rjnFOxfvpuiA6nRPAkBND9GNG4ZvcQ8jdG+BU56KKD+he/eEDEsirNkKh5VgoAYWSWQLa91YGF73tk6LJ2lv54HGaFEmFDxrIkodRH1fAkEAlZyO0E4mv9LEBlux8SfvEWB5+rW47+y6wQFvlLZ2CIsykf20v8YP/JbXj+tSYdMbr9kJFZo32Ukq+PxsZg3dHQJBAIFZWsw3u/2P6B8VO+zzEPO6K/g5hJN05BA0a/l7dHp0ZBtriGbMzhK5C0GM96O5tEFmrs9Kqh5xIeS0F3MmMc8=";
		//System.out.println(source);
		// 要加密的字符串
		//String cryptograph = encrypt(source, true);// 生成的密文
		//System.out.println(cryptograph);
		// String
		// cryptograph =
		// "jJIDpSMhia6RMuhYYRDZdWWCsnTbJoMUO31JW/8a+48lcUszpD0n86NunpfyhPtWkq6k6Ehj3MgYia5sL/HmK73YY8rp/7g0IbAfeVZ+HfSpzQd+wpV03eqchVgItQH5jiGsfNmm78Wc+Sbyn+mUExlpZl8vCM0Q9yl6l3rSlTw=";
		// cryptograph="dkKhC7sM3ElHmyQ1qiuzZOipjj5lmI8vVdH1AATzeV4E88WjEiGVhFI/Nu8L5jN6mx2ydEn8gwYnfcS6E5dwzCshkCoRMz8rxbVBXIh7cI9scEs/Hek8PiKevjMSalitB/UTk7J84Zy8omjLV/74YKNn7JQlm5UZggPD6BbCTS4=";
		// cryptograph="ZemClpI2zQe/PEJIA7Uk0H+ryMLvfyhJ8N+SvWJViNEgiVfjriPChXCYMsmLyNPDUNzxyxLeqOfSYn8F5GW9xLXzWXN5Cx+uK3yDqbTzHkX+xtd/YeVCN82Sivf7UfYUy7zXw5uOabZA3fV8tiyojfQtejx/eCJM7mPN48kUdAE=";
		// cryptograph="SXDMOPeBKODMsVl/tPRz7baRIi9zLu0DmhQ5PM4b3XYwTNTIDDXBskhGUbk3sXj17okGfCGN3594Vmrc7Nb7WGb3UdHAqbxBt2U4U0ps6ISyk44Po8aC9RrvgbP2MGcwlQOCH3eCTp7Jg6bpayhEk0O4WkhBW0SJ7IS77E3dOdg=";
		//String target = decrypt(cryptograph, true);// 解密密文
		//System.out.println(target);

		// 签名
//		 source="296502429874592438576248524admin";
//		 String s=sign(source);
//		 System.out.println(s);
//		 //验证
//		 System.out.println(verify(source, s));
		String encoding=System.getProperty("sun.jnu.encoding");
		System.out.println(encoding);
		source = "*U&Y6t5r";
		System.out.println(source);
		// 要加密的字符串
		String cryptograph = encrypt(source, true);// 生成的密文
		System.out.println(cryptograph);
		//解密
		cryptograph="lYLhPPMkQCphNKCfMsJJ8rq5U2ZTF0Bi9nWrECLYUTdyO42R2vPPH7sCtYJL9db/tGOZ0ddZFquzUtSGAtaPmz49x7aAdkzkj3kmeU5GNcBJurv/ReBgi2e/uB3QBYYNbDkboY04J6VZLywywgBwlIKeExSpbCrJNSN4HUsHrqE=";
		String target = decrypt(cryptograph, true);// 解密密文
//		target=new String(target.getBytes("utf-8"), encoding);
		System.out.println(target);
	}
}
