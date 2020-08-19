package com.wiscess.oauth.config;

import lombok.Getter;

@Getter
public enum OAuthAway {
    /**
     * 内存方式
     */
    memory,
    /**
     * jdbc方式
     */
    jdbc,
    /**
     * redis方式
     */
    redis
}
