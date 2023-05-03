package com.xjh.common.utils;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.valueobject.DishesImgVO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.xjh.common.store.DirUtils.workDir;

public class ImageHelper {
    public static List<DishesImgVO> resolveImgs(String base64Imgs) {
        if (CommonUtils.isBlank(base64Imgs)) {
            return new ArrayList<>();
        }
        List<DishesImgVO> imgs = new ArrayList<>();
        if (CommonUtils.isNotBlank(base64Imgs)) {
            String json = base64Imgs;
            if (!json.contains("[")) {
                json = Base64.decodeStr(base64Imgs);
            }
            List<DishesImgVO> arr = JSONArray.parseArray(json, DishesImgVO.class);
            if (arr != null) {
                imgs.addAll(arr);
            }
        }
        return imgs;
    }

    public static String getImageDir() {
        return getImageDir(workDir());
    }

    public static String getImageDir(String dir) {
        return dir + "/images/";
    }

    public static String getImageUrl(String url) {
        if (CommonUtils.isBlank(url)) {
            url = "/img/book1.jpg";
        }
        String path = "file:" + getImageDir() + url.replaceAll("\\\\", "/");
        File file = new File(path);
        Logger.info("图片路劲:" + file.exists() + ", " + path);
        if (!file.exists()) {
            Properties runtimeProp = SysConfigUtils.loadRuntimeProperties();
            path = "file:" + getImageDir(runtimeProp.getProperty("work_dir")) + url.replaceAll("\\\\", "/");
            file = new File(path);
            Logger.info("图片路劲(备份):" + file.exists() + ", " + path);
        }
        return path;
    }

    public static ImageView buildImageView(String imgUrl) {
        if (CommonUtils.isBlank(imgUrl)) {
            return null;
        }
        return new ImageView(new Image(getImageUrl(imgUrl)));
    }
}
