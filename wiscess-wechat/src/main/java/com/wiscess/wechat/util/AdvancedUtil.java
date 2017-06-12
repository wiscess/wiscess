package com.wiscess.wechat.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.wiscess.wechat.pojo.SNSUserInfo;
import com.wiscess.wechat.pojo.WeixinMedia;
import com.wiscess.wechat.pojo.WeixinOauth2Token;
import com.wiscess.wechat.pojo.WeixinUserInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 网页授权凭证工具类
 * @author liudongge
 * @date 2014-7-17
 */
@Slf4j
public class AdvancedUtil {
	
	/**
	 * 获取网页授权凭证
	 * @param appId 公众帐号的唯一标识
	 * @param appSecret 公众帐号的密钥
	 * @param code
	 * @return WeixinOauth2Token
	 */
	public static WeixinOauth2Token getOauth2AccessToken(String appId,String appSecret,String code){
		WeixinOauth2Token wat = null;
		//拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		log.debug("基类：获取网页授权凭证：code="+code);
		requestUrl = requestUrl.replace("APPID", appId);
		requestUrl = requestUrl.replace("SECRET", appSecret);
		requestUrl = requestUrl.replace("CODE", code);
		
		//获取网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if(null != jsonObject){
			try {
				wat = new WeixinOauth2Token();
				wat=WeixinOauth2Token.builder()
						.accessToken(getString(jsonObject,"access_token"))
						.expiresIn(getInt(jsonObject,"expires_in"))
						.refreshToken(getString(jsonObject,"refresh_token"))
						.openId(getString(jsonObject,"openid"))
						.scope(getString(jsonObject,"scope"))
						.unionId(getString(jsonObject,"unionid"))
						.build();
			} catch (Exception e) {
				wat = null;
				int errorCode = getInt(jsonObject,"errcode");
				String errorMsg = getString(jsonObject,"errmsg");
				log.error("获取网页授权凭证失败 errcode:{} errmsg:{}",errorCode,errorMsg);
			}
		}
		return wat;
	}
	
	public static String getString(JSONObject jsonObject,String key){
		if(jsonObject.containsKey(key))
			return jsonObject.getString(key);
		return null;
	}
	
	public static Integer getInt(JSONObject jsonObject,String key){
		if(jsonObject.containsKey(key))
			return jsonObject.getInt(key);
		return null;
	}
	/**
	 * 刷新网页授权凭证
	 * @param appId 公众帐号的唯一标识号
	 * @param refreshToken
	 * @return WeixinOauth2Token
	 */
	public static WeixinOauth2Token refreshOauth2AccessToken(String appId,String refreshToken){
		WeixinOauth2Token wat = null;
		//拼接请求地址
		String requestUrl = "http://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		requestUrl = requestUrl.replace("APPID", appId);
		requestUrl = requestUrl.replace("REFRESH_TOKEN", refreshToken);
		//刷新网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				wat = new WeixinOauth2Token();
				wat=WeixinOauth2Token.builder()
						.accessToken(getString(jsonObject,"access_token"))
						.expiresIn(getInt(jsonObject,"expires_in"))
						.refreshToken(getString(jsonObject,"refresh_token"))
						.openId(getString(jsonObject,"openid"))
						.scope(getString(jsonObject,"scope"))
						.unionId(getString(jsonObject,"unionid"))
						.build();
			} catch (Exception e) {
				wat = null;
				int errorCode = getInt(jsonObject,"errcode");
				String errorMsg = getString(jsonObject,"errmsg");
				log.error("刷新网页授权凭证失败 errcode:{} errmsg:{}",errorCode,errorMsg);
			}
		}
		return wat;
	}
	
	
	/**
	 * 通过网页授权获取用户信息
	 * @param accessToken 网页授权接口调用凭证
	 * @param openId 用户标识
	 * @return SNSUserInfo
	 */
	@SuppressWarnings({"deprecation" , "unchecked"})
	public static SNSUserInfo getSNSUserInfo(String accessToken,String openId){
		SNSUserInfo snsUserInfo = null;
		//拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		//通过网页授权获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				snsUserInfo = new SNSUserInfo();
				//用户标识
				snsUserInfo.setOpenId(getString(jsonObject,"openid"));
				//union标识
				snsUserInfo.setUnionId(getString(jsonObject,"unionid"));
				//昵称
				snsUserInfo.setNichname(getString(jsonObject,"nickname"));
				//性别（1 是男性，2是女性，0是未知）
				snsUserInfo.setSex(getInt(jsonObject,"sex"));
				//用户所在国家
				snsUserInfo.setCountry(getString(jsonObject,"country"));
				//用户所在省份
				snsUserInfo.setProvince(getString(jsonObject,"province"));
				//用户所在城市
				snsUserInfo.setCity(getString(jsonObject,"city"));
				//用户头像
				snsUserInfo.setHeadImgUrl(getString(jsonObject,"headimgurl"));
				//用户特权信息
				snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"),List.class));
			} catch (Exception e) {
				snsUserInfo = null;
				int errorCode = getInt(jsonObject,"errcode");
				String errorMsg = getString(jsonObject,"errmsg");
				log.error("获取用户信息失败 errcode:{} errmsg:{}",errorCode,errorMsg);
			}
		}
		return snsUserInfo;
	}
	
	/**
	 * 上传媒体文件
	 * 
	 * @param accessToken 接口访问凭证
	 * @param type 媒体文件类型（image、voice、video和thumb）
	 * @param mediaFileUrl 媒体文件的url
	 */
	public static WeixinMedia uploadMedia(String accessToken, String type, String mediaFileUrl) {
		WeixinMedia weixinMedia = null;
		// 拼装请求地址
		String uploadMediaUrl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

		// 定义数据分隔符
		String boundary = "------------7da2e536604c8";
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod("POST");
			// 设置请求头Content-Type
			uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();

			URL mediaUrl = new URL(mediaFileUrl);
			HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
			meidaConn.setDoOutput(true);
			meidaConn.setRequestMethod("GET");

			// 从请求头中获取内容类型
			String contentType = meidaConn.getHeaderField("Content-Type");
			// 根据内容类型判断文件扩展名
			String fileExt = CommonUtil.getFileExt(contentType);
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n", fileExt).getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());

			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				// 将媒体文件写到输出流（往微信服务器写数据）
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();
			meidaConn.disconnect();

			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();

			// 使用JSON-lib解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
			weixinMedia = new WeixinMedia();
			weixinMedia.setType(getString(jsonObject,"type"));
			// type等于thumb时的返回结果和其它类型不一样
			if ("thumb".equals(type))
				weixinMedia.setMediaId(getString(jsonObject,"thumb_media_id"));
			else
				weixinMedia.setMediaId(getString(jsonObject,"media_id"));
			weixinMedia.setCreatedAt(getInt(jsonObject,"created_at"));
		} catch (Exception e) {
			weixinMedia = null;
			log.error("上传媒体文件失败：{}", e);
		}
		return weixinMedia;
	}

	/**
	 * 下载媒体文件
	 * 
	 * @param accessToken 接口访问凭证
	 * @param mediaId 媒体文件标识
	 * @param savePath 文件在服务器上的存储路径
	 * @return
	 */
	public static String getMedia(String accessToken, String mediaId, String savePath) {
		String filePath = null;
		// 拼接请求地址
		String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");

			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			// 根据内容类型获取扩展名
			String fileExt = CommonUtil.getFileExt(conn.getHeaderField("Content-Type"));
			// 将mediaId作为文件名
			filePath = savePath + mediaId + fileExt;

			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
			fos.close();
			bis.close();
			conn.disconnect();
			log.info("下载媒体文件成功，filePath=" + filePath);
		} catch (Exception e) {
			filePath = null;
			log.error("下载媒体文件失败：{}", e);
		}
		return filePath;
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @param accessToken 接口访问凭证
	 * @param openId 用户标识
	 * @return WeixinUserInfo
	 */
	public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
		WeixinUserInfo weixinUserInfo = null;
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				weixinUserInfo = new WeixinUserInfo();
				// 用户的标识
				weixinUserInfo.setOpenId(getString(jsonObject,"openid"));
				//unionId
				weixinUserInfo.setUnionId(getString(jsonObject,"unionid"));
				// 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
				weixinUserInfo.setSubscribe(getInt(jsonObject,"subscribe"));
				// 用户关注时间
				weixinUserInfo.setSubscribeTime(getString(jsonObject,"subscribe_time"));
				// 昵称
				weixinUserInfo.setNickname(getString(jsonObject,"nickname"));
				// 用户的性别（1是男性，2是女性，0是未知）
				weixinUserInfo.setSex(getInt(jsonObject,"sex"));
				// 用户所在国家
				weixinUserInfo.setCountry(getString(jsonObject,"country"));
				// 用户所在省份
				weixinUserInfo.setProvince(getString(jsonObject,"province"));
				// 用户所在城市
				weixinUserInfo.setCity(getString(jsonObject,"city"));
				// 用户的语言，简体中文为zh_CN
				weixinUserInfo.setLanguage(getString(jsonObject,"language"));
				// 用户头像
				weixinUserInfo.setHeadImgUrl(getString(jsonObject,"headimgurl"));
			} catch (Exception e) {
				if (0 == weixinUserInfo.getSubscribe()) {
					log.error("用户{}已取消关注", weixinUserInfo.getOpenId());
				} else {
					int errorCode = getInt(jsonObject,"errcode");
					String errorMsg = getString(jsonObject,"errmsg");
					log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
				}
			}
		}
		return weixinUserInfo;
	}
}
