/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: TextStyle
 * Author:   wh
 * Date:     2020/7/14 16:33
 * Description: 文本样式
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.exporter.word;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextStyle extends AssignedElement{
    private String url;    // 超链接
    private String text;    //文本内容
    private String fontFamily;    //字体
    private String fontSize;    //字体大小
    private String colorVal;    //字体颜色
    private String shdColor;    //底纹颜色
    private int position;    //文本位置
    private int spacingValue;    //间距
    private int indent;    //缩进
    private boolean isBlod;    //加粗
    private boolean isUnderLine;    //下划线
    private boolean underLineColor;    //
    private boolean isItalic;    //倾斜
    private boolean isStrike;    //删除线
    private boolean isDStrike;    //双删除线
    private boolean isShadow;    //阴影
    private boolean isVanish;    //隐藏
    private boolean isEmboss;    //阳文
    private boolean isImprint;    //阴文
    private boolean isOutline;    //空心
    private boolean isHightLight;    //突出显示文本
    private boolean isShd;    //底纹

}
