package com.wiscess.exporter.pdf;

import java.net.MalformedURLException;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.wiscess.exporter.util.PositionUtils;

public class PdfImage extends Image{
	
	private static ImageData notFoundData;
	private static ImageData notSupportData;
	static {
		try {
			ResourceLoader loader=new DefaultResourceLoader();
			notFoundData=ImageDataFactory.create(loader.getResource("/images/notfound.jpg").getURL());
			notSupportData=ImageDataFactory.create(loader.getResource("/images/notsupport.jpg").getURL());
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	/**
	 * 设置图片宽度
	 */
	private float width;
	/**
	 * 设置图片高度
	 */
	private float height;
	/**
	 * 是否自动缩放
	 */
	private boolean autoScale=true;
	
	/**
	 * 旋转角度
	 */
	private double rotationAngle=0f;
	/**
	 * 对齐方式
	 */
	private TextAlignment align=TextAlignment.LEFT;
	
	public static PdfImage getImage(String imagePath){
		ImageData imageData;
		try {

			if(imagePath.startsWith("data:image/")){
	            final String base64Data = imagePath.substring(imagePath.indexOf(",") + 1);
				imageData = ImageDataFactory.create(Base64.decode(base64Data));
			}else{
				imageData = ImageDataFactory.create(imagePath);
			}
		} catch (MalformedURLException e) {
			imageData=notSupportData;
		} catch (Exception e) {
			imageData=notFoundData;
		}
		return new PdfImage(imageData);
	}
	public PdfImage(ImageData img){
		super(img);
	}
	/**
	 * 设置图片大小
	 * @param width
	 * @param height
	 * @return
	 */
	public PdfImage size(float width,float height){
		return this.size(width, height, true);
	}
	/**
	 * 设置图片大小，是否保持纵横比
	 * @param width
	 * @param height
	 * @param autoScale
	 * @return
	 */
	public PdfImage size(float width,float height,boolean autoScale){
		this.width=width;
		this.height=height;
		this.autoScale=autoScale;
		return this;
	}
	/**
	 * 设置居中
	 * @param pageSize
	 * @return
	 */
	public PdfImage center(){
		//根据页面大小，计算居中
		this.align=TextAlignment.CENTER;
		return this;
	}
	/**
	 * 设置居右
	 * @param pageSize
	 * @return
	 */
	public PdfImage right(){
		//根据页面大小，计算居中
		this.align=TextAlignment.RIGHT;
		return this;
	}
	/**
	 * 设置旋转角度
	 */
	public PdfImage rotate(double angle){
		rotationAngle=angle;
		return this;
	}
	/**
	 * 处理图像，计算缩放、位置、旋转等
	 * @param pageSize
	 * @param margin
	 */
	public Image processImage(PageSize pageSize,PdfMargin margin){
		/**
		 * 是否自动缩放
		 */
		if(autoScale){
			//设置了宽高
			if(height>0 || width>0){
				/**
				 * 设置图片的宽度和高度，保持纵横比，图片等比例缩小到适应的宽度和高度
				 */
				scaleToFit(width, height);
			}else{
				//自动缩放
				setAutoScale(true);
			}
		}else{
			/**
			 * 设置图片的宽度和高度，不保持纵横比，图片被拉伸变形
			 */
			scaleAbsolute(width, height);
		}
		//记录图片缩放后的实际大小
		float w=getImageScaledWidth();
		float h=getImageScaledHeight();
		if(rotationAngle>0){
			//旋转后，宽高发生变化，需要重新计算宽高，用于计算显示位置
			double newW=w*Math.cos(rotationAngle)+h*Math.sin(rotationAngle);
			double newH=w*Math.sin(rotationAngle)+h*Math.cos(rotationAngle);
			w=(float)newW;
			h=(float)newH;
			setRotationAngle(rotationAngle);
		}
		
		if(pageSize!=null){
			//获取当前图片对应的页面大小和页边距
			float offsetX=PositionUtils.offsetX(pageSize, margin, align, UnitValue.createPointValue(w));
			setMarginLeft(offsetX);
		}
		return this;
	}
	
}
