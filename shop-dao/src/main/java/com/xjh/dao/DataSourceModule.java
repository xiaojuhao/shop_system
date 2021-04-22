package com.xjh.dao;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.datasource.LocalSqliteDataSourceProvider;
import com.xjh.dao.datasource.MysqlDataSourceProvider;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(HikariDataSource.class).annotatedWith(Names.named("mysql")).toProvider(MysqlDataSourceProvider.class);
        System.out.println("MysqlDS 耗时: " + timeRecord.getCostAndRestart());
        bind(HikariDataSource.class).annotatedWith(Names.named("sqlite")).toProvider(LocalSqliteDataSourceProvider.class);
        System.out.println("LocalSqliteDS 耗时: " + timeRecord.getCostAndClear());
    }
}
