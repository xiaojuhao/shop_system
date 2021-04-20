package com.xjh.dao.datasource;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.TimeRecord;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord.start();
        bind(HikariDataSource.class).annotatedWith(MysqlDS.class).toProvider(MysqlDataSourceProvider.class);
        System.out.println("MysqlDS 耗时: " + TimeRecord.getCostAndRestart());
        bind(HikariDataSource.class).annotatedWith(LocalSqliteDS.class).toProvider(LocalSqliteDataSourceProvider.class);
        System.out.println("LocalSqliteDS 耗时: " + TimeRecord.getCostAndClear());
    }
}
