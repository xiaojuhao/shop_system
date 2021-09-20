package com.xjh.common.utils;

import static com.xjh.common.store.SysConfigUtils.getWorkDir;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.xjh.common.valueobject.DishesImgVO;

import cn.hutool.core.codec.Base64;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageHelper {
    public static List<DishesImgVO> resolveImgs(String base64Imgs) {
        if (CommonUtils.isBlank(base64Imgs)) {
            return new ArrayList<>();
        }
        List<DishesImgVO> imgs = new ArrayList<>();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = Base64.decodeStr(base64Imgs);
            List<DishesImgVO> arr = JSONArray.parseArray(json, DishesImgVO.class);
            if (arr != null) {
                imgs.addAll(arr);
            }
        }
        return imgs;
    }

    public static String getImageDir() {
        return getWorkDir() + "images/";
    }

    public static String getImageUrl(String url) {
        if (CommonUtils.isBlank(url)) {
            url = "/img/book1.jpg";
        }
        String imageDir = getImageDir();
        return "file:" + imageDir + url.replaceAll("\\\\", "/");
    }

    public static ImageView buildImageView(String imgUrl) {
        if (CommonUtils.isBlank(imgUrl)) {
            return null;
        }
        return new ImageView(new Image(getImageUrl(imgUrl)));
    }
}
