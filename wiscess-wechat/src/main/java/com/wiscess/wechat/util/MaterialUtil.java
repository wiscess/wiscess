package com.wiscess.wechat.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wiscess.utils.StringUtils;
import com.wiscess.wechat.dto.WxItemDto;
import com.wiscess.wechat.dto.WxItemNewDto;
import com.wiscess.wechat.dto.WxItemParamsDto;
import com.wiscess.wechat.dto.WxItemsDto;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 素材管理类
 * 
 * @author liudg
 * @date 2017-09-06
 */
@Slf4j
public class MaterialUtil extends BaseUtil {
	
	/**
	 * 获取素材总数
	 * "voice_count":语音总数量
	 * "video_count":视频总数量
	 * "image_count":图片总数量
	 * "news_count":图文总数量
	 * 
	 */
	public static Map<String,Integer> findMaterialCount(String accessToken){
		String requestUrl="https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		//发送请求
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if(null != jsonObject){
			try {
				Map<String,Integer> map = new HashMap<String, Integer>();
				map.put("voice_count", getInt(jsonObject,"voice_count"));
				map.put("video_count", getInt(jsonObject,"video_count"));
				map.put("image_count", getInt(jsonObject,"image_count"));
				map.put("news_count", getInt(jsonObject,"news_count"));
				return map;
			} catch (Exception e) {
				int errorCode = getInt(jsonObject,"errcode");
				String errorMsg = getString(jsonObject,"errmsg");
				log.error("获取获取素材总数失败 errcode:{} errmsg:{}",errorCode,errorMsg);
			}
		}
		return null;
	}
	
	/**
	 * 获取素材列表
	 * @param accessToken
	 * @param paramJson
	 * 		示例：
	 *			JSONObject paramJson = new JSONObject();
	 *			//素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
	 *			paramJson.put("type", "news");
	 *			//从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	 *			paramJson.put("offset", 0);
	 *			//返回素材的数量，取值在1到20之间
	 *			paramJson.put("count", 20);
	 * @return
	 */
	public static WxItemDto findMaterialByParam(String accessToken,WxItemParamsDto params){
		String requestUrl="https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		
		JSONObject paramJson = JSONObject.fromObject(params);
		//发送请求--获取素材列表
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", paramJson.toString());
		
		if(null != jsonObject){
			try {
				WxItemDto itemObj = new WxItemDto();
				itemObj.setTotalCount(jsonObject.getInt("total_count"));
				itemObj.setItemCount(jsonObject.getInt("item_count"));
				
				//素材列表集合
				List<WxItemsDto> item_arr = new ArrayList<WxItemsDto>();
				JSONArray itemsArray = jsonObject.getJSONArray("item");
				if(itemsArray!=null && itemsArray.size()>0){
					@SuppressWarnings("unchecked")
					Iterator<Object> items = itemsArray.iterator();
					while (items.hasNext()) {
						JSONObject item = (JSONObject) items.next();
						WxItemsDto obj = new WxItemsDto();
						obj.setMediaId(item.getString("media_id"));
						if(paramJson.getString("type").equals("news")){
							//图文消息内容集合
							List<WxItemNewDto> news_arr = makeNewsByContent(item.getString("content"));
							obj.setContents(news_arr);
						}else{
							obj.setName(item.getString("name"));
							obj.setUrl(item.getString("url"));
						}
						obj.setUpdateTime(item.getString("update_time"));
						item_arr.add(obj);
					}
				}
				itemObj.setItems(item_arr);
				return itemObj;
			} catch (Exception e) {
				int errorCode = getInt(jsonObject,"errcode");
				String errorMsg = getString(jsonObject,"errmsg");
				log.error("获取素材列表失败 errcode:{} errmsg:{}",errorCode,errorMsg);
			}
		}
		return null;
	}
	
	/**
	 * 根据图文素材mediaId获取图文信息
	 * @param mediaId
	 * @return
	 */
	public static List<WxItemNewDto> getNewsByMediaId(String accessToken,String mediaId){
		String requestUrl="https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject params = new JSONObject();
		params.put("media_id", mediaId);
		System.out.println(params.toString());
		//发送请求--获取素材列表
		String jsonString = CommonUtil.httpsRequestToString(requestUrl, "POST", params.toString());
		System.out.println("jsonString:"+jsonString);
		
//		if(null != jsonObject){
//			try {
//				return makeNewsByContent(jsonObject.toString());
//			} catch (Exception e) {
//				int errorCode = getInt(jsonObject,"errcode");
//				String errorMsg = getString(jsonObject,"errmsg");
//				log.error("获取图文素材失败 errcode:{} errmsg:{}",errorCode,errorMsg);
//			}
//		}
		return null;
	}
	
	/**
	 * 组装图文消息集合
	 * @param newsArray
	 * @return
	 */
	public static List<WxItemNewDto> makeNewsByContent(String content){
		
		//图文消息内容集合
		List<WxItemNewDto> news_arr = new ArrayList<WxItemNewDto>();
		if(StringUtils.isNotEmpty(content)){
			JSONObject jsonObject = JSONObject.fromObject(content);
			JSONArray newsArray = jsonObject.getJSONArray("news_item");
			
			@SuppressWarnings("unchecked")
			Iterator<Object> new_items = newsArray.iterator();
			while (new_items.hasNext()) {
				JSONObject news = (JSONObject) new_items.next();
				WxItemNewDto new_item = WxItemNewDto.builder()
					.title(news.getString("title"))
					.thumbMediaId(news.getString("thumb_media_id"))
					.showCoverPic(news.getString("show_cover_pic"))
					.author(news.getString("author"))
					.digest(news.getString("digest"))
					.content(news.getString("content"))
					.url(news.getString("url"))
					.contentSourceUrl(news.getString("content_source_url"))
					.build();
				news_arr.add(new_item);
			}
		}
		return news_arr;
	}
	
	/**
	 * 下载永久素材图片
	 * @param accessToken
	 * @param mediaId
	 * @param savePath
	 * @return
	 */
	public static String getImageByMediaId(String accessToken,String mediaId,String savePath){
		
		// 拼接请求地址
		String requestUrl="https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject params = new JSONObject();
		params.put("media_id", mediaId);
		
		String filePath = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			
			OutputStream outputStream = conn.getOutputStream();
			// 注意编码格式
			outputStream.write(params.toString().getBytes("UTF-8"));
			outputStream.close();

			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			// 根据内容类型获取扩展名
			String fileExt = CommonUtil.getFileExt(conn.getHeaderField("Content-Type"));
			fileExt = StringUtils.isEmpty(fileExt) ? ".jpg" : fileExt;
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
			log.info("下载永久素材文件成功，filePath=" + filePath);
		} catch (Exception e) {
			filePath = null;
			log.error("下载永久素材文件失败：{}", e);
		}
		return filePath;
	}
}
