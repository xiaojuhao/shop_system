package com.xjh.dao.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class LocalSqliteDataSource extends HikariDataSource {
    public LocalSqliteDataSource(HikariConfig config) {
        super(config);
    }
}
