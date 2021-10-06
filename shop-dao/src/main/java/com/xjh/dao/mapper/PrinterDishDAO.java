package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.PrinterDishDO;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class PrinterDishDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public PrinterDishDO queryByDishesId(Integer dishesId) {
        if (dishesId == null) {
            return null;
        }
        PrinterDishDO cond = new PrinterDishDO();
        cond.setDishId(dishesId);
        return selectList(cond).stream().findFirst().orElse(null);
    }

    public Result<Integer> deleteById(Integer printerDishId) {
        if (printerDishId == null) {
            return Result.fail("参数错误");
        }
        try {
            PrinterDishDO id = new PrinterDishDO();
            id.setPrinterDishId(printerDishId);
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

    public int updateById(PrinterDishDO printerDish) {
        try {
            return Db.use(ds).update(EntityUtils.create(printerDish),
                    EntityUtils.idCond(printerDish));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int insert(PrinterDishDO printerDish) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(printerDish));
    }

    public List<PrinterDishDO> selectList(PrinterDishDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, PrinterDishDO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
