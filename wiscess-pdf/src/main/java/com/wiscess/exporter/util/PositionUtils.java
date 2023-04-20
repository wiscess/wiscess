package com.wiscess.exporter.util;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.wiscess.exporter.pdf.PdfMargin;

/**
 * 计算位置信息
 * @author wh
 *
 */
public class PositionUtils {

	/**
	 * 根据页面大小，页边距，对象宽度，对齐方式，计算左面的偏移量
	 * @return
	 */
	public static float offsetX(PageSize pageSize,PdfMargin margin,TextAlignment align,UnitValue objWidth){
		float offsetX=0;
		//显示区域宽度
		float areaWidth=pageSize.getWidth()-margin.getLeft()-margin.getRight();
		//根据objWidth的类型，计算实际宽度
		float objRealWidth=(objWidth.getUnitType()==UnitValue.POINT)?objWidth.getValue():areaWidth*objWidth.getValue()/100;
		switch (align) {
		case RIGHT:
			//居右
			//        ps.getWidth()
			//-----------------------------------|
			//|left  offsetX         w      right|
			//|    .............------------     |
			//|                 |          |     |
			//|                 ------------     |
			offsetX = (areaWidth - objRealWidth);
			break;
		case CENTER:
			//居中
			offsetX = (areaWidth - objRealWidth) / 2;
			break;
		default:
			//默认居左，不处理
			break;
		}
		return offsetX;
	}
}
