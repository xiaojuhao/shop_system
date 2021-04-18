package com.xjh.dao.datasource;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

import com.google.inject.Provider;
import com.zaxxer.hikari.HikariConfig;

public class LocalSqliteDataSourceProvider implements Provider<LocalSqliteDataSource> {
    @Override
    public LocalSqliteDataSource get() {
        File home = new File(".rundata/sqlite");
        System.out.println("Home path: " + home.getAbsolutePath());
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
        LocalSqliteDataSource ds = new LocalSqliteDataSource(hikariConfig);
        init(ds);
        return ds;
    }


    public void init(LocalSqliteDataSource ds) {
        try {
            Connection conn = ds.getConnection();
            String sql = "CREATE TABLE if not exists t_desk " +
                    "(id INT8 PRIMARY KEY     NOT NULL," +
                    " desk_name       TEXT    NOT NULL, " +
                    " status          INT     NOT NULL, " +
                    " max_person      INT     NOT NULL, " +
                    " desk_type       INT     NOT NULL, " +
                    " is_delete       INT     NOT NULL, " +
                    " ver_no          INT)";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Connection conn = ds.getConnection();
            for (int i = 0; i < 20; i++) {
                String sql = "INSERT INTO t_desk (ID,DESK_NAME,STATUS,MAX_PERSON,DESK_TYPE,IS_DELETE,VER_NO) " +
                        "VALUES (" + i + ", '" + i + "', 1, 1, 0,1,1 );";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
            conn.close();
        } catch (Exception ex) {

        }
    }
}
