package com.wiscess.wechat.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.wiscess.utils.MD5Util;
import com.wiscess.utils.StringUtils;
import com.wiscess.wechat.config.WechatProperties;
import com.wiscess.wechat.service.WechatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WechatPayServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7990991839833228487L;

	@Autowired
	protected WechatProperties wechat;
	@Autowired
	private WechatService wechatService;
	
	/**
	 * 请求校验与处理
	 */
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.debug("WechatPayServlet.service");
		
		StringBuffer ret = new StringBuffer("");
		try {
			//读取参数  
	        InputStream inputStream ;  
	        StringBuffer resxml = new StringBuffer();  
	        inputStream = req.getInputStream();  
	        String s ;  
	        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));  
	        while ((s = in.readLine()) != null){  
	        	resxml.append(s);  
	        }  
	        in.close();  
	        inputStream.close();
	        
	        Map<String,String> map = xml2map(resxml.toString());
	        String code = map.get("result_code");
	        //code="SUCCESS";
	        System.out.println("----回单信息--code--"+code);
			if(StringUtils.isNotEmpty(code) && code.equals("SUCCESS")){
				System.out.println("----回单信息----");
				System.out.println("公众账号ID："+map.get("appid")+"\n"+ "商户号:"+map.get("mch_id")+"\n"+ "设备号:"+map.get("device_info")+"\n"+ "随机字符串:"+map.get("nonce_str")+"\n"+
						"签名:"+map.get("sign")+"\n"+ "签名类型:"+map.get("sign_type")+"\n"+ "业务结果:"+map.get("result_code")+"\n"+ "错误代码:"+map.get("err_code")+"\n"+
						"错误代码描述:"+map.get("err_code_des")+"\n"+ "用户标识:"+map.get("openid")+"\n"+ "交易类型:"+map.get("trade_type")+"\n"+ "付款银行:"+map.get("bank_type")+"\n"+
						"订单金额:"+map.get("total_fee")+"\n"+ "应结订单金额:"+map.get("settlement_total_fee")+"\n"+ "货币种类:"+map.get("fee_type")+"\n"+
						"微信支付订单号:"+map.get("transaction_id")+"\n"+ "商户订单号:"+map.get("out_trade_no")+"\n"+ "商家数据包:"+map.get("attach")+"\n"+ "支付完成时间:"+map.get("time_end")+"\n");
				String old_sign = map.get("sign");
				map.remove("xml");
				map.remove("sign");
				String new_sign = sortMapByASCII(map, wechat.getPay().getPaternerKey());
				
				if(new_sign.equals(old_sign)){//待增加签名验证：sign.equals(retSign)
					//支付成功
					ret.append("<xml><return_code><![CDATA[");
					ret.append("SUCCESS");
					ret.append("]]></return_code><return_msg><![CDATA[");
					ret.append("OK");
					ret.append("]]></return_msg></xml>");
					map.put("sign_status", "1");
				}else{
					//签名验证失败
					map.put("sign_status", "0");
				}
				
				wechatService.payResult(map);
				System.out.println("交易成功");
			}else{
				//交易失败
				System.out.println("交易失败");
			}
			
			//------------------------------  
	        //处理业务完毕  
	        //------------------------------  
	        BufferedOutputStream out = new BufferedOutputStream(res.getOutputStream());  
	        out.write(ret.toString().getBytes());  
	        out.flush();  
	        out.close(); 
	        
		} catch (Exception e) {
			//交易异常
			System.out.println("交易异常");
			e.printStackTrace();
		}
	}
	
	/**
	 * xml转map
	 * @param xml
	 * @return
	 */
	public static Map<String,String> xml2map(String xml){
		Map<String,String> map = new HashMap<String, String>();
		 try {
			Document document = DocumentHelper.parseText(xml); 
			Element root=document.getRootElement();//获取根节点  
			getNodes(root,map);//从根节点开始遍历所有节点  
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 从指定节点开始,递归遍历所有子节点
	 * @author 
	 */
	public static void getNodes(Element node,Map<String, String> map){
		//当前节点的名称、文本内容和属性
		map.put(node.getName(), node.getTextTrim());
		@SuppressWarnings("unchecked")
		List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list
		for(Attribute attr:listAttr){//遍历当前节点的所有属性
			String name=attr.getName();//属性名称
			String value=attr.getValue();//属性的值
			map.put(name, value);
		}
		
		//递归遍历当前节点所有的子节点
		@SuppressWarnings("unchecked")
		List<Element> listElement=node.elements();//所有一级子节点的list
		for(Element e:listElement){//遍历所有一级子节点
			getNodes(e,map);//递归
		}
	}
	/**
	 * 将map以key进行ASCII顺序由小大到排列
	 * @param paraMap
	 * @return
	 */
	public static String sortMapByASCII(Map<String, String> paraMap,String keyStr){
        Map<String, String> map = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        
        map.putAll(paraMap);
        
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        String strSign = "";
        while (iter.hasNext()) {
            String key = iter.next();
            //map值为空不进行拼接
            if(StringUtils.isNotEmpty(map.get(key))){
            	strSign += "&"+key+"="+map.get(key);
            }
        }
        strSign += "&key="+keyStr;
        String sign = MD5Util.md5(strSign.substring(1)).toUpperCase();
        return sign;
    }
	
}
