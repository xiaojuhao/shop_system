package com.xjh.service.store;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.store.OssStore;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.service.domain.ConfigService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
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
        for (String dir : listImageDirs()) {
            if (new File(dir).exists()) {
                return dir;
            }
        }
        return getImageDir(workDir());
    }

    private static String getImageDir(String dir) {
        if(dir.endsWith("/")){
            return dir + "images/";
        }
        return dir + "/images/";
    }

    public static boolean localExists(String url){
        if (CommonUtils.isBlank(url)) {
            return true;
        }
        url = url.replaceAll("\\\\", "/");
        for (String dir : listImageDirs()) {
            String path = dir + url;
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    public static File resolveImgUrl(String url) {
        if (CommonUtils.isBlank(url)) {
            return null;
        }
        url = url.replaceAll("\\\\", "/");
        for (String dir : listImageDirs()) {
            String path = dir + url;
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }

        // 从OSS下载图片资源
        if (CommonUtils.isNotBlank(ConfigService.getOssAccessKeyId())) {
            // 下载文件保存路径
            String downloadDir = CommonUtils.firstOf(listImageDirs());
            File downloadToFile = new File(downloadDir + url);
            // 下载文件
            OssStore ossStore = new OssStore(ConfigService.getOssEndpoint(), ConfigService.getOssAccessKeyId(), ConfigService.getOssAccessKeySecret());
            if(ossStore.download("images/" + url, downloadToFile)) {
                return downloadToFile;
            }
        }

        URL logo = ImageHelper.class.getClassLoader().getResource("img/logo.png");

        return new File(logo.getFile());
    }

    static List<String> listImageDirs() {
        List<String> dirs = new ArrayList<>();
        Properties runtimeProp = SysConfigUtils.loadRuntimeProperties();
        String path2 = getImageDir(runtimeProp.getProperty("work_dir"));
        if (CommonUtils.isNotBlank(path2)) {
            dirs.add(normalizeDir(path2));
        }

        dirs.add(getImageDir(normalizeDir(workDir())));

        return dirs;
    }

    static String normalizeDir(String dir) {
        dir = dir.replaceAll("\\\\", "/");
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        return dir;
    }

    public static ImageView buildImageView(String imgUrl) {
        try {
            if (CommonUtils.isBlank(imgUrl)) {
                return null;
            }
            File imageFile = resolveImgUrl(imgUrl);
            if (imageFile == null || !imageFile.exists()) {
                return null;
            }

            return new ImageView(new Image("file:" + imageFile.getAbsolutePath()));
        } catch (Exception ex) {
            Logger.info("解析图片错误: " + ex.getMessage() + ", " + imgUrl);
            return null;
        }
    }
}
