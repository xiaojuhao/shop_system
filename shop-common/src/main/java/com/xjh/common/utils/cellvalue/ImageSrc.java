package com.xjh.common.utils.cellvalue;

public class ImageSrc {
    public String imgUrl;
    public double width;
    public double height;

    public ImageSrc(String url) {
        this.imgUrl = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
