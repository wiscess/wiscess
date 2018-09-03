package com.wiscess.exporter.pdf;

import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Pdf的元素，
 * 基于iText7.1.2，分为三大类：
 * IBlockElement：可以直接添加到pdf中
 *   Cell:
 *   Div:
 *     Listitem:
 *   LineSeparator:
 *   List:
 *   Paragraph:
 *   Table:
 * ILargeElement:可以直接添加到pdf中
 *   Table:
 * ILeafElement: 叶子元素，无法添加其他元素内容
 *   Image: 可以直接添加到pdf中
 *   Tab:
 *   Text:  需添加到Paragraph
 *     Link:  
 * @author wh
 *
 */
public abstract class PdfElement implements IElement{
	public IRenderer createRendererSubTree() {
		return null;
	}

	public IRenderer getRenderer() {
		return null;
	}

	public void setNextRenderer(IRenderer arg0) {
	}

	public void deleteOwnProperty(int arg0) {
	}

	public <T1> T1 getDefaultProperty(int arg0) {
		return null;
	}

	public <T1> T1 getOwnProperty(int arg0) {
		return null;
	}

	public <T1> T1 getProperty(int arg0) {
		return null;
	}

	public boolean hasOwnProperty(int arg0) {
		return false;
	}

	public boolean hasProperty(int arg0) {
		return false;
	}

	public void setProperty(int arg0, Object arg1) {
	}
}
