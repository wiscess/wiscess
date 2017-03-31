package com.wiscess.wechat.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.wiscess.wechat.message.model.Article;
import com.wiscess.wechat.message.model.Music;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SendMessageUtil {
	
	//////////////////////////////////////////////////////////////////////////
	//						**发送客服消息**									//
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * 组装文本客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param content 文本消息内容
	 * @return
	 */
	public static String makeTextCustomMessage(String openId, String content) {
		// 对消息内容中的双引号进行转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}

	/**
	 * 组装图片客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @return
	 */
	public static String makeImageCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * 组装语音客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @return
	 */
	public static String makeVoiceCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"voice\",\"voice\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * 组装视频客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @param thumbMediaId 视频消息缩略图的媒体id
	 * @return
	 */
	public static String makeVideoCustomMessage(String openId, String mediaId, String thumbMediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"video\",\"video\":{\"media_id\":\"%s\",\"thumb_media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId, thumbMediaId);
	}

	/**
	 * 组装音乐客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param music 音乐对象
	 * @return
	 */
	public static String makeMusicCustomMessage(String openId, Music music) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"music\",\"music\":%s}";
		jsonMsg = String.format(jsonMsg, openId, JSONObject.fromObject(music).toString());
		// 将jsonMsg中的thumbmediaid替换为thumb_media_id
		jsonMsg = jsonMsg.replace("thumbmediaid", "thumb_media_id");
		return jsonMsg;
	}

	/**
	 * 组装图文客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param articleList 图文消息列表
	 * @return
	 */
	public static String makeNewsCustomMessage(String openId, List<Article> articleList) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\":%s}}";
		jsonMsg = String.format(jsonMsg, openId, JSONArray.fromObject(articleList).toString().replaceAll("\"", "\\\""));
		// 将jsonMsg中的picUrl替换为picurl
		jsonMsg = jsonMsg.replace("picUrl", "picurl");
		return jsonMsg;
	}

	/**
	 * 发送客服消息
	 * 
	 * @param accessToken 接口访问凭证
	 * @param jsonMsg json格式的客服消息（包括touser、msgtype和消息内容）
	 * @return true | false
	 */
	public static int sendCustomMessage(String accessToken, String jsonMsg) {
		log.info("消息内容：{}", jsonMsg);
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
		
		//预设-1，若为-1，则证明没有返回结果
		int errorCode = -1 ;
		if (null != jsonObject) {
			errorCode = jsonObject.getInt("errcode");
		}
		return errorCode;
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	//						**高级群发**										//
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * 高级群发接口
	 * 
	 * @param accessToken 接口访问凭证
	 * @param jsonMsg json格式的客服消息（包括touser、msgtype和消息内容）
	 * @return true | false
	 */
	public static boolean sendAllMessage(String accessToken, String jsonMsg) {
		log.info("消息内容：{}", jsonMsg);
		boolean result = false;
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("群发消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("群发消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	
	/**
	 * 组装文本高级群发消息
	 * 
	 * @param openId 消息发送对象
	 * @param content 文本消息内容
	 * @return
	 */
	public static String makeTextAdvancedMessage(List<String> openId, String content) {
		// 对消息内容中的双引号进行转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":%s,\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//						**高级群发**										//
	//////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 高级群发接口
	 * 
	 * @param accessToken 接口访问凭证
	 * @param jsonMsg json格式的客服消息（包括touser、msgtype和消息内容）
	 * @return true | false
	 */
	public static int sendTemplateMessage(String accessToken, String jsonMsg) {
		int result = 0;
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				log.info("模版消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				result = errorCode;
				log.error("模版消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg+": "+accessToken);
			}
		}
		return result;
	}
	
	/**
	 * 组装模版消息
	 * 		（acnam3uXKSmAnzl3iUXp_6wGkd022ziL7HkTwu_M9Hw -- 日历事件提醒模版）
	 * 
	 * @param openId 消息发送对象
	 * @param content 文本消息内容
	 * @return
	 */
	public static String makeTextTemplateMessage(String openId,String templateId,String url,String topcolor, String first,String schedule,String time,String remark) {
		// 对消息内容中的双引号进行转义
		String jsonMsg = "{\"touser\":\"%s\",\"template_id\":\"%s\",\"url\":\"%s\",\"topcolor\":\"%s\"," +
				"\"data\":{\"first\":{\"value\":\"%s\",\"color\":\"#743A3A\"},\"schedule\":{\"value\":\"%s\",\"color\":\"#743A3A\"}," +
				"\"time\":{\"value\":\"%s\",\"color\":\"#743A3A\"},\"remark\":{\"value\":\"%s\",\"color\":\"#743A3A\"}}}";
		return String.format(jsonMsg, openId,templateId,url,topcolor, first,schedule,time,remark);
	}
}
