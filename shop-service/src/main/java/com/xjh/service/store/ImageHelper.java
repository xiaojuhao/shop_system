package com.xjh.service.store;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSSClient;
import com.xjh.common.model.ConfigurationBO;
import com.xjh.common.store.OssStore;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.service.domain.ConfigService;
import com.xjh.service.printers.StringUtil;
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

    private static String getImageDir(String dir) {
        return dir + "/images/";
    }

    private static File resolveImgUrl(String url) {
        if (CommonUtils.isBlank(url)) {
            return null;
        }
        url = url.replaceAll("\\\\", "/");
        String path = getImageDir() + url;
        File file = new File(path);
        //Logger.info("图片路劲:" + file.exists() + ", " + path);
        if (!file.exists()) {
            Properties runtimeProp = SysConfigUtils.loadRuntimeProperties();
            String path2 = getImageDir(runtimeProp.getProperty("work_dir")) + url;
            file = new File(path2);
            //Logger.info("图片路劲(备份):" + file.exists() + "," + path);
        }
        if(!file.exists()){
            ConfigurationBO cfg = ConfigService.loadConfiguration();
            if(CommonUtils.isNotBlank(cfg.getOssAccessKeyId())){
                OssStore ossStore = new OssStore(cfg.getOssEndpoint(), cfg.getOssAccessKeyId(), cfg.getOssAccessKeySecret());
                ossStore.download("images/"+url, file);
            }
        }
        return file;
    }

    public static ImageView buildImageView(String imgUrl) {
        if (CommonUtils.isBlank(imgUrl)) {
            return null;
        }
        File imageFile = resolveImgUrl(imgUrl);
        if (imageFile == null || !imageFile.exists()) {
            return null;
        }
        try {
            return new ImageView(new Image("file:" + imageFile.getAbsolutePath()));
        } catch (Exception ex) {
            Logger.info("解析图片错误: " + ex.getMessage() + ", " + imageFile.getAbsolutePath());
            return null;
        }
    }
}
