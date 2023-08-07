package com.xjh.service.printers;

public enum PrinterStatus {
    PRINTING(0, "打印中"), NORMAL(1, "正常"), NO_PAPER(2, "缺纸"), TRAPPED_PAPER(3, "卡纸"), UNKNOWN(4, "未知"), SOCKET_TIMEOUT(5, "打印机离线超时"), DATA_READ_TIMEOUT(6, "读取打印机返回字节超时"), INIT(7, "打印机结果刚new出来的初始状态"),
    ;

    PrinterStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public final int status;
    public final String remark;

    public static PrinterStatus of(Integer status) {
        if (status == null) {
            return UNKNOWN;
        }
        for (PrinterStatus s : PrinterStatus.values()) {
            if (status.equals(s.status)) {
                return s;
            }
        }
        return UNKNOWN;
    }
}
