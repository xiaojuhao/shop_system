package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class PrinterDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> deleteById(Integer printerId) {
        if (printerId == null) {
            return Result.fail("参数错误");
        }
        try {
            PrinterDO id = new PrinterDO();
            id.setPrinterId(printerId);
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

    public Result<Integer> updateById(PrinterDO printer) {
        try {
            int i = Db.use(ds).update(EntityUtils.create(printer),
                    EntityUtils.idCond(printer));
            if (i == 0) {
                return Result.fail("未更新到打印机信息");
            }
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("更新打印信息异常: " + ex.getMessage());
        }
    }

    public Result<Integer> insert(PrinterDO printer) throws SQLException {
        try {
            int i = Db.use(ds).insert(EntityUtils.create(printer));
            if (i == 0) {
                return Result.fail("插入打印机信息失败");
            }
            return Result.success(i);
        } catch (Exception ex) {
            Logger.error("插入打印机失败" + ex.getMessage());
            return Result.fail("新增打印机失败:" + ex.getMessage());
        }
    }

    public PrinterDO selectByPrinterId(Integer printerId) {
        if (printerId == null) {
            return null;
        }
        PrinterDO cond = new PrinterDO();
        cond.setPrinterId(printerId);
        return selectList(cond).stream().findFirst().orElse(null);
    }

    public List<PrinterDO> selectList(PrinterDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, PrinterDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
