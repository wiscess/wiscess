/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: JwtTokenUtils
 * Author:   wh
 * Date:     2020/8/10 8:46
 * Description: JwtTokenUtils
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.jwt;

import com.wiscess.security.WiscessSecurityProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionalOnProperty(prefix = "security.jwt", value = "enabled", matchIfMissing = true)
@Configuration
public class JwtTokenUtils {
    private static final String CLAIM_KEY_USERNAME = "sub";

    private static final String CLAIM_KEY_CREATED = "created";

    private static final String CLAIM_KEY_ROLES = "roles";

    @Autowired
    protected WiscessSecurityProperties wiscessSecurityProperties;

    /**
     * 从token中获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            username =getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            username = null;
        }
    	log.debug("getUsernameFromToken - token:{},username:{}",token,username);
        return username;
    }

    /**
     * 从token中获取生成时间
     * @param token
     * @return
     */
    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 从token中获取过期时间
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            String secret=wiscessSecurityProperties.getJwt().getSecret();
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成过期时间
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + wiscessSecurityProperties.getJwt().getExpiration() * 1000);
    }

    /**
     * 是否已经过期
     * @param token
     * @return
     */
     private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 根据用户对象生成token
     * @param userDetails
     * @return
     */
    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_ROLES, userDetails.getAuthorities());
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, wiscessSecurityProperties.getJwt().getSecret())
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 校验token
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        return (
                username.equals(user.getUsername())
                        && isTokenExpired(token)==false);
    }
}
