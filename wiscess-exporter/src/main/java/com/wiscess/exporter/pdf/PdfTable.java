package com.wiscess.exporter.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.wiscess.exporter.util.PositionUtils;

public class PdfTable extends Table{

	/**
	 * 表格显示宽度，分两种显示方式
	 * 1.绝对宽度
	 * 2.百分比
	 */
	private UnitValue unitWidth;
	/**
	 * 对齐方式
	 */
	private TextAlignment align=TextAlignment.LEFT;
	
	public PdfTable(float[] pointColumnWidths) {
		this(pointColumnWidths,false);
	}
	public PdfTable(float[] columnWidths, boolean largeTable) {
		super(columnWidths, largeTable);
	}
	/**
	 * 设置表格宽度
	 * @param width
	 * @return
	 */
	public PdfTable setWidth(float width){
		unitWidth=UnitValue.createPointValue(width);
		return (PdfTable) super.setWidth(unitWidth);
	}
	/**
	 * 设置表格宽度，百分比形式
	 * @param width
	 * @return
	 */
	public PdfTable setWidthPercent(float width){
		unitWidth=UnitValue.createPercentValue(width);
		return (PdfTable)super.setWidth(unitWidth);
	}
	/**
	 * 设置居中
	 * @param pageSize
	 * @return
	 */
	public PdfTable center(){
		//根据页面大小，计算居中
		this.align=TextAlignment.CENTER;
		return this;
	}
	/**
	 * 设置居右
	 * @param pageSize
	 * @return
	 */
	public PdfTable right(){
		//根据页面大小，计算居中
		this.align=TextAlignment.RIGHT;
		return this;
	}
	
	public Table addHeaderCell(String content) {
		super.addHeaderCell(new HeaderCell().add(content));
		return this;
	}
	public Table addFooterCell(String content) {
		super.addFooterCell(new FooterCell().add(content));
		return this;
	}
	
	/**
	 * 处理表格
	 * @return
	 */
	public void processTable(Document doc,PageSize pageSize,PdfMargin margin){
		//设置后，每列的宽度才正常
		setFixedLayout();
		
		/**
		 * 计算位置
		 */
		float offsetX=PositionUtils.offsetX(pageSize, margin, align, this.getWidth());
		setProperty(Property.POSITION, LayoutPosition.RELATIVE);
		setProperty(Property.LEFT, offsetX);

		
		doc.add(this);
		if(!isComplete())
			complete();
	}
}
