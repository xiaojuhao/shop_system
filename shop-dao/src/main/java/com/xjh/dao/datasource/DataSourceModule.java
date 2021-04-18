package com.xjh.dao.datasource;

import com.google.inject.AbstractModule;

public class DataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        long start = System.currentTimeMillis();
        bind(MysqlDataSource.class).toProvider(MysqlDataSourceProvider.class);
        System.out.println("DataSourceModule 耗时: " + (System.currentTimeMillis() - start));
    }
}
