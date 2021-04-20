package com.xjh.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.Singleton;
import com.xjh.dao.ReaderDAO;
import com.xjh.dao.dataobject.Reader;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 读者DAO的实现类
 */
@Singleton
public class ReaderDAOImpl implements ReaderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    @Override
    public List<Reader> selectReaders() throws SQLException {
        List<Entity> entityList = Db.use(ds).query("SELECT * FROM t_reader ");
        List<Reader> readerList = new ArrayList<>();
        for (Entity entity : entityList) {
            readerList.add(convertReader(entity));
        }
        return readerList;
    }

    @Override
    public int deleteById(long id) throws SQLException {
        return Db.use(ds).del(
                Entity.create("t_reader").set("id", id)
        );
    }

    @Override
    public Long insertReader(Reader reader) throws SQLException {
        return Db.use(ds).insertForGeneratedKey(
                Entity.create("t_reader")
                        .set("name", reader.getName())
                        .set("avatar", reader.getAvatar())
                        .set("role", reader.getRole())
                        .set("department", reader.getDepartment())
                        .set("join_date", reader.getJoinDate())
                        .set("email", reader.getEmail())
                        .set("mobile", reader.getMobile())
        );
    }

    @Override
    public int countByRole(String role) throws SQLException {
        return Db.use(ds).queryNumber("SELECT COUNT(*) FROM t_reader WHERE role = ? ", role).intValue();
    }

    @Override
    public int countByDepartment(String department) throws SQLException {
        return Db.use(ds).queryNumber("SELECT COUNT(*) FROM t_reader WHERE department = ? ", department).intValue();
    }

    @Override
    public int countReaders() throws SQLException {
        return Db.use(ds).queryNumber("SELECT COUNT(*) FROM t_reader  ").intValue();

    }

    /**
     * 将Entity转换为Reader
     *
     * @param entity
     * @return Reader
     */
    private Reader convertReader(Entity entity) {
        Reader reader = new Reader();
        reader.setId(entity.getLong("id"));
        reader.setName(entity.getStr("name"));
        reader.setAvatar(entity.getStr("avatar"));
        reader.setRole(entity.getStr("role"));
        reader.setDepartment(entity.getStr("department"));
        reader.setEmail(entity.getStr("email"));
        reader.setMobile(entity.getStr("mobile"));
        reader.setJoinDate(entity.getDate("join_date"));
        return reader;
    }
}
