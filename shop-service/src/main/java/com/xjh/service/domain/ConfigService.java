package com.xjh.service.domain;

import com.google.inject.Singleton;
import com.sun.scenario.animation.shared.TimerReceiver;
import com.xjh.common.enumeration.EnumPropName;
import com.xjh.common.kvdb.impl.SysCfgDB;
import com.xjh.common.store.DirUtils;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Account;
import com.xjh.service.domain.model.ConfigItem;
import com.xjh.service.printers.StringUtil;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class ConfigService {
    static SysCfgDB sysCfgDB = SysCfgDB.inst();

    static Holder<Map<String, ConfigItem>> holder = new Holder<>();

    public static File findConfigFile(){
        File file = new File(DirUtils.workDir(), "config.xlsx");
        if(file.exists()){
            return file;
        }
        file = new File("c:/xjh", "config.xlsx");
        if(file.exists()){
            return file;
        }
        file = new File("d:/xjh", "config.xlsx");
        if(file.exists()){
            return file;
        }
        return null;
    }

    static String readCell(Cell cell){
        if(cell == null){
            return "";
        }
        String cellValue;
        if(cell.getCellTypeEnum() == CellType.NUMERIC){
            cellValue = cell.getNumericCellValue()+"";
        }else {
            cellValue = cell.getStringCellValue();
        }

        cellValue = CommonUtils.trim(cellValue);

        cellValue = cellValue.replaceAll("`", "");
        return cellValue;
    }

    public static Map<String, ConfigItem> getConfigMap(){
        startReadConfig();
        if(holder.get() != null){
            return holder.get();
        }
        return readConfig().getData();
    }

    static AtomicBoolean started = new AtomicBoolean(false);
    public static void startReadConfig(){
        if(started.compareAndSet(false, true)){
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                Result<Map<String, ConfigItem>> rs = readConfig();
                if(rs.isSuccess()){
                    holder.hold(rs.getData());
                }
            }, 10, 10, TimeUnit.SECONDS);
        }
    }

    public static boolean needReResolveConfig(){
        long startTime = System.currentTimeMillis();
        File file = findConfigFile();
        if(file == null){
            return false;
        }
        String lastTime = sysCfgDB.get("file_resolve_time_" + file.getName(), String.class);
        if(CommonUtils.isNotBlank(lastTime)){
            long ll = CommonUtils.parseLong(lastTime, 0L);
            System.out.println("检查配置文件更新, 耗时: " + (System.currentTimeMillis() - startTime) + "毫秒" );
            return file.lastModified() > ll;
        }
        System.out.println("检查配置文件更新, 耗时: " + (System.currentTimeMillis() - startTime) + "毫秒" );
        return true;
    }

    public static Result<Map<String, ConfigItem>> readConfig() {
        try{
            Map<String, ConfigItem> configMap = new HashMap<>();
            File file = findConfigFile();
            if(file == null){
                return Result.success(configMap);
            }
            long startTime = System.currentTimeMillis();
            Properties properties = SysConfigUtils.getDbConfig();
            String filePassword = properties.getProperty(EnumPropName.FILE_PASSWORD.name);
            if(CommonUtils.isNotBlank(filePassword)) {
                InputStream is = Files.newInputStream(file.toPath());
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
                EncryptionInfo encryptionInfo = new EncryptionInfo(poifsFileSystem);
                Decryptor decryptor = Decryptor.getInstance(encryptionInfo);
                decryptor.verifyPassword(filePassword);
                Workbook wb = new XSSFWorkbook(decryptor.getDataStream(poifsFileSystem));
                configMap = readConfigFromSheet(wb.getSheetAt(0));
                is.close();
            }else {
                Workbook wb = new XSSFWorkbook(file);
                configMap = readConfigFromSheet(wb.getSheetAt(0));
            }
            System.out.println("解析配置文件成功, 耗时: " + (System.currentTimeMillis() - startTime) + "毫秒");
            sysCfgDB.put("file_resolve_time_" + file.getName(), file.lastModified()+"");
            return Result.success(configMap);
        }catch (Exception ex){
            return Result.fail(ex.getMessage());
        }
    }

    static Map<String, ConfigItem> readConfigFromSheet(Sheet sheet){
        Map<String, ConfigItem> configMap = new HashMap<>();
        int rows = sheet.getLastRowNum();
        for(int i = 1; i <= rows; i++){
            Row row = sheet.getRow(i);
            ConfigItem item = new ConfigItem();
            item.setKey(readCell(row.getCell(0)));
            item.setVal(readCell(row.getCell(1)));
            item.setSensitive(readCell(row.getCell(2)));
            item.setRemark(readCell(row.getCell(3)));
            configMap.put(item.getKey(), item);
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
