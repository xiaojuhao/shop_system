package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.enumeration.EnumDeskStatus;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;
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

    public Result<List<Desk>> select(Desk desk) {
        try {
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
            return Result.success(CommonUtils.collect(list, this::convert));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("查询异常:" + ex.getMessage());
        }
    }

    public Result<Desk> getById(Integer id) throws SQLException {
        if (id == null) {
            return null;
        }
        Desk cond = new Desk();
        cond.setDeskId(id);
        Result<List<Desk>> desksRs = select(cond);
        if (CommonUtils.isNotEmpty(desksRs.getData())) {
            return Result.success(desksRs.getData().stream().findFirst().orElse(null));
        }
        return Result.fail("查询失败");
    }

    public Result<Integer> updateById(Desk desk) {
        try {
            int i = Db.use(ds).update(
                    EntityUtils.create(desk),
                    EntityUtils.idCond(desk)
            );
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新异常:" + ex.getMessage());
        }
    }

    public int useDesk(Desk desk) throws SQLException {
        return Db.use(ds).execute("update desks " +
                        " set orderId = ?," +
                        " createTime = ?, " +
                        " useStatus =  ? " +
                        " where deskId = ? ",
                desk.getOrderId(),
                DateBuilder.now().mills(),
                EnumDeskStatus.IN_USE.status(),
                desk.getDeskId());
    }

    public long getDeskLastUpdateTime(int deskId) {
        try {
            String sql = "select * from desk_add_or_remove_update where deskId='" + deskId + "'";
            List<Entity> list = Db.use(ds).query(sql);
            Entity entity = list.stream().findFirst().orElse(null);
            if (entity == null) {
                return 0;
            }
            return entity.getLong("lastUpdateTime");
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int freeDesk(Integer id) throws SQLException {
        return Db.use(ds).execute("update desks " +
                        " set orderId = 0," +
                        " createTime = null, " +
                        " useStatus = ? " +
                        " where deskId = ? ",
                EnumDeskStatus.FREE.status(),
                id);
    }

    private Desk convert(Entity entity) {
        Desk desk = new Desk();
        EntityUtils.convert(entity, desk);
        return desk;
    }
}
