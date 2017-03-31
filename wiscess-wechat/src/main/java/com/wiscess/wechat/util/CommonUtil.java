package com.wiscess.wechat.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.wiscess.wechat.pojo.Token;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

/**
 * 通用工具类
 * 
 * @author wanghai	
 * @date 2014-06-11
 */
@Slf4j
public class CommonUtil {

	// 凭证获取（GET）
	public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	/**
	 * 缓存的token
	 */
	public static Token tokenCache=null;
	public static Long lastTokenTime=0L;
	
	/**
	 * 发送https请求
	 * 
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https请求异常：{}", e);
		}
		return jsonObject;
	}

	/**
	 * 获取接口访问凭证
	 * 
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	public static Token getToken(String appid, String appsecret) {
		Token token = null;
		String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		//首先检查是否已经超时
		Long currentTimeStamp=Calendar.getInstance().getTimeInMillis();
		if(tokenCache==null || currentTimeStamp-lastTokenTime>3600000){
			//没有缓存的token或已超过1小时，重新获取token

			// 发起GET请求获取凭证
			JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

			if (null != jsonObject) {
				try {
					token = new Token();
					token.setAccessToken(jsonObject.getString("access_token"));
					token.setExpiresIn(jsonObject.getInt("expires_in"));
				} catch (Exception e) {//JSONException
					token = null;
					// 获取token失败
					log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
				}
			}
			tokenCache=token;
			lastTokenTime=currentTimeStamp;
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			log.info("最近一次token时间（"+sf.format(new Date())+"）:"+token.getAccessToken());
		}
		
		return tokenCache;
	}
	
	/**
	 * 根据内容类型判断文件扩展名
	 * 
	 * @param contentType 内容类型
	 * @return
	 */
	public static String getFileExt(String contentType) {
		String fileExt = "";
		if ("image/jpeg".equals(contentType))
			fileExt = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileExt = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileExt = ".amr";
		else if ("video/mp4".equals(contentType))
			fileExt = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileExt = ".mp4";
		return fileExt;
	}
	
}