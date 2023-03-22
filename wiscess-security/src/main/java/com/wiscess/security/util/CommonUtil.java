/**
 * Copyright (C), 2014-2021, 北京智成卓越科技有限公司
 * FileName: CommonUtil
 * Author:   wh
 * Date:     2021/4/19 16:10
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.wiscess.security.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
    //检测两个时间之间的间隔是多少分钟
    public static int  intervalMinutesBetweenTwoDateTime(Date date1, Date date2)
    {
        float  ret = 0;
        try
        {
            Calendar cal = Calendar.getInstance();

            cal.setTime(date1);
            long millisOfdate1 = cal.getTimeInMillis();

            cal.setTime(date2);
            long millisOfDate2 = cal.getTimeInMillis();

            float intervalOfTime = new Float(millisOfDate2 - millisOfdate1);
            float baseTime = new Float(60 * 1000);

            ret = intervalOfTime/baseTime;
            return Integer.parseInt(round(Float.toString(ret), 0));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /**
     * 提供精确的小数位四舍五入处理(>=.5就进一，如：2.55，保留一位小数，则该方法返回2.6；2.56，保留一位小数，则该方法返回2.6)。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(scale, RoundingMode.HALF_UP).toString();
    }
}
