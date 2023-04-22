package com.xjh.service.domain;

import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.mapper.PrinterDAO;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PrinterService {
    @Inject
    PrinterDAO printerDAO;

    public Result<List<PrinterDO>> query(PrinterDO cond) {
        try {
            List<PrinterDO> list = printerDAO.selectList(cond);
            return Result.success(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public PrinterDO getById(Integer printerId) {
        if (printerId == null) {
            return null;
        }
        PrinterDO cond = new PrinterDO();
        cond.setPrinterId(printerId);
        return printerDAO.selectList(cond)
                .stream().findFirst().orElse(null);
    }

    public Result<Integer> deleteById(Integer printerId) {
        return printerDAO.deleteById(printerId);
    }

    public Result<Integer> save(PrinterDO printer) {
        try {
            if (printer.getPrinterId() == null) {
                if (printer.getAddTime() == null) {
                    printer.setAddTime(DateBuilder.now().mills());
                }
                return printerDAO.insert(printer);
            }
            return printerDAO.updateById(printer);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }
}
