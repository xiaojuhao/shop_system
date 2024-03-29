package com.xjh.dao.datasource;

import com.google.inject.Provider;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

import static com.xjh.common.store.DirUtils.workDir;

public class LocalSqliteDataSourceProvider implements Provider<HikariDataSource> {
    private static class Inst {
        public static HikariDataSource ds = newDS();
    }

    @Override
    public HikariDataSource get() {
        return Inst.ds;
    }

    private static HikariDataSource newDS() {
        TimeRecord record = TimeRecord.start();
        String workDir = workDir();
        File home = new File(workDir + "database/sqlite");
        Logger.info("数据库目录：" + home.getAbsolutePath());
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
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Logger.info("初始化Sqlite数据源: cost " + record.getCost());
        return ds;
    }
}
