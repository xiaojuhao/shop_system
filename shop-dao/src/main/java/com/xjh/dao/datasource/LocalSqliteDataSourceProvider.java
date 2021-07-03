package com.xjh.dao.datasource;

import com.google.inject.Provider;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.LogUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

public class LocalSqliteDataSourceProvider implements Provider<HikariDataSource> {
    private static class Inst {
        public static HikariDataSource ds = newDS();
    }

    @Override
    public HikariDataSource get() {
        return Inst.ds;
    }

    private static HikariDataSource newDS() {
        String workDir = SysConfigUtils.getWorkDir();
        File home = new File(workDir + ".rundata/sqlite");
        LogUtils.info("数据库目录：" + home.getAbsolutePath());
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
