package com.wiscess.wechat.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通过网页授权获取的用户
 * @author liudongge
 * @date 2014-07-17
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SNSUserInfo {
	//用户标识
	private String openId;
	//union标识
	private String unionId;
	//用户昵称
	private String nichname;
	//性别（1 是男性，2 是女性，0 是未知）
	private int sex;
	//国家
	private String country;
	//省份
	private String province;
	//城市
	private String city;
	//用户头像链接
	private String headImgUrl;
	//用户特权信息
	private List<String> privilegeList;
	
}
