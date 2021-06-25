package com.xjh.dao.datasource;

import java.io.File;

import com.google.inject.Provider;
import com.xjh.common.utils.LogUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class LocalSqliteDataSourceProvider implements Provider<HikariDataSource> {
    @Override
    public HikariDataSource get() {
        File home = new File(".rundata/sqlite");
        LogUtils.info("Home path: " + home.getAbsolutePath());
        if (!home.exists()) {
            home.mkdirs();
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = "jdbc:sqlite:" + home.getAbsolutePath() + "/data.db";
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setUsername("SA");
        hikariConfig.setPassword("");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(hikariConfig);
    }
}
