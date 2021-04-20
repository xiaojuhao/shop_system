package com.xjh.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.Singleton;
import com.xjh.dao.TypeDAO;
import com.xjh.dao.dataobject.Type;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class TypeDAOImpl implements TypeDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    @Override
    public Long insertType(Type type) throws SQLException {
        //采用了另一种新增方法，可以返回插入记录的主键（Long类型）
        return Db.use(ds).insertForGeneratedKey(
                Entity.create("t_type")
                        .set("type_name", type.getTypeName())
        );
    }

    @Override
    public int deleteTypeById(long id) throws SQLException {
        return Db.use(ds).del(
                Entity.create("t_type").set("id", id)
        );
    }


    @Override
    public List<Type> selectAllTypes() throws SQLException {
        //查询得到List<Entity>
        List<Entity> entityList = Db.use(ds).query("SELECT * FROM t_type ");
        //创建一个List<Type>，存放具体的图书类别
        List<Type> typeList = new ArrayList<>();
        //遍历entityList，转换为typeList
        for (Entity entity : entityList) {
            typeList.add(convertType(entity));
        }
        return typeList;
    }

    @Override
    public Type getTypeById(long id) throws SQLException {
        //采用自定义带参查询语句，返回单个实体
        Entity entity = Db.use(ds).queryOne("SELECT * FROM t_type WHERE id = ? ", id);
        //将Entity转换为Type类型返回
        return convertType(entity);
    }

    @Override
    public int countTypes() throws SQLException {
        return Db.use(ds).queryNumber("SELECT COUNT(*) FROM t_type  ").intValue();
    }

    /**
     * 将Entity转换为Type类型
     *
     * @param entity
     * @return Type
     */
    private Type convertType(Entity entity) {
        Type type = new Type();
        type.setId(entity.getLong("id"));
        type.setTypeName(entity.getStr("type_name"));
        return type;
    }
}
