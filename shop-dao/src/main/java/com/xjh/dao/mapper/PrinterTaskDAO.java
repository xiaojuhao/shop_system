package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.PrinterTaskDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class PrinterTaskDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> deleteById(Integer printTaskId) {
        if (printTaskId == null) {
            return Result.fail("参数错误");
        }
        try {
            PrinterTaskDO id = new PrinterTaskDO();
            id.setPrintTaskId(printTaskId);
            int i = Db.use(ds).del(EntityUtils.idCond(id));
            if (i == 0) {
                return Result.fail("删除失败");
            }
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public int updateById(PrinterTaskDO printerTask) {
        try {
            return Db.use(ds).update(EntityUtils.create(printerTask),
                    EntityUtils.idCond(printerTask));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int insert(PrinterTaskDO printerTask) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(printerTask));
    }

    public List<PrinterTaskDO> selectList(PrinterTaskDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, PrinterTaskDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PrinterTaskDO selectByPrintTaskName(String name) {
        PrinterTaskDO cond = new PrinterTaskDO();
        cond.setPrintTaskName(name);
        return selectList(cond).stream().findFirst().orElse(null);
    }
}
