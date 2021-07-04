package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class DeskDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(Desk desk) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(desk));
    }

    public List<Desk> select(Desk desk) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from desks where 1 = 1 ");
        if (desk.getDeskId() != null) {
            sql.append(" and deskId = ").append(desk.getDeskId());
        }
        if (CommonUtils.isNotBlank(desk.getDeskName())) {
            sql.append(" and deskName like '%").append(desk.getDeskName()).append("%' ");
        }
        if (desk.getStatus() != null) {
            sql.append(" and useStatus = ").append(desk.getStatus());
        }
        if (desk.getOrderId() != null) {
            sql.append(" and orderId = '").append(desk.getOrderId()).append("'");
        }
        List<Entity> list = Db.use(ds).query(sql.toString());
        return CommonUtils.map(list, this::convert);
    }

    public Desk getById(Integer id) throws SQLException {
        if (id == null) {
            return null;
        }
        Desk cond = new Desk();
        cond.setDeskId(id);
        List<Desk> desks = select(cond);
        return desks.stream().findFirst().orElse(null);
    }

    public int updateById(Desk desk) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(desk),
                EntityUtils.idCond(desk)
        );
    }

    public int useDesk(Desk desk) throws SQLException {
        return Db.use(ds).execute("update desks " +
                        " set orderId = ?," +
                        " createTime = ?, " +
                        " useStatus =  ?, " +
                        " where deskId = ? ",
                desk.getOrderId(),
                DateBuilder.base(desk.getOrderCreateTime()).mills(),
                EnumDesKStatus.USED.status(),
                desk.getDeskId());
    }

    public int freeDesk(Integer id) throws SQLException {
        return Db.use(ds).execute("update desks " +
                        " set orderId = 0," +
                        " createTime = null, " +
                        " useStatus = ? " +
                        " where deskId = ? ",
                EnumDesKStatus.FREE.status(),
                id);
    }

    private Desk convert(Entity entity) {
        Desk desk = new Desk();
        EntityUtils.convert(entity, desk);
        return desk;
    }
}
