package com.wiscess.oauth.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.wiscess.oauth.exception.WiscessOAuth2Exception;

public interface AuthorizationDeniedResponse {
	 /**
     * Provide the response HttpStatus, default is 401
     *
     * @return {@link HttpStatus}
     */
    default HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    /**
     * Serialize the response content
     *
     * @param e         {@link ApiBootOAuth2Exception}
     * @param generator {@link JsonGenerator}
     */
    default void serializeResponse(WiscessOAuth2Exception e, JsonGenerator generator) {
        // default nothing to do
    }
}
