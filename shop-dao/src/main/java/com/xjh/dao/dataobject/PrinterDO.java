package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("printer_list")
public class PrinterDO {
    @Id
    @Column("printerId")
    Integer printerId;

    @Column("printerName")
    String printerName;

    @Column("printerIp")
    String printerIp;

    @Column("printerPort")
    Integer printerPort;

    @Column("printerInfo")
    String printerInfo;

    // 1:80mm;  0:58mm
    @Column("printerType")
    Integer printerType;

    @Column("printerStatus")
    Integer printerStatus;

    @Column("addTime")
    Long addTime;
}
