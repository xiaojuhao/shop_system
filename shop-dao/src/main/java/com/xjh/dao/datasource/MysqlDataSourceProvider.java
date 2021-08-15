package com.xjh.dao.datasource;

import java.util.Properties;

import com.google.inject.Provider;
import com.xjh.common.store.RtPropNames;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
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
        Properties properties = SysConfigUtils.getDbConfig();
        String url = properties.getProperty(RtPropNames.dbUrlName);
        String driver = properties.getProperty(RtPropNames.dbDriverName);
        String username = properties.getProperty(RtPropNames.dbUsernameName);
        String password = properties.getProperty(RtPropNames.dbPasswordName);
        TimeRecord record = TimeRecord.start();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Logger.info("初始化MySql数据源: cost " + record.getCost());
        return ds;
    }
}
