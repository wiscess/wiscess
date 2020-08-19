package com.wiscess.oauth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.wiscess.oauth.response.AuthorizationDeniedResponse;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
@JsonSerialize(using = WiscessOauthExceptionSerializer.class)
public class WiscessOAuth2Exception extends OAuth2Exception {

    private AuthorizationDeniedResponse response;

    public WiscessOAuth2Exception(String message, Throwable t, AuthorizationDeniedResponse response) {
        super(message, t);
        this.response = response;
    } 
}

