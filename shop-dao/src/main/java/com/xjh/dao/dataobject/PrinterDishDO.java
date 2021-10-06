package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("printer_dish")
public class PrinterDishDO {
    @Id
    @Column("printer_dish_id")
    Integer printerDishId;

    @Column("printer_id")
    Integer printerId;

    @Column("dish_id")
    Integer dishId;

    @Column("create_time")
    Long createTime;

    @Column("mod_time")
    Long modTime;
}
