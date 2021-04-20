package com.xjh.dao.datasource;

import com.google.inject.Provider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MysqlDataSourceProvider implements Provider<HikariDataSource> {
    @Override
    public HikariDataSource get() {
        Map<String, String> config = configMap();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("url"));
        hikariConfig.setDriverClassName(config.get("driver"));
        hikariConfig.setUsername(config.get("user"));
        hikariConfig.setPassword(config.get("pass"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(hikariConfig);
    }

    private static Map<String, String> configMap() {
        Map<String, String> map = new HashMap<>();
        URL url = DataSourceModule.class.getResource("/config/db.setting");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                map.putAll(asKV(line));
            }
            reader.close();
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
