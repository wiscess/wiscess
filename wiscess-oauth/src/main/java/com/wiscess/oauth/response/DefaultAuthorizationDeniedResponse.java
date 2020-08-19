package com.wiscess.oauth.response;

import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.wiscess.oauth.exception.WiscessOAuth2Exception;

public class DefaultAuthorizationDeniedResponse implements AuthorizationDeniedResponse {
	 @Override
	 public void serializeResponse(WiscessOAuth2Exception e, JsonGenerator generator) {
        try {
            String message = e.getMessage();
            if (message != null) {
                message = HtmlUtils.htmlEscape(message);
            }
            //模拟生成R对象的数据格式。R.error("message");
            generator.writeObjectField("success", false);
            generator.writeObjectField("message",message);
            generator.writeObjectField("code", "20001"); //HttpStatus.FORBIDDEN.getReasonPhrase());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
