package com.wiscess.wechat.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 弹出拍照或者相册发图 类型的按钮
 * 
 * @author liudg
 * @date 2014-11-05
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@Builder
public class PicPhotoOrAlbumButton extends Button {
	@Builder.Default
	private String type = "pic_photo_or_album";
	@Builder.Default
	private String key="rselfmenu_1_1";

}