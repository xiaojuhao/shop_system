package com.xjh.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.DeskDAO;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.datasource.MysqlDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DeskDAOImpl implements DeskDAO {
    @Inject
    MysqlDataSource ds;

    @Override
    public int insert(Desk desk) throws SQLException {
        Entity t = Entity.create("t_desk")
                .set("id", desk.getId())
                .set("desk_name", desk.getDeskName())
                .set("status", desk.getStatus())
                .set("max_person", desk.getMaxPerson())
                .set("desk_type", desk.getDeskType())
                .set("is_delete", desk.getIsDelete())
                .set("ver_no", CommonUtils.orElse(desk.getVerNo(), 1));
        return Db.use(ds).insert(t);
    }


    @Override
    public List<Desk> select(Desk desk) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from t_desk where 1 = 1 ");
        if (desk.getId() != null) {
            sql.append(" id = ").append(desk.getId());
        }
        if (CommonUtils.isNotBlank(desk.getDeskName())) {
            sql.append(" desk_name like '%").append(desk.getDeskName()).append("%' ");
        }
        if (desk.getStatus() != null) {
            sql.append(" status = ").append(desk.getStatus());
        }
        if (desk.getDeskType() != null) {
            sql.append(" desk_type = ").append(desk.getDeskType());
        }
        if (desk.getIsDelete() != null) {
            sql.append(" is_delete = ").append(desk.getIsDelete());
        }
        if (CommonUtils.isNotBlank(desk.getOrderId())) {
            sql.append(" order_id = '").append(desk.getOrderId()).append("'");
        }
        List<Entity> list = Db.use(ds).query(sql.toString());
        return CommonUtils.collect(list, this::convert);
    }

    @Override
    public int placeOrder(Desk desk) throws SQLException {
        return Db.use(ds).execute("update t_desk " +
                        " set order_id = ?," +
                        " order_create_time = ?, " +
                        " status = 2, " +
                        " ver_no = ver_no + 1 " +
                        " where id = ? ",
                desk.getOrderId(), desk.getOrderCreateTime(), desk.getId());
    }

    @Override
    public int clearOrder(Long id) throws SQLException {
        return Db.use(ds).execute("update t_desk " +
                " set order_id = null," +
                " order_create_time = null, " +
                " status = 1, " +
                " ver_no = ver_no + 1 " +
                " where id = ? ", id);
    }

    private Desk convert(Entity entity) {
        Desk desk = new Desk();
        desk.setId(entity.getLong("id"));
        desk.setDeskName(entity.getStr("desk_name"));
        desk.setDeskType(entity.getInt("desk_type"));
        desk.setMaxPerson(entity.getInt("max_person"));
        desk.setStatus(entity.getInt("status"));
        desk.setOrderId(entity.getStr("order_id"));
        desk.setVerNo(entity.getInt("ver_no"));
        desk.setOrderCreateTime(DateBuilder.base(entity.getTimestamp("order_create_time")).dateTime());
        return desk;
    }
}
