/**
 * Copyright (C), 2014-2020, 北京智成卓越科技有限公司
 * FileName: ParagraphStyle
 * Author:   wh
 * Date:     2020/7/14 16:34
 * Description: 段落样式
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
public class ParagraphStyle {
    private boolean isSpace;  //是否缩进
    private String before;      //段前磅数
    private String after;  //段后磅数
    private String beforeLines;        //段前行数
    private String afterLines;        //段后行数
    private boolean isLine;        //是否间距
    private String line;    //间距距离
    //段落缩进信息
    private String firstLine;
    private String firstLineChar;
    private String hanging;
    private String hangingChar;
    private String right;
    private String rightChar;
    private String left;
    private String leftChar;
}
