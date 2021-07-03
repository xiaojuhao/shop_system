package com.xjh.dao.datasource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Provider;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.DataSourceModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MysqlDataSourceProvider implements Provider<HikariDataSource> {
    private static class Inst {
        public static HikariDataSource ds = newDS();
    }

    @Override
    public HikariDataSource get() {
        return Inst.ds;
    }

    private static HikariDataSource newDS() {
        TimeRecord record = TimeRecord.start();
        Map<String, String> config = configMap();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("url"));
        hikariConfig.setDriverClassName(config.get("driver"));
        hikariConfig.setUsername(config.get("user"));
        hikariConfig.setPassword(config.get("pass"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        LogUtils.info("初始化MySql数据源: cost " + record.getCost());
        return ds;
    }

    private static Map<String, String> configMap() {
        Map<String, String> map = new HashMap<>();
        InputStream stream = DataSourceModule.class.getResourceAsStream("/config/db.setting");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (CommonUtils.isBlank(line) || line.startsWith("#")) {
                    continue;
                }
                map.putAll(asKV(line));
            }
            reader.close();
            LogUtils.info("加载数据库配置文件:" + map);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private static Map<String, String> asKV(String str) {
        Map<String, String> map = new HashMap<>();
        if (str == null || str.trim().isEmpty()) {
            return map;
        }
        if (str.trim().startsWith("#")) {
            return map;
        }
        int idx = str.indexOf("=");
        if (idx > 0) {
            map.put(str.substring(0, idx).trim(), str.substring(idx + 1).trim());
        }
        return map;
    }
}
