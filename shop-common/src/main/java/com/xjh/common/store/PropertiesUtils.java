package com.xjh.common.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;

public class PropertiesUtils {

    public static Properties loadFromWorkDir(String fileName) {
        String workDir = SysConfigUtils.getWorkDir();
        if (CommonUtils.isBlank(workDir)) {
            AlertBuilder.ERROR("异常", "工作目录未设置");
            throw new RuntimeException("工作目录未设置");
        }
        return loadProperties(workDir, fileName);
    }

    public static Properties loadProperties(String dir, String fileName) {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        File file = new File(dir + fileName);
        if (!file.exists()) {
            return new Properties();
        }
        try {

            InputStream inputStream = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(inputStream);
            inputStream.close();
            return prop;
        } catch (Exception ex) {
            AlertBuilder.ERROR("异常", "读取配置文件失败:" + fileName);
            throw new RuntimeException("读取配置文件失败" + ex.getMessage());
        }
    }

    public static void dumpProperties(File file, Properties properties) {

        try {
            FileWriter writer = new FileWriter(file);
            properties.store(writer, "properties config");
            writer.close();
        } catch (Exception ex) {
            AlertBuilder.ERROR("异常", "回写配置文件失败:" + file.getAbsolutePath());
            throw new RuntimeException("回写配置文件失败" + ex.getMessage());
        }
    }
}
