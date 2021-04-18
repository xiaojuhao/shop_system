package com.xjh.dao.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MysqlDataSource extends HikariDataSource {
    public MysqlDataSource(HikariConfig config) {
        super(config);
    }
}
