package com.xjh.service.domain;

import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumPropName;
import com.xjh.common.store.DirUtils;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.*;
import com.xjh.dao.dataobject.Account;
import com.xjh.service.domain.model.ConfigItem;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Singleton
public class ConfigService {

    static Holder<Map<String, ConfigItem>> holder = new Holder<>();

    static List<String> candidateFiles = new ArrayList<>();
    static {
        candidateFiles.add(DirUtils.workDir());
        candidateFiles.add("C://xjh");
        candidateFiles.add("D://xjh");
    }

    public static File findConfigFile(){
        for(String dir : candidateFiles) {
            File f = new File(dir, "config.xlsx");
            if(f.exists()){
                Logger.info("找到配置文件:" + f.getAbsolutePath());
                return f;
            }
            f = new File(dir, "config.enc.xlsx");
            if(f.exists()){
                Logger.info("找到配置文件:" + f.getAbsolutePath());
                return f;
            }
        }
        return null;
    }

    public static boolean needDecryption(File file){
        return file != null && file.exists() && file.getName().endsWith(".enc.xlsx");
    }

    public static Map<String, ConfigItem> getConfigMap(){
        loadConfiguration();
        if(holder.get() != null){
            return holder.get();
        }
        return readConfig().getData();
    }

    static AtomicBoolean started = new AtomicBoolean(false);
    public static void loadConfiguration(){
        if(started.compareAndSet(false, true)){
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                Result<Map<String, ConfigItem>> rs = readConfig();
                if(rs.isSuccess()){
                    holder.hold(rs.getData());
                }
            }, 1, 60, TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) {
        try{
            Result<Map<String, ConfigItem>> config = readConfig();
            for(ConfigItem item : config.getData().values()){
                System.out.println(item.getKey() + " = " + item.getVal());
            }
        }finally {
            System.exit(0);
        }
    }

    static Map<String, Long> lastResolvedTime = new ConcurrentHashMap<>();
    static Holder<Map<String, ConfigItem>> lastResolvedData = new Holder<>();

    private static Result<Map<String, ConfigItem>> readConfig() {
        try{
            Map<String, ConfigItem> configMap = new HashMap<>();
            File file = findConfigFile();
            if(file == null){
                return Result.success(configMap);
            }
            // 判断上次解析后配置文件是否修改过
            Long lastTime = lastResolvedTime.get(file.getAbsolutePath());
            if(lastTime != null && lastTime < file.getAbsoluteFile().lastModified()){
                if(lastResolvedData.get() != null) {
                    return Result.success(lastResolvedData.get());
                }
            }
            System.out.println("开始解析配置文件: " + file.getAbsolutePath());
            System.out.println("上次解析时间: " + (lastTime == null ? "无" :
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastTime))));
            System.out.println("文件修改时间: " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.getAbsoluteFile().lastModified())));

            long startTime = System.currentTimeMillis();
            // 如果文件是加密的
            if(needDecryption(file)) {
                Properties properties = SysConfigUtils.loadRuntimeProperties();
                String filePassword = properties.getProperty(EnumPropName.FILE_PASSWORD.name);
                InputStream is = Files.newInputStream(file.toPath());
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
                EncryptionInfo encryptionInfo = new EncryptionInfo(poifsFileSystem);
                Decryptor decryptor = Decryptor.getInstance(encryptionInfo);
                decryptor.verifyPassword(filePassword);
                Workbook wb = new XSSFWorkbook(decryptor.getDataStream(poifsFileSystem));
                configMap = readConfigFromSheet(wb.getSheetAt(0));
                is.close();
            }else {
                try(Workbook wb = new XSSFWorkbook(Files.newInputStream(file.toPath()))) {
                    configMap = readConfigFromSheet(wb.getSheetAt(0));
                }
            }
            System.out.println("解析配置文件成功, 耗时: " + (System.currentTimeMillis() - startTime) + "毫秒");
            lastResolvedTime.put(file.getAbsolutePath(), System.currentTimeMillis());
            lastResolvedData.hold(configMap);
            return Result.success(configMap);
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("解析配置文件失败: " + ex.getMessage());
            return Result.fail(ex.getMessage());
        }
    }

    static Map<String, ConfigItem> readConfigFromSheet(Sheet sheet){
        Map<String, ConfigItem> configMap = new HashMap<>();
        int rows = sheet.getLastRowNum();
        RowHeader<ConfigItem> header = ExcelUtils.readHeader(sheet, ConfigItem.class);
        for(int i = 1; i <= rows; i++){
            Row row = sheet.getRow(i);
            ConfigItem item = ExcelUtils.readRow(row, header).getData();
            if(item != null && CommonUtils.isNotBlank(item.getKey())) {
                configMap.put(item.getKey(), item);
            }
        }
        return configMap;
    }

    public static ConfigItem getConfig(String configKey){
        Map<String, ConfigItem> config = getConfigMap();
        if(config != null){
            return config.get(configKey);
        }
        return null;
    }

    public static String getOssAccessKeyId(){
        ConfigItem ci = getConfig("ossAccessKeyId");
        return ci != null ? ci.getVal() : "";
    }


    public static String getOssEndpoint(){
        ConfigItem ci = getConfig("ossEndpoint");
        return ci != null ? ci.getVal() : "";
    }

    public static String getOssAccessKeySecret(){
        ConfigItem ci = getConfig("ossAccessKeySecret");
        return ci != null ? ci.getVal() : "";
    }
    public static String getTickedUrl(){
        ConfigItem ci = getConfig("tickedUrl");
        return ci != null ? ci.getVal() : "";
    }

    public static String getPublicAddress(){
        ConfigItem ci = getConfig("publicAddress");
        return ci != null ? ci.getVal() : "";
    }

    public static Account getSu(){
        ConfigItem name = getConfig("su_name");
        ConfigItem pwd = getConfig("su_password");
        if(name != null && pwd != null){
            Account su = new Account();
            su.setAccountUser(name.getVal());
            su.setAccountPass(pwd.getVal());
            su.setAccountId(1);
            su.setAccountNickName("超级管理员");
            return su;
        }
        return null;
    }


}
