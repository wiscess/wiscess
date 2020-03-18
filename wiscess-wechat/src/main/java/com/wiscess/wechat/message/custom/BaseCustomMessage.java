/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: BaseCustomMessage
 * Author:   wh
 * Date:     2020/3/2 12:12
 * Description: 客服文本消息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.wechat.message.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

public  abstract class BaseCustomMessage implements ICustomMessage{

    public String toJson(){
    	 ObjectMapper mapper = new ObjectMapper();
    	 mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
    }

}
