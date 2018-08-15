package com.wiscess.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

/**
 * DES加密和解密工具,可以对字符串进行加密和解密操作 。
 */
public class DesUtils {

	private static String strDefaultKey = "cysy_cms.20151024";// 字符串默认键值
	private static enum Method {decrypt,encrypt};

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArray(String strIn) 互为可逆的转换过程
	 *
	 * @param arrB
	 *            需要转换的byte数组
	 * @return 转换后的字符串
	 */
	private static String byteArray2HexStr(byte[] arrB) {
		int iLen = arrB.length;
		StringBuffer sb = new StringBuffer(iLen * 2);// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			while (intTmp < 0) {// 把负数转换为正数
				intTmp = intTmp + 256;
			}
			if (intTmp < 16) {// 小于0F的数需要在前面补0
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArray2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 *
	 * @param strIn
	 *            需要转换的字符串
	 * @return 转换后的byte数组
	 */
	private static byte[] hexStr2ByteArray(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	private static Cipher encryptCipher(String key) throws InvalidKeyException, Exception{
		Cipher encryptCipher= Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, getKey(key.getBytes()));
		return encryptCipher;
	}
	private static Cipher decryptCipher(String key) throws InvalidKeyException, Exception{
		Cipher decryptCipher= Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, getKey(key.getBytes()));
		return decryptCipher;
	}
	/**
	 * 加密字节数组
	 *
	 * @param arrB
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	private static byte[] encryptByte(byte[] arrB) {
		return encryptByte(arrB,strDefaultKey);
	}
	private static byte[] encryptByte(byte[] arrB,String key) {
		byte[] encrypt = null;
		try {
			encrypt = encryptCipher(key).doFinal(arrB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypt;
	}

	/**
	 * 加密字符串
	 *
	 * @param strIn
	 *            需加密的字符串
	 * @return 加密后的字符串
	 * @throws Exception
	 */
	public static String encrypt(String strIn) {
		return byteArray2HexStr(encryptByte(strIn.getBytes()));
	}
	public static String encrypt(String strIn,String key) {
		return byteArray2HexStr(encryptByte(strIn.getBytes(),key));
	}

	/**
	 * 解密字节数组
	 *
	 * @param arrB
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	private final static byte[] decryptByte(byte[] arrB) {
		return decryptByte(arrB,strDefaultKey);
	}
	private final static byte[] decryptByte(byte[] arrB,String key) {
		byte[] decrypt = null;
		try {
			decrypt = decryptCipher(key).doFinal(arrB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}

	/**
	 * 解密字符串
	 *
	 * @param strIn
	 *            需解密的字符串
	 * @return 解密后的字符串
	 * @throws Exception
	 */
	public static String decrypt(String strIn) {
		return new String(decryptByte(hexStr2ByteArray(strIn)));
	}
	public static String decrypt(String strIn,String key) {
		return new String(decryptByte(hexStr2ByteArray(strIn),key));
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 *
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	private static Key getKey(byte[] arrBTmp) throws Exception {
		byte[] arrB = new byte[8];// 创建一个空的8位字节数组（默认值为0）
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {// 将原始字节数组转换为8位
			arrB[i] = arrBTmp[i];
		}
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");// 生成密钥
		return key;
	}
	
	public static void usage(){
		System.out.println("加密解密工具");
		System.out.println("java -jar wiscess-util.jar -des [OPTION] <source1> <source2>...");
		System.out.println("");
		System.out.println("选项：");
		System.out.println("");
		System.out.println("  /d          对输入的字符串进行解密");
		System.out.println("  /e          对输入的字符串进行加密");
		System.out.println("  /k:<key>    指定key");
		System.out.println("  <source1>   输入的字符串1");
		System.out.println("  <source2>   输入的字符串2");
	}
	/**
	 * main方法
	 */
	public static void main(String[] args) {
//		args=new String[]{"/e","wiscess"};
//		args=new String[]{"/d","ce9e1c1ab7d9f5c2"};
		if(args.length<=1){
			usage();
			return;
		}
		//从参数列表中读取各个参数
		Method method = null;
		String key=strDefaultKey;
		List<String> sources=new ArrayList<String>();
		for(String arg:args){
			if(arg.startsWith("/k:")){
				key=arg.substring(3);
				if(key.equals("")){
					key=strDefaultKey;
				}
			}	
			else if(arg.equalsIgnoreCase("/d")){
				method=Method.decrypt;
			}
			else if(arg.equalsIgnoreCase("/e")){
				method=Method.encrypt;
			}else{
				//字符串
				sources.add(arg);
			}
		}
		switch (method) {
		case decrypt:
			//解密
			for(String content:sources){
				System.out.println("解密前的字符串:"+content);
				System.out.println("解密后的字符串:"+DesUtils.decrypt(content,key));
			}
			break;
		case encrypt:
			//加密
			for(String content:sources){
				System.out.println("加密前的字符串:"+content);
				System.out.println("加密后的字符串:"+DesUtils.encrypt(content,key));
			}
			break;
		default:
			break;
		}
	}
}