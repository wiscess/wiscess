package com.wiscess.exporter.pdf.renderer;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

 /**
 * 
 * @author wh
 *
 */
public class ImageBackgroundCellRenderer extends CellRenderer {
    protected Image img;

    public ImageBackgroundCellRenderer(Cell modelElement, Image img) {
        super(modelElement);
        this.img = img;
    }

    @Override
    public void draw(DrawContext drawContext) {
        img.scaleToFit(getOccupiedAreaBBox().getWidth(), getOccupiedAreaBBox().getHeight());
        drawContext.getCanvas().addXObjectFittedIntoRectangle(img.getXObject(), getOccupiedAreaBBox());
        super.draw(drawContext);
    }
}