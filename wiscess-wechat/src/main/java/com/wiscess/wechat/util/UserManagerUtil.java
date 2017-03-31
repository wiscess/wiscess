package com.wiscess.wechat.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wiscess.wechat.pojo.WeixinUserList;



public class UserManagerUtil {

	private static Logger log = LoggerFactory.getLogger(UserManagerUtil.class);
	
	/**
	 * 获取关注者列表
	 * 
	 * @param accessToken 调用接口凭证
	 * @param nextOpenId 第一个拉取的openId，不填默认从头开始拉取
	 * @return WeixinUserList
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public static WeixinUserList getUserList(String accessToken, String nextOpenId) {
		WeixinUserList weixinUserList = null;

		if (null == nextOpenId)
			nextOpenId = "";

		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("NEXT_OPENID", nextOpenId);
		// 获取关注者列表
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				weixinUserList = new WeixinUserList();
				weixinUserList.setTotal(jsonObject.getInt("total"));
				weixinUserList.setCount(jsonObject.getInt("count"));
				weixinUserList.setNextOpenId(jsonObject.getString("next_openid"));
				JSONObject dataObject = (JSONObject) jsonObject.get("data");
				weixinUserList.setOpenIdList(JSONArray.toList(dataObject.getJSONArray("openid"), List.class));
			} catch (Exception e) {
				weixinUserList = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取关注者列表失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinUserList;
	}
	
	
	public static Integer getUserGroupId(String accessToken,String openId){
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		
		JSONObject jsonObject;
		try {
			//需要提交的JSON数据
			String jsonData = "{\"openid\":\"%s\"}";
			
			jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData,openId));
			
			if(null != jsonObject){
				try {
					return jsonObject.getInt("groupid");
				} catch (Exception e) {
					int errorCode = jsonObject.getInt("errcode");
					String errorMsg = jsonObject.getString("errmsg");
					log.error("获取关注者所在分组 errcode:{} errmsg:{}",errorCode,errorMsg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
}
