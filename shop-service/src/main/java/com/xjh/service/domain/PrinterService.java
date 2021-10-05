package com.xjh.service.domain;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Printer;
import com.xjh.dao.mapper.PrinterDAO;

@Singleton
public class PrinterService {
    @Inject
    PrinterDAO printerDAO;

    public Result<List<Printer>> query(Printer cond) {
        try {
            List<Printer> list = printerDAO.selectList(cond);
            return Result.success(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Printer getById(Integer printerId) {
        if (printerId == null) {
            return null;
        }
        Printer cond = new Printer();
        cond.setPrinterId(printerId);
        return printerDAO.selectList(cond)
                .stream().findFirst().orElse(null);
    }

    public int updateById(Printer printer) {
        if (printer.getPrinterId() == null) {
            return 0;
        }
        return printerDAO.updateById(printer);
    }

    public Result<Integer> save(Printer printer) {
        try {
            if (printer.getPrinterId() == null) {
                return Result.success(printerDAO.insert(printer));
            }
            return Result.success(printerDAO.updateById(printer));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }
}
