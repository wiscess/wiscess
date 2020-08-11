/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: DefaultUserMapRepository
 * Author:   wh
 * Date:     2020/8/10 13:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.jwt;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultUserMapRepository implements UserRepository{

    private static final Map<String, String> userMap = new HashMap<String,String>();

    public String findByToken(final String token){
        return userMap.get(token);
    }

    public void insert(String token,String user){
        userMap.put(token,user);
    }

    public void remove(String token){
        userMap.remove(token);
    }
}
