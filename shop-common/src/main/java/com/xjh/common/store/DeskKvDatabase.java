package com.xjh.common.store;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;

public class DeskKvDatabase {
    static final String deskDbName = "desks";
    static Database staticDB = null;

    public static Database getDB() {
        if (staticDB != null) {
            return staticDB;
        }
        synchronized (DeskKvDatabase.class) {
            if (staticDB != null) {
                return staticDB;
            }
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            Database db = BerkeleyDBEnv.getEnv().openDatabase(null, deskDbName, dbConfig);
            staticDB = db;
            return db;
        }
    }
}
