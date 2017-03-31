package com.wiscess.wechat.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.wiscess.wechat.pojo.JsApiTicket;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
public class JsSignUtil {
    /*
附录1-JS-SDK使用权限签名算法

jsapi_ticket 

	生成签名之前必须先了解一下jsapi_ticket，jsapi_ticket是公众号用于调用微信JS接口的临时票据。正常情况下，jsapi_ticket的有效期为7200秒，通过access_token来获取。由于获取jsapi_ticket的api调用次数非常有限，频繁刷新jsapi_ticket会导致api调用受限，影响自身业务，开发者必须在自己的服务全局缓存jsapi_ticket 。 
	1. 参考以下文档获取access_token（有效期7200秒，开发者必须在自己的服务全局缓存access_token）：../15/54ce45d8d30b6bf6758f68d2e95bc627.html 
	2. 用第一步拿到的access_token 采用http GET方式请求获得jsapi_ticket（有效期7200秒，开发者必须在自己的服务全局缓存jsapi_ticket）：
	https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi 

	成功返回如下JSON： 
	{
	"errcode":0,
	"errmsg":"ok",
	"ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
	"expires_in":7200
	}
	获得jsapi_ticket之后，就可以生成JS-SDK权限验证的签名了。 
	*/
	
	public static final String GETTICKET_URL="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    /**
	 * 缓存的JsApiTicket
	 */
	public static JsApiTicket jsApiTicketCache=null;
	public static Long lastJsApiTicketTime=0L;

	/**
	 * 获取网页JS签名
	 * @param appId 公众帐号的唯一标识
	 * @param appSecret 公众帐号的密钥
	 * @param code
	 * @return WeixinOauth2Token
	 */
	public static JsApiTicket getJsApiTicket(String accessToken){
		JsApiTicket ticket = null;
		
		//首先检查是否已经超时
		Long currentTimeStamp=Calendar.getInstance().getTimeInMillis();
		if(jsApiTicketCache==null || currentTimeStamp-lastJsApiTicketTime>3600000){
			//没有缓存的token或已超过1小时，重新获取token
		
			//拼接请求地址
			String requestUrl = GETTICKET_URL;
			//"https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
			requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
			
			//获取JS授权凭证
			JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
			
			if(null != jsonObject){
				try {
					ticket = new JsApiTicket();
					ticket.setErrcode(jsonObject.getInt("errcode"));
					ticket.setErrmsg(jsonObject.getString("errmsg"));
					ticket.setTicket(jsonObject.getString("ticket"));
					ticket.setExpiresIn(jsonObject.getInt("expires_in"));
				} catch (Exception e) {
					ticket = null;
					int errorCode = jsonObject.getInt("errcode");
					String errorMsg = jsonObject.getString("errmsg");
					log.error("获取JS授权凭证失败 errcode:{} errmsg:{}",errorCode,errorMsg);
				}
				
			}
			jsApiTicketCache=ticket;
			lastJsApiTicketTime=currentTimeStamp;
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			log.info("最近一次JsApiTicket时间（"+sf.format(new Date())+"）:"+ticket.getTicket());
		}
		return jsApiTicketCache;
	}
	
	/**
	 * 签名算法 

签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket, timestamp（时间戳）, url（当前网页的URL，不包含#及其后面部分） 。对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。 


 即signature=sha1(string1)。 示例： 
• noncestr=Wm3WZYTPz0wzccnW 
• jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg 
• timestamp=1414587457 
• url=http://mp.weixin.qq.com?params=value 


 步骤1. 对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1： 
jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=http://mp.weixin.qq.com?params=value



 步骤2. 对string1进行sha1签名，得到signature： 
0f9de62fce790f9a083d5c99e95740ceb90c27ed


注意事项 
1. 签名用的noncestr和timestamp必须与wx.config中的nonceStr和timestamp相同。 
2. 签名用的url必须是调用JS接口页面的完整URL。 
3. 出于安全考虑，开发者必须在服务器端实现签名的逻辑。 


 如出现invalid signature 等错误详见附录5常见错误及解决办法。 

	 */
	
    /**
     * 获取Js接口签名等信息
     * @param jsapi_ticket
     * @param url
     * @return
     */
    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }
    
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
