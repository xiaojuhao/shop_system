package com.xjh.dao.datasource;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.TimeRecord;

public class DataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord.start();
        bind(MysqlDataSource.class).toProvider(MysqlDataSourceProvider.class);
        System.out.println("MysqlDataSource 耗时: " + TimeRecord.getCostAndRestart());
        bind(LocalSqliteDataSource.class).toProvider(LocalSqliteDataSourceProvider.class);
        System.out.println("LocalSqliteDataSource 耗时: " + TimeRecord.getCostAndClear());
    }
}
