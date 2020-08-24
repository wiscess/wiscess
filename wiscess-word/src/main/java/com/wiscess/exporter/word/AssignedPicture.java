package com.wiscess.exporter.word;


/**
 * 图片信息
 *
 * @author wh
 */
public class AssignedPicture extends AssignedElement {

    /**
     * 文件路径：可以是http，也可以是绝对路径
     */
    private String imgFile;
    /**
     * 显示宽度
     */
    private int width;
    /**
     * 显示高度
     */
    private int height;

    public AssignedPicture(String imgFile, int width, int height) {
        this.imgFile = imgFile;
        this.width = width;
        this.height = height;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
