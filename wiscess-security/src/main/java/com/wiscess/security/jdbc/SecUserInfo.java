/**
 * Copyright (C), 2014-2021, 北京智成卓越科技有限公司
 * FileName: SecUserInfo
 * Author:   wh
 * Date:     2021/4/19 15:16
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecUserInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -652181178620021697L;

	private Integer userId;

    private String loginName;

    private String loginPwd;

    private String name;

    private String phone;

    private Boolean isUsed;

    private Date lastLoginTime;

    private Integer loginNum;

    private Integer loginFailNum;

    private Date lockTime;
}
