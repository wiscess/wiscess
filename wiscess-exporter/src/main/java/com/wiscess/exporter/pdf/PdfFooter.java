package com.wiscess.exporter.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 页码设置
 * @author wh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfFooter {
	
	protected String footerLeft;
	protected String footerCenter;
	protected String footerRight;
	/**
	 * 默认字体大小
	 */
	@Builder.Default
	private Integer fontSize=12;
	/**
	 * 开始页码，默认从1开始计算
	 * realPageNo=pageNumber-(1-firstPageNumber)
	 */
	protected Integer firstPageNumber=1;
	/**
	 * 首页是否显示页码
	 * 当不显示时，firstPageNumber要设置为0
	 */
	protected Boolean firstPageShow=true;
	/**
	 * 页码格式化
	 * 如：第{}页
	 * 只接收一个当前页的参数，不支持显示总页码
	 */
	protected String pageFormat="第{}页";
	
	/**
	 * 显示位置
	 * 默认居中
	 * 可设置：LEFT、CENTER、RIGHT、BOOK
	 * LEFT：固定居左
	 * CENTER：固定居中
	 * RIGHT：固定居右
	 * BOOK：类似书本翻页，奇数居右，偶数居左
	 */
	protected PageNoPosition position=PageNoPosition.CENTER;
	
	public enum PageNoPosition {
	    LEFT,
	    CENTER,
	    RIGHT,
	    BOOK
	}
}
