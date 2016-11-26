package com.wiscess.security.util;

import javax.crypto.Cipher;

import org.springframework.security.crypto.codec.Hex;

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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * RSA encrypt
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

	private static PublicKey publicKey;
	
	private static PrivateKey privateKey;
	/**
	 * generate keypair(publicKey and privateKey)
	 */
	public static void generateKeyPair() throws Exception {
		/** RSA need SecureRandom  */
		SecureRandom sr = new SecureRandom();
		/** create a KeyPairGenerator object */
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
		/** initialized this KeyPairGenerator object use SecureRandom  */
		kpg.initialize(KEYSIZE, sr);
		/** create keyPair */
		KeyPair kp = kpg.generateKeyPair();
		/** get public key */
		publicKey = kp.getPublic();
		/** get private key */
		privateKey = kp.getPrivate();
		/** save public key to file as text */
		
		writePublicKey(PUBLIC_KEY_FILE);
		writePrivateKey(PRIVATE_KEY_FILE);
	}

	/**
	 * get privateKey from default path
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void getPrivateKey() throws FileNotFoundException, IOException, ClassNotFoundException{
		if(privateKey==null){
			InputStream in = RSA_Encrypt.class.getResourceAsStream("/"+PRIVATE_KEY_FILE);
			getPrivateKey(in);
		}
	}
	
	/**
	 * get privateKey from assigned path
	 * @param privateKeyPath
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
 	public static void getPrivateKey(InputStream in) throws FileNotFoundException, IOException, ClassNotFoundException{
		/** 将文件中的私钥对象读出 */
		/** 将文件中的私钥对象读出 */
		String s = ReadFile(in);
        String mod = GetValue("m=", s);
        String priExp = GetValue("privateExponent=", s);
		 BigInteger m = new BigInteger(mod,16);  
         BigInteger e = new BigInteger(priExp,16); 
         RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m,e);
         KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
	         privateKey = keyFactory.generatePrivate(keySpec);  
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
	}
 	
 	/**
	 * get publicKey from default path
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void getPublicKey() throws FileNotFoundException, IOException, ClassNotFoundException{
		if(publicKey==null){
			InputStream f = RSA_Encrypt.class.getResourceAsStream("/"+PUBLIC_KEY_FILE);
			getPublicKey(f);
		}
	}
	
	/**
	 * get publicKey from assigned path
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	public static void getPublicKey(InputStream	inputStream) throws FileNotFoundException, IOException, ClassNotFoundException{
		/** 将文件中的公钥对象读出 */
		String s = ReadFile(inputStream);
        String mod = GetValue("m=", s);
        String pubExp = GetValue("e=", s);
		 BigInteger m = new BigInteger(mod,16);  
         BigInteger e = new BigInteger(pubExp,16); 
         RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m,e);
         KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
	         publicKey = keyFactory.generatePublic(keySpec);  
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
	}
	
    public static String ReadFile(InputStream f)
    {
        String s = "";
        try
        {
            byte b[] = new byte[f.available()];
            f.read(b);
            s = new String(b);
        }
        catch(Exception exception) { }
        return s;
    }

    public static String GetValue(String n, String src)
    {
        String s = "";
        int i1 = src.indexOf(n);
        i1 += n.length();
        int i2 = src.indexOf(";", i1);
        s = src.substring(i1, i2);
        return s;
    }

    /**
	 * write publicKey to Txt file
	 * @param publicKeyPath
	 */
	public static void writePublicKey(String publicKeyPath){
		try {
			PrintWriter pw;
			pw = new PrintWriter( new FileWriter( publicKeyPath ) );
			pw.println("-------------PUBLIC_KEY-------------"); 
			
			BigInteger m=((RSAPublicKeyImpl)publicKey).getModulus();
			BigInteger e=((RSAPublicKeyImpl)publicKey).getPublicExponent();
			pw.println("bitlen="+m.bitLength()+";");
			String mStr=m.toString(16);
			if((mStr.length() % 2)==1)
				mStr="0"+mStr;
			pw.println("m="+mStr+";");
			String eStr = e.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("e="+eStr+";");
			pw.println("-------------PUBLIC_KEY-------------"); 
			
	        pw.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	/**
	 * write publicKey to Txt file
	 * @param publicKeyPath
	 */
	public static void writePrivateKey(String privateKeyPath){
		try {
			PrintWriter pw;
			pw = new PrintWriter( new FileWriter( privateKeyPath ) );
			pw.println("-------------PRIVATE_KEY-------------"); 
			
			BigInteger m=((RSAPrivateCrtKey)privateKey).getModulus();
			BigInteger e=((RSAPrivateCrtKey)privateKey).getPublicExponent();
			BigInteger privateExponent=((RSAPrivateCrtKey)privateKey).getPrivateExponent();
			BigInteger p=((RSAPrivateCrtKey)privateKey).getPrimeP();
			BigInteger q=((RSAPrivateCrtKey)privateKey).getPrimeQ();
			BigInteger dP=((RSAPrivateCrtKey)privateKey).getPrimeExponentP();
			BigInteger dQ=((RSAPrivateCrtKey)privateKey).getPrimeExponentQ();
			BigInteger qInv=((RSAPrivateCrtKey)privateKey).getCrtCoefficient();
			
			
			pw.println("bitlen="+m.bitLength()+";");
			
			String mStr=m.toString(16);
			if((mStr.length() % 2)==1)
				mStr="0"+mStr;
			pw.println("m="+mStr+";");
			
			String eStr = e.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("e="+eStr+";");
			
			eStr = privateExponent.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("privateExponent="+eStr+";");
			
			eStr = p.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("p="+eStr+";");
			
			eStr = q.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("q="+eStr+";");
			
			eStr = dQ.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("dQ="+eStr+";");
			
			eStr = dP.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("dP="+eStr+";");
			
			eStr = qInv.toString(16);
			if((eStr.length() % 2)==1)
				eStr="0"+eStr;
			pw.println("qInv="+eStr+";");
			
			pw.println("-------------PRIVATE_KEY-------------"); 
			
	        pw.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * encrypt source data use publicKey
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String source) throws Exception {
		getPublicKey();
		/**  */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] b = source.getBytes();
		/** do encrypt */
		byte[] b1 = cipher.doFinal(b);
		return new String(Hex.encode(b1));
	}

	/**
	 * decrypt cryptograph use privateKey
	 */
	public static String decrypt(String cryptograph) throws Exception {
		getPrivateKey();
		/** get Cipher object */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] b1 = Hex.decode(cryptograph);
		/** do decrypt */
		byte[] b = cipher.doFinal(b1);
		return new String(b);
	}

	/**
	 * 
	 * 用私钥对信息生成数字签名
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(String data) {
		try{
			getPrivateKey();
			// 用私钥对信息生成数字签名
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	
			signature.initSign(privateKey);
	
			signature.update(data.getBytes());
			byte[] b1 =  signature.sign();
			return new String(Hex.encode(b1));
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**  
     * 校验数字签名  
     * @param data  
     *            加密数据  
     * @param sign  
     *            数字签名  
     * @return 校验成功返回true 失败返回false  
     * @throws Exception  
     */  
    public static boolean verify(String data, String sign){
    	try{
	        // 取公钥匙对象   
    		getPublicKey();
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);   
	
	        signature.initVerify(publicKey);
			byte[] b1 = Hex.decode(sign);
	        signature.update(data.getBytes());   
	
	        // 验证签名是否正常   
	        return signature.verify(b1);   
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }   

	public static void main(String[] args) throws Exception {
		//生成密钥对
		//generateKeyPair();
		getPrivateKey();
		getPublicKey();
		//writePublicKey(PUBLIC_KEY_FILE+".key");
		String source = "296502429874592438576248524admin";// 要加密的字符串
		String cryptograph = encrypt(source);// 生成的密文
		System.out.println(cryptograph);
		//String cryptograph = "jJIDpSMhia6RMuhYYRDZdWWCsnTbJoMUO31JW/8a+48lcUszpD0n86NunpfyhPtWkq6k6Ehj3MgYia5sL/HmK73YY8rp/7g0IbAfeVZ+HfSpzQd+wpV03eqchVgItQH5jiGsfNmm78Wc+Sbyn+mUExlpZl8vCM0Q9yl6l3rSlTw=";
		String target = decrypt(cryptograph);// 解密密文
		System.out.println(target);
		
		//签名
		//String source="296502429874592438576248524admin";
		String s=sign(source);
		System.out.println(s);
		//验证
		System.out.println(verify(source, s));
	}
}
