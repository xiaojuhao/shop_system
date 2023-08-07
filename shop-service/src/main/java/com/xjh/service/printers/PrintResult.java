/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.service.printers;

import lombok.Data;

import java.util.List;

/**
 * @author liangh
 */
@Data
public class PrintResult {
    private Printer printer;
    private List<Object> printDataList;
    private boolean isSuccess;
    private int resultCode = PrinterStatus.INIT.status;

    public PrintResult(Printer printer, List<Object> printDataList) {
        this.printer = printer;
        this.printDataList = printDataList;
    }

    public void toFailure(int code) {
        setSuccess(false);
        setResultCode(code);
    }

    public void toSuccess(int code) {
        setSuccess(false);
        setResultCode(code);
    }
}
