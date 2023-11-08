package com.xjh.common.enumeration;

public enum  EnumPropName {
    WORK_DIR("work_dir"),

    DB_URL("db_url"),

    DB_DRIVER("db_driver"),

    DB_USERNAME("db_username"),

    DB_PASSWORD("db_password"),

    FILE_PASSWORD("file_password")
    ;
    public String name;
    EnumPropName(String name){
        this.name = name;
    }

}
