package com.xjh.dao.datasource;

import com.google.inject.Provider;
import com.xjh.common.store.SysConfigUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

import static com.xjh.common.enumeration.EnumPropName.*;

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
        String url = properties.getProperty(DB_URL.name);
        String driver = properties.getProperty(DB_DRIVER.name);
        String username = properties.getProperty(DB_USERNAME.name);
        String password = properties.getProperty(DB_PASSWORD.name);
        TimeRecord record = TimeRecord.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setDriverClassName(driver);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource ds = new HikariDataSource(config);
        Logger.info("初始化MySql数据源: cost " + record.getCost());
        return ds;
    }
}
